package com.mkcloud.mqtt.autoconfiguration;

import com.mkcloud.mqtt.core.MkcloudMqttSubscribe;
import com.mkcloud.mqtt.core.MkcloudMqttTemplate;
import com.mkcloud.mqtt.properties.MkcloudMqttProperties;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * mqtt生产者及客户端自动配置
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/31 10:00
 */
@Configuration
@EnableConfigurationProperties(MkcloudMqttProperties.class)
public class MkcloudMqttOutboundConfiguration {

    /**
     * 创建mqtt client 工厂bean。
     *
     * @param mkcloudMqttProperties mqtt配置
     * @return mqtt client 工厂
     */
    @Bean
    @ConditionalOnProperty(prefix = "mkcloud.mqtt", value = {"url", "enabled"})
    @ConditionalOnMissingBean(MqttPahoClientFactory.class)
    public MqttPahoClientFactory clientFactory(MkcloudMqttProperties mkcloudMqttProperties) {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(mkcloudMqttProperties.isCleanSession());
        options.setUserName(mkcloudMqttProperties.getUsername());
        options.setPassword(mkcloudMqttProperties.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(mkcloudMqttProperties.getKeepAliveInterval());
        options.setConnectionTimeout(mkcloudMqttProperties.getConnectionTimeout());
        options.setServerURIs(mkcloudMqttProperties.getUrl().split(","));
        clientFactory.setConnectionOptions(options);
        MkcloudMqttProperties.Will will = mkcloudMqttProperties.getWill();
        if (will != null && will.isEnabled()) {
            Assert.state(StringUtils.isNotBlank(will.getTopic()), "遗嘱消息的topic不能为空");
            Assert.state(StringUtils.isNotBlank(will.getPayload()), "遗嘱消息的payload不能为空");
            options.setWill(will.getTopic(), will.getPayload().getBytes(StandardCharsets.UTF_8), will.getQos(), will.isRetained());
        }
        return clientFactory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler outbound(MkcloudMqttProperties mkcloudMqttProperties, MqttPahoClientFactory clientFactory) {
        String clientId = mkcloudMqttProperties.getProducer() == null ? null :
                mkcloudMqttProperties.getProducer().getClientId();
        if (StringUtils.isBlank(clientId)) {
            clientId = mkcloudMqttProperties.getClientId();
            if (StringUtils.isBlank(clientId)) {
                throw new IllegalArgumentException("'mkcloud.mqtt.clientId'和'mkcloud.mqtt.producer.client'必须配置一个");
            }
        }
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId, clientFactory);
        handler.setAsync(mkcloudMqttProperties.isAsync());
        handler.setDefaultQos(mkcloudMqttProperties.getDefaultQos());
        handler.setCompletionTimeout(mkcloudMqttProperties.getCompletionTimeout());
        return handler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @DependsOn({"mqttOutboundChannel", "mkcloudMqttProperties"})
    public MkcloudMqttTemplate mkcloudMqttTemplate(
            MkcloudMqttProperties mkcloudMqttProperties, MessageChannel mqttOutboundChannel) {
        MkcloudMqttTemplate mkcloudMqttTemplate = new MkcloudMqttTemplate(mkcloudMqttProperties,
                new DefaultPahoMessageConverter());
        mkcloudMqttTemplate.setOutputChannel(mqttOutboundChannel);
        return mkcloudMqttTemplate;
    }

}
