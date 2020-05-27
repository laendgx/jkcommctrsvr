package com.boco.commconfig;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class devCommUtils {
    /**
     * 设备编码
     */
    private static List<DevComms> devCommInfoListtemp;

    static {
        try {
            devCommInfoListtemp=new ArrayList<>();

            Resource resource = new ClassPathResource("./commconfig/devCommConfig.xml");
            InputStream InputStreamtemp=resource.getInputStream();
            //利用输入流获取XML文件内容
            BufferedReader br = new BufferedReader(new InputStreamReader(InputStreamtemp, "UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            br.close();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new InputSource(new ByteArrayInputStream(String.valueOf(buffer).getBytes("UTF-8"))));
            NodeList deveceNodes = doc.getElementsByTagName("devece");
            for (int k = 0; k < deveceNodes.getLength(); k++) {
                Node attr = deveceNodes.item(k);
                // 获取结点 student 结点的所有属性
                 NamedNodeMap namedNodeMap = attr.getAttributes();
                // 获取界定 student 的 属性 id
                //Node devid = namedNodeMap.getNamedItem("devid");
                String devid = namedNodeMap.getNamedItem("devid").getTextContent();
                String exchangeName = namedNodeMap.getNamedItem("exchangeName").getTextContent();
                String QueueName = namedNodeMap.getNamedItem("QueueName").getTextContent();
                String createtime = namedNodeMap.getNamedItem("createtime").getTextContent();
                DevComms tempdevComm=new DevComms();
                tempdevComm.setDeviceid(devid);
                tempdevComm.setExchangeName(exchangeName);
                tempdevComm.setQueueName(QueueName);
                tempdevComm.setCreatetime(createtime);
                devCommInfoListtemp.add(tempdevComm);
                System.out.println(devid);
            }
            System.out.println(devCommInfoListtemp);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
