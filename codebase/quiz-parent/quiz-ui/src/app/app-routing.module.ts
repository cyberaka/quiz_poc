import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then( m => m.LoginPageModule)
  },
  
  {
    path: 'topics',
    loadChildren: () => import('./topics/topics.module').then( m => m.TopicsPageModule)
  },
  {
    path: 'sub-topics',
    loadChildren: () => import('./sub-topics/sub-topics.module').then( m => m.SubTopicsPageModule)
  },
  {
    path: 'quiz',
    loadChildren: () => import('./quiz/quiz.module').then( m => m.QuizPageModule)
  },
  {
    path: 'quiz-settings',
    loadChildren: () => import('./quiz-settings/quiz-settings.module').then( m => m.QuizSettingsPageModule)
  },
  {
    path: 'score',
    loadChildren: () => import('./score/score.module').then( m => m.ScorePageModule)
  },
  {
    path: 'summary-quiz',
    loadChildren: () => import('./summary-quiz/summary-quiz.module').then( m => m.SummaryQuizPageModule)
  },
  
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
