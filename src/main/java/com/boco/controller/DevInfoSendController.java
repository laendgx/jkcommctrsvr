package com.boco.controller;

import com.boco.comm.CmsCmdProtocol;
import com.boco.cmsprotocolBody.ItemList;
import com.boco.cmsprotocolBody.PlayList;
import com.boco.cmsprotocolBody.WordList;
import com.boco.comm.CommResult;
import com.boco.commdevinfocache.DevVarInfoCache;
import com.boco.commdevinfocache.TDevVarInfoCacheMap;
import com.boco.commwebsocket.WebSocketCommPackage;
import com.boco.commwebsocket.WebSocketServer;
import com.boco.messageEncryption.MD5Util;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfo;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfoDataServiceImpl;
import com.boco.commdevinfocache.TProtocolbodyMap;
import com.boco.protocolBody.*;
import com.boco.redisUntil.JedisPoolUntil;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Resource(name="devcommInfoDataServiceImpl")
    private DevRabbitmqCommInfoDataServiceImpl devcommInfoDataServiceImpl; //设备配置信息

    @Autowired
    private Environment env;
    @GetMapping("/testImpl")
    public String sendDirectMtestImplessage() {
        String result="";
        try {
            DevRabbitmqCommInfo DevcommInfotemp = devcommInfoDataServiceImpl.getCurDevcommInfo("21240001");
            String exchangeName = DevcommInfotemp.getExchangeName();
            String devsvrQueueKey = DevcommInfotemp.getQueueRoutingKey();
            System.out.println("exchangeName-->" + exchangeName+"         devsvrQueueKey-->" + devsvrQueueKey);

            System.out.println("redisAddr-->" + env.getProperty("redisAddr"));
            result="exchangeName-->" + exchangeName+"         devsvrQueueKey-->" + devsvrQueueKey+
                    "        redisAddr-->" + env.getProperty("redisAddr");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage() {
        try {
            String messageId = String.valueOf(UUID.randomUUID());
            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Protocolbody Protocolbodytemp =new Protocolbody();
            Protocolbodytemp.setBusinessNo(messageId);
            Identity Identitytemp=new Identity();
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_CMD_CMS);
            Protocolbodytemp.setSubPackage(getSubPackage());

            JSONObject object = JSONObject.fromObject(Protocolbodytemp);
            String jsonstr = object.toString();
            String md5str= MD5Util.getInstance().encrypByMd5(jsonstr);
            String md5strother= MD5Util.getInstance().getMD5String(jsonstr);
            System.out.println("encrypByMd5-->" + md5str);
            System.out.println("getMD5String-->" + md5strother);

            Jedis Jediscur=JedisPoolUntil.getInstance().getJedis(env.getProperty("redisAddr"),env.getProperty("redisPort"));
            Jediscur.set(md5str,jsonstr);
            Jediscur.set(md5strother,jsonstr);
            String s = Jediscur.get(md5str);
            String s1 = Jediscur.get(md5str);
            System.out.println(s);
            System.out.println(s1);
            Jediscur.del(md5str);
            Jediscur.del(md5strother);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ok";
    }

    public PlayList getPlaylist() {
        PlayList playlist=new PlayList();
        playlist.setDpw(128);
        playlist.setDph(64);
        playlist.setDpt(1);
        List<ItemList> itemLists =new ArrayList<>();
        //第一屏信息
        ItemList itemList_1 =new ItemList();
        itemList_1.setDelay(3);
        itemList_1.setMode(1);
        itemList_1.setFc("r");
        itemList_1.setFs(32);
        itemList_1.setFn("s");
        itemList_1.setGraphList(null);
        //文字内容
        List<WordList> wordLists_1= new ArrayList<>();
        //第一行文字
        WordList wordList_1 =new WordList();
        wordList_1.setWx(0);
        wordList_1.setWy(0);
        wordList_1.setWc("安全驾驶");
        wordLists_1.add(wordList_1);//添加第一行文字
        //第二行文字
        WordList wordList_2 =new WordList();
        wordList_2.setWx(0);
        wordList_2.setWy(32);
        wordList_2.setWc("平安回家");
        wordLists_1.add(wordList_2);//添加第二行文字
        itemList_1.setWordList(wordLists_1);//添加文字内容
        //添加第一屏信息
        itemLists.add(itemList_1);
        //第二屏信息
        ItemList itemList_2 =new ItemList();
        itemList_2.setDelay(3);
        itemList_2.setMode(1);
        itemList_2.setFc("r");
        itemList_2.setFs(32);
        itemList_2.setFn("s");
        itemList_2.setGraphList(null);

        //文字内容
        List<WordList> wordLists_2= new ArrayList<>();
        //第一行文字
        WordList wordList_3 =new WordList();
        wordList_3.setWx(0);
        wordList_3.setWy(0);
        wordList_3.setWc("路途漫漫");
        wordLists_2.add(wordList_3); //添加第一行文字
        //第二行文字
        WordList wordList_4 =new WordList();
        wordList_4.setWx(0);
        wordList_4.setWy(32);
        wordList_4.setWc("文明相伴");
        wordLists_2.add(wordList_4);//添加第二行文字
        itemList_2.setWordList(wordLists_2);//添加文字内容
        //添加第二屏信息
        itemLists.add(itemList_2);

        playlist.setItemList(itemLists);
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
        devVarInfo.setDevVarTypeId("212401");
        devVarInfo.setDevVarTypeDesc("播放信息");
        PlayList playList=new PlayList();
        playList.setDpw(128);
        playList.setDph(64);
        playList.setDpt(1);
        List<ItemList> itemLists =new ArrayList<>();
        //第一屏信息
        ItemList itemList_1 =new ItemList();
        itemList_1.setDelay(3);
        itemList_1.setMode(1);
        itemList_1.setFc("r");
        itemList_1.setFs(32);
        itemList_1.setFn("s");
        itemList_1.setGraphList(null);
        //文字内容
        List<WordList> wordLists_1= new ArrayList<>();
        //第一行文字
        WordList wordList_1 =new WordList();
        wordList_1.setWx(0);
        wordList_1.setWy(0);
        wordList_1.setWc("安全驾驶");
        wordLists_1.add(wordList_1);//添加第一行文字
        //第二行文字
        WordList wordList_2 =new WordList();
        wordList_2.setWx(0);
        wordList_2.setWy(32);
        wordList_2.setWc("平安回家");
        wordLists_1.add(wordList_2);//添加第二行文字
        itemList_1.setWordList(wordLists_1);//添加文字内容
        //添加第一屏信息
        itemLists.add(itemList_1);
        //第二屏信息
        ItemList itemList_2 =new ItemList();
        itemList_2.setDelay(3);
        itemList_2.setMode(1);
        itemList_2.setFc("r");
        itemList_2.setFs(32);
        itemList_2.setFn("s");
        itemList_2.setGraphList(null);

        //文字内容
        List<WordList> wordLists_2= new ArrayList<>();
        //第一行文字
        WordList wordList_3 =new WordList();
        wordList_3.setWx(0);
        wordList_3.setWy(0);
        wordList_3.setWc("路途漫漫");
        wordLists_2.add(wordList_3); //添加第一行文字
        //第二行文字
        WordList wordList_4 =new WordList();
        wordList_4.setWx(0);
        wordList_4.setWy(32);
        wordList_4.setWc("文明相伴");
        wordLists_2.add(wordList_4);//添加第二行文字
        itemList_2.setWordList(wordLists_2);//添加文字内容
        //添加第二屏信息
        itemLists.add(itemList_2);

        playList.setItemList(itemLists);
        JSONObject object = JSONObject.fromObject(playList);
        String cmsplaylist = object.toString();

        devVarInfo.setDevVarValue(cmsplaylist);
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
            Protocolbodytemp.setBusinessNo(BusinessnoId);
            Identity Identitytemp = new Identity();
            Identitytemp.setSourceId("jkcommctrsvr");
            Identitytemp.setTargetId("collctrsvr");
            Identitytemp.setCreateTime(curTime);
            Protocolbodytemp.setIdentity(Identitytemp);
            Protocolbodytemp.setInfoType(InfoType.MSG_CMD_CMS);

            SubPackage subPackage = new SubPackage();
            subPackage.setUseId(CmsProtocolbody.getUseId());
            subPackage.setOrgId(CmsProtocolbody.getOrgId());
            subPackage.setDevId(CmsProtocolbody.getDevId());
            subPackage.setCollCtrTime(curTime);

            List<DevVarInfo> devVarInfolist = new ArrayList<>();
            DevVarInfo devVarInfo=new DevVarInfo();
            devVarInfo.setDevVarTypeId(CmsProtocolbody.getDevVarTypeId());
            devVarInfo.setDevVarTypeDesc("播放表信息");
            PlayList playList = new PlayList();
            playList = CmsProtocolbody.getPlayList();
            JSONObject object = JSONObject.fromObject(playList);
            String cmsplaylistvalue = object.toString();
            devVarInfo.setDevVarValue(cmsplaylistvalue);
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
     *获取设备变量信息
     * @param //webSocketClientsid webSocket客户端唯一标识
     */
    @CrossOrigin
    @RequestMapping(value = "getDevVarInfo",method = RequestMethod.POST)
    public @ResponseBody
    CommResult<String> getDevVarInfo(
            @RequestBody String webSocketClientsid, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommResult<String> result = new CommResult<String>();
        try {
            //List<DevVarInfoCache> AlldevVarInfo = new ArrayList<>();
            List<DevVarInfoCache> devVarInfos = new ArrayList<>();
            int DevVarCount=0;
            int DevVarCountnum=0;
            for (Map.Entry<String, DevVarInfoCache> entry : TDevVarInfoCacheMap.getInstance().gDevVarInfoCacheMap.entrySet()) {
                DevVarCount+=1;
                DevVarCountnum=DevVarCountnum+1;
                DevVarInfoCache devVarInfo = entry.getValue();

                if(devVarInfo.getDevVarLastValue().equals("1")) {
                    devVarInfos.add(devVarInfo);
                    //AlldevVarInfo.add(devVarInfo);
                    WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
                    webSocketCommPackage.setWebInfoType(ReturnCode.StatusDataReturn);
                    webSocketCommPackage.setDevVarInfoList(devVarInfos);
                    ReturnState returnState = new ReturnState();
                    returnState.setReturnCode(ReturnCode.ReturnCode_success);
                    returnState.setReturnMessage("设备状态信息");
                    webSocketCommPackage.setReturnState(returnState);

                    //System.out.println(sd);
                    if (DevVarCount >= 20) {
                        JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                        String DataProtocoltemp = objecttemp.toString();
                        this.SendWebSocketClient(DataProtocoltemp, webSocketClientsid);
                        devVarInfos.clear();
                        DevVarCount = 0;
                    }
                }
            }
            System.out.println("变量总数为"+DevVarCountnum);
            if(DevVarCount<20 && DevVarCount>0) {
                //System.out.println("变量数据为"+DevVarCount);
                WebSocketCommPackage webSocketCommPackageother = new WebSocketCommPackage();
                webSocketCommPackageother.setWebInfoType(ReturnCode.StatusDataReturn);
                webSocketCommPackageother.setDevVarInfoList(devVarInfos);
                ReturnState returnState = new ReturnState();
                returnState.setReturnCode(ReturnCode.ReturnCode_success);
                returnState.setReturnMessage("设备状态信息");
                webSocketCommPackageother.setReturnState(returnState);
                JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackageother);
                String DataProtocoltemp = objecttemp.toString();
                this.SendWebSocketClient(DataProtocoltemp, webSocketClientsid);
                devVarInfos.clear();
            }
//            JSONArray objecttemp = JSONArray.fromObject(AlldevVarInfo);
//            String AllDevData = objecttemp.toString();
            result.setResultCode("100");
            result.setResultData("变量总数为"+DevVarCountnum);//(AllDevData);
            result.setResultMsg("WebSocket已转发");//获取设备rabbitmq通讯信息
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("getDevVarInfo异常-->" + ex.toString());
        }
        return result;
    }


    /**
     *下发情报板播放表信息
     * @param //CmsCmdProtocol 情报板协议包
     */
    @CrossOrigin
    @RequestMapping(value = "devInfoSend",method = RequestMethod.POST)
    public @ResponseBody
    CommResult<String> devInfoSend(
            @RequestBody List<CmsCmdProtocol> CmsCmdProtocols, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CommResult<String> result = new CommResult<String>();
        try {
            if(CmsCmdProtocols.size()==0){
                result.setResultCode("101");
                result.setResultMsg("下发情报板播放表信息不能为空");
                return result;
            }
            for (CmsCmdProtocol cmsCmdProtocol : CmsCmdProtocols) {
                String deviceid = cmsCmdProtocol.getDevId();
                String jsonstr =GetSendjsonstr(cmsCmdProtocol);

                DevRabbitmqCommInfo DevcommInfotemp=devcommInfoDataServiceImpl.getCurDevcommInfo(deviceid);
                if(DevcommInfotemp.getDevid().equals(null)){
                    result.setResultCode("102");
                    result.setResultMsg("下发数据异常无此设备编码信息");
                    return result;
                }
                String exchangeName=DevcommInfotemp.getExchangeName();
                String devsvrQueueKey=DevcommInfotemp.getQueueRoutingKey();
                SendRabbitmqQueue(exchangeName,devsvrQueueKey, jsonstr);
            }

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
    public void  SendRabbitmqQueue(String Exchange,String RoutingKey,String jsonstr){
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
            Protocolbody revprotocolbody = (Protocolbody) JSONToObj(jsonstr, Protocolbody.class);
            TProtocolbodyMap.getInstance().add(revprotocolbody);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error("SendRabbitmqQueue_Controller下发异常"+ex.toString());
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
            logger.info("SendWebSocketClient发送-->"+databody);
            Integer WebSocketClientCount = WebSocketServer.getOnlineCount(); //客户端在线数
            if (WebSocketClientCount > 0) {
                WebSocketServer.sendInfo(databody, Clientsid);
            }

        }catch (IOException e) {
            logger.error("SendWebSocketClient异常" + e.toString());
        }
    }

    // List<String>排序
    private void sortUsingJava8(List<String> names){
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
    }
}
