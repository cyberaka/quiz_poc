import { Injectable } from '@angular/core';
import { LoadingController } from '@ionic/angular';
import { AlertController } from '@ionic/angular';


@Injectable({
  providedIn: 'root'
})
export class DialogService {
  loading: any;
  constructor(
    private loader: LoadingController,
    private alertCtrl: AlertController
  ) { }

  async showLoading() {
    this.loading = await this.loader.create({
      message: 'Loading...'
    });
    this.loading.present();
  }

  dismissLoading() {
    console.log('');
    this.loading?.dismiss();
  }

  async showAlertWithButtons(message: string, buttonName: any, callback : Function) {
    let alertControl = await this.alertCtrl.create({
      message: message,
      buttons: [
        {
          text: buttonName,
          role: 'OK',
          cssClass: 'cancelcss',
          handler: () => {
            callback(true);
          }

        }]
    });
    alertControl.present();
  }





}
