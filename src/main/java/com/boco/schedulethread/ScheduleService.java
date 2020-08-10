package com.boco.schedulethread;

import com.boco.commdevinfocache.DevVarInfoCache;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@EnableScheduling
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    private Environment env;

    @Resource(name = "devcommInfoDataServiceImpl")
    private DevRabbitmqCommInfoDataServiceImpl devRabbitmqCommInfoDataServiceImpl; //设备配置信息
    //第一次发送标识，为true时不再获取设备状态
    private boolean sendflag = false;

    // 每10秒调用一次 发送命令超时检测线程
    //@Scheduled(cron = "0/20 * * * * ?")// corn语句 每10秒调用一次
    @Scheduled(cron = "0 0/30 * * * ?") // corn语句  每10分钟调用一次
    @Async
    public void HeartbeatHandle() {
        try {
            for (WebSocketServer item : WebSocketServer.getWebSocketSet()) {
                try {
                       // logger.info("SendWebSocketClient编码-->" + item.sid.toString() + "发送内容-->" + message);
                        item.sendMessage(GetHeartData());
                } catch (IOException e) {
                    logger.error("推送心跳消息到窗口异常-->" + e.toString());
                    continue;
                }
            }
        } catch (Exception ex) {
            logger.error("DataOverTimeCheck处理异常！");
        }
    }

    /**
     * 向wesocket客户端发送心跳包
     * @return
     * @throws IOException
     */
    public String GetHeartData() throws IOException {
        String DataProtocoltemp = "";
        try {
            WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
            webSocketCommPackage.setWebInfoType(ReturnCode.Connect_heartbeat);
            webSocketCommPackage.setDevVarInfoList(null);
            ReturnState returnState = new ReturnState();
            returnState.setReturnCode(ReturnCode.ReturnCode_success);
            returnState.setReturnMessage("心跳包");
            webSocketCommPackage.setReturnState(returnState);
            JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
            DataProtocoltemp = objecttemp.toString();
        } catch (Exception e) {
            logger.error("SendHeartData异常" + e.toString());
        }
        return DataProtocoltemp;
    }

    @Scheduled(cron = "0/3 * * * * ?")// corn语句 每3秒调用一次
    @Async
    //系统第一次启动时，给各个设备采集客户端下发设备状态查询命令
    public void GetDevStatusCheck() {
        try {//加在程序启动后执行，如InitializingBean的afterPropertiesSet方法，rabbitmq报错，发送在rabbitmq连接之前执行了
            if (!sendflag) {
                List<DevRabbitmqCommInfo> devcommInfoList = devRabbitmqCommInfoDataServiceImpl.getDevcommInfoList();
                for (int i = 0; i < devcommInfoList.size(); i++) {
                    String DataProtocoltemp = GetSendjsonstr(devcommInfoList.get(i).getOrgId(), devcommInfoList.get(i).getDevid());
                    SendRabbitmqQueue(devcommInfoList.get(i).getExchangeName(), devcommInfoList.get(i).getQueueRoutingKey(), DataProtocoltemp);
                }
            }
            sendflag = true;
        } catch (Exception ex) {
            logger.error("GetDevStatusCheck处理异常！");
        }
    }

    // 每10秒调用一次 发送命令超时检测线程
    @Scheduled(cron = "0/3 * * * * ?")// corn语句 每10秒调用一次
    //@Scheduled(cron = "0 0/1 * * * ?") // corn语句  每1分钟调用一次
    @Async
    public void DataOverTimeCheck() {
        //System.out.println("超时检测服务DataOverTimeCheck运行");
        try {
            sendCtrlBack();
        } catch (Exception ex) {
            logger.error("DataOverTimeCheck处理异常！");
        }
    }



    /**
     * 获取间隔时间
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public long getIntervalSecond(Date beginTime, Date endTime) {
        long second1 = 0;
        try {
            second1 = (endTime.getTime() - beginTime.getTime()) / 1000;//除以1000是为了转换成秒
            // System.out.println("second1: "+second1);
        } catch (Exception ex) {
            logger.error("getIntervalSecond处理异常-->" + "beginTime:" + beginTime.toString() + "  endTime:" + endTime.toString());
        }
        return second1;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        Date strtodate = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            strtodate = formatter.parse(strDate, pos);
        } catch (Exception ex) {
            logger.error("strToDateLong时间转换异常-->" + strDate);
        }
        return strtodate;
    }

    /**
     * 发送包超时检测，超时转发至websocket接口
     */
    public void sendCtrlBack() {
        // Map<String, Protocolbody> protocolbodyList=TProtocolbodyMap.getInstance().gProtocolbodyMap;
        try {
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            for (Map.Entry<String, Protocolbody> entry : TProtocolbodyMap.getInstance().gProtocolbodyMap.entrySet()) {
                try {
                    String businessNo = entry.getKey();
                    logger.info("发送列表等待命令数量-->"
                            + TProtocolbodyMap.getInstance().gProtocolbodyMap.size());

                    Protocolbody Protocolbodyentry = entry.getValue();
                    if (Protocolbodyentry != null) {
                        String gCreateTime = Protocolbodyentry.getIdentity().getCreateTime();
                        System.out.println("businessNo-->" + businessNo + "  gCreateTime-->" + gCreateTime);
                        long IntervalSecond = getIntervalSecond(strToDateLong(gCreateTime), strToDateLong(curTime));
                        if (IntervalSecond > 5) {
                            SendWebSocketDataSend(Protocolbodyentry);
                            TProtocolbodyMap.getInstance().delete(businessNo);//超时清除发送列表中数据
                        }
                    }
                } catch (Exception ex) {
                    logger.error(entry.getKey() + " 超时检测异常！");
                }
            }
        } catch (Exception ex) {
            logger.error("sendCtrlBack处理异常！" + ex.toString());
        }
    }

    /**
     * 打包发送控制超时协议到websocket
     */
    public boolean SendWebSocketDataSend(Protocolbody databody) {
        boolean result = false;
        String DataProtocoltemp = "";

        try {
            System.out.println("超时检测Businessno: " + databody.getBusinessNo());
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (databody.getSubPackage().getDevVarInfoList().size() > 0) {
                WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
                webSocketCommPackage.setWebInfoType(ReturnCode.DevCtrReturn);
                List<DevVarInfoCache> ctrlReturnPackages = new ArrayList<>();
                DevVarInfoCache ctrlReturnPackage = new DevVarInfoCache();
                ctrlReturnPackage.setOrgId(databody.getSubPackage().getOrgId());
                ctrlReturnPackage.setDevId(databody.getSubPackage().getDevId());
                ctrlReturnPackage.setCollCtrTime(curTime);
                DevVarInfo DevVarInfotemp = ((DevVarInfo) databody.getSubPackage().getDevVarInfoList().get(0));
                ctrlReturnPackage.setDevVarTypeId(DevVarInfotemp.getDevVarTypeId());
                ctrlReturnPackage.setDevVarTypeDesc(DevVarInfotemp.getDevVarTypeDesc());
                ctrlReturnPackage.setDevVarGroupId(DevVarInfotemp.getDevVarGroupId());
                ctrlReturnPackage.setDevVarLastValue(DevVarInfotemp.getDevVarValue());
                ctrlReturnPackages.add(ctrlReturnPackage);
                webSocketCommPackage.setDevVarInfoList(ctrlReturnPackages);

                ReturnState returnState = new ReturnState();
                returnState.setReturnCode(ReturnCode.ReturnCode_formaterror);
                returnState.setReturnMessage("设备发送超时");

                webSocketCommPackage.setReturnState(returnState);

                JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                DataProtocoltemp = objecttemp.toString();
                if (WebSocketDataSend(DataProtocoltemp))
                    result = true;
            }
        } catch (Exception ex) {
            logger.error("SendWebSocketDataSend处理异常！" + ex.toString());
            return result;
        }
        return result;
    }

    /**
     * 通讯协议包父类
     */
    public boolean WebSocketDataSend(String databody) {
        boolean result = false;
        try {
            if (databody.equals("") || databody == null) {
                logger.error("WebSocketDataSend发送异常，发送数据为空");
                return result;
            }

            Integer WebSocketClientCount = WebSocketServer.getOnlineCount(); //客户端在线数
            if (WebSocketClientCount > 0) {
                WebSocketServer.sendInfo(databody, null);
                result = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(" 发送数据到websocket异常！" + ex.toString());
        }
        return result;
    }

//-----------------------------rabbitmq下发处理-------------------------------------------------------

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
            subPackage.setCollCtrTime(curTime);
            Protocolbodytemp.setSubPackage(subPackage);

            JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
            Sendjsonstr = objecttemp.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("GetSendjsonstr获得下发通讯协议json串异常" + ex.toString());
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
//            System.out.println("SendRabbitmq: " + "\n" + "Exchange-->" + Exchange +
//                    "RoutingKey-->" + RoutingKey + "\n" + jsonstr);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("SendRabbitmqQueue_Controller下发异常" + ex.toString());
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
