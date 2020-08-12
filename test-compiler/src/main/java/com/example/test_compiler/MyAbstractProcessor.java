package com.example.test_compiler;

import com.example.test_annotation.MyRouter;
import com.example.test_annotation.MyTestAnnotation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
public class MyAbstractProcessor extends AbstractProcessor {
    // 文件相关辅助类
    private Filer mFiler;
    // 日志相关辅助类
    private Messager mMessager;
    private Elements elementUtils;
    private Types types;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        elementUtils =processingEnvironment.getElementUtils();
        types =processingEnvironment.getTypeUtils();
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations=new LinkedHashSet<>();
        annotations.add(MyRouter.class.getCanonicalName());
        return annotations;
    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set!=null&& set.size()>0 ) {
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(MyRouter.class);
            try {
                this.parseRoutes(routeElements);

            } catch (Exception e) {

            }
            return true;
        }
        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) {
        TypeMirror type_Activity = elementUtils.getTypeElement("android.app.Activity").asType();

         /*
               Build input type, format as :

               ```Map<String,A.Class>```
             */
        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(Class.class)
        );



        ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();

              /*
              Build method : 'init'
             */
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);
        for(Element element:routeElements){
            TypeMirror tm = element.asType();
            MyRouter route = element.getAnnotation(MyRouter.class);
            if (types.isSubtype(tm,type_Activity)){
                ClassName className = ClassName.get((TypeElement) element);
                loadIntoMethodOfRootBuilder.addStatement("routes.put(\""+route.router()+"\","+className+".class)");
            }
        }

        TypeSpec myRouterTest = TypeSpec.classBuilder("MyRouterTest")
                .addModifiers(PUBLIC, Modifier.FINAL)
                .addMethod(loadIntoMethodOfRootBuilder.build())
                .build();
        JavaFile javaFile = JavaFile.builder("com.example.myTest", myRouterTest)
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
        }
    }
}
