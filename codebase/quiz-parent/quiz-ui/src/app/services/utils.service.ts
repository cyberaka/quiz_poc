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
      if(load) {
        loader.dismiss();
      }
      this.loader?.dismiss()
      this.loader = null
      this.isShowingLoader = false
    }
  }
}
