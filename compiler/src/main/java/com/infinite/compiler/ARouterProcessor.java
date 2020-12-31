package com.infinite.compiler;

import com.google.auto.service.AutoService;
import com.infinite.annotation.ARouter;
import com.infinite.annotation.RouterBean;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.io.PipedReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Constant.AROUTER_ANNOTATION_TYPE})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Constant.APT_PACKAGE, Constant.MODULE_NAME})
public class ARouterProcessor extends AbstractProcessor {
    private Elements elementUtil;
    private Messager messager;
    private Types typeUtil;
    private Filer filer;

    private String moduleName;

    private String packageNameForApt;

    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();
    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtil = processingEnvironment.getElementUtils();
        typeUtil = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        moduleName = processingEnvironment.getOptions().get(Constant.MODULE_NAME);
        packageNameForApt = processingEnvironment.getOptions().get(Constant.APT_PACKAGE);

        messager.printMessage(Diagnostic.Kind.NOTE, "moduleName=" + moduleName);
        messager.printMessage(Diagnostic.Kind.NOTE, "packageNameForApt=" + packageNameForApt);
        messager.printMessage(Diagnostic.Kind.NOTE, "start");

        if (moduleName == null || packageNameForApt == null) {
//            messager.printMessage(Diagnostic.Kind.ERROR, "moduleName and packageNameForApt should not be null");

        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        messager.printMessage(Diagnostic.Kind.NOTE, "getSupportedAnnotationTypes");
        return super.getSupportedAnnotationTypes();
    }

    /**
     * 处理注解的核心方法，生成java文件
     *
     * @param set              使用了注解的集合
     * @param roundEnvironment 当前或之前的运行环境，可以通过该对象查找找到注解
     * @return true表示以后不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        messager.printMessage(Diagnostic.Kind.NOTE, "set size=：" + set.size());

        if (set.isEmpty()) return false;

        //获取所有注解的集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);

        if (elements != null && elements.size() > 0) {
            try {
                parseElements(elements);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    //解析元素集合
    private void parseElements(Set<? extends Element> elements) throws IOException {

        TypeElement typeActivity = elementUtil.getTypeElement(Constant.ACTIVITY);
        TypeElement typeActivityX = elementUtil.getTypeElement(Constant.ACTIVITYX);

        for (Element element : elements) {
            if (element == null) {
                messager.printMessage(Diagnostic.Kind.ERROR, "element null");

            }
            TypeMirror typeMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息：" + typeMirror.toString());

            ARouter aRouter = element.getAnnotation(ARouter.class);

            //路由详细信息封装到实体类

            String group = aRouter.group();
            String path = aRouter.path();
            Class<? extends Annotation> clazz = aRouter.annotationType();

            RouterBean bean = new RouterBean.Builder()
                    .setGroup(group)
                    .setPath(path)
                    .setElement(element)
                    .build();

            if (typeUtil.isSubtype(typeMirror, typeActivity.asType()) ||
                    typeUtil.isSubtype(typeMirror, typeActivityX.asType())) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "target type annotated is wrong");
                throw new RuntimeException("注解只能用于activity");
            }

            //创建临时map存储以上信息，用来遍历时生成代码

            valueOfPathMap(bean);

        }

        //ARouterLoadGroup和ARouterLoadPath的类型，用来生成类文件时，实现接口

        TypeElement groupLoadType = elementUtil.getTypeElement(Constant.AROUTER_GROUP);

        TypeElement pathLoadType = elementUtil.getTypeElement(Constant.AROUTER_PATH);

        //1、生成路由的详细path文件
        createPathFile(pathLoadType);

        //2、生成路由组group文件
        createGroupFile(groupLoadType, pathLoadType);
    }

    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {
        /**
         *  @Override
         *     public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
         *         Map<String,Class<? extends ARouterLoadPath>> groupMap=new HashMap<>();
         *         groupMap.put("guide",ARouter$$Path$$guide.class);
         *         return groupMap;
         *     }
         */

