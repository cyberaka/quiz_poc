import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-quiz-settings',
  templateUrl: './quiz-settings.page.html',
  styleUrls: ['./quiz-settings.page.scss'],
})
export class QuizSettingsPage implements OnInit {
  quizType: string = '2';
  cntQAs: Number = 10;
  subTopic;
  constructor(
    private router: Router
  ) { 
    console.log(this.router.getCurrentNavigation()?.extras.state);
    let state  = this.router.getCurrentNavigation()?.extras.state;
    if(state && state['subTopic']) {
      this.subTopic = {...state['subTopic']};
    }
  }

  ngOnInit() {
  }

  startQuiz() {
    this.subTopic['quizType'] = this.quizType;
    this.subTopic['count'] = this.cntQAs;
    this.router.navigateByUrl('quiz', {
      replaceUrl : true,
      state :  this.subTopic
    });
  }
}
