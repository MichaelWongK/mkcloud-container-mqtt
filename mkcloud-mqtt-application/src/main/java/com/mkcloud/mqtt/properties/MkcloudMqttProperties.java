package com.mkcloud.mqtt.properties;

import com.mkcloud.mqtt.constants.MqttQos;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:wangmk13@163.com">micheal.wang</a>
 * @date 2023/4/17 9:29
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "mkcloud.mqtt")
public class MkcloudMqttProperties {

    private boolean enable = false;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     *  默认的服务质量，发布消息的交付级别，0为最多一次，1为至少一次，2为精确一次
     */
    private int defaultQos;
    /**
     * mqtt broker 地址，多个用','隔开
     */
    private String url;
    /**
     * 默认主题
     */
    private String defaultTopic;
    /**
     *  客户端id,如果两个clientId相同，新的client连接时，broker端会断开上一个同样clientId的连接。
     *  建议分别配置consumer和producer的clientId，否则会因为clientId相同导致consumer和producer频繁断开连接。
     *  官网给出的demo中consumer和producer中也使用了不同的clientId
     *  参见 https://github.com/spring-projects/spring-integration-samples/blob/master/basic/mqtt/src/main/java/org/springframework/integration/samples/mqtt/Application.java 。
     *  尝试过重写 {@link org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory#getClientInstance(String, String)}
     *  方法，改为统一client和uri返回相同的client实例，但会出现在收发消息时产生 {@link InterruptedException}
     */
    private String clientId;
    /**
     * 是否清除会话
     */
    private boolean cleanSession;
    /**
     *心跳间隔时间
     */
    private int keepAliveInterval;
    /**
     *  超时时间
     */
    private int connectionTimeout;
    /**
     *  是否异步发送
     */
    private boolean async;

    private int completionTimeout;
    /**
     *  默认的保留标志， 仅用于PUBLISH消息，当客户端发送消息给broker将此标志置为1时，broker将保留这条消息，
     *  然后当有新的订阅者订阅这个topic时，broker将会将最后一条保留消息发送给该订阅者，
     *  对于这个消息被保留前就已经订阅的订阅者，broker发送消息时会将此标志位置为0。
     */
    private boolean defaultRetained = false;

    /**
     *  消费者配置项
     */
    private MkcloudMqttConsumer consumer;
    /**
     *  生产者配置项
     */
    private MkcloudMqttProducer producer;
    /**
     *  遗嘱消息配置项
     */
    private Will will;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDefaultQos() {
        return defaultQos;
    }

    public void setDefaultQos(int defaultQos) {
        this.defaultQos = defaultQos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }

    public void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public int getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(int completionTimeout) {
        this.completionTimeout = completionTimeout;
    }

    public boolean isDefaultRetained() {
        return defaultRetained;
    }

    public void setDefaultRetained(boolean defaultRetained) {
        this.defaultRetained = defaultRetained;
    }

    public MkcloudMqttConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MkcloudMqttConsumer consumer) {
        this.consumer = consumer;
    }

    public MkcloudMqttProducer getProducer() {
        return producer;
    }

    public void setProducer(MkcloudMqttProducer producer) {
        this.producer = producer;
    }

    public Will getWill() {
        return will;
    }

    public void setWill(Will will) {
        this.will = will;
    }

    public static class Will {
        private boolean enabled = false;
        private String topic;
        private int qos = MqttQos.AT_LEAST_ONCE.getCode();
        private boolean retained = false;
        private String payload;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public int getQos() {
            return qos;
        }

        public void setQos(int qos) {
            this.qos = qos;
        }

        public boolean isRetained() {
            return retained;
        }

        public void setRetained(boolean retained) {
            this.retained = retained;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

    /**
     * 消费者配置
     */
    public static class MkcloudMqttConsumer {
        /**
         * @see MkcloudMqttProperties#clientId
         */
        private String clientId;
        /**
         * @see MkcloudMqttProperties#defaultQos
         */
        private int defaultQos = -1;
        /**
         * @see MkcloudMqttProperties#defaultRetained
         */
        private boolean defaultRetained;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public int getDefaultQos() {
            return defaultQos;
        }

        public void setDefaultQos(int defaultQos) {
            this.defaultQos = defaultQos;
        }

        public boolean isDefaultRetained() {
            return defaultRetained;
        }

        public void setDefaultRetained(boolean defaultRetained) {
            this.defaultRetained = defaultRetained;
        }
    }

    /**
     * 生产者配置
     */
    public static class MkcloudMqttProducer {
        /**
         * @see MkcloudMqttProperties#clientId
         */
        private String clientId;
        /**
         * @see MkcloudMqttProperties#defaultQos
         */
        private int defaultQos = -1;
        /**
         * @see MkcloudMqttProperties#defaultRetained
         */
        private boolean defaultRetained;
        /**
         * @see MkcloudMqttProperties#defaultTopic
         */
        private String defaultTopic;
        /**
         *  是否重复，当客户端或服务器尝试重发PUBLISH，PUBREL，SUBSCRIBE或UNSUBSCRIBE消息时为 {@code true}，
         *  ，适用于QoS值大于零。当 {@link defaultDuplicate} 为 {@code true}时，变量头包括消息ID。
         */
        private boolean defaultDuplicate;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public int getDefaultQos() {
            return defaultQos;
        }

        public void setDefaultQos(int defaultQos) {
            this.defaultQos = defaultQos;
        }

        public boolean isDefaultRetained() {
            return defaultRetained;
        }

        public void setDefaultRetained(boolean defaultRetained) {
            this.defaultRetained = defaultRetained;
        }

        public String getDefaultTopic() {
            return defaultTopic;
        }

        public void setDefaultTopic(String defaultTopic) {
            this.defaultTopic = defaultTopic;
        }

        public boolean isDefaultDuplicate() {
            return defaultDuplicate;
        }

        public void setDefaultDuplicate(boolean defaultDuplicate) {
            this.defaultDuplicate = defaultDuplicate;
        }
    }
}
