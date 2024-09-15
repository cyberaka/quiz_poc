import { APP_INITIALIZER, NgModule } from '@angular/core';
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
import { firstValueFrom } from 'rxjs';
import { AuthService } from './services/Auth/auth.service';

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
      ...env.auth,
      httpInterceptor: {
        ...env.httpInterceptor,
      },
    }),
    AppRoutingModule, HttpClientModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: authInterceptorFactory,
      deps: [AuthService],
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

// Factory to dynamically provide the interceptor
export function authInterceptorFactory(authService: AuthService) {
  return async () => {
    const isAuthenticated = await firstValueFrom(authService.isAuthenticated());
    if (isAuthenticated) {
      return {
        provide: HTTP_INTERCEPTORS,
        useClass: AuthHttpInterceptor,
        multi: true
      };
    }
    return null;
  };
}
