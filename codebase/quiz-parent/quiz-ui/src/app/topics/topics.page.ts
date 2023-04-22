import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';
import { environment } from '../../environments/environment';
import { Browser } from '@capacitor/browser';
import { Capacitor } from '@capacitor/core';
import { App } from '@capacitor/app';

export interface topic {
  topicId: Number,
  title: string
}

@Component({
  selector: 'app-topics',
  templateUrl: './topics.page.html',
  styleUrls: ['./topics.page.scss'],
})
export class TopicsPage implements OnInit {
  pageTitle: string = "Topics"
  topics: topic[] =  []
  loader: boolean = true
  constructor(
    private router: Router,
    private http: HttpService,
    private utils: UtilsService,
    private auth: AuthService,
  ) { 
    App.addListener('appUrlOpen', ({ url }) => {
      console.log('appUrlOpen');
      console.log('url');
      console.log(url);
    });
    
  }

  ngOnInit() {
    this.http.topicId = 0;
    this.getTopics();
  }

  getTopics() {
    this.http.getTopics().subscribe((topics: any) => {
      this.loader = false;
      this.topics = [...topics];
      this.utils.stopLoader();
    });
  }

  topicClick(getTopic: topic) {
    // this.utils.showLoader();
    this.router.navigateByUrl('sub-topics', {
      replaceUrl : true,
      state : {
        topicId : getTopic.topicId
      }
    });
  }

  logout() {
    let returnTo = environment.auth.authorizationParams.redirect_uri;
    if (Capacitor.isNativePlatform()) { 
      this.auth
      .logout({ 
        logoutParams: {
          returnTo
        },
        async openUrl(url: string) {
         await Browser.open({ url, windowName: '_self' })} 
      })
      .subscribe();
    } else {
      this.auth
      .logout({ 
        logoutParams: {
          returnTo
        },
      }).subscribe((c) => {
        this.router
        .navigateByUrl('login', { replaceUrl: true });
      });
    }
     
  }

}
