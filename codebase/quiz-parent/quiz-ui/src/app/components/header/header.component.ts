import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {  NavController } from '@ionic/angular';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent  implements OnInit {
  @Input() title = '';
  constructor(
    private router : Router,
    private nav: NavController
  ) { }

  ngOnInit() {
  }

  backBtnClick() {
    this.nav.setDirection('back');
    if(this.title == 'Topics') {
      this.router.navigateByUrl('login', {replaceUrl: true});
    } else if(this.title == 'Sub Topics') {
      this.router.navigateByUrl('topics', {replaceUrl: true});
    } else if(this.title == 'Quiz Settings') {
      this.router.navigateByUrl('sub-topics', {replaceUrl: true});
    }
  }
}
