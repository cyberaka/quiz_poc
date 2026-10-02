# qajit.com deployment runbook

qajit.com runs on the shared quizwrap.com server (`23.239.19.23`, Ubuntu 24.04,
host nginx + certbot, behind Cloudflare), next to quizwrap, mint, squeezy and
seekanswers. It was previously deployed by hand on `170.187.238.150`.

## How deploys work

Every push to `master` that touches `codebase/quiz-parent/**`, `deploy/**` or
the workflow runs `.github/workflows/deploy-production.yml`; it can also be run
by hand from the Actions tab or with `gh workflow run deploy-production`.

1. Fails early if any required secret is missing.
2. Builds `quiz-ws` (Maven, Java 17) and `quiz-ui` (Angular/Ionic) inside Docker
   and pushes `cyberaka/quiz-ws` and `cyberaka/quiz-ui` as `:prod` and
   `:prod-<git sha>`.
3. Copies `deploy/docker-compose.prod.yml` to `~/qajit-com` on the server.
4. Over SSH, pulls the images and recreates only `qajitcom-ws` and `qajitcom-ui`,
   then waits up to 6 minutes for both health checks and fails the job otherwise.

| Container | Host port | Upstream for |
|---|---|---|
| `qajitcom-ui` | `127.0.0.1:8400` | `qajit.com/` |
| `qajitcom-ws` | `127.0.0.1:8401` | `qajit.com/api/` |

Rules for the shared server:

- **Deploy only through the workflow.** Secrets live only in GitHub; a hand-run
  `docker compose up` refuses to start without them. A plain `docker restart`
  or a server reboot is fine, since containers keep their environment.
- Keep `name: qajitcom` in the compose file. quizwrap's log shipper ingests any
  compose project named `qajit` into quizwrap's production analytics.
- Keep `~/qajit` untouched; that directory is quizwrap's production stack.
- Never `docker compose down`, and never prune volumes or networks on this host.
- `QUIZ_DDL_AUTO` must stay `none`. `create` re-imports questions from Google
  Sheets into the shared MongoDB Atlas database.

## One-time setup

### 1. Deploy key

The workflow reuses the existing deployment key whose public half is already in
`~/.ssh/authorized_keys` for `cyberaka` on the server. No new key is needed;
only its private half has to be added to this repository's secrets (step 2),
because GitHub secrets are per repository.

### 2. GitHub secrets (cyberaka/quiz_poc)

The MongoDB URI and Auth0 management client values are in `~/reboot-quiz.sh`
on the old server (also in the backup of its home directory).

```bash
R=cyberaka/quiz_poc
gh secret set DOCKER_USERNAME -R $R --body cyberaka
gh secret set DOCKER_PASSWORD -R $R          # Docker Hub access token
gh secret set SERVER_HOST -R $R --body 23.239.19.23   # origin IP; the domain resolves to Cloudflare
gh secret set SERVER_USER -R $R --body cyberaka
gh secret set SSH_PRIVATE_KEY -R $R < <path to the existing deployment private key>
gh secret set QUIZ_CONNECTION_STRING -R $R
gh secret set QUIZ_AUTH_MANAGEMENT_CLIENT_ID -R $R
gh secret set QUIZ_AUTH_MANAGEMENT_CLIENT_SECRET -R $R
```

### 3. MongoDB Atlas

Add `23.239.19.23` to the project's IP access list (Network Access), unless it
already allows `0.0.0.0/0`.

### 4. First deploy

Merge to `master` (or run the workflow by hand). The old server keeps serving
qajit.com; nothing public changes yet. Then on the server:

```bash
curl -s http://127.0.0.1:8401/api/public          # "Public Endpoint."
curl -s http://127.0.0.1:8401/api/topics | head -c 300   # real topics = Atlas reachable
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8400/
```

### 5. nginx site (sudo)

```bash
cd ~/projects/github && git clone git@github.com:cyberaka/quiz_poc.git   # or git pull
sudo cp ~/projects/github/quiz_poc/deploy/nginx/qajit.com.conf /etc/nginx/sites-available/qajit.com
sudo ln -s /etc/nginx/sites-available/qajit.com /etc/nginx/sites-enabled/qajit.com
sudo nginx -t && sudo systemctl reload nginx
```

Test before touching DNS:

```bash
curl -s -H 'Host: qajit.com' http://127.0.0.1/api/public
curl -s -o /dev/null -w '%{http_code}\n' -H 'Host: qajit.com' http://127.0.0.1/
```

### 6. DNS cutover and TLS

qajit.com's DNS is on Cloudflare, but not in the account that holds
quizwrap.com.

1. In that Cloudflare account, point the `qajit.com` and `www.qajit.com` A
   records at `23.239.19.23` (keep them proxied). Delete `oidc.qajit.com`; it
   only served the retired Keycloak test.
2. Immediately issue the certificate. HTTP-01 works through the Cloudflare
   proxy, as for seekanswers.in and adbar.ai:
   ```bash
   sudo certbot --nginx -d qajit.com -d www.qajit.com
   ```
   Until certbot finishes (a minute or two), Cloudflare in Full mode cannot
   reach the origin over HTTPS and visitors may see a 5xx error.
3. Check https://qajit.com: login, topics, sub-topics, a quiz, the score page.

### 7. Retire the old server

Leave `170.187.238.150` running for a few days as the rollback target. Then:

- Remove the `maker.qajit.com` DNS record (or redirect it to quizwrap.com).
- In Auth0, remove `https://maker.qajit.com/callback` and the unused
  "Quiz Maker API".
- Terminate the server. A backup of its home directory, nginx, certificates and
  Docker volumes is in `~/Backups` on the owner's Mac.

## Rollback

- **Bad release:** revert the commit on `master`; the workflow redeploys the
  previous code. Every release also exists as `cyberaka/quiz-ws:prod-<sha>` and
  `cyberaka/quiz-ui:prod-<sha>`.
- **Before the old server is terminated:** point the Cloudflare A records back
  at `170.187.238.150`. The old containers are untouched.
