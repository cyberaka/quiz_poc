import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { HttpService } from '../services/http.service';

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
    private http: HttpService
  ) { 

  }

  ngOnInit() {
    this.http.topicId = 0;
    this.getTopics();
  }
  getTopics() {
    this.http.getTopics().subscribe((topics: any) => {
      console.log(topics);
      this.topics = [...topics];
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
