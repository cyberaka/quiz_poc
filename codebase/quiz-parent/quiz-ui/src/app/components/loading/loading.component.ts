import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss'],
})
export class LoadingComponent  implements OnInit {

  constructor(
    private auth: AuthService,
    private router: Router,
    private toastController: ToastController
  ) { }

  ngOnInit() {
    this.auth.isAuthenticated$.subscribe(res => {
      if(res) {
        this.router.navigateByUrl('topics', {replaceUrl : true}).then(c => {
          this.getDetails();
        });
      } else {
        this.router.navigateByUrl('login', {replaceUrl : true});
      }
    });
  }

  getDetails() {

    this.auth.user$.subscribe((c) => {
      // console.log(c);
    });
    this.auth.getAccessTokenSilently().subscribe((c) => {
      // console.log(c);
    });
    /* this.auth.handleRedirectCallback().subscribe(c => {
      console.log(c);
    }); */
  }

}
