package com.michaelflisar.bundlebuilder;

import android.os.Bundle;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by Michael on 14.02.2017.
 */

public class ArgElement
{
    private final Element mElement;
    private final String mParamName;
    private final TypeMirror mType;
    private final boolean mOptional;
    private final boolean mNullable;

    public ArgElement(Element e)
    {
        mElement = e;
        String extraValue = e.getAnnotation(Arg.class).value();
        mParamName =  extraValue != null && extraValue.trim().length() > 0 ? extraValue : e.getSimpleName().toString();
        mType = e.asType();
        mOptional = e.getAnnotation(Arg.class).optional();
        mNullable = Util.hasAnnotation(e, "Nullable");
    }

    public boolean isOptional()
    {
        return mOptional;
    }

    public String getParamName()
    {
        return mParamName;
    }

    public void addFieldsToClass(TypeSpec.Builder builder, boolean useConstructorForMandatoryArgs)
    {
        if (!mOptional && useConstructorForMandatoryArgs)
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE, Modifier.FINAL);
        else
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE);
    }

    public void addToConstructor(MethodSpec.Builder constructor, boolean useConstructorForMandatoryArgs)
    {
        if (mOptional)
            return;

        if (useConstructorForMandatoryArgs)
        {
            constructor.addParameter(TypeName.get(mType), mParamName);
            if (!mNullable)
            {
                Object primitiveDefaultValue = Util.getPrimitiveTypeDefaultValue(mType);
                if (primitiveDefaultValue == null)
                    Util.addNullCheckWithException(constructor, this);
            }

            constructor.addStatement("this.$N = $N", mParamName, mParamName);
        }
    }

    public void addFieldToIntent(MethodSpec.Builder buildMethod, boolean initPrimitives)
    {
        if (!isOptional())
        {
            Object primitiveDefaultValue = Util.getPrimitiveTypeDefaultValue(mType);
            if (primitiveDefaultValue == null)
            {
                if (!mNullable)
                    Util.addNullCheckWithException(buildMethod, this);
            }
            else if (initPrimitives)
            {
                buildMethod
                        .addStatement("$N = $L", mParamName, primitiveDefaultValue);
            }
        }
        buildMethod.addStatement("intent.putExtra($S, $N)", mParamName, mParamName);
    }

    public void addFieldToBundle(Elements elementUtils, Types typeUtils, Messager messager, MethodSpec.Builder buildMethod, boolean initPrimitives)
    {
        String bundleFunctionName = Util.getBundleFunctionName(elementUtils, typeUtils, messager, mType);
        if (bundleFunctionName != null)
        {
            if (!isOptional())
            {
                Object primitiveDefaultValue = Util.getPrimitiveTypeDefaultValue(mType);
                if (primitiveDefaultValue == null)
                {
                    if (!mNullable)
                        Util.addNullCheckWithException(buildMethod, this);
                }
//                else if (initPrimitives)
//                {
//                    buildMethod
//                            .addStatement("$N = $L", mParamName, primitiveDefaultValue);
//                }
            }

            buildMethod.addStatement("bundle.put$L($S, $N)", bundleFunctionName, mParamName, mParamName);
        }
        else
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Field type \"%s\" not supported!", mType.toString()));
    }

    public void addFieldToInjection(MethodSpec.Builder injectMethod)
    {
        injectMethod.beginControlFlow("if (args != null && args.containsKey($S))", mParamName)
                .addStatement("annotatedClass.$N = ($T) args.get($S)", mElement.getSimpleName().toString(), mType, mParamName)
                .nextControlFlow("else");

        Object primitiveDefaultValue = Util.getPrimitiveTypeDefaultValue(mType);
        if (primitiveDefaultValue == null)
        {
            injectMethod
                    .addStatement("annotatedClass.$N = null", mElement.getSimpleName().toString());
        }
        else
        {
            injectMethod
                    .addStatement("annotatedClass.$N = $L", mElement.getSimpleName().toString(), primitiveDefaultValue);
        }
        injectMethod
                .endControlFlow();
    }

    public void addSetter(TypeSpec.Builder builder, ClassName className, String prefix)
    {
        builder.addMethod(MethodSpec.methodBuilder(getFieldSetterName(prefix))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(mType), mParamName)
                .addStatement("this.$N = $N", mParamName, mParamName)
                .addStatement("return this")
                .returns(className)
                .build());
    }

    public void addFieldGetter(TypeSpec.Builder builder)
    {
        MethodSpec.Builder getterMethod = MethodSpec
                .methodBuilder(getFieldGetterName())
                .returns(ClassName.get(mType))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Bundle.class, "bundle")
                .beginControlFlow("if (bundle != null && bundle.containsKey($S))", mParamName)
                .addStatement("return ($T) bundle.get($S)", mType, mParamName)
                .nextControlFlow("else");
        Object primitiveDefaultValue = Util.getPrimitiveTypeDefaultValue(mType);
        if (primitiveDefaultValue == null)
        {
            getterMethod
                    .addStatement("return null");
        }
        else
        {
            getterMethod
                    .addStatement("return $L", primitiveDefaultValue);
        }
        getterMethod
                .endControlFlow();
        builder.addMethod(getterMethod.build());
    }

    public String getFieldGetterName()
    {
        return "get" + mParamName.substring(0, 1).toUpperCase() + mParamName.substring(1);
    }

    public String getFieldSetterName(String setterPrefix)
    {
        if (setterPrefix == null || setterPrefix.length() == 0)
            return mParamName;
        else
            return setterPrefix + mParamName.substring(0, 1).toUpperCase() + mParamName.substring(1);
    }


}
