package cms.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cms.IGroupCmsProtocol;
import cms.cmsconst.CmsTypeConstant;
import cms.entity.CMSIconParam;
import cms.entity.CMSWordParam;
import cms.entity.CmsProtocalEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 南京金晓情报板播放表类
 * @author 孙冠义
 *
 */
public class GroupNJJXCmsProtocol extends IGroupCmsProtocol {
	
	/**
	 * 将通用的播放表转换为可变情报板南京金晓的播放表
	 */
	@Override
	public String buildProtocal(String json) {
		String result = "";
		List<CmsProtocalEntity> list;
		
		try {
			list = this.cmsProtocalJson2Entity(json);
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		
		int id = 0;
		result = "[playlist]" + "\r\n" + 
				"item_no=" + list.size() +  "\r\n";
		for(CmsProtocalEntity entity : list){
			String itemProtocal = this.buildItemProtocal(entity);
			itemProtocal = "item" + id + "=" + itemProtocal + "\r\n";
			result += itemProtocal;
			id++;
		}
		
		return result;
	}

	/**
	 * 将具体的情报板播放表字符串转换为情报板通用协议实体
	 */
	@Override
	public List<CmsProtocalEntity> parseProtocalEntity(String protocolString) {
		List<CmsProtocalEntity> list = new ArrayList<CmsProtocalEntity>();
		
		String splitStr = "\r\n";
		String[] splits = protocolString.split(splitStr);
		if (splits.length < 3){
			return list;
		}
		
		String[] itemArr = Arrays.copyOfRange(splits, 2, splits.length);
		for(String item : itemArr){
			CmsProtocalEntity entity = new CmsProtocalEntity();
			int index = item.indexOf("=");		
			item = item.substring(index + 1);
			
			System.out.println(item);
			
			//停留时间
			index = item.indexOf(",");
			String ele = item.substring(0, index);
			entity.setTimeDelay(Integer.valueOf(ele));			
			item = item.substring(index + 1);
			System.out.println("ele=" + ele);
			
			//显示方式
			index = item.indexOf(",");
			ele = item.substring(0, index);
			entity.setTransition(Integer.valueOf(ele));			
			item = item.substring(index + 1);
			System.out.println("ele=" + ele);
			
			//显示方式
			index = item.indexOf(",");
			ele = item.substring(0, index);
			entity.setParam(Integer.valueOf(ele));			
			item = item.substring(index + 1);
			
			System.out.println("item ==>" + item);
			this.parseGraphWord(item, entity);
			list.add(entity);			
		}
		return list;
	}
	
	/**
	 * 将具体的情报板播放表字符串转换为情报板通用协议json串
	 * @throws JsonProcessingException 
	 * @throws Exception 
	 */
	@Override
	public String parseProtocalJson(String protocolString){
		List<CmsProtocalEntity> list = this.parseProtocalEntity(protocolString);
		ObjectMapper objectMapper = new ObjectMapper();
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, CmsProtocalEntity.class);
		String result = null;
		try {
			result = objectMapper.writeValueAsString(list);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 将情报板的单屏播放表的图标参数、文字参数转换为实体
	 * @param item
	 * @param entity
	 */
	private void parseGraphWord(String item, CmsProtocalEntity entity){
		if (entity == null){
			return;
		}
		
		List<CMSIconParam> graphList = new ArrayList<CMSIconParam>();
		List<CMSWordParam> wordList = new ArrayList<CMSWordParam>();
		
		int length = item.length();
		int index = 0;
		String x = null;
		String y = null;
		String fontColor = null;
		String fontBackColor = null;
		String fontShadowColor = null;
		String wordSpace = null;
		String fontName = null;
		String fontSize_HH = null;
		String fontSize_WW = null;
		String wordContent = null;
		String graphId = null;
		boolean flag = false;
		while(index < length){
			char mychar = item.charAt(index);
			index++;
			
			//功能码
			char funcChar;
			if (mychar == '\\'){				
				funcChar = item.charAt(index);
				index++;
				
				switch (funcChar) {				
				case 'C':
					//位置
					x = item.substring(index, index + 3);
					index += 3;
					y = item.substring(index, index + 3);
					index += 3;
					break;
				case 'B':
					// %SYSDIR%\BMP 目录下的图标
					graphId = item.substring(index, index + 3);
					index +=3;
					graphList.add(this.createCMSIconParam(x, y, graphId));
					x = null;
					y = null;
					break;
				case 'I':
					//%SYSDIR%\ICO 目录下的图标
					index +=3;
					break;
				case 'F':
					//%SYSDIR%\FLC 目录下的动画
					index += 5;
					break;
				case 'y':
					index += 1;
					break;
				case 'c' :
					//字符颜色
					if (item.charAt(index) == 't'){
						fontColor = "255255000000";
						index += 1;
					} else {
						fontColor = item.substring(index, index+12);
						index += 12;
					}
					break;
				case 'b':
					//背景色
					if (item.charAt(index) == 't'){
						index += 1;
					} else {
						fontBackColor = item.substring(index, index+12);
						index += 12;
					}
					break;
				case 's' :
					//字符阴影颜色
					if (item.charAt(index) == 't'){
						index += 1;
					} else {
						fontShadowColor = item.substring(index, index+12);
						index += 12;
					}
					break; 
				case 'S':
					wordSpace = item.substring(index, index + 2);
					index += 2;
					break;
				case 'f':
					fontName = item.substring(index, index + 1);
					index += 1;
					fontSize_HH = item.substring(index, index + 2);
					index += 2;
					fontSize_WW = item.substring(index, index + 2);
					index += 2;
					break;
				case 'N':
					index += 2;
					break;
				case 'r':
					index += 12;
					break;			
				case 'n':		
					flag = true;
					break;
				default:
					break;
				}
			} else {
				index--;
				String subWords = item.substring(index);
				if (subWords.trim().length() > 0) {
					int charIndex = subWords.indexOf('\\');
					if (charIndex < 0) {
						if (flag) {
							flag = true;
							try{
								int h = Integer.parseInt(fontSize_HH);
								int lastY = Integer.parseInt(y);
								int presentY = lastY + h;
								y = presentY + "";
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}
						
						wordContent = subWords;
						index += subWords.length();
						//判断情报板类型
						if (fontColor != null && fontColor.length() == 12){
							if (fontColor.substring(9).equals("255")){
								//琥珀色
								entity.setDispScrType(CmsTypeConstant.CmsType_HuPo);
							} else {
								//双基色或全彩
								entity.setDispScrType(CmsTypeConstant.CmsType_Colors);
							}
						} else {
							//不需要指定颜色情报板
							entity.setDispScrType(CmsTypeConstant.CmsType_None);
						}
						CMSWordParam wordParam = this.createCMSWordParam(x, y, fontColor, fontBackColor, fontShadowColor,
								wordSpace, fontName, fontSize_HH, fontSize_WW, wordContent);
						wordList.add(wordParam);						
					} else {
						if (flag) {
							flag = true;
							try{
								int h = Integer.parseInt(fontSize_HH);
								int lastY = Integer.parseInt(y);
								int presentY = lastY + h;
								y = presentY + "";
							}catch(Exception ex){
								ex.printStackTrace();
							}
						} 
						wordContent = item.substring(index, index + charIndex);
						index += charIndex;
						//判断情报板类型
						if (fontColor != null && fontColor.length() == 12){
							if (fontColor.substring(9).equals("255")){
								//琥珀色
								entity.setDispScrType(CmsTypeConstant.CmsType_HuPo);
							} else {
								//双基色或全彩
								entity.setDispScrType(CmsTypeConstant.CmsType_Colors);
							}
						} else {
							//不需要指定颜色情报板
							entity.setDispScrType(CmsTypeConstant.CmsType_None);
						}
						CMSWordParam wordParam = this.createCMSWordParam(x, y, fontColor, fontBackColor, fontShadowColor, 
								wordSpace, fontName, fontSize_HH, fontSize_WW, wordContent);
						wordList.add(wordParam);
					}
				}
			}
			
		}
		entity.setGraphList(graphList);
		entity.setWordList(wordList);
	}
	
	/**
	 * 创建情报板文字实体
	 * @param x
	 * @param y
	 * @param fontColor
	 * @param fontBackColor
	 * @param fontShadowColor
	 * @param wordSpace
	 * @param fontName
	 * @param fontSize_HH
	 * @param fontSize_WW
	 * @param wordContent
	 * @return
	 */
	private CMSWordParam createCMSWordParam(String x, String y, String fontColor,
			String fontBackColor, String fontShadowColor, String wordSpace,
			String fontName, String fontSize_HH, String fontSize_WW,
			String wordContent){
		
		CMSWordParam para = new CMSWordParam();
		para.setWordXXX(Integer.valueOf(x));
		para.setWordYYY(Integer.valueOf(y));
		para.setFontColor(this.rgb2HexColorString(fontColor));
		para.setFontBackColor(this.rgb2HexColorString(fontBackColor));
		para.setFontShadowColor(this.rgb2HexColorString(fontShadowColor));
		para.setFontName(fontName);
		para.setFontSize_HH(Integer.valueOf(fontSize_HH));
		para.setFontSize_WW(Integer.valueOf(fontSize_WW));
		para.setWordSpace(Integer.valueOf(wordSpace));
		para.setWordContent(wordContent);
		
		return para;
	}
	/**
	 * 创建情报板图标实体
	 * @param xxx
	 * @param yyy
	 * @param graphId
	 * @return
	 */
	private CMSIconParam createCMSIconParam(String xxx, String yyy, String graphId){
		CMSIconParam para = new CMSIconParam();
		Integer x = 0;
		Integer y = 0;
		if (xxx != null){
			x = Integer.valueOf(xxx);
		}
		if (yyy != null){
			y = Integer.valueOf(yyy);
		}
		
		para.setGraphXXX(x);
		para.setGraphYYY(y);
		para.setGraphId(graphId);
		
		return para;
	}
	
	/**
	 * 将rgb转换为16进制颜色值
	 * @param rgb
	 * @return
	 */
	private String rgb2HexColorString(String rgb){
		String result = null;
		if (rgb == null || rgb.trim().length() == 0){
			return result;
		}
		String r = rgb.substring(0,3);
		String g = rgb.substring(3, 6);
		String b = rgb.substring(6, 9);
		String y = rgb.substring(9);				
		
		if (y.equals("255")){
			//琥珀色
			result = "FFFF00";
		} else {
			result = String.format("%02x", Integer.valueOf(r)) +
				String.format("%02x", Integer.valueOf(g)) +
				String.format("%02x", Integer.valueOf(b));
		}
		return result;
	}
	
	/**
	 * 将单条播放表转换为情报板协议
	 * @param item
	 * @return
	 */
	private String buildItemProtocal(CmsProtocalEntity item){
			Integer delay = item.getTimeDelay() * 100;
			Integer trans = 1;//item.getTransition();
			Integer para = item.getParam();
			
			if (trans > 5){
				trans = 1;
			}
			
			String protocolString = delay + "," + trans + "," + para + ",";
			
			List<CMSIconParam> graphs = item.getGraphList();
			String graphProtocol = graphParaToString(graphs);
			protocolString += graphProtocol;
			
			protocolString += this.wordParaToString(item);
			return protocolString;		
	}
	
	/**
	 * 将可变情报板的文字参数转换为协议字符串
	 * @param //list
	 * @return
	 */
	private String wordParaToString(CmsProtocalEntity item){
		String result = "";
		if (item == null){
			return result;
		}
		
		List<CMSWordParam> list = item.getWordList();
		if (list == null || list.size() == 0){
			return result;
		}
		
		for(CMSWordParam para : list){
			if (para.getWordXXX() == null){
				result += "\\C000";
			}else{
				result += "\\C" + this.intTo3SizeString(para.getWordXXX());
			}
			
			if (para.getWordYYY() == null){
				result += "000";
			} else{
				result += this.intTo3SizeString(para.getWordYYY());
			}
			
			switch (item.getDispScrType()) {
			//双基色或者全彩
			case 0:		
				//字符颜色
				if (para.getFontColor() == null){
					result += "\\c255255000000";
				} else {
					result += "\\c" + this.hexColor2RGB(para.getFontColor());
				}
				
//				result += "\\c000000000255";
				//背景颜色
				result += "\\bt";
//				if (para.getFontBackColor() != null){
//					result += "\\b" + this.hexColor2RGB(para.getFontBackColor());
//				}
				break;
			//琥珀色
			case 1:	
				result += "\\c000000000255";
				break;
			//无颜色
			case 2:	
				result += "\\c000000000255";
				break;
			default:
				break;
			}
			
			//字体
			if (para.getFontName() == null){
				result += "\\fh";
			} else {
				result += "\\f" + para.getFontName();
			}
			
			//字体大小－高度
			if (para.getFontSize_HH() == null){
				result += "32";
			} else {
				result += String.format("%02d", para.getFontSize_HH());
			}
			
			//字体大小－宽度
			if (para.getFontSize_WW() == null){
				result += "32";
			}else{
				result += String.format("%02d", para.getFontSize_WW());
			}
			
			//字间距
			result += "\\S" + this.workspaceTo2SizeString(para.getWordSpace());	
			
			//文字内容
			if (para.getWordContent() != null){
				result += para.getWordContent();
			}
		}
		
		return result;
	}
	
	/**
	 * 将可变情报板图标参数转换为协议字符串
	 */
	private String graphParaToString(List<CMSIconParam> list) {
		String result = "";
		if (list == null || list.size() == 0)
			return result;
		
		for(CMSIconParam icon : list){
		result +=  "\\C" + this.intTo3SizeString(icon.getGraphXXX()) + this.intTo3SizeString(icon.getGraphYYY()) +
			   "\\B" + icon.getGraphId();
		}
		return result;
	}
	
	
	/**
	 * 将整数转换成3位的字符串
	 * @param value
	 * @return
	 */
	private String intTo3SizeString(Integer value) {
		String str = "";
		if (value == null) {
			str = "000";
		} else {
			str = String.format("%03d", value);
		}
		return str;
	}
	
	/**
	 * 将字间距转换为两位的字符串
	 * @param value
	 * @return
	 */
	private String workspaceTo2SizeString(Integer value){
		String result = "00";
		
		if (value == null){
			return result;
		}
		
		if (value > 99){
			result = "99";
			return result;
		}
		
		result = String.format("%02d", value);		
		return result;
	}
	
	/**
	 * 将16进制颜色值转换为rgb字符串
	 * @return
	 */
	private String hexColor2RGB(String hex){
		String result = "";
		String str = hex.replaceAll("^#", "");
		
		try {
			int color = Integer.valueOf(str, 16);
			short b = (short) (color & 0xFF);
			short g = (short) ((color >> 8) & 0xFF);
			short r = (short) ((color >> 16) & 0xFF);
			String bsz = String.format("%03d", b);
			String gsz = String.format("%03d", g);
			String rsz = String.format("%03d", r);
			
			result = rsz + gsz + bsz + "000";
		} catch (Exception ex) {
			result = "255255000000";
		}
		return result;
	}

	
}
