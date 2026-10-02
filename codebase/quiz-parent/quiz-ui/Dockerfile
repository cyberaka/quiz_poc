# Build context: codebase/quiz-parent/quiz-ui
# auth_config.json is gitignored; CI copies deploy/auth_config.prod.json here before building.
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration production

FROM nginx:alpine
WORKDIR /app
COPY --from=build /app/www/ .
COPY ./nginx.conf /etc/nginx/nginx.conf
EXPOSE 8080
