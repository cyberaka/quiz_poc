import { Component, NgZone, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../services/http.service';
import { DialogService } from '../services/dialog.service';
import { UtilsService } from '../services/utils.service';
import { AuthService } from '@auth0/auth0-angular';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  pageTitle: string = "Login";
  userName: string = 'cyberaka';
  password: string = 'abcd1234';
  constructor(
    private router: Router,
    private dialog: DialogService,
    private http: HttpService,
    private utils: UtilsService,
    private auth: AuthService,
  ) { 
  }

  ngOnInit() {
  }

  /**
   * Login clicked Hit the API Based on response redirected to Topics screen
   * 
   */
  async login() {
    this.auth.loginWithRedirect().subscribe(c => {
      console.log(c);
    });
    /* this.utils.showLoader();
    this.http.login(this.userName, this.password).subscribe(c => {
      this.http.userDet = c;
      this.router.navigateByUrl('topics', {
        replaceUrl: true
      });
    }, (_) => {
      this.dialog.showAlert(
        'Invalid Credentials',
        [
          {
            text: 'Ok',
            handlerReturn: 'ok'
          }
        ]
      ).then((c) => {
        console.log(c);
      });
      this.utils.stopLoader();
    }); */
  }

}
