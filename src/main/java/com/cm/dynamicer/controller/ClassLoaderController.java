package com.cm.dynamicer.controller;


/*
 * 	此源代码为北京圣博润高新技术股份有限公司资产，非北京圣博润
 * 高新技术股份有限公司公司员工严禁保留、拷贝、修改此代码。
 *
 * Copyright 北京圣博润高新技术股份有限公司. All rights reserved.
 */

/*
 * @ClassName:ClassLoaderController
 * @Description TODO
 * @Author 曹传红
 * @Time 2019-06-18 16:16
 */

import com.cm.dynamicer.classloader.ClassloaderResponsity;
import com.cm.dynamicer.classloader.ModuleClassLoader;
import com.cm.dynamicer.util.SpringContextUtil;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/loader")
public class ClassLoaderController {


    @GetMapping(value = "/beans")
    public List<Map<String, Object>> beans(){
        return SpringContextUtil.getAllBean();
    }

    @GetMapping(value = "/deleteModule")
    public List<Map<String, Object>> deleteModule(String moduleName){
        if(ClassloaderResponsity.getInstance().containsClassLoader(moduleName)){
            ClassloaderResponsity.getInstance().removeClassLoader(moduleName);
        }
        return beans();
    }

    @GetMapping(value = "/loadJar")
    public List<?> loadJar(String jarPath){
        File jar = new File(jarPath);
        URI uri = jar.toURI();
        String moduleName = jarPath.substring(jarPath.lastIndexOf("/")+1,jarPath.lastIndexOf("."));
        try {

            if(ClassloaderResponsity.getInstance().containsClassLoader(moduleName)){
                ClassloaderResponsity.getInstance().removeClassLoader(moduleName);
            }

            ModuleClassLoader classLoader = new ModuleClassLoader(new URL[]{uri.toURL()}, Thread.currentThread().getContextClassLoader());
            SpringContextUtil.getBeanFactory().setBeanClassLoader(classLoader);
            Thread.currentThread().setContextClassLoader(classLoader);
            classLoader.initBean();
            ClassloaderResponsity.getInstance().addClassLoader(moduleName,classLoader);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SpringContextUtil.getAllBean();
    }

    @GetMapping(value = "/invoke")
    public Object invokeBean(String beanName){
        Method method = ReflectionUtils.findMethod(SpringContextUtil.getBean(beanName).getClass(), "users");
        Object result = ReflectionUtils.invokeMethod(method, SpringContextUtil.getBean(beanName));
        return result;
    }

}
