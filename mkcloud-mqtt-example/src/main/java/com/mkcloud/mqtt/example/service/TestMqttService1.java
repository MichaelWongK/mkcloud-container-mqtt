package com.mkcloud.mqtt.example.service;

import com.mkcloud.mqtt.annotation.MkcloudMqttService;
import com.mkcloud.mqtt.core.MkcloudMqttTemplate;
import com.mkcloud.mqtt.service.MkcloudMqttServiceI;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/6/1 11:09
 * @Description
 */
@MkcloudMqttService(name = "testMqttService", value = {"mqtt-1", "mqtt-2"})
public class TestMqttService1 implements MkcloudMqttServiceI {
    public static final Logger LOGGER = LoggerFactory.getLogger(TestMqttService1.class);

    @Autowired
    private MkcloudMqttTemplate mkcloudMqttTemplate;

    @Override
    public void onMessage(String topic, MqttMessage message) {
        LOGGER.info(String.format("testMqttService received message-- topic:[%s], message:[%s]", topic, new String(message.getPayload())));
        mkcloudMqttTemplate.sendMessage("mqtt-3", "test1");
    }
}
