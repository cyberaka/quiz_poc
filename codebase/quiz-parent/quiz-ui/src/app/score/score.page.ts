import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { DialogService } from '../services/dialog.service';
import { HttpService } from '../services/http.service';

@Component({
  selector: 'app-score',
  templateUrl: './score.page.html',
  styleUrls: ['./score.page.scss'],
})
export class ScorePage implements OnInit {
  isGuest: boolean;
  userAnswers: any = [];
  actualUserAnswers: any = [];
  freeTextAns: any = [];
  userScore:number = 0;
  constructor(
    private router: Router,
    private http: HttpService,
    private nav: NavController,
    private dialog: DialogService
  ) {
    this.isGuest = (localStorage.getItem('mode') === 'guest') ? true : false;
    let state = this.router.getCurrentNavigation()?.extras.state;
    if(state) {
      this.userAnswers = state['answers'];
      this.actualUserAnswers = state['answers'];
      this.freeTextAns = state['customAns'];
    }
  }

  ngOnInit() {
    this.checkAnswers();
  }

  checkAnswers() {
    this.userAnswers.forEach((question: any, idx: any) => {
      if (question.answers && question.answers.length > 1 && question.options && question.options.length > 0) {
        let ans: any = [];
        let userAnswers: any = [];
        question.answers = question.answers.map((c:any) => c.trim());
        for(var c of question.options) {
          if (c.checked) {
            ans.push(c.option);
          }
          if(c.checked && (question.answers.includes(c.option.toLowerCase()) || question.answers.includes(c.option.toUpperCase()))) {
            userAnswers.push(c.option);
          }
        }
        if (userAnswers.length == question.answers.length) {
          ++this.userScore;
          this.userAnswers[idx]['correct'] = true;
        }
        this.userAnswers[idx]['userAnswers'] = ans;
      }  else if (question.answers && question.answers.length == 1 && question.options && question.options.length > 0) {
        let selectedOptions = question.options.filter((c: any) => c.checked);
        if(selectedOptions.length && question.answers[0].toLowerCase() == selectedOptions[0].option.toLowerCase()) {
          ++this.userScore;
          this.userAnswers[idx]['correct'] = true;
        }
        if(selectedOptions.length) {
          this.userAnswers[idx]['userAnswers'] = [...selectedOptions[0].option];
        } else {
          this.userAnswers[idx]['userAnswers'] = [''];
        }

      } else {
        if(this.freeTextAns[idx].trim().toLowerCase() == question.answers[0].toLowerCase()) {
          ++this.userScore;
          this.userAnswers[idx]['correct'] = true;
        }
        this.userAnswers[idx]['userAnswers'] = [this.freeTextAns[idx]];
      }
    });
    this.scrollIntoScore();
  }

  scrollIntoScore() {
    let ele: any = document.getElementById('scoreDetail');
    if(ele) {
      setTimeout(() => {
        ele.scrollIntoView();
      }, 1000);
    }
  }

  publish() {
    this.http.publishAnswers(this.userAnswers).subscribe(c => {
      this.showAlert('Published Successfully!', 'topics');
    }, () => {
      this.showAlert('Error');
    });
  }

  summay() {
    this.router.navigateByUrl('summary-quiz', {
      replaceUrl: true,
      state : {
        allDet : this.userAnswers,
        answers: this.actualUserAnswers,
        customAns : this.freeTextAns
      }
    })
  }

  showAlert(message: string, type: string = '') {
    this.dialog.showAlert(
      message,
      [
        {
          text: 'Ok',
          handlerReturn: 'ok'
        }
      ]
    ).then((c: any) => {
      if (type == 'topics') {
        this.nav.setDirection('forward');
        this.http.topicId = this.userAnswers[0].topicId;

        this.router.navigateByUrl('topics', {
          replaceUrl: true,
        });
      }
    });
  }

}
