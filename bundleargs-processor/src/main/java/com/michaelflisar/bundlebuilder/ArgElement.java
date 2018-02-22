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

public class ArgElement {
    private final Element mElement;
    private final String mParamIsSetPostFix;
    private final String mParamName;
    private final TypeMirror mType;
    private final boolean mOptional;
    private final boolean mNullable;

    public ArgElement(Element e) {
        mElement = e;
        String extraValue = e.getAnnotation(Arg.class).name();
        mParamIsSetPostFix = "IsSet";
        mParamName = extraValue != null && extraValue.trim().length() > 0 ? extraValue : e.getSimpleName().toString();
        mType = e.asType();
        mOptional = e.getAnnotation(Arg.class).optional();
        mNullable = Util.hasAnnotation(e, "Nullable");
    }

    public boolean isOptional() {
        return mOptional;
    }

    public String getParamName() {
        return mParamName;
    }

    public void addFieldsToClass(TypeSpec.Builder builder, boolean useConstructorForMandatoryArgs) {
        builder.addField(TypeName.BOOLEAN, mParamName + mParamIsSetPostFix, Modifier.PRIVATE);
        if (!mOptional && useConstructorForMandatoryArgs) {
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE, Modifier.FINAL);
        } else {
            builder.addField(TypeName.get(mType), mParamName, Modifier.PRIVATE);
        }
    }

    public void addToConstructor(MethodSpec.Builder constructor, boolean useConstructorForMandatoryArgs) {
        if (mOptional) {
            return;
        }

        if (useConstructorForMandatoryArgs) {
            constructor.addParameter(TypeName.get(mType), mParamName);
            if (!mNullable && !Util.isPrimitiveType(mType)) {
                Util.addNullCheckWithException(constructor, this, false);
            }

            constructor
                    .addStatement("this.$N.put($S, new Pair(true, $N))", Util.FIELD_HASH_MAP_NAME, mParamName, mParamName);

//            constructor
//                    .addStatement("this.$N = $N", mParamName, mParamName)
//                    .addStatement("this.$N = true", mParamName + mParamIsSetPostFix);
        }
    }

    public void addFieldToIntent(MethodSpec.Builder buildMethod, boolean initPrimitives) {
        if (!isOptional()) {
            if (!mNullable && !Util.isPrimitiveType(mType)) {
                Util.addNullCheckWithException(buildMethod, this, true);
            }
//            else if (initPrimitives)
//            {
//                buildMethod
//                        .addStatement("$N = $L", mParamName, primitiveDefaultValue);
//            }
        }
        buildMethod
                .beginControlFlow("if ($N.get($S) != null)", Util.FIELD_HASH_MAP_NAME, mParamName)
                .addStatement("intent.putExtra($S, ($T)$N.get($S).second)", mParamName, mType, Util.FIELD_HASH_MAP_NAME, mParamName);
        if (mNullable) {
            buildMethod
                    .nextControlFlow("else")
                    .addStatement("intent.putExtra($S, ($T)null)", mParamName, mType);
        }
        buildMethod.endControlFlow();

        //buildMethod.addStatement("intent.putExtra($S, $N)", mParamName, mParamName);
    }

    public void addFieldToBundle(Elements elementUtils, Types typeUtils, Messager messager, MethodSpec.Builder buildMethod, boolean initPrimitives) {
        String bundleFunctionName = Util.getBundleFunctionName(elementUtils, typeUtils, messager, mType);
        if (bundleFunctionName != null) {
            if (!isOptional()) {
                buildMethod
                        .beginControlFlow("if (!$N.containsKey($S) || !$N.get($S).first)", Util.FIELD_HASH_MAP_NAME, mParamName, Util.FIELD_HASH_MAP_NAME, mParamName)
                        //.beginControlFlow("if (!$N)", mParamName + mParamIsSetPostFix)
                        .addStatement("throw new RuntimeException($S)", String.format("Mandatory field '%s' missing!", mParamName))
                        .endControlFlow();
            }

            buildMethod
                    .beginControlFlow("if ($N.get($S).first)", Util.FIELD_HASH_MAP_NAME, mParamName)
                    .addStatement("bundle.put$L($S, ($T)$N.get($S).second)", bundleFunctionName, mParamName, mType, Util.FIELD_HASH_MAP_NAME, mParamName)
                    //.beginControlFlow("if ($N)", mParamName + mParamIsSetPostFix)
                    //.addStatement("bundle.put$L($S, $N)", bundleFunctionName, mParamName, mParamName)
                    .endControlFlow();
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Field type \"%s\" not supported!", mType.toString()));
        }
    }

    public void addFieldToInjection(MethodSpec.Builder injectMethod) {
        if (!mOptional) {
            Util.addContainsCheckWithException(injectMethod, this, "args");
        }

        injectMethod.beginControlFlow("if (args != null && args.containsKey($S))", mParamName)
                .addStatement("annotatedClass.$N = ($T) args.get($S)", mElement.getSimpleName().toString(), mType, mParamName)
                .endControlFlow();
    }

    public void addSetter(TypeSpec.Builder builder, ClassName className, String prefix) {
        builder.addMethod(MethodSpec.methodBuilder(getFieldSetterName(prefix))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(mType), mParamName)
                .addStatement("this.$N.put($S, new Pair(true, $N))", Util.FIELD_HASH_MAP_NAME, mParamName, mParamName)
                //.addStatement("this.$N = $N", mParamName, mParamName)
                //.addStatement("this.$N = true", mParamName + mParamIsSetPostFix)
                .addStatement("return this")
                .returns(className)
                .build());
    }

    public void addFieldGetter(Element annotatedElement, TypeSpec.Builder builder) {
        MethodSpec.Builder getterMethod = MethodSpec
                .methodBuilder(getFieldGetterName())
                .returns(ClassName.get(mType))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(TypeName.get(annotatedElement.asType()), "annotatedClass")
                .addParameter(Bundle.class, "bundle");

        getterMethod
                .beginControlFlow("if (bundle != null && bundle.containsKey($S))", mParamName)
                .addStatement("return ($T) bundle.get($S)", mType, mParamName)
                .nextControlFlow("else")
                //.addStatement("return annotatedClass.$L",  mParamName)
                .addStatement("return annotatedClass.$L.get($s)", Util.FIELD_HASH_MAP_NAME, mParamName)
                .endControlFlow();

        builder.addMethod(getterMethod.build());
    }

    public String getFieldGetterName() {
        return "get" + mParamName.substring(0, 1).toUpperCase() + mParamName.substring(1);
    }

    public String getFieldSetterName(String setterPrefix) {
        if (setterPrefix == null || setterPrefix.length() == 0) {
            return mParamName;
        } else {
            return setterPrefix + mParamName.substring(0, 1).toUpperCase() + mParamName.substring(1);
        }
    }


}
