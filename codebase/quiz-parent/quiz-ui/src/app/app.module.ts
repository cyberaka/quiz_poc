import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ScorePipe } from './score.pipe';
import { AuthModule } from '@auth0/auth0-angular';


@NgModule({
  declarations: [AppComponent, ScorePipe],
  imports: [
    BrowserModule,
    IonicModule.forRoot(
      {
        mode: 'ios'
      }
    ), AuthModule.forRoot({
      domain: 'https://quiz-dev-qfyy7zspmvytp6j0.us.auth0.com/authorize',
      clientId: 'PzCYYKck97fVNCgK8pMzm6TV9Rjr3r8b',
      authorizationParams: {
        redirect_uri: window.location.href
      }
    }), AppRoutingModule, HttpClientModule],
  providers: [{ provide: RouteReuseStrategy, useClass: IonicRouteStrategy }],
  bootstrap: [AppComponent],
})
export class AppModule { }
