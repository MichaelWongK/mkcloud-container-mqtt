package com.mkcloud.mqtt.message;

import com.mkcloud.mqtt.annotation.MkcloudMqttService;
import com.mkcloud.mqtt.service.MkcloudMqttServiceI;
import com.mkcloud.mqtt.util.MkcloudApplicationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 继承自{@link MqttPahoMessageDrivenChannelAdapter}, 接收到消息并将消息转发到 {@link #outputChannel}
 *
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/5/31 8:55
 */
public class MkcloudMqttMessageDrivenChannelAdapter extends MqttPahoMessageDrivenChannelAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MkcloudMqttMessageDrivenChannelAdapter.class);

    public MkcloudMqttMessageDrivenChannelAdapter() {
        super(null, new DefaultMqttPahoClientFactory());
    }

    public MkcloudMqttMessageDrivenChannelAdapter(String clientId, MqttPahoClientFactory clientFactory, String... topic) {
        super(clientId, clientFactory, topic);
    }

    /**
     * 在父类的{@link org.springframework.integration.context.IntegrationObjectSupport#afterPropertiesSet}方法之前执行，
     * 添加basePackages下的所有{@link MkcloudMqttService}注解中包含的topic到父类的
     * {@link org.springframework.integration.mqtt.inbound.AbstractMqttMessageDrivenChannelAdapter#topics}中
     */
    @PostConstruct
    public void init() {
        Map<String, Object> serviceBeans = MkcloudApplicationUtil.getApplicationContext().getBeansWithAnnotation(MkcloudMqttService.class);
        if (!CollectionUtils.isEmpty(serviceBeans)) {
            serviceBeans.forEach((serviceBeanName, serviceBean) -> {
                Class<?> clazz = AopProxyUtils.ultimateTargetClass(serviceBean);
                if (MkcloudMqttServiceI.class.isAssignableFrom(clazz)) {
                    MkcloudMqttService mqttService = clazz.getAnnotation(MkcloudMqttService.class);
                    String[] topics = mqttService.value();
                    Assert.state(topics != null, String.format("类%s上的注解 @XlcloudMqttService 的 value 属性不能为空", serviceBean.getClass()));
                    addTopic(topics);
                }
            });
        }
    }
}
