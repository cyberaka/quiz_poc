import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SubTopicsPageRoutingModule } from './sub-topics-routing.module';

import { SubTopicsPage } from './sub-topics.page';
import { SharedModule } from '../components/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SharedModule,
    SubTopicsPageRoutingModule
  ],
  declarations: [SubTopicsPage]
})
export class SubTopicsPageModule {}
