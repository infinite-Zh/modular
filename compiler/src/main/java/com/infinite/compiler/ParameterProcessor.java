package com.infinite.compiler;

import com.google.auto.service.AutoService;
import com.infinite.annotation.Parameter;
import com.infinite.compiler.factory.ParameterFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedAnnotationTypes(Constant.PARAMETER_ANNOTATION_TYPE)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends AbstractProcessor {

    private Elements elementUtil;
    private Messager messager;
    private Filer filer;
    private Types typeUtil;

    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtil = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        typeUtil = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.size() > 0) {
            //
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
            if (elements != null) {
                valueOfParameterMap(elements);

                try {
                    createParameterFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            }
        }
        return false;
    }

    private void createParameterFile() throws IOException {
        if (tempParameterMap.isEmpty()) {
            return;
        }
        /**
         * MainActivity t = (MainActivity) target;
         *
         *         t.name = t.getIntent().getStringExtra("name");
         *         t.age = t.getIntent().getIntExtra("age", t.age);
         */
        TypeElement parameterType = elementUtil.getTypeElement(Constant.PARAMETER_LOAD);
        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, Constant.PARAMETER_NAME).build();

        for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            ClassName className = ClassName.get(typeElement);

            ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                    .setClassName(className)
                    .setMessager(messager)
                    .setTypeUtil(typeUtil)
                    .build();
            factory.addFirstStatement();

            for (Element e : entry.getValue()) {
                factory.buildStatement(e);
            }

            String finalClassName =  typeElement.getSimpleName()+Constant.PARAMETER_FILE_NAME;

            JavaFile.builder(className.packageName(),
                    TypeSpec.classBuilder(finalClassName)
                            .addModifiers(Modifier.PUBLIC)
                            .addSuperinterface(ClassName.get(parameterType))
                            .addMethod(factory.build())
                            .build())
                    .build()
                    .writeTo(filer);

        }
    }

    private void valueOfParameterMap(Set<? extends Element> elements) {
        for (Element e : elements) {
            TypeElement parent = (TypeElement) e.getEnclosingElement();
            List<Element> list = tempParameterMap.get(parent);
            if (list == null) {
                list = new ArrayList<>();
                tempParameterMap.put(parent, list);
            }
            list.add(e);
        }
    }
}
