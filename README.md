# BundleArgs [![Release](https://jitpack.io/v/MFlisar/BundleArgs.svg)](https://jitpack.io/#MFlisar/BundleArgs) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BundleArgs-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5289) ![Method count](https://img.shields.io/badge/Method%20count-4%20methods%20(6%20fields)-brightgreen.svg)

Let your IDE create your bundles/intents for you. This library is:
* **reflection free**
* typesafe
* fast
* **very small** - just the annotation interfaces are added to your akp (4 methods, 6 fields)
* **supports any class**:`Activities`, `Fragments` and any other class => use one processor for all your needs
* **support save/restore** to save changes of the arguments in an `Activities` `onSaveInstanceState` function and restore them again from the `savedInstanceState` if desired
* creates nice helper functions if appropriate
 
### How to add it to your project - Gradle (via [JitPack.io](https://jitpack.io/))

1) add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2) add the compile statement to your module's `build.gradle` and apply the apt plugin:
```groovy
dependencies {
    compile 'com.github.MFlisar.BundleArgs:bundleargs-annotation:1.3'
    annotationProcessor 'com.github.MFlisar.BundleArgs:bundleargs-processor:1.3'
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
		
		// Optionally: if desired, a persist method is generated that must be called in onSaveInstanceState
		// if this is enabled, following will either read the arguments OR the last values from the savedInstanceState
		// MyActivityBundleBuilder.inject(savedInstanceState != null ? savedInstanceState : getIntent().getExtras(), this);
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Optionally: this will save all fields into the state, so that you can inject current values when activity is restored
        // MyActivityBundleBuilder.persist(outState, this);
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
	.buildIntent(context);
```

4) If the annotated class extends `Activity`, following method will be added to start the activity directly;

```groovy
new MyActivityBundleBuilder()
	.stringArg("Test")
	.startActivity(context);
```

5) if the enable `useConstructorForMandatoryArgs` in the `@BundleBuilder` annotation, the generated builder will force you to set mandatory fields via the constructor like following:


```groovy
Bundle b = new TestBundleBuilder(1L, "Test") // you MUST supply mandatory fields
	.optionalValue("optionalValue") // optional fields can be set via builder functions
	.build();
```

### Customisation

**`@BundleBuilder`**

You can define some setup variables like following (each one is optional):

    @BundleBuilder(useConstructorForMandatoryArgs = true, setterPrefix = "with", generateGetters = false, generateIntentBuilder = false)
    
* `boolean useConstructorForMandatoryArgs()`:  default: `false`... if true, all mandatory fields will be part of the constructor, otherwise all mandatory fields need to be set with the builder style
* `String setterPrefix()`:  default `""`... if not empty, all setters for the builder will be prefixed with this String. I.e. the field `customField` will be settable via a function `builder.withCustomField(...)` if the `setterPrefix == "with"`...
* `boolean generateGetters()`: default: `false`... defines, if the builder offers getter functions for each field to retrieve fields from a bundle directly
* `boolean generateIntentBuilder()`: default: `false`... defines, if the `buildIntent` method is generated for non activity classes as well
* `boolean generatePersist()`: default: `false`... define if support activities / fragments are supported or not
* `boolean supportAndroidX()`: default: `false`... define if androiX activities / fragments are supported or not
* `boolean isKotlinClass()`: default: `false`... define if the annotated class is a kotlin class or not
* `boolean generatePersist()`: default: `false`... define if you want to generate a simply persist method to write all current values to the provided bundle

**`@Arg`**

     @Arg(value = "", optional = false)

* `String name()`: default: `""`... if set, the builder setter will get the custom name instead of the one derived from the field name
* `boolean optional()`: default: `false`... if true, fields are optional, if not, they must be set via constructor or via setter

<!-- Additional, fields can be annotated with `@Nullable` to define, if the field is allowed to be null or not, the builder will make the corresponding checks if necessary -->

### Demo

For an example with activities, check out the demo: [Demo](https://github.com/MFlisar/BundleArgs/tree/master/sample/src/main/java/com/michaelflisar/bundlebuilder/sample)

### Credits

This project is generally based on https://github.com/emilsjolander/IntentBuilder but was improved and restructured heavily in the meantime.

### TODO

* conductor/fragment/class demo?
* ???
