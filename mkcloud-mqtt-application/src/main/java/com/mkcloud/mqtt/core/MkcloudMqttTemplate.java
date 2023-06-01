package com.mkcloud.mqtt.core;

import com.mkcloud.mqtt.constants.MqttQos;
import com.mkcloud.mqtt.properties.MkcloudMqttProperties;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

/**
 * 支持Mqtt消息发送
 *
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/6/1 9:02
 */
public class MkcloudMqttTemplate extends MessageProducerSupport {
    private volatile MqttMessageConverter converter;
    private int defaultQos;
    private String defaultTopic;
    private boolean defaultRetained;
    private boolean defaultDuplicate ;

    public MkcloudMqttTemplate(MkcloudMqttProperties mkcloudMqttProperties, MqttMessageConverter converter) {
        super();
        this.converter = converter;
        String defaultTopic = null;
        int defaultQos = -1;
        boolean defaultRetained = false;
        boolean defaultDuplicate = false;
        if (mkcloudMqttProperties.getProducer() != null) {
            defaultQos = mkcloudMqttProperties.getProducer().getDefaultQos();
            defaultTopic = mkcloudMqttProperties.getProducer().getDefaultTopic();
            defaultRetained = mkcloudMqttProperties.getProducer().isDefaultRetained();
            defaultDuplicate = mkcloudMqttProperties.getProducer().isDefaultDuplicate();
        } else {
            this.defaultRetained = mkcloudMqttProperties.isDefaultRetained();
        }
        if (defaultQos < 0) {
            defaultQos = mkcloudMqttProperties.getDefaultQos();
            if (defaultQos < 0) {
                defaultQos = MqttQos.AT_LEAST_ONCE.getCode();
            }
        }
        if (StringUtils.isBlank(defaultTopic)) {
            defaultTopic = mkcloudMqttProperties.getDefaultTopic();
        }
        this.defaultQos = defaultQos;
        this.defaultTopic = defaultTopic;
        this.defaultRetained = defaultRetained;
        this.defaultDuplicate = defaultDuplicate;
        this.converter = converter;
    }

    public MqttMessageConverter getConverter() {
        return this.converter;
    }

    public void sendMessage(String topic, MqttMessage mqttMessage) {
        if (StringUtils.isBlank(topic)) {
            topic = this.defaultTopic;
        }
        Message<?> message = this.getConverter().toMessage(topic, mqttMessage);
        super.sendMessage(message);
    }

    public <T> void sendMessage(String topic, T payload, Integer qos, Boolean isDuplicate, Boolean isRetained) {
        if (StringUtils.isBlank(topic)) {
            topic = this.defaultTopic;
        }
        Message<T> message = new GenericMessage<>(payload);
        AbstractIntegrationMessageBuilder<T> messageBuilder = getMessageBuilderFactory().fromMessage(message);
        messageBuilder.setHeader(MqttHeaders.QOS, qos)
                .setHeader(MqttHeaders.DUPLICATE, isDuplicate)
                .setHeader(MqttHeaders.RETAINED, isRetained);
        if (topic != null) {
            messageBuilder.setHeader(MqttHeaders.TOPIC, topic);
        }
        super.sendMessage(messageBuilder.build());
    }

    public <T> void sendMessage(String topic, T payload, Integer qos) {
        sendMessage(topic, payload, qos, this.defaultDuplicate, this.defaultRetained);
    }

    public <T> void sendMessage(String topic, T payload) {
        if (StringUtils.isBlank(topic)) {
            topic = this.defaultTopic;
        }
        if (payload instanceof MqttMessage) {
            sendMessage(topic, (MqttMessage) payload);
            return;
        }
        if (payload instanceof Message) {
            super.sendMessage((Message) payload);
            return;
        }
        sendMessage(topic, payload, this.defaultQos, this.defaultDuplicate, this.defaultRetained);
    }

}