package com.boco.commconfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "devece")
@XmlAccessorType(XmlAccessType.FIELD)
public class DevcommInfo {
    @XmlAttribute(name = "devid")   //设备编码
    private String devid;
    @XmlAttribute(name = "exchangeName") //设备隶属rabbitmq的路由名称
    private String exchangeName;
    @XmlAttribute(name = "queueRoutingKey") //设备隶属rabbitmq的队列路由键值key
    private String queueRoutingKey;
    @XmlAttribute(name = "createtime") //创建时间
    private String createtime;
}
