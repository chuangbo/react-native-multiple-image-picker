> @chuangbo
> 
> *The original repo [liaoyuan-io/react-native-multiple-image-picker](https://github.com/liaoyuan-io/react-native-multiple-image-picker) as some compile issues on Android to me. This fork fixed the issues. Also, it provides the simpler way to install on iOS (CocoaPods)*

## react-native-multiple-image-picker

React Native Multiple Image Picker is a React Native native module wrapping [TZImagePickerController](https://github.com/banchichen/TZImagePickerController) for iOS (iOS 8+ for using PhotoKit) and [RxGalleryFinal](https://github.com/FinalTeam/RxGalleryFinal) for Android (Android 4.1+). This module allows you to pick multiple images for further processing.


### Known Issues

- Error:The number of method references in a .dex file cannot exceed 64K.

    Add this to dependencies

    ```
    compile 'com.android.support:multidex:1.0.1'
    ```

    Then add `multiDexEnabled true`

    ```
    android {
        defaultConfig {
            // add this to defaultConfig
            multiDexEnabled true
        }
    }
    ```

- values-v24.xml No resource found that matches the given name 'android:TextAppearance.Material.Widget.Button.Borderless.Colored'

    Unknown reason. Here is my workaround: use sdk version 24 in `app/build.gradle`

    ```
    compileSdkVersion 24
    buildToolsVersion '24.0.1'
    compile 'com.android.support:appcompat-v7:24.0.1'
    ```

### Install

```
npm install --save chuangbo/react-native-multiple-image-picker
# or
yarn add chuangbo/react-native-multiple-image-picker
```

#### iOS

Only Support install using CocoaPods

```
pod 'react-native-multiple-image-picker', :path => '../node_modules/react-native-multiple-image-picker'
```

React needs to put in your CocoaPods as well

```
# Remove React from Library, and

pod 'React',
  :path => "../node_modules/react-native"
pod 'yoga',
  :path => "../node_modules/react-native/ReactCommon/yoga"
pod 'DoubleConversion',
  :podspec => "../node_modules/react-native/third-party-podspecs/DoubleConversion.podspec",
  :inhibit_warnings => true
pod 'Folly',
  :podspec => "../node_modules/react-native/third-party-podspecs/Folly.podspec",
  :inhibit_warnings => true
pod 'GLog',
  :podspec => "../node_modules/react-native/third-party-podspecs/GLog.podspec",
  :inhibit_warnings => true

```

#### Android

```
react-native link react-native-multiple-image-picker
```

##### Manually

1. Add `new MultipleImagePickerPackage()` to your `getPackages` return in `android/app/src/main/java/com/your/path/MainApplication.java`.
2. Add following to your `android/app/src/main/AndroidManifest.xml`:
    ```
    // permission declaration
    <uses-feature android:name="android.hardware.camera" android:required="true"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    
    // in application
        <activity
                android:name="cn.finalteam.rxgalleryfinal.ui.activity.MediaActivity"
                android:exported="true"
                android:screenOrientation="portrait"/>
    ```
3. Add `compile project(':react-native-multiple-image-picker')` to `dependencies` section in `android/app/build.gradle` .
4. Add following to your `android/settings.gradle`:

    ```
    include ':react-native-multiple-image-picker'
    project(':react-native-multiple-image-picker').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-multiple-image-picker/android')
    ```

### Usage

```javascript
import MultipleImagePicker from 'react-native-multiple-image-picker';

const options = {
    maxImagesCount: 9,      // Max number of images user can select; if maxImagesCount == 1, Single mode (i.e. Tap to Select & Finish) will be activated.
    selectedPaths: [
        '/Users/tshen/Library/Developer/CoreSimulator/Devices/8C416B45-F555-4A63-A1B0-09E61109F0A0/data/Containers/Data/Application/A1790255-CDE8-486C-A6BA-1693BA2AA87B/Documents/BB6ADD56-09E7-402C-BF0E-AD79400D3889-7539-000007B93A6B5733/0.jpg',
        '/Users/tshen/Library/Developer/CoreSimulator/Devices/8C416B45-F555-4A63-A1B0-09E61109F0A0/data/Containers/Data/Application/A1790255-CDE8-486C-A6BA-1693BA2AA87B/Documents/BB6ADD56-09E7-402C-BF0E-AD79400D3889-7539-000007B93A6B5733/1.jpg',
        '/Users/tshen/Library/Developer/CoreSimulator/Devices/8C416B45-F555-4A63-A1B0-09E61109F0A0/data/Containers/Data/Application/A1790255-CDE8-486C-A6BA-1693BA2AA87B/Documents/BB6ADD56-09E7-402C-BF0E-AD79400D3889-7539-000007B93A6B5733/2.jpg',
        '/Users/tshen/Library/Developer/CoreSimulator/Devices/8C416B45-F555-4A63-A1B0-09E61109F0A0/data/Containers/Data/Application/A1790255-CDE8-486C-A6BA-1693BA2AA87B/Documents/BB6ADD56-09E7-402C-BF0E-AD79400D3889-7539-000007B93A6B5733/3.jpg'
    ]                       // Currently selected paths, must be from result of previous calls. Empty array allowed.
};
MultipleImagePicker.launchImageGallery(options).then((newSelectedPaths) => {
    // newSelectedPaths will be an Array of String, like [ '/path/1', '/path/2' ], and may be used for `selectedPaths` on the next invocation
});
```

### Error Codes

| Code                          | Platform         | Description                                                                                                            |
| ----------------------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `camera_permission_not_granted` | iOS              | User has not granted CAMERA permission to your app. Should guide user to Settings > Privacy > Camera .                 |
| `create_directory_failed`       | iOS              | The app has failed to create the temp folder for photo processing due to insufficient storage or other system errors.  |
| `user_cancelled`                | iOS              | User has cancelled the image picker.                                                                                   |
