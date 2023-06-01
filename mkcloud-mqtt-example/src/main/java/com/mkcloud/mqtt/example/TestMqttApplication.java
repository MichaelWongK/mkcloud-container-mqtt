package com.mkcloud.mqtt.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/6/1 11:20
 * @Description
 */
@SpringBootApplication(scanBasePackages = {"com.mkcloud.**", "${mkcloud.application.base-package:}"})
public class TestMqttApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestMqttApplication.class, args);
    }
}
