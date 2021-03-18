package com.cm.dynamicer.classloader;


/*
 * 	此源代码为北京圣博润高新技术股份有限公司资产，非北京圣博润
 * 高新技术股份有限公司公司员工严禁保留、拷贝、修改此代码。
 *
 * Copyright 北京圣博润高新技术股份有限公司. All rights reserved.
 */

/*
 * @ClassName:ClassloaderResponsity
 * @Description TODO
 * @Author 曹传红
 * @Time 2019-06-18 14:45
 */

import com.cm.dynamicer.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassloaderResponsity {

    private static Logger logger = LoggerFactory.getLogger(ClassloaderResponsity.class);

    private ClassloaderResponsity(){}


    private Map<String,ModuleClassLoader> responsityMap = new ConcurrentHashMap<>();



    public void addClassLoader(String moduleName,ModuleClassLoader moduleClassLoader){
        responsityMap.put(moduleName,moduleClassLoader);
    }

    public boolean containsClassLoader(String key){
        return responsityMap.containsKey(key);
    }

    public ModuleClassLoader getClassLoader(String key){
        return responsityMap.get(key);
    }

    public void removeClassLoader(String moduleName){
        ModuleClassLoader moduleClassLoader = responsityMap.get(moduleName);
        try {
            List<String> registeredBean = moduleClassLoader.getRegisteredBean();

            for (String beanName : registeredBean) {
                logger.info("删除bean:"+beanName);
                SpringContextUtil.getBeanFactory().removeBeanDefinition(beanName);
            }
            
            moduleClassLoader.close();
            responsityMap.remove(moduleName);
            
        } catch (IOException e) {
            logger.error("删除"+moduleName+"模块发生错误");
        }
    }





    private static class ClassloaderResponsityHodler{
        private static ClassloaderResponsity instamce = new ClassloaderResponsity();
    }

    public static ClassloaderResponsity getInstance(){
        return ClassloaderResponsityHodler.instamce;
    }

}
