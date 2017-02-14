package com.michaelflisar.bundlebuilder;

import android.os.Bundle;
import android.os.Parcelable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.Serializable;
import java.util.HashMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by Michael on 14.02.2017.
 */

public class Util
{
    public static HashMap<String, Object> PRIMITIVE_CLASSES_DEFAULTS = new HashMap<String, Object>()
    {
        {
            put(boolean.class.getName(), false);
            put(byte.class.getName(), 0);
            put(char.class.getName(), "");
            put(short.class.getName(), 0);
            put(int.class.getName(), 0);
            put(long.class.getName(), 0L);
            put(float.class.getName(), 0.0);
            put(double.class.getName(), 0.0);
        }
    };

    // Maps class names to Bundle.put<...> functions
    private static HashMap<String, String> BUNDLE_FUNCTIONS_MAP = new HashMap<String, String>(){{

        put(boolean.class.getName(), "Boolean");
        put(Boolean.class.getName(), "Boolean");
        put(byte.class.getName(), "Byte");
        put(Byte.class.getName(), "Byte");
        put(char.class.getName(), "Char");
        put(Character.class.getName(), "Char");
        put(short.class.getName(), "Short");
        put(Short.class.getName(), "Short");
        put(int.class.getName(), "Int");
        put(Integer.class.getName(), "Int");
        put(long.class.getName(), "Long");
        put(Long.class.getName(), "Long");
        put(float.class.getName(), "Float");
        put(Float.class.getName(), "Float");
        put(double.class.getName(), "Double");
        put(Double.class.getName(), "Double");
        put(String.class.getName(), "String");
        put(CharSequence.class.getName(), "CharSequence");
        put(Parcelable.class.getName(), "Parcelable");
        put(Parcelable[].class.getName(), "ParcelableArray");
//        put(ArrayList<? extends Parcelable>.class.getName(), "ParcelableArrayList");
//        put(SparseArray<? extends Parcelable>.class.getName(), "SparseParcelableArray");
//        put(ArrayList<Integer>.class.getName(), "IntegerArrayList");
//        put(ArrayList<String>.class.getName(), "StringArrayList");
//        put(ArrayList<CharSequence>.class.getName(), "CharSequenceArrayList");
        put(Serializable.class.getName(), "Serializable");
        put(boolean[].class.getName(), "BooleanArray");
        put(Boolean[].class.getName(), "BooleanArray");
        put(byte[].class.getName(), "ByteArray");
        put(Byte[].class.getName(), "ByteArray");
        put(short[].class.getName(), "ShortArray");
        put(Short[].class.getName(), "ShortArray");
        put(char[].class.getName(), "CharArray");
        put(Character[].class.getName(), "CharArray");
        put(int[].class.getName(), "IntArray");
        put(Integer[].class.getName(), "IntArray");
        put(long[].class.getName(), "LongArray");
        put(Long[].class.getName(), "LongArray");
        put(float[].class.getName(), "FloatArray");
        put(Float[].class.getName(), "FloatArray");
        put(double[].class.getName(), "DoubleArray");
        put(Double[].class.getName(), "DoubleArray");
        put(String[].class.getName(), "StringArray");
        put(CharSequence[].class.getName(), "CharSequenceArray");
        put(Bundle.class.getName(), "Bundle");
    }};

    public static void addFieldToConstructor(TypeSpec.Builder builder, MethodSpec.Builder constructor , Element e, String paramName, boolean isMandatory)
    {
        TypeMirror type = e.asType();
        builder.addField(TypeName.get(e.asType()), paramName, Modifier.PRIVATE, Modifier.FINAL);
        constructor.addParameter(TypeName.get(e.asType()), paramName);
        if (isMandatory)
        {
            Object primitiveDefaultValue = Util.PRIMITIVE_CLASSES_DEFAULTS.get(type.toString());
            if (primitiveDefaultValue == null)
            {
                constructor
                        .beginControlFlow("if ($N == null)", paramName)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", paramName))
                        .endControlFlow();
            }
        }
        constructor.addStatement("this.$N = $N", paramName, paramName);
    }

    public static void addFieldToBundle(MethodSpec.Builder buildMethod, Element e, String paramName, boolean isMandatory, boolean initPrimitives)
    {
        TypeMirror type = e.asType();
        String bundleFunctionName = BUNDLE_FUNCTIONS_MAP.get(type.toString());
        if (bundleFunctionName != null)
        {
            if (isMandatory)
            {
                Object primitiveDefaultValue = Util.PRIMITIVE_CLASSES_DEFAULTS.get(type.toString());
                if (primitiveDefaultValue == null)
                {
                    buildMethod
                            .beginControlFlow("if ($N == null)", paramName)
                            .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", paramName))
                            .endControlFlow();
                }
                else if (initPrimitives)
                {
                    buildMethod
                            .addStatement("$N = $L", paramName, primitiveDefaultValue);
                }
            }

            buildMethod.addStatement("bundle.put$L($S, $N)", bundleFunctionName, paramName, paramName);
        }
        else
            buildMethod.addStatement("throw new RuntimeException($S)", String.format("Field type \"%s\" not supported!", type.toString()));
    }

    public static void addFieldToIntent(MethodSpec.Builder buildMethod, Element e, String paramName, boolean isMandatory, boolean initPrimitives)
    {
        TypeMirror type = e.asType();
        if (isMandatory)
        {
            Object primitiveDefaultValue = Util.PRIMITIVE_CLASSES_DEFAULTS.get(type.toString());
            if (primitiveDefaultValue == null)
            {
                buildMethod
                        .beginControlFlow("if ($N == null)", paramName)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", paramName))
                        .endControlFlow();
            }
            else if (initPrimitives)
            {
                buildMethod
                        .addStatement("$N = $L", paramName, primitiveDefaultValue);
            }


        }
        buildMethod.addStatement("intent.putExtra($S, $N)", paramName, paramName);
    }

    public static void addFieldToInjectFunction(MethodSpec.Builder injectMethod, Element e, String paramName)
    {
        TypeMirror type = e.asType();
        injectMethod.beginControlFlow("if (args != null && args.containsKey($S))", paramName)
                .addStatement("annotatedClass.$N = ($T) args.get($S)", e.getSimpleName().toString(), e.asType(), paramName)
                .nextControlFlow("else");

        Object primitiveDefaultValue = Util.PRIMITIVE_CLASSES_DEFAULTS.get(type.toString());
        if (primitiveDefaultValue == null)
        {
            injectMethod
                    .addStatement("annotatedClass.$N = null", e.getSimpleName().toString());
        }
        else
        {
            injectMethod
                    .addStatement("annotatedClass.$N = $L", e.getSimpleName().toString(), primitiveDefaultValue);
        }
        injectMethod
                .endControlFlow();
    }

    public static void addFieldGetter(TypeSpec.Builder builder, Element e, String paramName, String getterName)
    {
        TypeMirror type = e.asType();
        MethodSpec.Builder getterMethod = MethodSpec
                .methodBuilder(getterName)
                .returns(ClassName.get(e.asType()))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Bundle.class, "bundle")
                .beginControlFlow("if (bundle != null && bundle.containsKey($S))", paramName)
                .addStatement("return ($T) bundle.get($S)", e.asType(), paramName)
                .nextControlFlow("else");
        Object primitiveDefaultValue = Util.PRIMITIVE_CLASSES_DEFAULTS.get(type.toString());
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
}