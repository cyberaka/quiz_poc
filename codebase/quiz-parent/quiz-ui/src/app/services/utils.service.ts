import { Injectable } from '@angular/core';
import { LoadingController } from '@ionic/angular';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {
  isShowingLoader: boolean = false;
  loader: any = null;
  constructor(
    private loadingController: LoadingController
  ) { }


  async showLoader() {
    if (!this.isShowingLoader) {
      this.isShowingLoader = true
      this.loader = await this.loadingController.create({
        message: 'Please wait...'
      });
      await this.loader.present();
    }
  }
  async stopLoader() {
    if (this.isShowingLoader) {
      const loader = this.loadingController;
      const load = await loader.getTop();
      if (load) {
        loader.dismiss();
      }
      this.loader?.dismiss()
      this.loader = null
      this.isShowingLoader = false
    }
  }

  linkify(text: string) {
    let urlRegex = /(((https?:\/\/)|(www\.))[^\s]+)/g;
    let mailRegex = /\S+@\S+\.[a-zA-Z]{2,}/g;
    let phoneRegex = /([+][9][1]|[9][1]|[0]){0,1}([0-9]{10})/g;
    if (!text) {
      return '';
    }
    return text.replace(urlRegex, function (url: string, b: any, c: any) {
      let url2 = (c == 'www.') ? 'http://' + url : url;
      // console.log('url', text.match(/\bhttps?::\/\/\S+/gi));
      // console.log('url2', url2);
      return '<div><a href="' + url2 + '" target="_blank" class="linkify">' + url + '</a></div>';
    });
     /*  .replace(phoneRegex, (phone: string) => `<a href='tel:${phone}' target='_system'>${phone}</a>`)
      .replace(mailRegex, (mail: string) => `<a href='mailto:${mail}' target='_system'>${mail}</a>`) */;
  }

  urlLikefy(text: string) {
    let urlRegex = /(((http(s)?:\/\/.)|(www\.))[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*))/g;
    return text.replace(urlRegex, function (url, b, c) {
      let url2 = (c == 'www.') ? 'http://' + url : url;
      return '<a href="' + url2 + '" target="_blank">' + url + '</a>';
    })
  }
}
