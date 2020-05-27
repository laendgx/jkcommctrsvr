package com.boco.commonCenter;

import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送数据缓存信息
 */
public class TDevVarInfoCacheMap {
    private static final Logger logger= LoggerFactory.getLogger(TDevVarInfoCacheMap.class.getName());

    /**
     * 发送数据缓存信息
     */
    public List<DevVarInfoCache> gDevVarInfoCacheMap;

    /**
     * 构造方法
     */
    public TDevVarInfoCacheMap(){
        gDevVarInfoCacheMap = new ArrayList<>();
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
     * gDevVarInfoCacheMap
     * @param devVarInfoCache
     * @return
     */
    public boolean add(DevVarInfoCache devVarInfoCache) {
        boolean result = false;
        try {
            for (int i = 0; i < gDevVarInfoCacheMap.size(); i++) {
                if (gDevVarInfoCacheMap.get(i).getOrgId().equals(devVarInfoCache.getOrgId())
                        && gDevVarInfoCacheMap.get(i).getDevId().equals(devVarInfoCache.getDevId())
                        && gDevVarInfoCacheMap.get(i).getDevvartypeid().equals(devVarInfoCache.getDevvartypeid())
                        && gDevVarInfoCacheMap.get(i).getDevvargroupid().equals(devVarInfoCache.getDevvargroupid())
                ) {
                    gDevVarInfoCacheMap.get(i).setCollCtrTime(devVarInfoCache.getCollCtrTime());
                    gDevVarInfoCacheMap.get(i).setDevvarlastvalue(devVarInfoCache.getDevvarlastvalue());
                    //System.out.println("update_(devVarInfoCache)");
                    result = true;
                    break;
                }
            }
            if (!result) {
                gDevVarInfoCacheMap.add(devVarInfoCache);
                System.out.println("add_(devVarInfoCache)");
            }
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
        if (gDevVarInfoCacheMap.size() > 0) {
            //数据缓存中不包含此设备信息，直接进行websocket数据上传
            JSONArray objecttemp = JSONArray.fromObject(gDevVarInfoCacheMap);
            result = objecttemp.toString();
        }
        return result;
    }

    /**
     * 获取变化设备变量信息列表，并更新最后变化数据
     * @param colldevVarInfoList
     * @return
     */
    public String getChangeDevVarInfoList(List<DevVarInfoCache> colldevVarInfoList) {
        String result = "";
        List<DevVarInfoCache> changeDevVarInfoList = new ArrayList<>();
        for (int k = 0; k < colldevVarInfoList.size(); k++) {
            switch (checkContains(colldevVarInfoList.get(k))) {
                case 1://变量信息存在，值无变化
                    this.update(k, colldevVarInfoList.get(k));
                    break;
                case 2://变量信息存在，值有变化
                    changeDevVarInfoList.add(colldevVarInfoList.get(k));
                    this.update(k, colldevVarInfoList.get(k));
                    break;
                default://变量信值不存在
                    changeDevVarInfoList.add(colldevVarInfoList.get(k));
                    this.add(colldevVarInfoList.get(k));
            }
        }
        if (changeDevVarInfoList.size() > 0) {
            //数据缓存中不包含此设备信息，直接进行websocket数据上传
            JSONArray objecttemp = JSONArray.fromObject(changeDevVarInfoList);
            result = objecttemp.toString();
        }
        return result;
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
        try {
            for (int i = 0; i < gDevVarInfoCacheMap.size(); i++) {
                if (gDevVarInfoCacheMap.get(i).getOrgId().equals(devVarInfoCache.getOrgId())
                        && gDevVarInfoCacheMap.get(i).getDevId().equals(devVarInfoCache.getDevId())
                        && gDevVarInfoCacheMap.get(i).getDevvartypeid().equals(devVarInfoCache.getDevvartypeid())) {
                    if (gDevVarInfoCacheMap.get(i).getDevvarlastvalue().equals(devVarInfoCache.getDevvarlastvalue()))
                        result = 1;
                    else
                        result = 2;
//                    if(result==1) {//判断分组变化信息
//                        if (gDevVarInfoCacheMap.get(i).getDevvargroupid().equals(devVarInfoCache.getDevvargroupid()))
//                            result = 1;
//                        else
//                            result = 2;
//                    }
                    break;
                }
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
    public boolean update(Integer i,DevVarInfoCache devVarInfoCache) {
        boolean result = false;
        try {
            gDevVarInfoCacheMap.get(i).setDevvargroupid(devVarInfoCache.getDevvargroupid());
            gDevVarInfoCacheMap.get(i).setCollCtrTime(devVarInfoCache.getCollCtrTime());
            gDevVarInfoCacheMap.get(i).setDevvarlastvalue(devVarInfoCache.getDevvarlastvalue());
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
                        && gDevVarInfoCacheMap.get(i).getDevvartypeid().equals(devVarInfoCache.getDevvartypeid())) {
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
