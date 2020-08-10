package com.boco.rabbitmqcommconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service("devcommInfoDataServiceImpl")
public class DevRabbitmqCommInfoDataServiceImpl implements DevRabbitmqCommInfoDataService {
    private static final Logger logger = LoggerFactory.getLogger(DevRabbitmqCommInfoDataServiceImpl.class);
    @Override
    public List<DevRabbitmqCommInfo> getDevcommInfoList() throws Exception {

        DevRabbitmqCommInfoList devcommInfoList=null;

        StringBuffer buffer = new StringBuffer();
        try {

            File file = new File("config/devCommConfig.xml"); // 这里表示从jar同级目录加载
            if (!file.exists()) { // 如果同级目录没有，则去config下面找
                //logger.info("getDevcommInfoList读取路径----------------->!file.exists()");
                file = new File("config/devCommConfig.xml");
            }

            Resource resource = new FileSystemResource(file);

            if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
                //logger.info("getDevcommInfoList读取路径----------------->从jar包中获取");
                resource = new ClassPathResource("./config/devCommConfig.xml");
            }

//        Resource resource = new ClassPathResource("config/devCommConfig.xml");
//        //利用输入流获取XML文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        String line = "";
        while ((line = br.readLine()) != null) {
            //logger.info("line----------------->" + line.toString());
            buffer.append(line);
        }
        br.close();
        //XML转为JAVA对象
        devcommInfoList = (DevRabbitmqCommInfoList) XmlBuilder.xmlStrToObject(DevRabbitmqCommInfoList.class, buffer.toString());
      } catch (Exception ex) {
            logger.info("getlistDevVarInfo读取异常----------------->" + ex.toString());
        }
        return devcommInfoList.getDevcommInfoLst();
    }

    @Override
    public DevRabbitmqCommInfo getCurDevcommInfo(String devid)  throws Exception {
        DevRabbitmqCommInfo result = new DevRabbitmqCommInfo();

        File file = new File("config/devCommConfig.xml"); // 这里表示从jar同级目录加载
        if (!file.exists()) { // 如果同级目录没有，则去config下面找
            logger.info("getDevcommInfoList读取路径----------------->!file.exists()");
            file = new File("config/devCommConfig.xml");
        }

        Resource resource = new FileSystemResource(file);

        if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
            //logger.info("getDevcommInfoList读取路径----------------->从jar包中获取");
            resource = new ClassPathResource("./config/devCommConfig.xml");
        }

        //读取Resource目录下的XML文件
        //Resource resource = new ClassPathResource("config/devCommConfig.xml");

        //利用输入流获取XML文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));

        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        br.close();
        //XML转为对象
        DevRabbitmqCommInfoList devcommInfoList = (DevRabbitmqCommInfoList) XmlBuilder.xmlStrToObject(DevRabbitmqCommInfoList.class, buffer.toString());
        List<DevRabbitmqCommInfo> DevcommInfos = devcommInfoList.getDevcommInfoLst();
        for (int k = 0; k < DevcommInfos.size(); k++) {
            if (DevcommInfos.get(k).getDevid().equals(devid)) {
                return DevcommInfos.get(k);
            }

        }
        return result;
    }
}
