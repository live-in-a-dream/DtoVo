package com.demo.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import lombok.Data;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.ElementKindVisitor7;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author linghongkang
 * @description:
 * @create: 2018-12-13 15:40
 **/
@SupportedAnnotationTypes(value = "com.demo.processor.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MyProcessor extends AbstractProcessor {

    /**
     * 文件写入器，用于生成class文件
     */
    private Filer filer;
    /**
     * 元素操作工具
     */
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(EnabledDtoVo.class)) {
            EnabledDtoVo annotation = elem.getAnnotation(EnabledDtoVo.class);
            //注解-》类
            if(elem.getKind().isClass()){
                ArrayList<VariableElement> dtos = new ArrayList<>();
                ArrayList<VariableElement> vos = new ArrayList<>();

                TypeElement typeElement = (TypeElement) elem;

                for (Element e:typeElement.getEnclosedElements()){
                    //注解-》变量
                    if(e.getKind().isField()){
                        VariableElement variableElement = (VariableElement) e;
                        Dto dto = variableElement.getAnnotation(Dto.class);
                        Vo vo = variableElement.getAnnotation(Vo.class);
                        DtoVo dtoVo = variableElement.getAnnotation(DtoVo.class);

                        if(dto != null){
                            dtos.add(variableElement);
                        }else if(vo != null){
                            vos.add(variableElement);
                        } else if(dtoVo != null){
                            dtos.add(variableElement);
                            vos.add(variableElement);
                        }
                    }
                }

                createFile("Vo",typeElement,vos);
                createFile("Dto",typeElement,dtos);
            }
        }

        return true;
    }

    /**
     * 创建Dto和Vo Class文件
     * @Author LXC
     * @Date 2022/1/27 22:37
     **/
    public void createFile(String DtoVo,TypeElement typeElement,ArrayList<VariableElement> variableElements){
        ArrayList<FieldSpec> arrayList = new ArrayList<>();

        String fileName = typeElement.getSimpleName().toString();

        for (VariableElement d : variableElements) {
            FieldSpec field = FieldSpec.builder(TypeName.get(d.asType()),d.getSimpleName().toString()).addModifiers(d.getModifiers().toArray(Modifier[]::new)).build();
            arrayList.add(field);
        }

        // 类名则为 superSimpleName + Factory
        TypeSpec typeSpec = TypeSpec.classBuilder(fileName+DtoVo)
                .addModifiers(Modifier.PUBLIC)
                .addFields(arrayList)
                .addAnnotation(Data.class)
                .build();

        // 5.写入java文件
        // 获取到包名信息
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString()+"."+DtoVo.toLowerCase();
        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
