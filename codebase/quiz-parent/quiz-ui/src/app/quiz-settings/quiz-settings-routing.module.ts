import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { QuizSettingsPage } from './quiz-settings.page';

const routes: Routes = [
  {
    path: '',
    component: QuizSettingsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class QuizSettingsPageRoutingModule {}
