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
  ) {
   }

  ngOnInit() {
    if(localStorage.getItem('token')) {
      this.router.navigateByUrl('topics', {replaceUrl : true});
    } else {
      this.router.navigateByUrl('login', {replaceUrl : true});
    }
   /*  this.auth.isLoading$.subscribe(c => {
      if(!c) {
        if(localStorage.getItem('token')) {
          this.router.navigateByUrl('topics', {replaceUrl : true}).then(c => {
            // this.getDetails();
          });
        } else {
          this.auth.getAccessTokenSilently().subscribe((c) => {
            localStorage.setItem('token', c);
            this.router.navigateByUrl('topics', {replaceUrl : true}).then(c => {
              // this.getDetails();
            });
          }, (err) => {
            this.router.navigateByUrl('login', {replaceUrl : true})
           
          });
        }
      }
    }, (err)=> {
      this.router.navigateByUrl('login', {replaceUrl : true});
    }) */
    /* let authenticated: boolean = false;
    var _this = this;
    if(localStorage.getItem('token')) {
      this.router.navigateByUrl('topics', {replaceUrl : true}).then(c => {
        this.getDetails();
      });
    } else {
      this.auth.error$.subscribe((c) => {
        alert('error');
        _this.router.navigateByUrl('login', {replaceUrl : true})
      });
      this.auth.getAccessTokenSilently().subscribe((c) => {
        console.log(c);
        localStorage.setItem('token', c);
      }, (err) => {
        _this.router.navigateByUrl('login', {replaceUrl : true})
       
      });
      this.auth.isAuthenticated$.subscribe({
        next: (v) =>{
          authenticated = v;
        },
        error: (e) =>  {
          alert(e);
          _this.router.navigateByUrl('login', {replaceUrl : true})
        },
        complete: () => {
          console.log('authenticated ', authenticated);
          if(authenticated) {
            _this.router.navigateByUrl('topics', {replaceUrl : true}).then(c => {
              _this.getDetails();
            });
          } else {
            _this.router.navigateByUrl('login', {replaceUrl : true});
          }
        } 
      });
    } */
   
  }

  getDetails() {

    this.auth.user$.subscribe((c) => {
      console.log(c);
      
    });
    this.auth.getAccessTokenSilently().subscribe((c) => {
      console.log(c);
      localStorage.setItem('token', c);
    });
    /* this.auth.handleRedirectCallback().subscribe(c => {
      console.log(c);
    }); */
  }

}
