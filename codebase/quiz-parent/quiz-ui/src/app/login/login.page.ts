import { Component, NgZone, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../services/http.service';
import { DialogService } from '../services/dialog.service';
import { UtilsService } from '../services/utils.service';
import { AuthService } from '@auth0/auth0-angular';
import { Capacitor } from '@capacitor/core';
import { Browser } from '@capacitor/browser';

let window: any;
@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  pageTitle: string = 'Login';
  loading: boolean = true;
  
  isAuthenticated: boolean = false;
  constructor(
    private router: Router,
    private dialog: DialogService,
    private http: HttpService,
    private utils: UtilsService,
    public auth: AuthService
  ) {}

  ngOnInit() {
    /* let token = localStorage.getItem('token') || "";
    if(token) {
      let data = JSON.parse(atob(token.split('.')[1])).exp;
      if(new Date().getTime() < data * 1000) {

      }
    } */
    this.getLoggedDetails();
  }


  getLoggedDetails() {
    this.auth.isAuthenticated$.subscribe((res) =>{
      if(res) {
        this.router.navigateByUrl('topics', {replaceUrl : true});
        this.auth.user$.subscribe((res) => {
          console.log(res);
          this.http.userDet = res;
        })
      } else {
        this.loading = false;
        this.isAuthenticated = false;
      }
    });
  }
  /**
   * Login clicked Hit the API Based on response redirected to Topics screen
   *
   */
  async login() {
    if (Capacitor.isNativePlatform()) {
      this.auth
        .loginWithRedirect({
          async openUrl(url: string) {
            const res = await Browser.open({ url, windowName: '_self' });
            console.log(res);
          },
        })
        .subscribe((res) => {
          this.auth.isAuthenticated$.subscribe((res) => {
            if (res) {
              this.setToken();
              this.router
                .navigateByUrl('topics', { replaceUrl: true });
            }
          });
        });
    } else {
      this.auth.loginWithRedirect().subscribe((c) => {
        console.log('loginWithRedirect');
      });
    }
  }
  setToken() {
    this.auth.getAccessTokenSilently().subscribe((c) => {
      localStorage.setItem('token', c);
    });
  }
}
