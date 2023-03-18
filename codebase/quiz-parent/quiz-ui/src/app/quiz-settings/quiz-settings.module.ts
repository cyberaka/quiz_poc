import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { QuizSettingsPageRoutingModule } from './quiz-settings-routing.module';

import { QuizSettingsPage } from './quiz-settings.page';
import { SharedModule } from '../components/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SharedModule,
    QuizSettingsPageRoutingModule
  ],
  declarations: [QuizSettingsPage]
})
export class QuizSettingsPageModule {}
