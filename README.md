# OCast-Java
[![Build Status](https://travis-ci.org/Orange-OpenSource/OCast-Java.png)](https://travis-ci.org/Orange-OpenSource/OCast-Java)

The Orange OCast SDK provides all required API to implement cast enabled applications and devices.

The Example project aims at demonstrating the basic instruction set of the Orange OCast SDK to help you get started.

Here are the main features of the Sample project:


```
• Wifi connection to the receiver

• Application stop and restart

• Video cast Play/Pause/Stop

• Image cast

• Volume management

• Time seek management

• Media tracks management

• Custom messages handling
```

## Sample

To run the sample projects, you will need the [Device Simulator](https://github.com/Orange-OpenSource/OCast-DongleTV-JS) :
```
$git clone https://github.com/Orange-OpenSource/OCast-DongleTV-JS.git
$cd OCast-DongleTV-JS
$npm install
$npm start
```
Once the device simulator is running, you can run the sample mobile application:
```
./gradlew mobile:installDebug
adb shell am start -n org.ocast.sample.mobile/org.ocast.sample.mobile.MainActivity
```
or the sample plain Java application:
```
./gradlew desktop:run
```

## Author

Orange Labs
