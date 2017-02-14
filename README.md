#BundleArgs [![Release](https://jitpack.io/v/MFlisar/BundleArgs.svg)](https://jitpack.io/#MFlisar/BundleArgs)
Type safe bundle/intent building for any class (activities, fragments, ...)

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
    compile 'com.github.MFlisar:BundleBuilder:0.2'
    apt 'com.github.MFlisar:BundleBuilder:0.2'
}
```

### Usage

Here's a simple example:

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
                .build(context));
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

Alternatively, if you annotate your classes with `@BundleBuilder(useConstructorForMandatoryArgs = true)`, the processor will create a constructor that forces you to pass in all required arguments and only option arguments will be settable via a builder like chaining of setters.

The context will not be necessary in future releases, currently I use it because I create `Bundles` via `Intents` because they allow to pass in values as `Objects` and don't need to distinct between each value class.

For an example with activities, check out the demo: [Demo](https://github.com/MFlisar/BundleArgs/tree/master/sample/src/main/java/com/michaelflisar/bundlebuilder/sample)

### Credits

This project is based on https://github.com/emilsjolander/IntentBuilder

### TODO

* support primitive types
* improve the bundle builder and make bundles without going the way over the intent (and avoid the need of a context for simple bundles)
* custom exception type for missing argument?
* conductor/fragment/class demo?
* ???
