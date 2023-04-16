# quiz_poc
Quiz POC


## Quiz POC -- UI

### Software Requirements
Install [Node](https://nodejs.org/en)

Install [Ionic](https://ionicframework.com/docs/cli)

`prerequisite`

Node version v18.16+


### Web

Using  [Node](https://nodejs.org/en) in your project directory run the following command:

```
npm install
```
Preview your app in Browser

```
ionic serve
```

Take a build 
```
 ionic build --prod
```

Now you are getting `www` folder, Use this folder and host.


### Android

```
npm install 
npm install @capacitor/android
npx cap add android
ionic build --prod && npx cap sync
npx cap open android
```

**Note:** Using below comments to add custom URL scheme into your `AndroidManifest.xml`

This article assumes you will be using Custom URL Schemes to handle the callback within your application. To do this, register your YOUR_PACKAGE_ID as a URL scheme for your chosen platform. To learn more, [Create Deep Links](https://developer.android.com/training/app-links/deep-linking) to App Content for Android.

```
          <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="@string/custom_url_scheme" />
          </intent-filter>
```

### iOS

```
npm install 
npm install @capacitor/ios
npx cap add ios
ionic build --prod && npx cap sync
npx cap open ios
```

**Note:** Using below comments to add custom URL scheme into your `Xcode project`

Back in Xcode, go to the **Info** tab of your app target settings. In the **URL Types** section, click the ＋ button to add a new entry. There, enter `auth0` into the Identifier field and `$(PRODUCT_BUNDLE_IDENTIFIER)` into the URL Schemes field.

**Ref. below image**


<img width="1327" alt="deep-linking-url-scheme" src="https://user-images.githubusercontent.com/30489397/232323001-b42cf5b6-14ca-4991-b6ff-4637a4588bc4.png">

