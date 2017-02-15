package com.michaelflisar.bundlebuilder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;

import com.squareup.javapoet.MethodSpec;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

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
    public static HashMap<String, String> BUNDLE_FUNCTIONS_MAP = new HashMap<String, String>(){{

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

    public static boolean hasAnnotation(Element e, String simpleClassName)
    {
        for (AnnotationMirror annotation : e.getAnnotationMirrors())
        {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(simpleClassName))
                return true;
        }
        return false;
    }

    public static void addNullCheckWithException(MethodSpec.Builder buildMethod, ArgElement argElement)
    {
        buildMethod
                .beginControlFlow("if ($N == null)", argElement.getParamName())
                .addStatement("throw new RuntimeException($S)", String.format("Mandatory field \"%s\" missing!", argElement.getParamName()))
                .endControlFlow();
    }

    public static boolean checkForConstructorWithBundle(Element element)
    {
        for (ExecutableElement cons : ElementFilter.constructorsIn(element.getEnclosedElements()))
        {
            if (cons.getParameters().size() == 1 && cons.getParameters().get(0).asType().toString().equals(Bundle.class.getName()))
                return true;

        }

        return false;
    }

    public static boolean checkIsOrExtendsActivity(Elements elementUtils, Types typeUtil, Element element)
    {
        TypeMirror activity = elementUtils.getTypeElement(Activity.class.getName()).asType();
        if (typeUtil.isAssignable(element.asType(), activity))
            return true;
        return false;
    }
}