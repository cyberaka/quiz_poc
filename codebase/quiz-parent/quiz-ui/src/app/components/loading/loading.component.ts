import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { ToastController } from '@ionic/angular';
import { take } from 'rxjs';

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
  ) {
   }

  ngOnInit() {
    if(localStorage.getItem('token')) {
      this.router.navigateByUrl('topics', {replaceUrl : true});
    } else {
      this.router.navigateByUrl('login', {replaceUrl : true});
    }
  }

  getDetails() {

    this.auth.user$.subscribe((c) => {
      console.log(c);

    });
    this.auth.getAccessTokenSilently().pipe(take(1)).subscribe((c) => {
      console.log(c);
      localStorage.setItem('token', c);
    });
    /* this.auth.handleRedirectCallback().subscribe(c => {
      console.log(c);
    }); */
  }

}
