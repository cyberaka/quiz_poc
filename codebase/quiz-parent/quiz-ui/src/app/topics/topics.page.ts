import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '@auth0/auth0-angular';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';

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
  constructor(
    private router: Router,
    private http: HttpService,
    private utils: UtilsService,
    private auth: AuthService,
  ) { 

    
  }

  ngOnInit() {
    this.http.topicId = 0;
    this.getTopics();
  }

  getTopics() {
    this.http.getTopics().subscribe((topics: any) => {
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

}
