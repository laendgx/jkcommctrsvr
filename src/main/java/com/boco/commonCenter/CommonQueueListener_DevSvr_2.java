package com.boco.commonCenter;

import com.boco.protocolBody.CmsProtocolbody;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//@RabbitListener(queues = "${devsvrQueueName_2}")
public class CommonQueueListener_DevSvr_2 {
    private static final Logger logger= LoggerFactory.getLogger(CommonQueueListener_DevSvr_2.class);

    @RabbitHandler
    public void process(String Protocolbody) {
        try {
            System.out.println("CommonQueueListener_DevSvr_2_rev-->" + Protocolbody);
            JSONObject jsonobject = JSONObject.fromObject(Protocolbody);
            CmsProtocolbody Protocolbodytest = (CmsProtocolbody) JSONObject.toBean(jsonobject, CmsProtocolbody.class);
            String busno = Protocolbodytest.getBusinessno();
            System.out.println("CommonQueueListener_DevSvr_2_Protocolbodytest-->getBusinessno: " + busno);

            Integer WebSocketClientCount = WebSocketServer.getOnlineCount();
            if (WebSocketClientCount > 0) {
                WebSocketServer.sendInfo("数据转发-->" + Protocolbody, null);
            }
        } catch (
                IOException e) {
            logger.error("数据转发异常" + e.toString());
        }
    }
}
