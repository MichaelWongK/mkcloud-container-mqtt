package com.mkcloud.mqtt.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/30 14:03
 * @Description mqtt消费服务注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MkcloudMqttService {

    String name() default "";

    /**
     * 监听的Topic数组
     * @return
     */
    String[] value() default {};

    /**
     * qos如果是-1，取${mkcloud.mqtt.defaultQos}
     */
    int qos() default -1;
}
