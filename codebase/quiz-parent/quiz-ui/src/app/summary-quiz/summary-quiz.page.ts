import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { DialogService } from '../services/dialog.service';
import { HttpService } from '../services/http.service';
import { UtilsService } from '../services/utils.service';


@Component({
  selector: 'app-summary-quiz',
  templateUrl: './summary-quiz.page.html',
  styleUrls: ['./summary-quiz.page.scss'],
})
export class SummaryQuizPage implements OnInit {
  isGuest: boolean;
  userAnswers: any = [];
  actualUserAnswers: any = [];
  freeTextAns: any = [];
  constructor(
    private router: Router,
    private http: HttpService,
    private nav: NavController,
    private dialog: DialogService,
    public utils: UtilsService
  ) {
    this.isGuest = (localStorage.getItem('mode') === 'guest') ? true : false;
    let state = this.router.getCurrentNavigation()?.extras.state;
    if(state && state['allDet']) {
      this.userAnswers = state['allDet'];
      this.actualUserAnswers = state['answers'];
      this.freeTextAns = state['customAns'];
    }
  }

  ngOnInit() {
  }

  goBack() {
    this.nav.setDirection('back');
    this.router.navigateByUrl('score', {
      state: {
        answers: this.actualUserAnswers,
        customAns : this.freeTextAns
      }
    });
  }

  quitQuiz() {
    this.nav.setDirection('forward');
    this.http.topicId = this.userAnswers[0].topicId;
    this.router.navigateByUrl('topics', {
      replaceUrl: true,
    }).then(c => {
      console.log(this.router)
    });
  }

  publishQuiz() {
    this.http.publishAnswers(this.userAnswers).subscribe(c => {
      this.showAlert('Published Successfully!', 'topics');
    }, () => {
      this.showAlert('Error');
    });
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
