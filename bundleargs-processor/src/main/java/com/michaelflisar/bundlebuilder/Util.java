package com.michaelflisar.bundlebuilder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;

import com.squareup.javapoet.MethodSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Michael on 14.02.2017.
 */

public class Util {
    public static final String FIELD_HASH_MAP_NAME = "mFieldMap";

    public static String getBundleBuilderName(Element annotatedElement) {
        return String.format("%sBundleBuilder", annotatedElement.getSimpleName());
    }

    private static HashSet<String> PRIMITIVE_CLASSES = new HashSet<String>() {
        {
            add(boolean.class.getName());
            add(byte.class.getName());
            add(char.class.getName());
            add(short.class.getName());
            add(int.class.getName());
            add(long.class.getName());
            add(float.class.getName());
            add(double.class.getName());
        }
    };

    // Maps class names to Bundle.put<...> functions
    private static HashMap<String, String> BUNDLE_FUNCTIONS_MAP = new HashMap<String, String>() {{

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

    private static HashMap<String, String> BUNDLE_ARRAY_FUNCTIONS_MAP = new HashMap<String, String>() {{
        put(String.class.getName(), "StringArrayList");
        put(int.class.getName(), "IntegerArrayList");
        put(Integer.class.getName(), "IntegerArrayList");
        put(char.class.getName(), "CharSequenceArrayList");
        put(Character.class.getName(), "CharSequenceArrayList");
    }};

    public static String getBundleFunctionName(Elements elementUtils, Types typeUtils, Messager messager, TypeMirror typeMirror) {
        String functionName = getArrayBundleFunctionName(elementUtils, typeUtils, messager, typeMirror);
        if (functionName == null) {
            functionName = getSimpleBundleFunctionName(elementUtils, typeUtils, typeMirror);
        }
        return functionName;
    }

    public static boolean isPrimitiveType(TypeMirror typeMirror) {
        return PRIMITIVE_CLASSES.contains(typeMirror.toString());
    }

    private static String getSimpleBundleFunctionName(Elements elementUtils, Types typeUtils, TypeMirror typeMirror) {
        String functionName = BUNDLE_FUNCTIONS_MAP.get(typeMirror.toString());
        if (functionName == null) {
            if (typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement("android.os.Parcelable").asType())) {
                functionName = "Parcelable";
            } else if (typeUtils.isAssignable(typeMirror, elementUtils.getTypeElement(Serializable.class.getName()).asType())) {
                functionName = "Serializable";
            }
        }
        return functionName;
    }

    private static String getArrayBundleFunctionName(Elements elementUtils, Types typeUtils, Messager messager, TypeMirror typeMirror) {
//        if (true)
//            return "String";
        for (String key : BUNDLE_ARRAY_FUNCTIONS_MAP.keySet()) {
            TypeMirror tm = getArrayListType(elementUtils, typeUtils, key);
            if (tm != null && typeUtils.isAssignable(typeMirror, tm)) {
                return BUNDLE_ARRAY_FUNCTIONS_MAP.get(key);
            }
        }
        if (typeUtils.isAssignable(typeMirror, getWildcardType(elementUtils, typeUtils, "android.util.SparseArray", "android.os.Parcelable"))) {
            return "SparseParcelableArray";
        }


        return null;
    }

    private static TypeMirror getArrayListType(Elements elementUtils, Types typeUtils, String elementType) {
        TypeElement arrayList = elementUtils.getTypeElement("java.util.ArrayList");
        TypeElement typeElement = elementUtils.getTypeElement(elementType);
        TypeMirror elType = typeElement != null ? typeElement.asType() : null;
        if (elType != null) {
            return typeUtils.getDeclaredType(arrayList, elType);
        }
        return null;
    }

    private static TypeMirror getWildcardType(Elements elementUtils, Types typeUtils, String type, String elementType) {
        TypeElement arrayList = elementUtils.getTypeElement(type);
        TypeMirror elType = elementUtils.getTypeElement(elementType).asType();
        return typeUtils.getDeclaredType(arrayList, typeUtils.getWildcardType(elType, null));
    }

    public static boolean hasAnnotation(Element e, String simpleClassName) {
        for (AnnotationMirror annotation : e.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().asElement().getSimpleName().toString().equals(simpleClassName)) {
                return true;
            }
        }
        return false;
    }

    public static void addNullCheckWithException(MethodSpec.Builder buildMethod, ArgElement argElement, boolean checkHashMap) {
        if (checkHashMap) {
            buildMethod
                    .beginControlFlow("if (!$N.containsKey($S) || $N.get($S).second == null)", Util.FIELD_HASH_MAP_NAME, argElement.getParamName(), Util.FIELD_HASH_MAP_NAME,
                            argElement.getParamName());
        } else {
            buildMethod.beginControlFlow("if ($N == null)", argElement.getParamName());
        }
        buildMethod
                .addStatement("throw new RuntimeException($S)", String.format("Mandatory field '%s' missing!", argElement.getParamName()))
                .endControlFlow();
    }

    public static void addContainsCheckWithException(MethodSpec.Builder buildMethod, ArgElement argElement, String bundleName) {
        buildMethod
                .beginControlFlow("if (" + bundleName + " == null || !" + bundleName + ".containsKey($S))", argElement.getParamName())
                .addStatement("throw new RuntimeException($S)", String.format("Mandatory field '%s' missing in " + bundleName + "!", argElement.getParamName()))
                .endControlFlow();
    }

    public static boolean checkForConstructorWithBundle(Element element) {
        for (ExecutableElement cons : ElementFilter.constructorsIn(element.getEnclosedElements())) {
            if (cons.getParameters().size() == 1 && cons.getParameters().get(0).asType().toString().equals(Bundle.class.getName())) {
                return true;
            }

        }

        return false;
    }

    public static boolean checkIsOrExtendsActivity(Elements elementUtils, Types typeUtil, Element element) {
        TypeMirror activity = elementUtils.getTypeElement(Activity.class.getName()).asType();
        if (typeUtil.isAssignable(element.asType(), activity)) {
            return true;
        }
        return false;
    }

    public static boolean checkIsOrExtendsFragment(Elements elementUtils, Types typeUtil, Element element) {
        TypeMirror fragment = elementUtils.getTypeElement(Fragment.class.getName()).asType();
        TypeMirror supportFragment = elementUtils.getTypeElement(android.support.v4.app.Fragment.class.getName()).asType();
        if (typeUtil.isAssignable(element.asType(), fragment) || typeUtil.isAssignable(element.asType(), supportFragment)) {
            return true;
        }
        return false;
    }

    public static String getPackageName(Element e) {
        while (!(e instanceof PackageElement)) {
            e = e.getEnclosingElement();
        }
        return ((PackageElement) e).getQualifiedName().toString();
    }
}