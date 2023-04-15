import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@auth0/auth0-angular';
import { ErrorComponent } from './components/error/error.component';
import { LoadingComponent } from './components/loading/loading.component';

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
    loadChildren: () => import('./topics/topics.module').then( m => m.TopicsPageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'sub-topics',
    loadChildren: () => import('./sub-topics/sub-topics.module').then( m => m.SubTopicsPageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'quiz',
    loadChildren: () => import('./quiz/quiz.module').then( m => m.QuizPageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'quiz-settings',
    loadChildren: () => import('./quiz-settings/quiz-settings.module').then( m => m.QuizSettingsPageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'score',
    loadChildren: () => import('./score/score.module').then( m => m.ScorePageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'summary-quiz',
    loadChildren: () => import('./summary-quiz/summary-quiz.module').then( m => m.SummaryQuizPageModule),
    canActivate: [AuthGuard],
  },
  {
    path: 'error',
    component: ErrorComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
