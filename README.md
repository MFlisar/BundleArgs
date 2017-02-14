#BundleBuilder
Type safe bunlde/intent building for any class (activities, fragments, ...)

BundleBuilder is a type safe way of creating intents/bundles and populating them with extras. Intents/Bundles were created to be very dynamic but often times the dynamic nature of intents is not needed and just gets in the way of writing safe code.

 
### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```groovy
dependencies {
    compile 'com.github.MFlisar:RXBus:1.0'
}
```
3. apply the apt plugin in your project and add the dependencies
```groovy
apply plugin: 'com.neenbedankt.android-apt'

dependencies {
    compile 'com.github.MFlisar:BundleBuilder:1.0'
    apt 'com.github.MFlisar:BundleBuilder:1.0'
}
```