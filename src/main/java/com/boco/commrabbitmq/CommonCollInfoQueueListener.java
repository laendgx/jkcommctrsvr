package com.boco.commrabbitmq;

import com.boco.commdevinfocache.DevVarInfoCache;
import com.boco.commdevinfocache.TDevVarInfoCacheMap;
import com.boco.commdevinfocache.TProtocolbodyMap;
import com.boco.protocolBody.*;
import com.boco.commwebsocket.WebSocketCommPackage;
import com.boco.commwebsocket.WebSocketServer;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfo;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfoDataServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RabbitListener(queues = "${collInfoQueueName}")
public class CommonCollInfoQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(CommonCollInfoQueueListener.class);
    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法
    @Autowired
    private Environment env;
    @Resource(name="devcommInfoDataServiceImpl")
    private DevRabbitmqCommInfoDataServiceImpl devcommInfoDataServiceImpl; //设备配置信息

    @RabbitHandler
    public void process(String revdatabody) {
        try {
            //System.out.println("接收队列collInfo消息-->" + revdatabody);
//            JSONObject jsonobject = JSONObject.fromObject(revdatabody);
//            Protocolbody revprotocolbody = (Protocolbody) JSONObject.toBean(jsonobject, Protocolbody.class);
            Protocolbody revprotocolbody =  (Protocolbody)JSONToObj(revdatabody,Protocolbody.class);

            String InfoTypeRev = revprotocolbody.getInfoType();
            String databody = "";
            switch (InfoTypeRev) {
                case InfoType.MSG_CMD_CMS:  //采集服务的情报板控制命令反馈处理
                    //if(WebSocketServer.getOnlineCount()>0) {
                        logger.info("接收情报板反馈设备id-->" + revprotocolbody.getSubPackage().getDevId());
                        databody = getwebCmsProtocol(revprotocolbody);
                        SendMessagePressQueue(revprotocolbody);
                        DelProtocolbodyMap(revprotocolbody.getBusinessNo());
                        if (databody != "") {
                            SendWebSocketClient(databody, null);
                        } else {
                            logger.info("接收队列collInfo消息未在系统发送列表中-->" + revdatabody);
                        }
                    //}
                    break;
                case InfoType.MSG_DATA_VD:
                case InfoType.MSG_DATA_CMS:
                    //if(WebSocketServer.getOnlineCount()>0) {
                        databody = getwebDevProtocol(revprotocolbody);
                        SendWebSocketClient(databody, null);
                    //}
                    break;
                default:
                    System.out.println("无效处理数据-->" + revdatabody);
            }

        } catch (Exception e) {
            logger.error("process数据转发异常" + e.toString());
        }
    }

    /**
     * 发送websocket客户端
     * @param databody
     * @param Clientsid 客户端编码，websocket客户端连接是提供唯一编码，为空时发送所有客户端
     */
    public void SendWebSocketClient(String databody,String Clientsid) {
        try {
            if (databody.equals("") || databody == null) {
                return;
            }
            //logger.info("SendWebSocketClient发送-->"+databody);
            Integer WebSocketClientCount = WebSocketServer.getOnlineCount(); //客户端在线数
            if (WebSocketClientCount > 0) {
                WebSocketServer.sendInfo(databody, Clientsid);
            }

        }catch (IOException e) {
            logger.error("SendWebSocketClient异常" + e.toString());
        }
    }

    /**
     * 设备实时状态打包，数据发送到websocket客户端
     *
     * @param revprotocolbody
     * @return
     */
    public String getwebDevProtocol(Protocolbody revprotocolbody) {
        String DataProtocoltemp = "";
        try {
            WebSocketCommPackage webSocketCommPackage=new WebSocketCommPackage();
            webSocketCommPackage.setWebInfoType(ReturnCode.StatusDataReturn);

            SubPackage subPackage = revprotocolbody.getSubPackage();
            String orgid="";//orgid=subPackage.getOrgId(); 采集服务端不需传输orgid内容
            String DevId=subPackage.getDevId();

            DevRabbitmqCommInfo DevcommInfotemp=devcommInfoDataServiceImpl.getCurDevcommInfo(DevId);
            if(DevcommInfotemp!=null){
                orgid=DevcommInfotemp.getOrgId();
            }

            List<DevVarInfoCache> ColldevVarInfoList=new ArrayList<>();
            for (int i = 0; i < subPackage.getDevVarInfoList().size(); i++) {
                DevVarInfoCache devVarInfoCache = new DevVarInfoCache();
                devVarInfoCache.setOrgId(orgid);
                devVarInfoCache.setDevId(DevId);
                devVarInfoCache.setCollCtrTime(subPackage.getCollCtrTime());
                devVarInfoCache.setDevVarTypeId(subPackage.getDevVarInfoList().get(i).getDevVarTypeId());
                devVarInfoCache.setDevVarTypeDesc(subPackage.getDevVarInfoList().get(i).getDevVarTypeDesc());
                devVarInfoCache.setDevVarLastValue(subPackage.getDevVarInfoList().get(i).getDevVarValue());
                devVarInfoCache.setDevVarGroupId(subPackage.getDevVarInfoList().get(i).getDevVarGroupId());
                ColldevVarInfoList.add(devVarInfoCache);
            }

            if(ColldevVarInfoList.size()>0){
                List<DevVarInfoCache> devVarInfoCaches= TDevVarInfoCacheMap.getInstance().getChangeDevVarInfoList(ColldevVarInfoList);
                if(devVarInfoCaches.size()>0) {
                    webSocketCommPackage.setDevVarInfoList(devVarInfoCaches);
                    webSocketCommPackage.setReturnState(null);
                    JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                    DataProtocoltemp = objecttemp.toString();
                }
            }

        } catch (Exception ex) {
            logger.error("getwebDevProtocol数据处理异常" + ex.toString());
        }
        return DataProtocoltemp;
    }

    /**
     * 情报板命令下发反馈 发送至websocket客户端
     *
     * @param revprotocolbody
     * @return
     */
    public String getwebCmsProtocol(Protocolbody revprotocolbody) {
        String DataProtocoltemp = "";
        try {
            String businessNo = revprotocolbody.getBusinessNo();

//            logger.info("TProtocolbodyMap获取businessNo--->" + businessNo);
//            logger.info("TProtocolbodyMap.getInstance().gProtocolbodyMap.size()--->" + TProtocolbodyMap.getInstance().gProtocolbodyMap.size());
            Protocolbody getprotocolbody = TProtocolbodyMap.getInstance().get(businessNo);
            if (getprotocolbody != null) {
                //System.out.println("接收Businessno: " + businessNo + "   发送存放Businessno-->" + getprotocolbody.getBusinessno());
                SubPackage subPackage = revprotocolbody.getSubPackage();
                WebSocketCommPackage webSocketCommPackage=new WebSocketCommPackage();
                webSocketCommPackage.setWebInfoType(ReturnCode.DevCtrReturn);
                List<DevVarInfoCache> ctrlReturnPackages=new ArrayList<>();

                DevVarInfoCache ctrlReturnPackage = new DevVarInfoCache();
                ctrlReturnPackage.setOrgId(getprotocolbody.getSubPackage().getOrgId());
                ctrlReturnPackage.setDevId(subPackage.getDevId());
                ctrlReturnPackage.setCollCtrTime(subPackage.getCollCtrTime());
                DevVarInfo DevVarInfotemp=((DevVarInfo)getprotocolbody.getSubPackage().getDevVarInfoList().get(0));
                ctrlReturnPackage.setDevVarTypeId(DevVarInfotemp.getDevVarTypeId());
                ctrlReturnPackage.setDevVarTypeDesc(DevVarInfotemp.getDevVarTypeDesc());
                ctrlReturnPackage.setDevVarGroupId(DevVarInfotemp.getDevVarGroupId());
                ctrlReturnPackage.setDevVarLastValue(DevVarInfotemp.getDevVarValue());
                ctrlReturnPackages.add(ctrlReturnPackage);

                ReturnState returnState = new ReturnState();
                if (revprotocolbody.getReturnState() != null) {
                    returnState.setReturnCode(revprotocolbody.getReturnState().getReturnCode());
                    returnState.setReturnMessage(revprotocolbody.getReturnState().getReturnMessage());
                } else {
                    returnState.setReturnCode(ReturnCode.ReturnCode_success);
                    returnState.setReturnMessage("命令发送成功");
                }
                webSocketCommPackage.setReturnState(returnState);
                webSocketCommPackage.setDevVarInfoList(ctrlReturnPackages);

                JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                DataProtocoltemp = objecttemp.toString();
            }
        } catch (Exception ex) {
            logger.error("getwebCmsProtocol数据处理异常" + ex.toString());
        }
        return DataProtocoltemp;
    }

    /**
     * 下发至消息处理队列,入库操作
     *
     * @param Pressprotocolbody
     * @return
     */
    public void SendMessagePressQueue(Protocolbody Pressprotocolbody) {
        String DataProtocoltemp = "";
        try {
            String businessNo = Pressprotocolbody.getBusinessNo();
            Protocolbody getprotocolbody = TProtocolbodyMap.getInstance().get(businessNo);
            if (getprotocolbody != null) {
                getprotocolbody.setReturnState(Pressprotocolbody.getReturnState());
                JSONObject objecttemp = JSONObject.fromObject(getprotocolbody);
                DataProtocoltemp = objecttemp.toString();

                SendRabbitmqQueue(env.getProperty("exchangeName"), env.getProperty("dataProcessQueueKey"), DataProtocoltemp);
            }

        } catch (Exception ex) {
            logger.error("getwebCmsProtocol数据处理异常" + ex.toString());
        }
    }

    /**
     * 删除发送缓存信息
     * @param businessNo
     */
    public void DelProtocolbodyMap(String businessNo){
        TProtocolbodyMap.getInstance().delete(businessNo);//删除发送数据缓存中的下发数据
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
//            System.out.println("SendRabbitmq: " + "\n" + "Exchange-->" + Exchange +
//                    "RoutingKey-->" + RoutingKey + "\n" + jsonstr);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("下发数据异常发生异常" + ex.toString());
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
}
