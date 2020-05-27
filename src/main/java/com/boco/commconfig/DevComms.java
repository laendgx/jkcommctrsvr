package com.boco.commconfig;

import java.io.Serializable;

public class DevComms implements Serializable {

    private static final long serialVersionUID = 8213071296958754934L;
    /**
     * 设备编码
     */
    private  String deviceid;
    /**
     * 设备隶属rabbitmq的路由名称
     */
    private   String exchangeName;
    /**
     * 设备隶属rabbitmq的队列路由键值key
     */
    private   String QueueName;
    /**
     * 创建时间
     */
    private   String createtime;


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getQueueName() {
        return QueueName;
    }

    public void setQueueName(String queueName) {
        QueueName = queueName;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
