import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SummaryQuizPageRoutingModule } from './summary-quiz-routing.module';

import { SummaryQuizPage } from './summary-quiz.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SummaryQuizPageRoutingModule
  ],
  declarations: [SummaryQuizPage]
})
export class SummaryQuizPageModule {}
