import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';

export interface subTopic {
  subTopicId: Number,
  topicId: Number,
  title: string
}

@Component({
  selector: 'app-sub-topics',
  templateUrl: './sub-topics.page.html',
  styleUrls: ['./sub-topics.page.scss'],
})
export class SubTopicsPage implements OnInit {
  pageTitle: string = "Sub Topics"
  subTopics: subTopic[] =  [];
  topicId: Number;
  constructor(
    private router: Router,
    private http: HttpService,
    private utils: UtilsService
  ) { 
    let state = this.router.getCurrentNavigation()?.extras.state;
    if(state && state['topicId']) {
      this.topicId = state['topicId'] as Number;
      this.http.topicId = this.topicId;
    } else {
      this.topicId = this.http.topicId || 0;
    }
    if(!this.http.userDet) {
      this.router.navigateByUrl('login', {
        replaceUrl: true
      });
    }
  }

  ngOnInit() {
    this.getSubTopics();
  }
  getSubTopics() {
    this.http.getSubTopics(this.topicId).subscribe((list: any) => {
      console.log(list);
      this.subTopics = [...list];
      this.utils.stopLoader();
    });
  }

  subTopicClick(subTopic: any) {
    subTopic['quizType'] = 2;
    subTopic['count'] = 10;
    this.router.navigateByUrl('quiz', {
      replaceUrl: true,
      state : {...subTopic}
    });
  }

}
