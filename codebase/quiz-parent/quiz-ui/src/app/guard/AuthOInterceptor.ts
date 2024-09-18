import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpService } from '../services/http.service';


@Injectable()
export class oAuthInterceptor implements HttpInterceptor {
    constructor(private httpService: HttpService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add authorization header with oAuth token if available

        let token = localStorage.getItem('mode') === 'guest' ? '' : this.httpService?.oauthToken || "";
        if (token) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            });
        }
        return next.handle(request);
    }
}

