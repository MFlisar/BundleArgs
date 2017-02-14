package com.michaelflisar.bundlebuilder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class Processor extends AbstractProcessor
{
    // --------------------
    // General functions
    // --------------------

    public static Processor instance;

    public Types typeUtils;
    public Elements elementUtils;
    public Filer filer;
    public Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return new HashSet<String>() {{
            add(BundleBuilder.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        instance = this;
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    // --------------------
    // Process class
    // --------------------

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(BundleBuilder.class))
        {
            // Make sure element is a field or a method declaration
            if (!annotatedElement.getKind().isClass())
            {
                logError(annotatedElement, "Only classes can be annotated with @%s", BundleBuilder.class.getSimpleName());
                return true;
            }

            try
            {
                TypeSpec builderSpec = getBuilderSpec(annotatedElement);
                JavaFile builderFile = JavaFile.builder(getPackageName(annotatedElement), builderSpec).build();
                builderFile.writeTo(filer);
            }
            catch (Exception e)
            {
                logError(annotatedElement, "Could not create intent builder for %s: %s", annotatedElement.getSimpleName(), e.getMessage());
            }
        }
        return true;
    }

    // --------------------
    // Processor implementation
    // --------------------

    private TypeSpec getBuilderSpec(Element annotatedElement)
    {
        List<Element> required = new ArrayList<>();
        List<Element> optional = new ArrayList<>();
        List<Element> all = new ArrayList<>();

        getAnnotatedFields(annotatedElement, required, optional);
        all.addAll(required);
        all.addAll(optional);

        // 1) create class
        final String name = String.format("%sBundleBuilder", annotatedElement.getSimpleName());
        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // 2) create constructor with all necessary fields
        createConstructor(annotatedElement, builder, required);

        // 3) add methods for all optional fields
        addSetters(name, annotatedElement, builder, required, optional);

        // 4) add buildIntent method to create an intent
        addBuildIntentFunction(annotatedElement, builder, required, optional);

        // TODO: can be done without Intent! But the intent does not need to handle each variable type explicitely where as the Bundle needs to!
        // 5) add build method to create a bundle
        addBuildBundleFunction(annotatedElement, builder, required, optional);

        // 6) add inject method to read all fields into an annotated class
        addInjectFunction(annotatedElement, builder, all);

        // 7) add getter functions for each fields
        addGetters(builder, all);

        // 8) OPTIONAL: add list of arguments to generated class
        addAllArgumentsAsListGetter(annotatedElement, builder, all);

        return builder.build();
    }

    // --------------------
    // Main functions
    // --------------------

    private void createConstructor(Element annotatedElement, TypeSpec.Builder builder, List<Element> required)
    {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        // if desired, we add all mandatory fields to the constructor
        if (annotatedElement.getAnnotation(BundleBuilder.class).useConstructorForMandatoryArgs())
        {
            for (Element e : required)
            {
                String paramName = getParamName(e);
                builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE, Modifier.FINAL);
                constructor.addParameter(TypeName.get(e.asType()), paramName);
                constructor.addStatement("this.$N = $N", paramName, paramName);
            }
        }

        builder.addMethod(constructor.build());
    }

    private void addSetters(String name, Element annotatedElement, TypeSpec.Builder builder, List<Element> required, List<Element> optional)
    {
        List<Element> elementsToPrecess = new ArrayList<>();

        // if desired, we add setters for all required fields
        if (!annotatedElement.getAnnotation(BundleBuilder.class).useConstructorForMandatoryArgs())
            elementsToPrecess.addAll(required);
        // we always add setters for all optional fields
        elementsToPrecess.addAll(optional);

        for (Element e : elementsToPrecess)
        {
            String paramName = getParamName(e);
            builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE);
            builder.addMethod(MethodSpec.methodBuilder(paramName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(e.asType()), paramName)
                    .addStatement("this.$N = $N", paramName, paramName)
                    .addStatement("return this")
                    .returns(ClassName.get(getPackageName(annotatedElement), name))
                    .build());
        }
    }

    private void addBuildIntentFunction(Element annotatedElement, TypeSpec.Builder builder, List<Element> required, List<Element> optional)
    {
        MethodSpec.Builder buildIntentMethod = MethodSpec.methodBuilder("buildIntent")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, TypeName.get(annotatedElement.asType()));

        if (!annotatedElement.getAnnotation(BundleBuilder.class).useConstructorForMandatoryArgs())
        {
            for (Element e : required)
            {
                String paramName = getParamName(e);
                buildIntentMethod
                        .beginControlFlow("if ($N == null)", paramName)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", paramName))
                        .endControlFlow()
                        .addStatement("intent.putExtra($S, $N)", paramName, paramName);
            }
        }

        for (Element e : optional)
        {
            String paramName = getParamName(e);
            buildIntentMethod.addStatement("intent.putExtra($S, $N)", paramName, paramName);
        }
        buildIntentMethod.returns(Intent.class)
                .addStatement("return intent");
        builder.addMethod(buildIntentMethod.build());
    }

    private void addBuildBundleFunction(Element annotatedElement, TypeSpec.Builder builder, List<Element> required, List<Element> optional)
    {
        // TODO: can be done without Intent! But the intent does not need to handle each variable type explicitely where as the Bundle needs to!
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Context.class, "context")
                .addStatement("$T intent = new Intent(context, $T.class)", Intent.class, TypeName.get(annotatedElement.asType()));

        if (!annotatedElement.getAnnotation(BundleBuilder.class).useConstructorForMandatoryArgs())
        {
            for (Element e : required)
            {
                String paramName = getParamName(e);
                buildMethod
                        .beginControlFlow("if ($N == null)", paramName)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", paramName))
                        .endControlFlow()
                        .addStatement("intent.putExtra($S, $N)", paramName, paramName);
            }
        }

        for (Element e : optional)
        {
            String paramName = getParamName(e);
            buildMethod.addStatement("intent.putExtra($S, $N)", paramName, paramName);
        }

        buildMethod.returns(Bundle.class)
                .addStatement("return intent.getExtras()");
        builder.addMethod(buildMethod.build());
    }

    private void addInjectFunction(Element annotatedElement, TypeSpec.Builder builder, List<Element> all)
    {
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Bundle.class, "args")
                .addParameter(TypeName.get(annotatedElement.asType()), "annotatedClass");
        for (Element e : all)
        {
            String paramName = getParamName(e);
            injectMethod.beginControlFlow("if (args.containsKey($S))", paramName)
                    .addStatement("annotatedClass.$N = ($T) args.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                    .nextControlFlow("else")
                    .addStatement("annotatedClass.$N = null", e.getSimpleName().toString())
                    .endControlFlow();
        }
        builder.addMethod(injectMethod.build());
    }

    private void addGetters(TypeSpec.Builder builder, List<Element> all)
    {
        for (Element e : all)
        {
            String paramName = getParamName(e);
            MethodSpec.Builder getterMethod = MethodSpec
                    .methodBuilder(getGetterName(paramName))
                    .returns(ClassName.get(e.asType()))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Bundle.class, "bundle")
                    .beginControlFlow("if (bundle != null && bundle.containsKey($S))", paramName)
                    .addStatement("return ($T) bundle.get($S)", e.asType(), paramName)
                    .nextControlFlow("else")
                    .addStatement("return null")
                    .endControlFlow();
            builder.addMethod(getterMethod.build());
        }
    }

    private void addAllArgumentsAsListGetter(Element annotatedElement, TypeSpec.Builder builder, List<Element> all)
    {
        if (annotatedElement.getAnnotation(BundleBuilder.class).createListOfArgs())
        {
            MethodSpec.Builder listMethod = MethodSpec.methodBuilder("getArguments")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Bundle.class, "bundle")
                    .addStatement("$T<Object> list = new $T<Object>()", ArrayList.class, ArrayList.class)
                    ;
            for (Element e : all)
            {
                String paramName = getParamName(e);
                listMethod.beginControlFlow("if (bundle.containsKey($S))", paramName)
                        .addStatement("list.add($L(bundle))", getGetterName(paramName))
                        .endControlFlow();
            }
            listMethod.returns(List.class)
                    .addStatement("return list");
            builder.addMethod(listMethod.build());
        }
    }

    // --------------------
    // Helper functions
    // --------------------

    private void logError(Element e, String msg, Object... args)
    {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private String getPackageName(Element e)
    {
        while (!(e instanceof PackageElement))
            e = e.getEnclosingElement();
        return ((PackageElement)e).getQualifiedName().toString();
    }

    private void getAnnotatedFields(Element annotatedElement, List<Element> required, List<Element> optional)
    {
        for (Element e : annotatedElement.getEnclosedElements())
        {
            if (e.getAnnotation(Arg.class) != null)
            {
                if (hasAnnotation(e, "Nullable"))
                    optional.add(e);
                else
                    required.add(e);
            }
        }

        List<? extends TypeMirror> superTypes = Processor.instance.typeUtils.directSupertypes(annotatedElement.asType());
        TypeMirror superClassType = superTypes.size() > 0 ? superTypes.get(0) : null;
        Element superClass = superClassType == null ? null : Processor.instance.typeUtils.asElement(superClassType);
        if (superClass != null && superClass.getKind() == ElementKind.CLASS)
            getAnnotatedFields(superClass, required, optional);
    }

    private boolean hasAnnotation(Element e, String name)
    {
        for (AnnotationMirror annotation : e.getAnnotationMirrors())
        {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(name))
                return true;
        }
        return false;
    }

    private String getParamName(Element e)
    {
        String extraValue = e.getAnnotation(Arg.class).value();
        return extraValue != null && !extraValue.trim().isEmpty() ? extraValue : e.getSimpleName().toString();
    }

    private String getGetterName(String paramName)
    {
        return "get" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
    }
}