        TypeName methodReturn = ParameterizedTypeName
                .get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))));
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constant.GROUP_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(methodReturn);

        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            builder.addStatement("$T<$T,Class<? extends $T>> groupMap=new $T()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(pathLoadType),
                    HashMap.class);

            builder.addStatement("$N.put($S,$T.class)",
                    Constant.GROUP_PARAMETER_NAME,
                    entry.getKey(),
                    ClassName.get(packageNameForApt,entry.getValue()));

            builder.addStatement("return $N", Constant.GROUP_PARAMETER_NAME);

            String fileName =  Constant.GROUP_FILE_NAME+entry.getKey();
            JavaFile.builder(packageNameForApt,
                    TypeSpec
                            .classBuilder(fileName)
                            .addSuperinterface(ClassName.get(groupLoadType))//实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(builder.build())// 方法构建
                            .build()).build()
                    .writeTo(filer);

        }


    }

    private void createPathFile(TypeElement pathLoadType) throws IOException {

        if (tempPathMap.size() == 0) return;

        if (pathLoadType == null) {
            messager.printMessage(Diagnostic.Kind.ERROR, "pathLoadType==null");
        }

        TypeName methodReturn = ParameterizedTypeName
                .get(ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(RouterBean.class));
        //遍历分组，每个分组创建一个arouter类文件，如：ARouter$$Path$$app,ARouter$$Path$$guide
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            // 写方法体
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturn);

            //不循环部分 Map<String ,RouterBean> pathMap=new HashMap<>();
            methodBuilder.addStatement("$T<$T,$T> $N=new $T<>()", ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constant.PATH_PARAMETER_NAME,
                    ClassName.get(HashMap.class));

            // 循环部分
            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                // 方法内容循环部分
                /**
                 * pathMap.put("guide/GuideMainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                 *                  GuideMainActivity.class,
                 *                  "guide",
                 *                  "guide/GuideMainActivity"));
                 */

                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constant.PATH_PARAMETER_NAME,
                        bean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),
                        ClassName.get((TypeElement) bean.getElement()),
                        bean.getGroup(),
                        bean.getPath());
            }
            //return pathMap;
            methodBuilder.addStatement("return $N", Constant.PATH_PARAMETER_NAME);
            // 生成文件，ARouter$$Path$$app

            String finalName = Constant.PATH_FILE_NAME + entry.getKey();

            messager.printMessage(Diagnostic.Kind.NOTE, "apt 生成path文件>>> " + packageNameForApt + "." + finalName);

            JavaFile.builder(packageNameForApt,
                    TypeSpec
                            .classBuilder(finalName)
                            .addSuperinterface(ClassName.get(pathLoadType))//实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())// 方法构建
                            .build()).build()
                    .writeTo(filer);

            TypeElement element=elementUtil.getTypeElement(packageNameForApt + "." + finalName);

            tempGroupMap.put(entry.getKey(), finalName);

        }


    }

    private void valueOfPathMap(RouterBean bean) {
        messager.printMessage(Diagnostic.Kind.NOTE, "routerBean >>>" + bean.toString());

        if (checkRouterBean(bean)) {
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            if (routerBeans == null) {
                List<RouterBean> list = new ArrayList<>();
                list.add(bean);
                tempPathMap.put(bean.getGroup(), list);
            } else {
//                tempPathMap.put(bean.getGroup(), routerBeans);
                routerBeans.add(bean);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "path 未按规范书写");
        }


    }

    private boolean checkRouterBean(RouterBean bean) {

        String group = bean.getGroup();
        String path = bean.getPath();
        if (path == null || path.isEmpty() || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "path 未按规范书写,如/app/MainActivity");
            return false;
        }

        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "path 未按规范书写,如/app/MainActivity");
            return false;
        }

        String finalGroup = path.substring(1, path.lastIndexOf("/"));
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, finalGroup + ">>>path2 未按规范书写,如/app/MainActivity");

            return false;
        }

        if (group != null && group.length() > 0 && !moduleName.equalsIgnoreCase(group)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "group值必须与子模块moduleName相同");
            return false;
        }
        if (group == null || group.isEmpty()) {
            bean.setGroup(finalGroup);
        }
        return true;
    }
}
