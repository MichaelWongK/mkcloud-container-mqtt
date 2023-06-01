package com.mkcloud.mqtt.service;

import com.mkcloud.mqtt.annotation.MkcloudMqttService;
import com.mkcloud.mqtt.util.MkcloudApplicationUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;

/**
 * 从basePackage中扫描注解，注册被{@link com.mkcloud.mqtt.annotation.MkcloudMqttService}
 * 修饰的bean到容器中
 *
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/30 11:49
 * @Description
 */
public class MkcloudMqttServiceBeanRegister implements ImportBeanDefinitionRegistrar,
        BeanFactoryAware, EnvironmentAware, ResourceLoaderAware {

    private BeanFactory beanFactory;

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Collection<String> basePackages = MkcloudApplicationUtil.getBasePackages(beanFactory, environment);
        if (basePackages.isEmpty()) {
            basePackages = Collections.singleton(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(MkcloudMqttService.class, true));
        Set<String> registerBeanClassName = new HashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                String beanClassName = candidateComponent.getBeanClassName();
                if (registerBeanClassName.contains(beanClassName)) {
                    //可能basePackage配置的有重复，防止同一个类注入两次
                    continue;
                }

                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
                    if (metadata != null && metadata.isAnnotated(MkcloudMqttService.class.getName())) {
                        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(MkcloudMqttService.class.getName());
                        String beanName = null;
                        if (annotationAttributes.containsKey("name")) {
                            beanName = (String) annotationAttributes.get("name");
                        }
                        if (StringUtils.isBlank(beanName)) {
                            // TODO 截取bean名称
                            beanName = beanClassName;
                        }
                        registry.registerBeanDefinition(beanName, annotatedBeanDefinition);
                        registerBeanClassName.add(beanName);
                    }
                }
            }
        }


    }
}
