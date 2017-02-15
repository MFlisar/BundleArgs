#BundleArgs [![Release](https://jitpack.io/v/MFlisar/BundleArgs.svg)](https://jitpack.io/#MFlisar/BundleArgs) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BundleArgs-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5289)
Type safe bundle/intent builder for any class (activities, fragments, ...) 

BundleBuilder is a type safe and fast way of creating intents/bundles and populating them with extras.
 
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
    compile 'com.github.MFlisar:BundleBuilder:0.5'
    apt 'com.github.MFlisar:BundleBuilder:0.5'
}
```

### Usage - Definitions

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
    
    // define a constructor with Bundle args and the processor will create a build method that directly creates an object of this class
    public Test(Bundle args)
    {
        TestBundleBuilder.inject(args, this);
    }
}
```

And this is how you define it in an activity:

```groovy
@BundleBuilder
public class MyActivity extends Activity
{
	@Arg
	String stringArg;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		MyActivityBundleBuilder.inject(getIntent().getExtras(), this);
	}
}
```

### Usage - Builder

1) If you have defined the default costructor with a bundle as args in it, you can directly create a class like this:

```groovy
Test test = new TestBundleBuilder()
                .id(1L)
                .value("Test")
                .optionalValue("optionalValue")
                .create();
```

2) You can always just create a `Bundle` with the builder like following:

```groovy
Bundle bundle = new TestBundleBuilder()
                .id(1L)
                .value("Test")
                .optionalValue("optionalValue")
                .build();
```

3) You can always just create an `Intent` with the builder like following (if the annotated class is an `Activity` or if the boolean flag `alwaysAddIntentBuilder` of the `BundleBuilder` is set to true:

```groovy
Intent intent = new TestBundleBuilder()
                .id(1L)
                .value("Test")
                .optionalValue("optionalValue")
                .buildIntent();
```

4) If the annotated class extends `Activity`, following method will be added to start the activity directly;

```groovy
new MyActivityBundleBuilder()
        .stringArg("Test")
        .startActivity(context);
```
###Customisation

**`@BundleBuilder`**

You can define some setup variables like following (each one is optional):

    @BundleBuilder(useConstructorForMandatoryArgs = true, setterPrefix = "with", alwaysAddIntentBuilder = false)
    
* `boolean useConstructorForMandatoryArgs()`:  default: `false`... if true, all mandatory fields will be part of the constructor, otherwise all mandatory fields need to be set with the builder style
* `String setterPrefix()`:  default `""`... if not empty, all setters for the builder will be prefixed with this String. I.e. the field `customField` will be settable via a function `builder.withCustomField(...)` if the `setterPrefix == "with"`...
* `boolean alwaysAddIntentBuilder()`: default: `false`... defines, if the `buildIntent` method is generated for non activity classes as well

**`@Arg`**

     @Arg(value = "", optional = false)

* `String value()`: default: `""`... if set, the builder setter will get the custom value instead of the one derived from the field name
* `boolean optional()`: default: `false`... if true, fields are optional, if not, they must be set via constructor or via setter

Additional, fields can be annotated with `@Nullable` to define, if the field is allowed to be null or not, the builder will make the corresponding checks if necessary

###Demo

For an example with activities, check out the demo: [Demo](https://github.com/MFlisar/BundleArgs/tree/master/sample/src/main/java/com/michaelflisar/bundlebuilder/sample)

### Credits

This project is based on https://github.com/emilsjolander/IntentBuilder

### TODO

* conductor/fragment/class demo?
* ???
