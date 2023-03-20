import { Injectable } from '@angular/core';
import { AlertController } from '@ionic/angular';


@Injectable({
  providedIn: 'root'
})
export class DialogService {
  loading: any;
  constructor(
    private alertCtrl: AlertController
  ) { }

  /**
   * 
   * @param message > User Read this message
   * @param buttons > Takes an array of buttons show action
   *                Ex: [
   *                   * {
   *                   *   text : 'OK/Cancel',
   *                   *   handlerReturn: 'ok/cancel' //It will return ok or cancel  
   *                   * }         
   *                    ] 
   * @returns 
   */
  showAlert(message: string, buttonArray: any) {
    return new Promise(async (resolve, reject) => {
      if (!message || !buttonArray.length) {
        reject('Unmatched Params');
      }
      let alertControl = await this.alertCtrl.create({
        message: message,
        buttons: [
          ...buttonArray.map((button: any) => {
            return {
              text: button?.text || 'Title',
              handler: () => {
                resolve(button?.handlerReturn || 'ok');
              }
            }
          })
        ]
      });
      alertControl.present();
    });
  }
}
