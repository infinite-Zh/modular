package com.infinite.compiler.factory;

import com.infinite.annotation.Parameter;
import com.infinite.compiler.Constant;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class ParameterFactory {
    private static final String CONTENT = "$T t=($T)target";

    private MethodSpec.Builder methodBuilder;

    private Messager messager;

    private Types typeUtil;

    private ClassName className;

    private ParameterFactory(Builder builder) {
        messager = builder.messager;
        typeUtil = builder.typeUtil;
        className = builder.className;

        methodBuilder = MethodSpec.methodBuilder(Constant.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.getParameterSpec());

    }

    public void addFirstStatement() {
        methodBuilder.addStatement(CONTENT, className, className);
    }

    public void buildStatement(Element e) {
        TypeMirror typeMirror = e.asType();
        // 获取被注解参数的类型编号
        int type = typeMirror.getKind().ordinal();
        //获取参数名
        String fieldName = e.getSimpleName().toString();

        //获取注解的值
        String annotationValue = e.getAnnotation(Parameter.class).name();

        if (annotationValue.isEmpty()) {
            annotationValue = fieldName;
        }
        String finalValue = "t." + fieldName;

        String methodContent = finalValue + "=t.getIntent().";

        if (type== TypeKind.INT.ordinal()){
            methodContent=methodContent+"getIntExtra($S,"+finalValue+")";
        }else if (type==TypeKind.BOOLEAN.ordinal()){
            methodContent=methodContent+"getBooleanExtra($S,"+finalValue+")";
        }else if (type==TypeKind.FLOAT.ordinal()){
            methodContent=methodContent+"getFloatExtra($S,"+finalValue+")";
        }else if (type==TypeKind.LONG.ordinal()){
            methodContent=methodContent+"getLongExtra($S,"+finalValue+")";
        }else if (typeMirror.toString().equalsIgnoreCase(Constant.STRING)){
            methodContent=methodContent+"getStringExtra($S)";
        }

        if (methodContent.endsWith(")")){
            methodBuilder.addStatement(methodContent,annotationValue);
        }else {
            messager.printMessage(Diagnostic.Kind.ERROR, "methodContent illegal");
        }

    }

    public MethodSpec build(){
        return methodBuilder.build();
    }

    public static class Builder {

        private Messager messager;

        private Types typeUtil;

        private ClassName className;

        private ParameterSpec parameterSpec;


        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public ParameterSpec getParameterSpec() {
            return parameterSpec;
        }


        public Messager getMessager() {
            return messager;
        }

        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Types getTypeUtil() {
            return typeUtil;
        }

        public Builder setTypeUtil(Types typeUtil) {
            this.typeUtil = typeUtil;
            return this;
        }

        public ClassName getClassName() {
            return className;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build() {
            return new ParameterFactory(this);
        }

    }
}
