package com.boco.commwebsocket;

import com.boco.commdevinfocache.DevVarInfoCache;
import com.boco.commdevinfocache.TDevVarInfoCacheMap;
import com.boco.protocolBody.ReturnCode;
import com.boco.protocolBody.ReturnState;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebfsocketServer
 */
@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {
    private static final Logger logger= LoggerFactory.getLogger(WebSocketServer.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet =
            new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid="";

/**
 * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        try {
            logger.info("有新客户端连接" +sid+"  session.id-->"+session.getId());
            this.session = session;
            //this.session.getId();
            //checkSession(sid);
            webSocketSet.add(this);     //加入set中
            addOnlineCount();           //在线数加1
            logger.info("新客户端监听id: " + sid + ",当前在线人数为" + getOnlineCount());
            this.sid = sid;
            sendMessage(ConnectIniInfo());
            // SendDevVarInfosToClient(sid);
        } catch (IOException e) {
            logger.error("websocket IO异常");
        }
    }

    /**
     * 检测客户端，重复id的进行删除操作
     * @param sid
     */
    public void checkSession(String sid){
        boolean flag=false;
        for (WebSocketServer item : webSocketSet) {
            if(item.sid.equals(sid)){
                flag=true;
                webSocketSet.remove(item);  //从set中删除
                subOnlineCount();           //在线数减1
            }
        }
    }

    /**
     * 向客户端发送连接成功数据包
     * @return
     * @throws IOException
     */
    public String ConnectIniInfo() throws IOException {
        String DataProtocoltemp = "";
        try {
            WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
            webSocketCommPackage.setWebInfoType(ReturnCode.Connect_success);
            webSocketCommPackage.setDevVarInfoList(null);
            ReturnState returnState = new ReturnState();
            returnState.setReturnCode(ReturnCode.ReturnCode_success);
            returnState.setReturnMessage("连接成功");
            webSocketCommPackage.setReturnState(returnState);
            JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
            DataProtocoltemp = objecttemp.toString();
        } catch (Exception e) {
            logger.error("ConnectIniInfo异常" + e.toString());
        }
        return DataProtocoltemp;
    }

    /**
     * 获取所有设备变量信息列表，发送到websocket客户端 针对新连接用户
     */
    public void SendDevVarInfosToClient(String Clientsid) {
        try {
            List<DevVarInfoCache> devVarInfos = new ArrayList<>();
            int DevVarCountnum=0;
            for (Map.Entry<String, DevVarInfoCache> entry : TDevVarInfoCacheMap.getInstance().gDevVarInfoCacheMap.entrySet()) {
                DevVarCountnum = DevVarCountnum + 1;
                DevVarInfoCache devVarInfo = entry.getValue();

                devVarInfos.add(devVarInfo);

                if (devVarInfos.size() >= 20) {
                    System.out.println("发送设备变量数量-->" + devVarInfos.size());
                    WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
                    webSocketCommPackage.setWebInfoType(ReturnCode.StatusDataReturn);
                    webSocketCommPackage.setDevVarInfoList(devVarInfos);
                    ReturnState returnState = new ReturnState();
                    returnState.setReturnCode(ReturnCode.ReturnCode_success);
                    returnState.setReturnMessage("设备状态信息");
                    webSocketCommPackage.setReturnState(returnState);
                    JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                    String DataProtocoltemp = objecttemp.toString();
                    this.sendInfo(DataProtocoltemp, Clientsid);
                    devVarInfos.clear();
                }
            }
            if(devVarInfos.size()<20 && devVarInfos.size()>0) {
                WebSocketCommPackage webSocketCommPackageother = new WebSocketCommPackage();
                webSocketCommPackageother.setWebInfoType(ReturnCode.StatusDataReturn);
                webSocketCommPackageother.setDevVarInfoList(devVarInfos);
                ReturnState returnState = new ReturnState();
                returnState.setReturnCode(ReturnCode.ReturnCode_success);
                returnState.setReturnMessage("设备状态信息");
                webSocketCommPackageother.setReturnState(returnState);
                JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackageother);
                String DataProtocoltemp = objecttemp.toString();
                this.sendInfo(DataProtocoltemp, Clientsid);
                devVarInfos.clear();
            }
            System.out.println("发送变量总数为"+DevVarCountnum);
        }catch (IOException e) {
            logger.error("SendWebSocketClient异常" + e.toString());
        }
    }

/**
 * 连接关闭调用的方法
 */
    @OnClose
    public void onClose() {
        try {
            webSocketSet.remove(this);  //从set中删除
            subOnlineCount();           //在线数减1
            logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
        } catch (Exception e) {
            logger.error("onClose异常" + e.toString());
        }
    }

/**
 * 收到客户端消息后调用的方法
 *
 * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            logger.info("收到来自窗口" + sid + "的信息:" + message);
            if (message.equals("getDevVarInfo")) {
                SendDevVarInfosToClient(sid);
            }
        } catch (Exception e) {
            logger.error("onClose异常" + e.toString());
        }
//        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                if(message.equals("getDevVarInfo"))
//                {
//                    //这里可以设定只推送给这个sid的，为null则全部推送
//                    if(sid==null) {
//                        item.sendMessage(message);
//                    }else if(item.sid.equals(sid)){
//                        item.sendMessage(message);
//                    }
//                }
//                item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

/**
 *
 * @param session
 * @param error
 */
    @OnError
    public void onError(Session session, Throwable error) {
        try {
            logger.error("消息到窗口" + sid + "发生异常");
            error.printStackTrace();
        } catch (Exception e) {
            logger.error("onError消息到窗口" + sid + "发生异常-->"+e.toString());
        }
    }

/**
 * 实现服务器主动推送
 */
    public void sendMessage(String message) throws IOException {
        try {
            //logger.info("send session id-->"+this.session.getId());
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.error("sendMessage消息到窗口" + sid + "发生异常-->"+e.toString());
        }
    }

/**
 * 群发自定义消息
 * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
        try {
            //logger.info("推送消息到窗口" + sid + "，推送内容:" + message);
            for (WebSocketServer item : webSocketSet) {
                try {
                    //这里可以设定只推送给这个sid的，为null则全部推送
                    if (sid == null) {
                        logger.info("SendWebSocketClient编码-->" + item.sid.toString() + "发送内容-->" + message);
                        item.sendMessage(message);
                    } else if (item.sid.equals(sid)) {
                        item.sendMessage(message);
                    }
                } catch (IOException e) {
                    logger.error("推送消息到窗口" + sid + "异常-->" + e.toString());
                    continue;
                }
            }
        } catch (Exception e) {
            logger.error("sendInfo消息到窗口" + sid + "发生异常-->"+e.toString());
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }
}
