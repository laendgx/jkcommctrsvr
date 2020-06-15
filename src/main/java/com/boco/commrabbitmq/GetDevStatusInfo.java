package com.boco.commrabbitmq;

import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfo;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfoDataServiceImpl;
import com.boco.commdevinfocache.TProtocolbodyMap;
import com.boco.protocolBody.Identity;
import com.boco.protocolBody.InfoType;
import com.boco.protocolBody.Protocolbody;
import com.boco.protocolBody.SubPackage;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

//@Configuration未用
@Component
public class GetDevStatusInfo implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(GetDevStatusInfo.class);

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法
    @Resource(name = "devcommInfoDataServiceImpl")
    private DevRabbitmqCommInfoDataServiceImpl devRabbitmqCommInfoDataServiceImpl; //设备配置信息

    @Override
    public void afterPropertiesSet() throws Exception {
//
//        System.out.println("afterPropertiesSet------------------------test");
//        List<DevRabbitmqCommInfo> devcommInfoList=devRabbitmqCommInfoDataServiceImpl.getDevcommInfoList();
//        for (int i=0;i<devcommInfoList.size();i++) {
//            String DataProtocoltemp = GetSendjsonstr(devcommInfoList.get(i).getOrgId(),devcommInfoList.get(i).getDevid());
//            //SendRabbitmqQueue(devcommInfoList.get(i).getExchangeName(), devcommInfoList.get(i).getQueueRoutingKey(), DataProtocoltemp);
//        }
    }

    /**
     * 获得下发通讯协议json串
     *
     * @param //CmsCmdProtocol 情报板协议包
     * @return rabbitmq下发通讯协议json串
     */
    public String GetSendjsonstr(String orgid, String devid) {
        String Sendjsonstr = "";
        try {
            String BusinessnoId = getUUID();
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Protocolbody Protocolbodytemp = new Protocolbody();
            Protocolbodytemp.setBusinessNo(BusinessnoId);
            Identity Identitytemp = new Identity();
            Identitytemp.setSourceId("jkcommctrsvr");
            Identitytemp.setTargetId("collctrsvr");
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_GET_DEVSTATUS);

            SubPackage subPackage = new SubPackage();
            subPackage.setOrgId(orgid);
            subPackage.setDevId(devid);
            Protocolbodytemp.setSubPackage(subPackage);

            JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
            Sendjsonstr = objecttemp.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            //logger.error("GetSendjsonstr获得下发通讯协议json串异常" + ex.toString());
        }
        return Sendjsonstr;
    }

    /**
     * Rabbitmq队列下发
     *
     * @param Exchange
     * @param RoutingKey
     * @param jsonstr
     */
    public void SendRabbitmqQueue(String Exchange, String RoutingKey, String jsonstr) {
        try {
            if (jsonstr == null || jsonstr.equals("")) {
                System.out.println("SendRabbitmq数据发送不能为空， " + "\n" + "Exchange-->" + Exchange +
                        "RoutingKey-->" + RoutingKey + "\n" + jsonstr);
                return;
            }
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(Exchange);
            rabbitTemplate.setRoutingKey(RoutingKey);
            rabbitTemplate.convertAndSend(jsonstr);
            System.out.println("SendRabbitmq: " + "\n" + "Exchange-->" + Exchange +
                    "RoutingKey-->" + RoutingKey + "\n" + jsonstr);
            Protocolbody revprotocolbody = (Protocolbody) JSONToObj(jsonstr, Protocolbody.class);
            TProtocolbodyMap.getInstance().add(revprotocolbody);
        } catch (Exception ex) {
            ex.printStackTrace();
            //logger.error("SendRabbitmqQueue_Controller下发异常"+ex.toString());
        }
    }

    /**
     * json字符串与对象之间的转换
     *
     * @param jsonStr
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> Object JSONToObj(String jsonStr, Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(jsonStr,
                    obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid.replaceAll("-", "");
    }

}
