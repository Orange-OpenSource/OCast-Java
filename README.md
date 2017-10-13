# OCast-Java
The Orange OCast SDK provides all required API to implement cast applications for the Orange stick.

The Example project aims at demonstrating the basic instruction set of the Orange OCast SDK to help you get started.

Here are the main features of the Sample project:


```
• Wifi connection to the receiver

• Application stop and restart

• Video cast Play/Pause/Stop

• Image cast

• Time seek management

• Media tracks management

• Custom messages handling
```

## Example

To run the example mobile project, clone the repo, and run :
```
./gradlew mobile:installDebug
adb shell am start -n org.ocast.sample.mobile/org.ocast.sample.mobile.MainActivity
```

## Author

Orange Labs