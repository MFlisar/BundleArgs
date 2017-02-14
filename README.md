#BundleArgs [![Release](https://jitpack.io/v/MFlisar/BundleArgs.svg)](https://jitpack.io/#MFlisar/BundleArgs)
Type safe bundle/intent building for any class (activities, fragments, ...) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BundleArgs-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5289)

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
    compile 'com.github.MFlisar:BundleBuilder:0.3'
    apt 'com.github.MFlisar:BundleBuilder:0.3'
}
```

### Usage

Here's a simple example that demonstrates the use in ANY class that needs a `Bundle` argument:

```groovy
@BundleBuilder
public class Test
{
    @Arg
    Long id;
    @Arg
    String value;
    @Arg @Nullable
    String optionalValue;
    
    public Test(Bundle args)
    {
        TestBundleBuilder.inject(args, this);
    }
}
```

And here's how you use the created builder:

```groovy
Test test = new Test(new TestBundleBuilder()
                .id(1L)
                .value("Test")
                .optionalValue("optionalValue")
                .build());
```

For activities use the provider `buildIntent(context)` function to get an `Intent` - **IMPORTANT:** of course, the test class must be an activity in this case!
```groovy
Intent intent new TestActivityBundleBuilder()
                .id(1L)
                .value("Test")
                .optionalValue("optionalValue")
                .buildIntent(TestActivity.this));
startActivity(intent);
```

Alternatively, if you annotate your classes with `@BundleBuilder(useConstructorForMandatoryArgs = true)`, the processor will create a constructor that forces you to pass in all required arguments and only optional arguments will be settable via a builder like chaining of setters.

For an example with activities, check out the demo: [Demo](https://github.com/MFlisar/BundleArgs/tree/master/sample/src/main/java/com/michaelflisar/bundlebuilder/sample)

### Credits

This project is based on https://github.com/emilsjolander/IntentBuilder

### TODO

* conductor/fragment/class demo?
* ???
