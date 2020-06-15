package com.boco.rabbitmqcommconfig;

import java.util.List;

public interface DevRabbitmqCommInfoDataService {
    List<DevRabbitmqCommInfo> getDevcommInfoList() throws Exception;
    DevRabbitmqCommInfo getCurDevcommInfo(String devid)  throws Exception;
}
