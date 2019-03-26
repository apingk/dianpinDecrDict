package handle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import httpasync.DocumentRequestUtil;


public class CodeDictForDianpingUtil {
	
	/**
	 * 暂时只发现这三个
	 */
	private static final String FULL_ENCRYPT_CSS_URL = "http://s3plus.meituan.net/v1/mss_0a06a471f9514fc79c981b5466f56b91/svgtextcss/d89007046ea2f67c77f0c084d48476b0.css";
	private static final String FULL_ENCRYPT_CSS_URL2 = "http://s3plus.meituan.net/v1/mss_0a06a471f9514fc79c981b5466f56b91/svgtextcss/1eb515893adcc915f7b5cb7d12b8772f.css";
	private static final String FULL_ENCRYPT_CSS_URL3 = "http://s3plus.meituan.net/v1/mss_0a06a471f9514fc79c981b5466f56b91/svgtextcss/a1b35e043d242cf3f26a907283c9d14f.css";
	
	private static final String ENCRYPT_COORDINATE_REGEX_PATTEN = ".(\\w{6})\\{background:-([0-9]+?).0px\\s-([0-9]+?).0px";
	private static final String DECODE_COORDINATE_URL_REGEX_PATTEN = "\\w{1}\\[class\\^\\=\"(\\w{3})\"\\].*?url\\(//(.*?)\\)";
	
	
	/**
	 * 获取加密坐标和解密文本池SVG文件
	 * @param url
	 * @return
	 */
	private static String _requestCssUrl(String url){
		String cssContent;
		try {
			Document html = DocumentRequestUtil.request(url);
			cssContent = html.body().text();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cssContent;
	}
	
	/**
	 * 获取加密坐标
	 * @param source
	 * @return
	 */
	private static Map<String,Integer[]> getEncryptCoordinateMatcher(String source) {  
		if(StringUtil.isBlank(source)){
			return null;
		}
        Pattern pattern = Pattern.compile(ENCRYPT_COORDINATE_REGEX_PATTEN);
        Matcher matcher = pattern.matcher(source);
        Map<String,Integer[]> resultMap = new HashMap<String,Integer[]>();
        while (matcher.find()) {
        	resultMap.put(matcher.group(1), new Integer[]{Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3))});
        }
        return resultMap;
    }
	
	/**
	 * 获取文本池SVG文件&&组装解密字典
	 * @param source
	 * @return
	 */
	private static Map<String,String> getDecodeCoordinateMatcher(String source) {  
        Pattern pattern = Pattern.compile(DECODE_COORDINATE_URL_REGEX_PATTEN);
        Matcher matcher = pattern.matcher(source);
        Map<String,String> resultMap = new HashMap<String,String>();
        while (matcher.find()) {
        	String encryptCodePre3 = matcher.group(1);
        	String svgUrl = matcher.group(2);
        	System.out.println(encryptCodePre3 + "--" + svgUrl);
        	//解析svg文件
        	try {
				Document svgDoc = DocumentRequestUtil.request("http://" + svgUrl,true);
				Elements textDocs = svgDoc.getElementsByTag("text");
				for(int i=0;i<textDocs.size();i++){//逐行扫描
					resultMap.put(encryptCodePre3 + "-" + textDocs.get(i).attr("y"), textDocs.get(i).text());  
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        return resultMap;
    }
	
	/**
	 * 生成密码本（只包含一部分）
	 */
	public static Map<String,String> buildCodeDict(){
		String cssContent = _requestCssUrl(FULL_ENCRYPT_CSS_URL);
		cssContent += _requestCssUrl(FULL_ENCRYPT_CSS_URL2);
		cssContent += _requestCssUrl(FULL_ENCRYPT_CSS_URL3);
		Map<String, Integer[]> encryptCoordinateMap = getEncryptCoordinateMatcher(cssContent);
		Map<String, String> decodeCoordinateMap = getDecodeCoordinateMatcher(cssContent);
		
		Map<String,String> resultMap = new HashMap<String,String>();
		Iterator<Map.Entry<String, Integer[]>> entries = encryptCoordinateMap.entrySet().iterator(); 
		while (entries.hasNext()) { 
		  Map.Entry<String, Integer[]> entry = entries.next();
		  String encryptKey = entry.getKey();
		  if(encryptKey.startsWith("xpn") || encryptKey.startsWith("wqd") || encryptKey.startsWith("pzs") ||  encryptKey.startsWith("qfr") ){//中文字符编码1
			  String decodeContentLine = decodeCoordinateMap.get(encryptKey.substring(0, 3) + "-" + String.valueOf((entry.getValue()[1] + 23)));
			  char goalCode = decodeContentLine.charAt(entry.getValue()[0] / 14);
			  resultMap.put(encryptKey, String.valueOf(goalCode));
		  }else if(encryptKey.startsWith("zog") || encryptKey.startsWith("oul") || encryptKey.startsWith("vhk")){//数字
			  String decodeContentLine = decodeCoordinateMap.get(encryptKey.substring(0, 3) + "-" + String.valueOf((entry.getValue()[1] + 23)));
			  char goalCode = decodeContentLine.charAt((entry.getValue()[0] - 8) / 14);
			  resultMap.put(encryptKey, String.valueOf(goalCode));
		  }else if(encryptKey.startsWith("hpr")){//还没找到解码方式的数字
			  String decodeContentLine = decodeCoordinateMap.get(encryptKey.substring(0, 3) + "-" + String.valueOf((entry.getValue()[1] + 24)));
//			  char goalCode = decodeContentLine.charAt((entry.getValue()[0] - 8) / 14);//FIXME 
//			  resultMap.put(encryptKey, String.valueOf(goalCode));
		  }else{
			  //新密码，需做记录
		  }
		  
		}
		return resultMap;
	}
	
	
	public static void main(String[] args) {
		Map<String, String> buildCodeDict = buildCodeDict();
		System.out.println(buildCodeDict);
		
	}
	
	
}
