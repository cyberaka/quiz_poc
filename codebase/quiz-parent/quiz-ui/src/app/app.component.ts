import { Component, OnInit, NgZone  } from '@angular/core';
import { Browser } from '@capacitor/browser';
import { App } from '@capacitor/app';
import { AuthService } from '@auth0/auth0-angular';
import { mergeMap } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit  {
  constructor(
    private auth: AuthService,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit(): void {
    App.addListener('appUrlOpen', ({ url }) => {
      // Must run inside an NgZone for Angular to pick up the changes
      // https://capacitorjs.com/docs/guides/angular
      this.ngZone.run(() => {
        console.log('appUrlOpen');
        console.log(url);
        console.log('redirect URI');
        console.log(environment.auth.authorizationParams.redirect_uri);
        if (url?.startsWith(environment.auth.authorizationParams.redirect_uri)) {
          if (
            url.includes('state=') &&
            (url.includes('error=') || url.includes('code='))
          ) {
            console.log('state');
            console.log(url);
            this.auth
              .handleRedirectCallback(url)
              .pipe(mergeMap(() => 
                Browser.close().then(c => {
                  console.log('close');
                  console.log('url');
                })
              ))
              .subscribe({
                next: (val) => {
                  console.log('state');
                  console.log(val);
                }
              });
          } else {
            this.auth.isAuthenticated$.subscribe(c => {
              if(!c) {
                this.homeRedirect();
              } else {
                Browser.close().then(c => {
                  console.log(c);
                });
              }
            })
           
          }
        }
      });
    });
  }

  homeRedirect() {
    this.ngZone.run(() => { 
      this.router
      .navigateByUrl('login', { replaceUrl: true }).then(c => {
        Browser.close().then(c => {
          console.log(c);
        }).catch(e => {
          console.log(e);
        });
      });
    });
  }
}
