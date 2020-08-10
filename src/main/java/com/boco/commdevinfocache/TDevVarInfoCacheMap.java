package com.boco.commdevinfocache;

import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备变量数据缓存信息
 */
public class TDevVarInfoCacheMap {
    private static final Logger logger= LoggerFactory.getLogger(TDevVarInfoCacheMap.class.getName());

    /**
     * 设备变量数据缓存信息
     */
    //public List<DevVarInfoCache> gDevVarInfoCacheMap;

    /**
     * 设备变量数据缓存信息
     */
    public Map<String,DevVarInfoCache> gDevVarInfoCacheMap;


    /**
     * 构造方法
     */
    public TDevVarInfoCacheMap(){
        //gDevVarInfoCacheMap = new ArrayList<>();
        gDevVarInfoCacheMap = new ConcurrentHashMap<String,DevVarInfoCache>();
    }

    private static TDevVarInfoCacheMap singleton = null;

    /**
     * 静态工厂方法
     */
    public static TDevVarInfoCacheMap getInstance(){
        if (singleton == null){
            singleton = new TDevVarInfoCacheMap();
        }
        return singleton;
    }

    /**
     * gDevVarInfoCacheMap  map中有设备变量数据进行修改采集时间和采集值，无设备变量信息进行添加
     * @param devVarInfoCache
     * @return
     */
    public boolean add(DevVarInfoCache devVarInfoCache) {
        boolean result = false;
        try {
            String devVarInfoKey=devVarInfoCache.getOrgId()+devVarInfoCache.getDevId()
                    +devVarInfoCache.getDevVarTypeId()+devVarInfoCache.getDevVarGroupId();
            gDevVarInfoCacheMap.put(devVarInfoKey, devVarInfoCache);
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 获取所有设备变量信息列表，用于websocket客户端 新连接后发送所有变量信息
     * @return
     */
    public String getAllDevVarInfoList() {
        String result = "";
        for(Map.Entry<String, DevVarInfoCache> entry:TDevVarInfoCacheMap.getInstance().gDevVarInfoCacheMap.entrySet()){
            DevVarInfoCache devVarInfo = entry.getValue();
            result+=devVarInfo;
        }
        //数据缓存中不包含此设备信息，直接进行websocket数据上传
        JSONArray objecttemp = JSONArray.fromObject(result);
        result = objecttemp.toString();
        return result;
    }

    /**
     * 获取变化设备变量信息列表，并更新最后变化数据
     * @param colldevVarInfoList
     * @return
     */
    public List<DevVarInfoCache> getChangeDevVarInfoList(List<DevVarInfoCache> colldevVarInfoList) {
          List<DevVarInfoCache> changeDevVarInfoList = new ArrayList<>();
        for (int k = 0; k < colldevVarInfoList.size(); k++) {
            switch (checkContains(colldevVarInfoList.get(k))) {
                case 1://变量信息存在，值无变化
                    //System.out.println("变量信息存在，值无变化"+colldevVarInfoList.get(k).getDevId()+" VarValue:"+colldevVarInfoList.get(k).getDevVarLastValue());
                    this.update(colldevVarInfoList.get(k));
                    break;
                case 2://变量信息存在，值有变化
                    //System.out.println("变量信息存在，值有变化"+colldevVarInfoList.get(k).getDevId()+" VarValue:"+colldevVarInfoList.get(k).getDevVarLastValue());
                    changeDevVarInfoList.add(colldevVarInfoList.get(k));
                    this.update(colldevVarInfoList.get(k));
                    break;
                default://变量信值不存在
//                    System.out.println("变量信值不存在，getOrgId"+colldevVarInfoList.get(k).getOrgId()
//                            +"   getDevId:"+colldevVarInfoList.get(k).getDevId()
//                            +"   getDevVarTypeId:"+colldevVarInfoList.get(k).getDevVarTypeId());
                    changeDevVarInfoList.add(colldevVarInfoList.get(k));
                    this.add(colldevVarInfoList.get(k));
            }
        }
//        if (changeDevVarInfoList.size() > 0) {
//            //数据缓存中不包含此设备信息，直接进行websocket数据上传
//            JSONArray objecttemp = JSONArray.fromObject(changeDevVarInfoList);
//        }
        return changeDevVarInfoList;
    }

    /**
     * 筛查列表中传入设备采集变量数据
     * 有此变量信息存在，值无变化返回 1，
     * 有此变量信息存在，值有变化返回 2，
     * 否则返回0
     * @param devVarInfoCache
     * @return
     */
    public Integer  checkContains(DevVarInfoCache devVarInfoCache) {
        Integer result = 0;
        String devVarInfoKey=devVarInfoCache.getOrgId()+devVarInfoCache.getDevId()
                +devVarInfoCache.getDevVarTypeId()+devVarInfoCache.getDevVarGroupId();
        try {
            if (gDevVarInfoCacheMap.containsKey(devVarInfoKey)) {
                if(devVarInfoCache.getDevVarLastValue().equals(gDevVarInfoCacheMap.get(devVarInfoKey).getDevVarLastValue()))
                    result = 1;
                else
                    result = 2;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }



    /**
     * gDevVarInfoCacheMap
     * @param devVarInfoCache
     * @return
     */
    public boolean update(DevVarInfoCache devVarInfoCache) {
        boolean result = false;
        try {
            String devVarInfoKey=devVarInfoCache.getOrgId()+devVarInfoCache.getDevId()
                    +devVarInfoCache.getDevVarTypeId()+devVarInfoCache.getDevVarGroupId();
            gDevVarInfoCacheMap.put(devVarInfoKey, devVarInfoCache);
            //System.out.println("update_(devVarInfoCache)");
            result = true;
        } catch (Exception ex) {
            logger.info("DevVarInfoCacheMap-->update异常" + ex.toString());
        }
        return result;
    }

    /**
     * gDevVarInfoCacheMap中删除特定的通讯设备对象
     * @param devVarInfoCache
     * @return
     */
    public boolean delete(DevVarInfoCache devVarInfoCache) {
        boolean result = false;
        try {
            for (int i = 0; i < gDevVarInfoCacheMap.size(); i++) {
                if (gDevVarInfoCacheMap.get(i).getOrgId().equals(devVarInfoCache.getOrgId())
                        && gDevVarInfoCacheMap.get(i).getDevId().equals(devVarInfoCache.getDevId())
                        && gDevVarInfoCacheMap.get(i).getDevVarTypeId().equals(devVarInfoCache.getDevVarTypeId())) {
                    gDevVarInfoCacheMap.remove(i);
                    result = true;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.info("DevVarInfoCacheMap-->delete异常" + ex.toString());
        }
        return result;
    }

}
