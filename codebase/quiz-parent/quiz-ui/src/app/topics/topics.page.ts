import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';
import { AuthService as localAuth } from '../services/Auth/auth.service';
import { take } from 'rxjs';

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
    private authO: AuthService,
    private auth: localAuth
  ) {
  }

  ngOnInit() {
    this.http.topicId = 0;
    if(this.auth.isGuestMode()) {
      this.getTopics();
    } else {
      this.authO.getAccessTokenSilently().subscribe(token => {
        if(token) {
          this.http.oauthToken = token;
          this.getTopics();
        }
      })
    }
  }

  getTopics() {
    this.http.getTopics().subscribe((topics: any) => {
      this.loader = false;
      this.topics = [...topics];
      this.utils.stopLoader();
    }, err => {
      console.log(err);
      this.loader = false;
    });
  }

  topicClick(getTopic: topic) {
    this.router.navigateByUrl('sub-topics', {
      replaceUrl : true,
      state : {
        topicId : getTopic.topicId
      }
    });
  }

}
