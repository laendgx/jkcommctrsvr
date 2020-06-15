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
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.info("有新窗口开始监听:"+sid+",当前在线人数为" + getOnlineCount());
        this.sid=sid;
        try {
            sendMessage(ConnectIniInfo());
            SendDevVarInfosToClient(sid);
        } catch (IOException e) {
            logger.error("websocket IO异常");
        }
    }

    public String ConnectIniInfo() throws IOException{
        WebSocketCommPackage webSocketCommPackage=new WebSocketCommPackage();
        webSocketCommPackage.setWebInfoType(ReturnCode.Connect_success);
        webSocketCommPackage.setDevVarInfoList(null);
        ReturnState returnState = new ReturnState();
        returnState.setReturnCode(ReturnCode.ReturnCode_success);
        returnState.setReturnMessage("连接成功");
        webSocketCommPackage.setReturnState(returnState);
        JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
        String DataProtocoltemp = objecttemp.toString();
        return DataProtocoltemp;
    }


    /**
     * 获取所有设备变量信息列表，发送到websocket客户端 针对新连接用户
     */
    public void SendDevVarInfosToClient(String Clientsid) {
        try {
            for (Map.Entry<String, DevVarInfoCache> entry : TDevVarInfoCacheMap.getInstance().gDevVarInfoCacheMap.entrySet()) {
                DevVarInfoCache devVarInfo = entry.getValue();
                List<DevVarInfoCache> devVarInfos = new ArrayList<>();
                devVarInfos.add(devVarInfo);

                WebSocketCommPackage webSocketCommPackage = new WebSocketCommPackage();
                webSocketCommPackage.setWebInfoType(ReturnCode.StatusDataReturn);
                webSocketCommPackage.setDevVarInfoList(devVarInfos);
                ReturnState returnState = new ReturnState();
                returnState.setReturnCode(ReturnCode.ReturnCode_success);
                returnState.setReturnMessage("设备状态信息");
                webSocketCommPackage.setReturnState(returnState);
                JSONObject objecttemp = JSONObject.fromObject(webSocketCommPackage);
                String DataProtocoltemp = objecttemp.toString();

                if (DataProtocoltemp.equals("") || DataProtocoltemp == null) {
                    return;
                }
                logger.info("SendWebSocketClient" + Clientsid + "-->" + DataProtocoltemp);
                this.sendInfo(DataProtocoltemp, Clientsid);

                devVarInfos.clear();
            }
        }catch (IOException e) {
            logger.error("SendWebSocketClient异常" + e.toString());
        }
    }

/**
 * 连接关闭调用的方法
 */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

/**
 * 收到客户端消息后调用的方法
 *
 * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到来自窗口"+sid+"的信息:"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/**
 *
 * @param session
 * @param error
 */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }

/**
 * 实现服务器主动推送
 */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

/**
 * 群发自定义消息
 * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
        logger.info("推送消息到窗口"+sid+"，推送内容:"+message);
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if(sid==null) {
                    item.sendMessage(message);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
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