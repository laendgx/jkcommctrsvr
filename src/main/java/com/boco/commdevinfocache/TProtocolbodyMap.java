package com.boco.commdevinfocache;

import com.boco.protocolBody.Protocolbody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 情报板发送数据缓存信息，用于设备发送超时检测
 */
public class TProtocolbodyMap {
    private static final Logger logger= LoggerFactory.getLogger(TProtocolbodyMap.class.getName());

    /**
     * 发送数据缓存信息
     */
    public Map<String,Protocolbody> gProtocolbodyMap;

    /**
     * 构造方法  ConcurrentHashMap
     */
    public TProtocolbodyMap(){
        gProtocolbodyMap = new ConcurrentHashMap<String,Protocolbody>();
    }

    private static TProtocolbodyMap singleton = null;

//    public  Map<String,Protocolbody> getgProtocolbodyMap(){
//        return this.gProtocolbodyMap;
//    }

    /**
     * 静态工厂方法
     */
    public static TProtocolbodyMap getInstance(){
        if (singleton == null){
            singleton = new TProtocolbodyMap();
        }
        return singleton;
    }

    /**
     * 往gProtocolbodyMap中增加通讯设备对象
     * @param protocolbody
     * @return
     */
    public boolean add(Protocolbody protocolbody){
        //System.out.println("add(Protocolbody protocolbody)" );
        if (!gProtocolbodyMap.containsKey(protocolbody.getBusinessNo())){
            gProtocolbodyMap.put(protocolbody.getBusinessNo(), protocolbody);
            //System.out.println("gProtocolbodyMap" );
            return true;
        }
        return false;
    }

    /**
     * gProtocolbodyMap中删除特定的通讯设备对象
     * @param businessNo
     * @return
     */
    public boolean delete(String businessNo){
        if (gProtocolbodyMap != null) {
            if (gProtocolbodyMap.containsKey(businessNo)) {
                try {
                    gProtocolbodyMap.remove(businessNo);
                    return true;
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    logger.error("TProtocolbodyMap处理gProtocolbodyMap.remove异常：" + ex.toString());
                }
            }
        }
        return false;
    }

    /**
     * 从gProtocolbodyMap中获取通讯设备对象
     * @param businessNo
     * @return
     */
    public Protocolbody get(String businessNo) {
        Protocolbody result=new Protocolbody();
        if (gProtocolbodyMap.containsKey(businessNo)) {
            result=gProtocolbodyMap.get(businessNo);
            //gProtocolbodyMap.remove(businessNo);
            System.out.println("剩余发送队列消息-->" + gProtocolbodyMap.size());
            return result;
        } else
            return null;
    }




}
