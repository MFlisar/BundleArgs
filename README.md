#BundleBuilder [![Release](https://jitpack.io/v/MFlisar/BundleArgs.svg)](https://jitpack.io/#MFlisar/BundleArgs)
Type safe bunlde/intent building for any class (activities, fragments, ...)

BundleBuilder is a type safe way of creating intents/bundles and populating them with extras. Intents/Bundles were created to be very dynamic but often times the dynamic nature of intents is not needed and just gets in the way of writing safe code.

 
### Gradle (via [JitPack.io](https://jitpack.io/))

1) add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2) add the compile statement to your module's `build.gradle` and apply the apt plugin:
```groovy
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile 'com.github.MFlisar:BundleBuilder:0.1'
    apt 'com.github.MFlisar:BundleBuilder:0.1'
}
```
### Credits

This project is based on https://github.com/emilsjolander/IntentBuilder
