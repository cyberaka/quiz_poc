import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoadingController, NavController } from '@ionic/angular';
import { DialogService } from '../services/dialog.service';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.page.html',
  styleUrls: ['./quiz.page.scss'],
})
export class QuizPage implements OnInit {
  QAs: any = [];
  questionNoShow: boolean;
  currentQusIndex: number = 0;
  score = 0;
  topics: any;
  customAns: any[] = []; //user enter the value
  singleAns: any; //user single selection
  reachEnd: boolean;
  constructor(
    private router: Router,
    private http: HttpService,
    private loader: LoadingController,
    private nav: NavController,
    private utils : UtilsService,
    private dialog: DialogService,
  ) {
    let state = this.router.getCurrentNavigation()?.extras.state;
    console.log(state);
    if (state) {
      this.topics = state;
    }
    setTimeout(() => {
      this.questionNoShow = true;
    }, 500);
  }

  async ngOnInit() {
    if (this.topics) {
      const loading = await this.loader.create({
        message: 'Loading...'
      });
      this.http.getQAs(this.topics).subscribe((qas: any) => {
        if (qas.length == 0) {
          this.showAlert('No Questions are there, Please select different level', 'settingsPage');
        } else {
          this.customAns = [];
          this.QAs = qas.map((question: any) => {
            this.customAns.push('');
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
          console.log(this.QAs);
        }
      });
    }

  }

  showAlert(message: string, type: string) {
    this.dialog.showAlert(
      message,
      [
        {
          text: 'Ok',
          handlerReturn: 'ok'
        }
      ]
    ).then((c: any) => {
      if (type == 'settingsPage') {
        this.redirectTopicScreen();
      } else if (type == 'topics') {
        this.nav.setDirection('forward');
        this.http.topicId = this.topics.topicId;
        this.router.navigateByUrl('topics', {
          replaceUrl: true,
        });
      }
    });
  }

  redirectTopicScreen() {
    this.nav.setDirection('back');
    this.http.topicId = this.topics.topicId;
    this.router.navigateByUrl('quiz-settings', {
      replaceUrl: true,
      state: {
        subTopic: {
          subTopicId: this.topics.subTopicId,
          topicId: this.topics.topicId,
          title: this.topics.title
        }
      }
    });
  }

  singleAnswerSelection() {
    let updatedOptions = [...this.QAs[this.currentQusIndex]['customOptions'].map((option : any) => {
      if(option.value == this.singleAns) {
        option.checked= true;
      } else {
        option.checked = false;
      }
      return option;
    })];
    this.QAs[this.currentQusIndex]['customOptions'] = [...updatedOptions];
    this.QAs[this.currentQusIndex]['userAnswers'] = [...this.singleAns];
  }

  NextClick() {
    this.resetModal();
    if(this.currentQusIndex >= 0)
      this.currentQusIndex += 1;  
    if (this.currentQusIndex == this.QAs.length - 1) {
      this.reachEnd = true;
    }
  }
  
  previousClick() {
    this.resetModal();
    if(this.currentQusIndex > 0) {
      this.currentQusIndex -= 1;
    }
  }

  resetModal() {
    this.singleAns = '';
  }

  submitAns() {
    this.utils.showLoader();
    let missedAns: any = [];
    this.QAs.forEach((qa: any, idx: any) => {
      if(qa.options.length > 0) {
        let len = qa.customOptions.filter((ele: any) => ele.checked);
        if(len == 0) {
          missedAns.push(idx);
        }
      } else {
        if(this.customAns[idx].trim() == '') {
          missedAns.push(idx);
        }
      }
    });
    if(!missedAns.length) {
      setTimeout(() => {
        this.utils.stopLoader();
      }, 100);
      this.router.navigateByUrl('score', {
         replaceUrl: true,
         state : {
           answers: this.QAs,
           customAns : this.customAns
         }
       });
    } else {
      setTimeout(() => {
        this.utils.stopLoader();
      }, 100);
       this.dialog.showAlert(
        `You have not attempted ${missedAns.length} question(s)`,
      [
        {
          text: 'Show Question',
          handlerReturn: 'ok'
        }
      ]
    ).then((c: any) => {
      this.currentQusIndex = missedAns[0];
    });
    }
  }

  publishAnswer() {
    this.http.publishAnswers(this.QAs).subscribe(c => {
      this.showAlert('Published Successfully!', 'topics');
    });
  }
}
