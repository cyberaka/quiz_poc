import { Component, NgZone, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../services/http.service';
import { DialogService } from '../services/dialog.service';
import { LoadingController } from '@ionic/angular';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  pageTitle: string = "Login";
  userName: string = '';
  password: string = '';
  constructor(
    private router : Router,
    private dialog :DialogService,
    private http: HttpService,
    private loader: LoadingController,
  ) { }

  ngOnInit() {
  }

  async login() {
    const loading = await this.loader.create({
      message: 'Loading...'
    });
    this.http.login(this.userName, this.password).subscribe(c => {
      this.http.userDet = c;
      loading.dismiss();
      this.router.navigateByUrl('topics', {
        replaceUrl : true
      });
    }, (err) => {
      loading.dismiss();
      this.dialog.showAlertWithButtons('Invalid Credentials', 'OK', (c: boolean) => {
        console.log(c);
      });
    });
  }

}
