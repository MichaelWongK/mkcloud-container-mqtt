package com.mkcloud.mqtt.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/4/14 15:32
 * @Description  基础服务工具类对象
 */
@Slf4j
@Component
public class MkcloudApplicationUtil implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MkcloudApplicationUtil.class);

    public static final String BASE_PACKAGE = "mkcloud.application.base-package";

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext != null) {
            MkcloudApplicationUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取当前容器上下文对象
     *
     * @return 容器上下文对象
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 查找当前容器中bean实例
     *
     * @param clazz         实例类型
     * @param defaultValue  默认值
     * @param <T>           泛型
     * @return 实例
     */
    public static <T> T getBean(Class<T> clazz, T defaultValue) {
        Assert.notNull(clazz, "class must not be null");
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            LOGGER.warn("当前容器不存在类型为[{}]的实例对象.", clazz.getCanonicalName());
            return defaultValue;
        }
    }

    private static void checkApplicationContext() {
        if (Objects.isNull(applicationContext)) {
            throw new RuntimeException("applicationContext未注入,未找到微服务上下文,当前容器未初始化完成.");
        }
    }

    public static Collection<String>getBasePackages(BeanFactory beanFactory, Environment environment) {
        Collection<String> basePackages = AutoConfigurationPackages.has(beanFactory) ?
                AutoConfigurationPackages.get(beanFactory) :
                Collections.emptyList();
        String configuredBasePackage = environment.getProperty(BASE_PACKAGE);
        if (StringUtils.isNotBlank(configuredBasePackage)) {
            if (basePackages.isEmpty()) {
                basePackages =Collections.singleton(configuredBasePackage);
            } else {
                basePackages.add(configuredBasePackage);
            }
        }
        return basePackages;
    }



}
