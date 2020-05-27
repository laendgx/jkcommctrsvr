package com.boco.commconfig;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service("devcommInfoDataServiceImpl")
public class DevcommInfoDataServiceImpl implements DevcommInfoDataService {
    @Override
    public List<DevcommInfo> listDevcommInfo() throws Exception {
        //读取Resource目录下的XML文件
        Resource resource = new ClassPathResource("./commconfig/devCommConfig.xml");
        //利用输入流获取XML文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "GBK"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        br.close();
        //XML转为JAVA对象
        DevcommInfoList devcommInfoList = (DevcommInfoList) XmlBuilder.xmlStrToObject(DevcommInfoList.class, buffer.toString());
        return devcommInfoList.getDevcommInfoLst();
    }

    @Override
    public DevcommInfo getCurDevcommInfo(String devid)  throws Exception
    {
        DevcommInfo result=new DevcommInfo();

        //读取Resource目录下的XML文件
        Resource resource = new ClassPathResource("./commconfig/devCommConfig.xml");
        //利用输入流获取XML文件内容
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "GBK"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        br.close();
        //XML转为对象
        DevcommInfoList devcommInfoList = (DevcommInfoList) XmlBuilder.xmlStrToObject(DevcommInfoList.class, buffer.toString());
        List<DevcommInfo> DevcommInfos=devcommInfoList.getDevcommInfoLst();
        for (int k = 0; k < DevcommInfos.size(); k++) {
            if(DevcommInfos.get(k).getDevid().equals(devid))
            {
                return DevcommInfos.get(k);
            }

        }
        return result;
    }
}
