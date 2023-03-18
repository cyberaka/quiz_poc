import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SubTopicsPage } from './sub-topics.page';

const routes: Routes = [
  {
    path: '',
    component: SubTopicsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SubTopicsPageRoutingModule {}
