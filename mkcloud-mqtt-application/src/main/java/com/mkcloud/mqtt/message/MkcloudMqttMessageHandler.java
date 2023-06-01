package com.mkcloud.mqtt.message;

import com.mkcloud.mqtt.annotation.MkcloudMqttService;
import com.mkcloud.mqtt.constants.MqttQos;
import com.mkcloud.mqtt.properties.MkcloudMqttProperties;
import com.mkcloud.mqtt.service.MkcloudMqttServiceBeanRegister;
import com.mkcloud.mqtt.service.MkcloudMqttServiceI;
import com.mkcloud.mqtt.util.MkcloudApplicationUtil;
import com.sun.xml.internal.txw2.IllegalAnnotationException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessagingException;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 统一Mqtt消息处理器
 * {@link MkcloudMqttMessageHandler} 需要在 {@link MkcloudMqttServiceBeanRegister} 之后加载,
 * 获取被{@link MkcloudMqttService}修饰的且实现了{@link MkcloudMqttServiceI}接口的的bean,
 * 将{@link MkcloudMqttService@value()}中定义的topic与对应的bean绑定,监听到topic消息会执行
 * {@link MkcloudMqttServiceI#onMessage(String, MqttMessage)}方法
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/30 17:04
 */
public class MkcloudMqttMessageHandler implements SmartInitializingSingleton, MessageHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MkcloudMqttMessageHandler.class);

    private Map<String, List<MkcloudMqttServiceI>> mqttServiceMap = new ConcurrentHashMap<>();

    private MessageProcessor<String> topicProcessor =
            message -> message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);

    private boolean started = false;

    private int defaultQos;

    private boolean defaultRetain;

    private DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter(this.defaultQos,
            MqttMessageConverter.defaultQosProcessor(), this.defaultRetain, MqttMessageConverter.defaultRetainedProcessor());

    public MkcloudMqttMessageHandler(MkcloudMqttProperties mkcloudMqttProperties) {
        super();
        int defaultQos = -1;
        if (mkcloudMqttProperties.getConsumer() != null) {
            defaultQos = mkcloudMqttProperties.getConsumer().getDefaultQos();
            this.defaultRetain = mkcloudMqttProperties.getConsumer().isDefaultRetained();
        } else {
            this.defaultRetain = mkcloudMqttProperties.isDefaultRetained();
        }
        if (defaultQos < 0) {
            defaultQos = mkcloudMqttProperties.getDefaultQos();
            if (defaultQos < 0) {
                defaultQos = MqttQos.AT_LEAST_ONCE.getCode();
            }
        }
        this.defaultQos = defaultQos;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> serviceBeans = MkcloudApplicationUtil.getApplicationContext().getBeansWithAnnotation(MkcloudMqttService.class);
        if (!CollectionUtils.isEmpty(serviceBeans)) {
            serviceBeans.forEach((serviceBeanName, serviceBean) -> {
                Class<?> clazz = AopProxyUtils.ultimateTargetClass(serviceBean);
                if (MkcloudMqttServiceI.class.isAssignableFrom(clazz)) {
                    MkcloudMqttService annotation = clazz.getAnnotation(MkcloudMqttService.class);
                    String[] topics = annotation.value();
                    if (topics == null || topics.length == 0) {
                        throw new IllegalArgumentException("类" + serviceBean.getClass() + "上的注解@MkcloudMqttService的value注解不能为空");
                    }
                    for (String topic : topics) {
                        List<MkcloudMqttServiceI> serviceList = mqttServiceMap.get(topic);
                        if (serviceList == null) {
                            serviceList = new ArrayList<>();
                            this.mqttServiceMap.put(topic, serviceList);
                        }
                        serviceList.add((MkcloudMqttServiceI) serviceBean);
                    }
                } else {
                    throw new IllegalAnnotationException("被 @XlcloudMqttService 修饰的类必须实现 XlcloudMqttServiceI 接口");
                }
            });
        }
        this.started = true;
    }

    /**
     * 将消息转化为mqtt类型的消息，并根据topic分发处理，执行{@link MkcloudMqttServiceI#onMessage(String, MqttMessage)}方法
     * @param message
     * @throws MessagingException
     */
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MqttMessage mqttMessage = this.converter.fromMessage(message, Object.class);
        String topic = this.topicProcessor.processMessage(message);
        if (topic == null) {
            throw new MessageHandlingException(message,
                    "No topic could be determined from the message and no default topic defined");
        }
        if (this.started) {
            List<MkcloudMqttServiceI> serviceList = mqttServiceMap.get(topic);
            for (MkcloudMqttServiceI mqttService : serviceList) {
                mqttService.onMessage(topic, mqttMessage);
            }
        }
    }

    public void adService(String[] topics, MkcloudMqttServiceI service) {
        for (String topic : topics) {
            LOGGER.debug("添加主题: " + topic + "的监听服务");
            List<MkcloudMqttServiceI> serviceList = mqttServiceMap.get(topic);
            if (serviceList != null) {
                serviceList.add(service);
                continue;
            }
            mqttServiceMap.put(topic, Collections.singletonList(service));
        }
    }

    public void removeService(String... topics) {
        for (String topic : topics) {
            LOGGER.debug("移除主题：" + topic + "的监听服务");
            mqttServiceMap.remove(topic);
        }
    }
}
