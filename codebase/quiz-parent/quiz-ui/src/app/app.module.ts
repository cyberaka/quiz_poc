import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ScorePipe } from './score.pipe';
import { AuthHttpInterceptor, AuthModule } from '@auth0/auth0-angular';
import { ErrorComponent } from './components/error/error.component';
import { LoadingComponent } from './components/loading/loading.component';
import { environment as env } from '../environments/environment';


@NgModule({
  declarations: [AppComponent, ScorePipe, ErrorComponent, LoadingComponent],
  imports: [
    BrowserModule,
    IonicModule.forRoot(
      {
        mode: 'ios'
      }
    ), 
    AuthModule.forRoot({
      domain: 'dev-ybfald6zel3sqp5d.us.auth0.com',
      clientId: 'I6irZPjiEJxxCsjrVAIzfhgWChw43abd',
      authorizationParams: {
        redirect_uri: window.location.origin
      },
      httpInterceptor: {
        allowedList : [
          {
            uri: `${env.baseURL}/*`,
          }
        ]
      },
      errorPath: "/error",
    }), 
    AppRoutingModule, HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthHttpInterceptor,
      multi: true,
    },
    { 
      provide: RouteReuseStrategy, 
      useClass: IonicRouteStrategy 
    }
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
