package com.mkcloud.mqtt.core;

import com.mkcloud.mqtt.message.MkcloudMqttMessageDrivenChannelAdapter;
import com.mkcloud.mqtt.message.MkcloudMqttMessageHandler;
import com.mkcloud.mqtt.service.MkcloudMqttServiceI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mqtt topic订阅器
 *
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/6/1 8:54
 * @Description
 */
public class MkcloudMqttSubscribe {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MkcloudMqttSubscribe.class);

    private MkcloudMqttMessageDrivenChannelAdapter adapter;
    private MkcloudMqttMessageHandler handler;

    public MkcloudMqttSubscribe(MkcloudMqttMessageDrivenChannelAdapter adapter, MkcloudMqttMessageHandler handler) {
        this.adapter = adapter;
        this.handler = handler;
    }

    public void sub(MkcloudMqttServiceI service, String... topic) {
        LOGGER.debug("添加对主题" + topic + "的监听");
        adapter.addTopic(topic);
        handler.adService(topic, service);
    }

    public void unsub(String... topic) {
        LOGGER.debug("移除对主题" + topic + "的监听");
        adapter.removeTopic(topic);
        handler.removeService(topic);
    }
}
