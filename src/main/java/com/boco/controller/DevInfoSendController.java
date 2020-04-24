package com.boco.controller;

import com.boco.comm.CommResult;
import com.boco.protocolBody.CmsProtocolbody;
import net.sf.json.JSONObject;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 设备数据下发
 * @Author: dong
 */
@RestController
@RequestMapping("/collsvr")
public class DevInfoSendController {
    @RequestMapping("/gethello")
    public String getHello(String name){
        return "hello";
    }

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    @Autowired
    private Environment env;

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        try {
            String messageId = String.valueOf(UUID.randomUUID());
            String messageData = "test message, hello!";
            String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            CmsProtocolbody cmsProtocolbodytemp =new CmsProtocolbody();
            cmsProtocolbodytemp.setBusinessno("222");
            JSONObject object = JSONObject.fromObject(cmsProtocolbodytemp);
            String jsonstr = object.toString();
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("exchangeName"));
            rabbitTemplate.setRoutingKey(env.getProperty("devsvrQueueKey_1"));
            rabbitTemplate.convertAndSend(jsonstr);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    @RequestMapping(value = "/devInfoSend",method = RequestMethod.POST)
    public @ResponseBody
    CommResult<String> devInfoSend(
            @RequestBody CmsProtocolbody devinfo, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception{
        CommResult<String> result = new CommResult<String>();
        try {
            String businessno =devinfo.getBusinessno();
            result.setResultCode("100");
            result.setResultMsg("下发成功");
        }catch (Exception ex){
            ex.printStackTrace();
            result.setResultCode("101");
            result.setResultMsg("下发数据异常发生异常");
        }
        return result;
    }



}
