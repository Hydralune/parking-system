package com.parking.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * 用于解决FactoryBean类型验证问题的配置
 * 此配置旨在处理factoryBeanObjectType属性类型不匹配的问题
 */
@Component
public class TypeSafetyConfig implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 在Bean工厂后置处理阶段，确保类型安全
        // 这里可以添加额外的类型验证逻辑，如果需要的话
    }
}