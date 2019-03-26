package httpasync;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import util.MyUtil;


public class DocumentRequestUtil {

	private static List<String> userAgentList = new ArrayList<String>();
	static {
		//几个有效的user-agent
		userAgentList.add(
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		userAgentList.add(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		userAgentList.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
	}

	public static void main(String[] args) {
	}

	public static Document request(String url) throws Exception {
		return request(url, false);
	}

	public static Document request(String url, boolean ignoreContentType) throws Exception {
		String userAgent = getUserAgent();

		Document doc = null;
		try {
			doc = Jsoup.connect(url).ignoreContentType(ignoreContentType).header("User-Agent", userAgent)
					.header("X-Forwarded-For", genIp()).header("Accept-Encoding", "gzip, deflate")
					.header("Host", "www.dianping.com").header("Accept-Language", "zh-CN,zh;q=0.8")
					.header("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
					.timeout(30000).get();
		} catch (Exception e) {
			System.out.println(userAgent);
			e.printStackTrace();
		}
		return doc;
	}

	private static String genIp() {
		return MyUtil.getInstance().getRandom(1, 254) + "." + MyUtil.getInstance().getRandom(1, 254) + "."
				+ MyUtil.getInstance().getRandom(1, 254) + "." + MyUtil.getInstance().getRandom(1, 254);
	}

	public static Map<String,String> getDocByProxy(String href, String ip, Integer port) {
		InputStream is = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		int responseCode  = 0;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
			URL url = new URL(href);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection(proxy);
			urlcon.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			urlcon.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlcon.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			urlcon.setRequestProperty("Host", "www.dianping.com");
			urlcon.setRequestProperty("user-agent", getUserAgent());
			urlcon.setUseCaches(false);
			urlcon.setConnectTimeout(6000);
			urlcon.setReadTimeout(6000);
			System.out.println("*****获取连接**"+href+"***");
			urlcon.connect(); // 获取连接
			responseCode = urlcon.getResponseCode();
			Map<String, List<String>> headerFields = urlcon.getHeaderFields();
			String contentEncoding = urlcon.getContentEncoding();
			System.out.println("*****响应码："+responseCode+"*****");
			System.out.println("*****响应Encoding："+contentEncoding+"*****");
			GZIPInputStream gZIPInputStream = null;
			
			if(responseCode != 200){
				return null;
			}
			if("gzip".equals(contentEncoding)){
				gZIPInputStream = new GZIPInputStream(urlcon.getInputStream());
	            bufferedReader = new BufferedReader(new InputStreamReader(gZIPInputStream));
	            String line = null;
	            while ((line = bufferedReader.readLine()) != null) {
	                //转化为UTF-8的编码格式
	                line = new String(line.getBytes("UTF-8"));
	                stringBuffer.append(line);
	            }
	            bufferedReader.close();
			}else{
				is = urlcon.getInputStream();
				inputStreamReader = new InputStreamReader(is, "utf-8");
				bufferedReader = new BufferedReader(inputStreamReader);
				// 创建StringBuffer，用来接收输入流的读取内容
				
				String temp = null;
				// 通过while语句不断的读取数据放入stringbuffer
				while ((temp = bufferedReader.readLine()) != null) {
					stringBuffer.append(temp);
				}
				bufferedReader.close();
				inputStreamReader.close();
				is.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Map<String,String> map = new HashMap<String,String>();
			map.put("responseCode", String.valueOf(responseCode));
			map.put("responseBodyLength", String.valueOf(stringBuffer.length()));
			map.put("responseBody", stringBuffer.toString());
			return map;
		}
		
	}

	private static String getUserAgent() {
		return userAgentList.get(MyUtil.getInstance().getRandom(0, userAgentList.size() - 1));
	}

}
