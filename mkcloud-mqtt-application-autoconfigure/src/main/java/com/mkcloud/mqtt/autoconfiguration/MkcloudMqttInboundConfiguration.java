package com.mkcloud.mqtt.autoconfiguration;

import com.mkcloud.mqtt.core.MkcloudMqttSubscribe;
import com.mkcloud.mqtt.message.MkcloudMqttMessageDrivenChannelAdapter;
import com.mkcloud.mqtt.message.MkcloudMqttMessageHandler;
import com.mkcloud.mqtt.properties.MkcloudMqttProperties;
import com.mkcloud.mqtt.service.MkcloudMqttServiceBeanRegister;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageHandler;

/**
 * Mqtt消费者自动配置
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/31 10:08
 */
@Configuration
@AutoConfigureAfter(MkcloudMqttOutboundConfiguration.class)
@Import({MkcloudMqttServiceBeanRegister.class})
public class MkcloudMqttInboundConfiguration {


    /**
     * 创建用于接收消息并将消息转到 {@link #inbound}的适配器bean
     * @param mkcloudMqttProperties mqtt配置
     * @param clientFactory client工厂
     * @return 消息源实体
     */
    @Bean
    public MkcloudMqttMessageDrivenChannelAdapter inboundProducer(
            MkcloudMqttProperties mkcloudMqttProperties, MqttPahoClientFactory clientFactory) {
        String clientId = mkcloudMqttProperties.getConsumer() == null ? null :
                mkcloudMqttProperties.getConsumer().getClientId();
        if (StringUtils.isEmpty(clientId)){
            clientId = mkcloudMqttProperties.getClientId();
            if (StringUtils.isEmpty(clientId)){
                throw new IllegalArgumentException("'xlcloud.mqtt.clientId'和'xlcloud.mqtt.connsumer.client'必须配置一个");
            }
        }
        MkcloudMqttMessageDrivenChannelAdapter adapter = new MkcloudMqttMessageDrivenChannelAdapter(clientId, clientFactory);
        adapter.setCompletionTimeout(3000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }

    /**
     * 创建消费者流程bean
     * @param inboundProducer 消息源
     * @author inbound 消息处理器
     * @return 流程实体
     */
    @Bean
    public IntegrationFlow mqttInFlow(MessageProducerSupport inboundProducer,
            @Qualifier("inbound") @Autowired() MessageHandler inbound) {
        return IntegrationFlows.from(inboundProducer)
                .handle(inbound)
                .get();
    }

    /**
     * 创建用于接收消息的MessageHandler bean
     * @param mkcloudMqttProperties mqtt配置
     * @return 消息处理器实体
     */
    @Bean
    public MkcloudMqttMessageHandler inbound(MkcloudMqttProperties mkcloudMqttProperties) {
        MkcloudMqttMessageHandler messageHandler = new MkcloudMqttMessageHandler(mkcloudMqttProperties);
        return messageHandler;
    }

    @Bean
    public MkcloudMqttSubscribe mkcloudMqttSubscribe(
            MkcloudMqttMessageDrivenChannelAdapter adapter, MkcloudMqttMessageHandler handler) {
        return new MkcloudMqttSubscribe(adapter, handler);
    }
}
