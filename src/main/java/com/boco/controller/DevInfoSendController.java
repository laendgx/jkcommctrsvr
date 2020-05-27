package com.boco.controller;

import com.boco.cmsprotocolBody.CmsCmdProtocol;
import com.boco.cmsprotocolBody.Itemlist;
import com.boco.cmsprotocolBody.Playlist;
import com.boco.cmsprotocolBody.Wordlsit;
import com.boco.comm.CommResult;
import com.boco.commconfig.DevcommInfo;
import com.boco.commconfig.DevcommInfoDataServiceImpl;
import com.boco.commonCenter.TProtocolbodyMap;
import com.boco.protocolBody.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 设备数据下发
 * @Author: dong
 */
@RestController
@RequestMapping("/collsvr")
public class DevInfoSendController {
    private static final Logger logger= LoggerFactory.getLogger(DevInfoSendController.class);

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
//            CmsCmdProtocol CmsProtocolbody=new CmsCmdProtocol();
//            CmsProtocolbody.setOrgid("1101");
//            CmsProtocolbody.setDevId("21210001");
//            CmsProtocolbody.setDevvartypeid("212101");
//            SubPackage subPackage=getSubPackage();
//            CmsProtocolbody.setPlaylist(getPlaylist());
//            JSONObject object1 = JSONObject.fromObject(CmsProtocolbody);
//            String CmsProtocolbodytemp = object1.toString();
            DevcommInfoDataServiceImpl DevcommInfoData=new DevcommInfoDataServiceImpl();
            //List<DevcommInfo> sdfsdf=DevcommInfoData.listCity();
            DevcommInfo DevcommInfotemp=DevcommInfoData.getCurDevcommInfo("21210001");
            String exchangeName=DevcommInfotemp.getExchangeName();

