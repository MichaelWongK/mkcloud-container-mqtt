package com.mkcloud.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt消费者重写, 重写{@link #onMessage(String, MqttMessage)}方法进行消息的处理
 *
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/30 14:20
 */
public interface MkcloudMqttServiceI {

    /**
     * 收到消息进行处理
     * @param topic 消息主题
     * @param message 消息内容
     */
    void onMessage(String topic, MqttMessage message);
}
