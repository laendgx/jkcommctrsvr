package com.boco.commonCenter;

import com.boco.protocolBody.*;
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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@EnableScheduling
public class ScheduleService {

    private static final Logger logger= LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    private Environment env;

    // 每30秒调用一次
    @Scheduled(cron = "0/20 * * * * ?")// corn语句 没20秒调用一次
    //@Scheduled(cron = "0 0/1 * * * ?") // corn语句  每1分钟调用一次
    @Async
    public void DataOverTimeCheck() {
        System.out.println("超时检测服务DataOverTimeCheck运行");
        try {
            sendCtrlBack();
        } catch (Exception ex) {
            logger.error("DataOverTimeCheck处理异常！");
        }
    }

    /**
     * 间隔时间
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

            System.out.println("TProtocolbodyMap.getInstance().gProtocolbodyMap.size()-->"
                    + TProtocolbodyMap.getInstance().gProtocolbodyMap.size());
            for (Map.Entry<String, Protocolbody> entry : TProtocolbodyMap.getInstance().gProtocolbodyMap.entrySet()) {
                try {
                    String businessNo = entry.getKey();
                    System.out.println("businessNo-->" + businessNo);

                    Protocolbody Protocolbodyentry = entry.getValue();
                    if (Protocolbodyentry != null) {
                        String gCreateTime = Protocolbodyentry.getIdentity().getCreateTime();
                        System.out.println("businessNo-->" + businessNo+"  gCreateTime-->" + gCreateTime);
                        long IntervalSecond = getIntervalSecond(strToDateLong(gCreateTime), strToDateLong(curTime));
                        if (IntervalSecond > 60) {
                            if (SendWebSocketDataSend(Protocolbodyentry))
                                TProtocolbodyMap.getInstance().delete(businessNo);
                        }
                    }
                } catch (Exception ex) {
                    logger.error(entry.getKey() + " 超时检测异常！");
                }
            }
        } catch (Exception ex) {
            logger.error("sendCtrlBack处理异常！"+ex.toString());
        }
    }

    /**
     * 打包发送控制超时协议到websocket
     */
    public boolean  SendWebSocketDataSend(Protocolbody databody) {
        boolean result = false;
        String DataProtocoltemp = "";

        try {
            System.out.println("超时检测Businessno: " + databody.getBusinessno());
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            CtrlReturnPackage ctrlReturnPackage = new CtrlReturnPackage();
            ctrlReturnPackage.setOrgId(databody.getSubPackage().getOrgId());
            ctrlReturnPackage.setDevId(databody.getSubPackage().getDevId());
            ctrlReturnPackage.setCollCtrTime(curTime);
            DevVarInfo DevVarInfotemp=((DevVarInfo)databody.getSubPackage().getDevVarInfoList().get(0));
            ctrlReturnPackage.setDevvartypeid(DevVarInfotemp.getDevvartypeid());
            ctrlReturnPackage.setDevvartypedesc(DevVarInfotemp.getDevvartypedesc());
            ctrlReturnPackage.setDevvargroupid(DevVarInfotemp.getDevvargroupid());
            ctrlReturnPackage.setDevvarvalue(DevVarInfotemp.getDevvarvalue());

            ReturnState returnState = new ReturnState();
            returnState.setReturnCode(ReturnCode.ReturnCode_unknown);
            returnState.setReturnMessage("设备"+databody.getSubPackage().getDevId()+"发送超时");
            ctrlReturnPackage.setReturnState(returnState);

            JSONObject objecttemp = JSONObject.fromObject(ctrlReturnPackage);
            DataProtocoltemp = objecttemp.toString();
            if (WebSocketDataSend(DataProtocoltemp))
                result = true;
        } catch (Exception ex) {
            logger.error("SendWebSocketDataSend处理异常！" + ex.toString());
        }
        return result;
    }

    /**
     * 通讯协议包父类
     */
    public boolean  WebSocketDataSend(String databody)
    {
        boolean result=false;
        try {
            if (databody.equals("") || databody == null) {
                logger.error("WebSocketDataSend发送异常，发送数据为空");
                return result;
            }

            Integer WebSocketClientCount = WebSocketServer.getOnlineCount(); //客户端在线数
            if (WebSocketClientCount > 0) {
                WebSocketServer.sendInfo(databody, null);
                result=true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(" 发送数据到websocket异常！"+ex.toString());
        }
        return result;
    }



}
