package cms;

import java.io.IOException;
import java.util.List;

import cms.entity.CmsProtocalEntity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class IGroupCmsProtocol {		
	/**
	 * 将通用播放表协议的json串转换为实体对象
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public List<CmsProtocalEntity> cmsProtocalJson2Entity(String protocalJson) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();  
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, CmsProtocalEntity.class);
		List<CmsProtocalEntity> list = objectMapper.readValue(protocalJson, javaType);		
		return list;
	}
	
	/**
	 * 将情报板通用协议字符串转换为具体的情报板播放表字符串
	 * @return
	 */
	public abstract String buildProtocal(String json);
	/**
	 * 将具体的情报板播放表字符串转换为情报板通用协议json串
	 */
	public abstract String parseProtocalJson(String protocolString);
	
	/**
	 * 将具体的情报板播放表字符串转换为情报板通用协议实体
	 */
	public abstract List<CmsProtocalEntity> parseProtocalEntity(String protocolString);
	
	/**
	 * 使用1字节就可以表示b
	 * 
	 * @param b
	 * @return
	 */
	public String numToHex8(int b) {
		return String.format("%02x", b);// 2表示需要两个16进行数
	}

	/**
	 * 需要使用2字节表示b
	 * 
	 * @param b
	 * @return
	 */
	public String numToHex16(int b) {
		return String.format("%04x", b);
	}

	/**
	 * 需要使用4字节表示b
	 * 
	 * @param b
	 * @return
	 */
	public String numToHex32(int b) {
		return String.format("%08x", b);
	}
	
	/**
	 * 16进制字符串转换为字节数组
	 * @param str
	 * @return
	 */
	public byte[] hexStrToBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
	
	/**
	 * 将字节数组转换为16进制字符串
	 * @param bytes
	 * @return
	 */
	public String bytesToHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }
	
	/**
	 * 替换掉字符串中所有的字母
	 * @param str
	 * @return
	 */
	public String replaceAllLetters(String str) {
		String strNew = str.replaceAll("[a-zA-Z]", "");
		return strNew;
	}
	
}
