package com.mkcloud.mqtt.constants;

/**
 * mqtt 消息的服务质量
*/
public enum MqttQos {
    /**
     *  最多一次，可能丢失消息
    */
    AT_MOST_ONCE(0),
    /**
     *  至少一次，可能重复消息
     */
    AT_LEAST_ONCE(1),
    /**
     *  精确一次
     */
    EXACTLY_ONCE(2);

    private int code;

    MqttQos(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
