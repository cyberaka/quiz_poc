import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';
import { NavController } from '@ionic/angular';

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
  loader: boolean = true
  constructor(
    private router: Router,
    private http: HttpService,
    private utils: UtilsService,
    private nav: NavController,
  ) {
    let state = this.router.getCurrentNavigation()?.extras.state;
    if(state && state['topicId']) {
      this.topicId = state['topicId'] as Number;
      this.http.topicId = this.topicId;
    } else {
      this.topicId = this.http.topicId || 0;
    }
  }

  ngOnInit() {
    this.getSubTopics();
  }

  goBack() {
    this.nav.setDirection('back');
    this.router.navigateByUrl('topics', {
      replaceUrl: true
    });
  }

  getSubTopics() {
    this.http.getSubTopics(this.topicId).subscribe((list: any) => {
      this.loader = false;
      this.subTopics = [...list];
    }, err => {
      this.loader = false;
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
