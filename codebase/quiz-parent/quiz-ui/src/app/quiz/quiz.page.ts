import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoadingController, NavController } from '@ionic/angular';
import { DialogService } from '../services/dialog.service';
import { HttpService } from '../services/http.service';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.page.html',
  styleUrls: ['./quiz.page.scss'],
})
export class QuizPage implements OnInit {
  QAs: any = [];
  currentQusIndex: number = 0;
  score = 0;
  topics : any;
  customAns : string; //user enter the value
  singleAns: any; //user single selection
  reachEnd: boolean;
  constructor(
    private router: Router,
    private http: HttpService,
    private loader: LoadingController,
    private nav: NavController,
    private dialog :DialogService,
  ) {
    let state = this.router.getCurrentNavigation()?.extras.state;
    if(state) {
      this.topics = state;
    } 
    if(!this.http.userDet) {
      this.nav.setDirection('back');
      this.router.navigateByUrl('login', {
        replaceUrl: true
      });
    }
   }

  async ngOnInit() {
    if(this.topics) {
      const loading = await this.loader.create({
        message: 'Loading...'
      });
     this.http.getQAs(this.topics).subscribe((qas: any) => {
      if(qas.length == 0) {
        this.dialog.showAlertWithButtons('No Questions are there, Please select different level', 'Ok', (c: any) => {
          this.nav.setDirection('back');
          this.http.topicId = this.topics.topicId;
          this.router.navigateByUrl('quiz-settings', {
            replaceUrl: true,
            state : {
              subTopic: {
                subTopicId: this.topics.subTopicId,
                topicId: this.topics.topicId,
                title: this.topics.title
              }
            }
          });
        });
      } else {
        this.QAs = qas.map((question: any) => {
          if (question != null && question.options != null && question.options.length > 0) {
            question['customOptions'] = [];
            for (var i = 0; i < question.options.length; i++) {
              question['customOptions'][i] = {
                text: question.options[i].substring(3),
                value: question.options[i].charAt(0),
                checked: false
              }
            }
          } 
          return question;
        });
        loading.dismiss();
      
      
      }
     });
    }
    
  }

  NextClick() {
    this.checkAnswer(this.currentQusIndex);
    if(this.currentQusIndex == this.QAs.length - 1) {
      this.reachEnd = true;
    } else {
      this.currentQusIndex += 1;
    }
  }

  checkAnswer(idx: any) {
    console.log(this.QAs);
    let question = {...this.QAs[idx]};
    //Mutiple Options
    if (question.answers && question.answers.length > 1 && question.options && question.options.length > 0) { 
      let ans:any = [];
      let userAnswers = question.customOptions.filter((c: any) => {
        if(c.checked) {
          ans.push(c.value);
        }
        return c.checked && question.answers.includes(c.value);
      });
      if(userAnswers.length == question.answers.length) {
        this.score += 1;
      }
      this.QAs[idx]['userAnswers'] = ans; 
    }
    //Single Options 
    else if(question.answers && question.answers.length == 1 && question.options  && question.options.length > 0) {
      if(question.answers[0] == this.singleAns) {
        this.score += 1;
      }
      this.QAs[idx]['userAnswers'] = [...this.singleAns]; 
    }
    //Manual Entry 
    else {
      if (this.customAns == question.answers[0]) {
        this.score += 1;
      }
      this.QAs[idx]['userAnswers'] = [...this.customAns]; 
    }
    delete this.QAs[idx]['customOptions'];
    this.singleAns = undefined;
    this.customAns = '';
  }

  publishAnswer() {
    this.http.publishAnswers(this.QAs).subscribe(c => {
      console.log(c);
      this.dialog.showAlertWithButtons('Published Successfully!', 'Ok', (c: any) => {
        this.nav.setDirection('forward');
        this.http.topicId = this.topics.topicId;
        this.router.navigateByUrl('login', {
          replaceUrl: true,
          });
        });
    });
  }
}