            String messageId = String.valueOf(UUID.randomUUID());
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Protocolbody Protocolbodytemp =new Protocolbody();
            Protocolbodytemp.setBusinessno("cmsctrl_123");
            Identity Identitytemp=new Identity();
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_CMD_CMS);
            Protocolbodytemp.setSubPackage(getSubPackage());

            JSONObject object = JSONObject.fromObject(Protocolbodytemp);
            String jsonstr = object.toString();
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("exchangeName"));
            rabbitTemplate.setRoutingKey(env.getProperty("devsvrQueueKey_1"));
            rabbitTemplate.convertAndSend(jsonstr);
            System.out.println("sendmessage-->" + jsonstr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    public Playlist getPlaylist() {
        Playlist playlist=new Playlist();
        playlist.setDpw(128);
        playlist.setDph(64);
        playlist.setDpt(1);
        List<Itemlist> itemlists =new ArrayList<>();
        //第一屏信息
        Itemlist itemlist_1 =new Itemlist();
        itemlist_1.setDelay(3);
        itemlist_1.setMode(1);
        itemlist_1.setFc("r");
        itemlist_1.setFs(32);
        itemlist_1.setFn("s");
        itemlist_1.setGraphList(null);
        //文字内容
        List<Wordlsit> wordLists_1= new ArrayList<>();
        //第一行文字
        Wordlsit wordlsit_1 =new Wordlsit();
        wordlsit_1.setWx(0);
        wordlsit_1.setWy(0);
        wordlsit_1.setWc("安全驾驶");
        wordLists_1.add(wordlsit_1);//添加第一行文字
        //第二行文字
        Wordlsit wordlsit_2 =new Wordlsit();
        wordlsit_2.setWx(0);
        wordlsit_2.setWy(32);
        wordlsit_2.setWc("平安回家");
        wordLists_1.add(wordlsit_2);//添加第二行文字
        itemlist_1.setWordList(wordLists_1);//添加文字内容
        //添加第一屏信息
        itemlists.add(itemlist_1);
        //第二屏信息
        Itemlist itemlist_2 =new Itemlist();
        itemlist_2.setDelay(3);
        itemlist_2.setMode(1);
        itemlist_2.setFc("r");
        itemlist_2.setFs(32);
        itemlist_2.setFn("s");
        itemlist_2.setGraphList(null);

        //文字内容
        List<Wordlsit> wordLists_2= new ArrayList<>();
        //第一行文字
        Wordlsit wordlsit_3 =new Wordlsit();
        wordlsit_3.setWx(0);
        wordlsit_3.setWy(0);
        wordlsit_3.setWc("路途漫漫");
        wordLists_2.add(wordlsit_3); //添加第一行文字
        //第二行文字
        Wordlsit wordlsit_4 =new Wordlsit();
        wordlsit_4.setWx(0);
        wordlsit_4.setWy(32);
        wordlsit_4.setWc("文明相伴");
        wordLists_2.add(wordlsit_4);//添加第二行文字
        itemlist_2.setWordList(wordLists_2);//添加文字内容
        //添加第二屏信息
        itemlists.add(itemlist_2);

        playlist.setItemlist(itemlists);
        return playlist;
    }

    /**
     * 情报板模拟协议json串
     */
    public SubPackage getSubPackage(){
        SubPackage subPackage=new SubPackage();

        subPackage.setOrgId("1101");
        subPackage.setDevId("21210001");

        List<DevVarInfo> DevVarInfolist = new ArrayList<>();
        DevVarInfo devVarInfo=new DevVarInfo();
        devVarInfo.setDevvartypeid("212101");
        devVarInfo.setDevvartypedesc("播放信息");
        Playlist playlist=new Playlist();
        playlist.setDpw(128);
        playlist.setDph(64);
        playlist.setDpt(1);
        List<Itemlist> itemlists =new ArrayList<>();
        //第一屏信息
        Itemlist itemlist_1 =new Itemlist();
        itemlist_1.setDelay(3);
        itemlist_1.setMode(1);
        itemlist_1.setFc("r");
        itemlist_1.setFs(32);
        itemlist_1.setFn("s");
        itemlist_1.setGraphList(null);
        //文字内容
        List<Wordlsit> wordLists_1= new ArrayList<>();
        //第一行文字
        Wordlsit wordlsit_1 =new Wordlsit();
        wordlsit_1.setWx(0);
        wordlsit_1.setWy(0);
        wordlsit_1.setWc("安全驾驶");
        wordLists_1.add(wordlsit_1);//添加第一行文字
        //第二行文字
        Wordlsit wordlsit_2 =new Wordlsit();
        wordlsit_2.setWx(0);
        wordlsit_2.setWy(32);
        wordlsit_2.setWc("平安回家");
        wordLists_1.add(wordlsit_2);//添加第二行文字
        itemlist_1.setWordList(wordLists_1);//添加文字内容
        //添加第一屏信息
        itemlists.add(itemlist_1);
        //第二屏信息
        Itemlist itemlist_2 =new Itemlist();
        itemlist_2.setDelay(3);
        itemlist_2.setMode(1);
        itemlist_2.setFc("r");
        itemlist_2.setFs(32);
        itemlist_2.setFn("s");
        itemlist_2.setGraphList(null);

        //文字内容
        List<Wordlsit> wordLists_2= new ArrayList<>();
        //第一行文字
        Wordlsit wordlsit_3 =new Wordlsit();
        wordlsit_3.setWx(0);
        wordlsit_3.setWy(0);
        wordlsit_3.setWc("路途漫漫");
        wordLists_2.add(wordlsit_3); //添加第一行文字
        //第二行文字
        Wordlsit wordlsit_4 =new Wordlsit();
        wordlsit_4.setWx(0);
        wordlsit_4.setWy(32);
        wordlsit_4.setWc("文明相伴");
        wordLists_2.add(wordlsit_4);//添加第二行文字
        itemlist_2.setWordList(wordLists_2);//添加文字内容
        //添加第二屏信息
        itemlists.add(itemlist_2);

        playlist.setItemlist(itemlists);
        JSONObject object = JSONObject.fromObject(playlist);
        String cmsplaylist = object.toString();

        devVarInfo.setDevvarvalue(cmsplaylist);
        DevVarInfolist.add(devVarInfo);
        subPackage.setDevVarInfoList(DevVarInfolist);
        return subPackage;
    }

    /**
     * 获得下发通讯协议json串
     * @param //CmsCmdProtocol 情报板协议包
     * @return rabbitmq下发通讯协议json串
     */
    public String GetSendjsonstr(CmsCmdProtocol CmsProtocolbody) {
        String Sendjsonstr = "";
        try {
            String BusinessnoId = getUUID();
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Protocolbody Protocolbodytemp = new Protocolbody();
            Protocolbodytemp.setBusinessno(BusinessnoId);
            Identity Identitytemp = new Identity();
            Identitytemp.setSourceId("jkcommctrsvr");
            Identitytemp.setTargetId("collctrsvr");
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_CMD_CMS);

            SubPackage subPackage = new SubPackage();
            subPackage.setUseid(CmsProtocolbody.getUseid());
            subPackage.setOrgId(CmsProtocolbody.getOrgid());
            subPackage.setDevId(CmsProtocolbody.getDevId());
            subPackage.setCollCtrTime(curTime);

            List<DevVarInfo> devVarInfolist = new ArrayList<>();
            DevVarInfo devVarInfo=new DevVarInfo();
            devVarInfo.setDevvartypeid(CmsProtocolbody.getDevvartypeid());
            devVarInfo.setDevvartypedesc("播放表信息");
            Playlist playlist = new Playlist();
            playlist = CmsProtocolbody.getPlaylist();
            JSONObject object = JSONObject.fromObject(playlist);
            String cmsplaylistvalue = object.toString();
            devVarInfo.setDevvarvalue(cmsplaylistvalue);
            devVarInfolist.add(devVarInfo);
            subPackage.setDevVarInfoList(devVarInfolist);

            Protocolbodytemp.setSubPackage(subPackage);

            JSONObject objecttemp = JSONObject.fromObject(Protocolbodytemp);
            Sendjsonstr = objecttemp.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("GetSendjsonstr获得下发通讯协议json串异常" + ex.toString());
        }
        return Sendjsonstr;
    }

    @Resource(name="devcommInfoDataServiceImpl")
    private DevcommInfoDataServiceImpl devcommInfoDataServiceImpl; //设备配置信息

    //获取情报板基础信息
    @RequestMapping(value="getBasicInfosTest", method=RequestMethod.POST)
    public @ResponseBody List<String> getBasicInfosTest(HttpSession session){
        //JkptBaseUser user = (JkptBaseUser) session.getAttribute("admin");
        Integer orgid = 1101;

        List<String> list = new ArrayList<>();
        try {
            String sdf="play.lst";
            String sdf213="路途漫漫";
            list.add(sdf);
            list.add(sdf213);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *下发情报板播放表信息
     * @param //CmsCmdProtocol 情报板协议包
     */
    @RequestMapping(value = "devInfoSend",method = RequestMethod.POST)
    public @ResponseBody
    CommResult<String> devInfoSend(
            @RequestBody CmsCmdProtocol Protocolbodytemp, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommResult<String> result = new CommResult<String>();
        try {
            String deviceid = Protocolbodytemp.getDevId();
            String jsonstr =GetSendjsonstr(Protocolbodytemp);

            DevcommInfo DevcommInfotemp=devcommInfoDataServiceImpl.getCurDevcommInfo(deviceid);
            if(DevcommInfotemp.getDevid().equals(null)){
                result.setResultCode("102");
                result.setResultMsg("下发数据异常无此设备编码信息");
                return result;
            }
            String exchangeName=DevcommInfotemp.getExchangeName();
            String devsvrQueueKey=DevcommInfotemp.getQueueRoutingKey();
            SendRabbitmqQueue(exchangeName,devsvrQueueKey, jsonstr);
            result.setResultCode("100");
            result.setResultMsg("下发到消息队列成功");//获取设备rabbitmq通讯信息

        } catch (Exception ex) {
            ex.printStackTrace();
            result.setResultCode("101");
            result.setResultMsg("下发情报板播放表信息异常或无此设备信息");
            logger.error("下发数据异常发生异常" + ex.toString());
        }
        return result;
    }

    /**
     * Rabbitmq队列下发
     * @param Exchange
     * @param RoutingKey
     * @param jsonstr
     */
    public void  SendRabbitmqQueue(String Exchange,String RoutingKey,String jsonstr)
    {
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
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error("下发数据异常发生异常"+ex.toString());
        }
    }

    /**
     * json字符串与对象之间的转换
     * @param jsonStr
     * @param obj
     * @param <T>
     * @return
     */
    public static<T> Object JSONToObj(String jsonStr,Class<T> obj) {
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
     * @return String UUID
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        //去掉“-”符号
        return uuid.replaceAll("-", "");
    }


}
