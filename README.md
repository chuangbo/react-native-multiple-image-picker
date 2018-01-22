> @chuangbo
> 
> *[liaoyuan-io/react-native-multiple-image-picker](https://github.com/liaoyuan-io/react-native-multiple-image-picker) 的版本在我这里出现很多编译错误。虽然完全不懂 Android 开发，但是在一番尝试下，这个版本是目前可以编译并且正常使用的。在我的 pull requests 都被合并以后，这个 repo 就没有存在的必要了。*

## react-native-multiple-image-picker

React Native Multiple Image Picker is a React Native native module wrapping [TZImagePickerController](https://github.com/banchichen/TZImagePickerController) for iOS (iOS 8+ for using PhotoKit) and [RxGalleryFinal](https://github.com/FinalTeam/RxGalleryFinal) for Android (Android 4.1+). This module allows you to pick multiple images for further processing.

React Native Multiple Image Picker 多图片选择器 是一个 React Native 原生模块，封装了 [TZImagePickerController](https://github.com/banchichen/TZImagePickerController)（用于 iOS 8+，因为使用了 PhotoKit）和 [RxGalleryFinal](https://github.com/FinalTeam/RxGalleryFinal)（用于 Android 4.1+）。使用这个模块你可以一次选择多张图片，以供进一步处理。


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

    不知道原因，但是解决方法是修改 app/build.gradle，使用 24 版本的 sdk

    ```
    compileSdkVersion 24
    buildToolsVersion '24.0.1'
    compile 'com.android.support:appcompat-v7:24.0.1'
    ```

### Install

#### iOS

1. Run `npm install --save github:chuangbo/react-native-multiple-image-picker` .
2. Add `RCTMultipleImagePicker` to your iOS project.
3. Add `libRCTMultipleImagePicker.a` to your `Link Binary with Libraries` section in `Build Phases` .
4. Copy `TZImagePickerController.framework` to your `Framework` folder.
5. Add `TZImagePickerController.framework` to your `Framework` group and `Embedded Binaries` section in `Target->General` .

如果有使用 CocoaPods 的话，TZImagePickerController 可以自动安装，添加下面这行到 Podfile 里

```
pod 'TZImagePickerController', :path => '../node_modules/react-native-multiple-image-picker/ios/TZImagePickerController'
```

#### Android

1. Run `npm install --save react-native-multiple-image-picker` .
2. Add `new MultipleImagePickerPackage()` to your `getPackages` return in `android/app/src/main/java/com/your/path/MainApplication.java`.
3. Add following to your `android/app/src/main/AndroidManifest.xml`:
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
4. Add `compile project(':react-native-multiple-image-picker')` to `dependencies` section in `android/app/build.gradle` .
5. Add following to your `android/settings.gradle`:

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
| camera_permission_not_granted | iOS              | User has not granted CAMERA permission to your app. Should guide user to Settings > Privacy > Camera .                 |
| create_directory_failed       | iOS              | The app has failed to create the temp folder for photo processing due to insufficient storage or other system errors.  |
| user_cancelled                | iOS              | User has cancelled the image picker.                                                                                   |
