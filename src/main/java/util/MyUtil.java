package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 常用方法工具
 * @author pingk
 *
 */
public class MyUtil {
	
	
	private static MyUtil huu = new MyUtil();
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	
	private final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	
	public static MyUtil getInstance() {
		return huu;
	}

	private MyUtil() {
	}

	public String nvl(String str) {
		if(str == null) {
			return "";
		}
		return str;
	}
	
	public void copyFile(File from, File to) throws Exception {
		FileChannel inC = new FileInputStream(from).getChannel();
		FileChannel outC = new FileOutputStream(to).getChannel();
		ByteBuffer b = null;
		int length = 1024 * 1024 * 2;
		while (true) {
			if (inC.position() == inC.size()) {
				inC.close();
				outC.close();
				return;
			}
			if ((inC.size() - inC.position()) < length) {
				length = (int) (inC.size() - inC.position());
			} else
				length = 2097152;
			b = ByteBuffer.allocateDirect(length);
			inC.read(b);
			b.flip();
			outC.write(b);
			outC.force(false);
		}
	}
	
	/**
	 * 功能: 删除指定目录及其中的�?有内容�??
	 * 
	 * @param dir
	 *            要删除的目录
	 * @return 删除成功时返回true，否则返回false�?
	 */
	public boolean deleteDirectory(File dir) {
		if ((dir == null) || !dir.isDirectory()) {
			throw new IllegalArgumentException("Argument " + dir + " is not a directory. ");
		}
		File[] entries = dir.listFiles();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].isDirectory()) {
				if (!deleteDirectory(entries[i])) {
					return false;
				}
			} else {
				if (!entries[i].delete()) {
					return false;
				}
			}
		}
		if (!dir.delete()) {
			return false;
		}
		return true;
	}
	
	public String getRandom(int length) {
		String seed = "abcd2wefg3da45678hijklmnopqrstuvwxyzABcdhnCDsxqaEFGHIJKLMNOPQRSTUVWXYZ"
				+ System.currentTimeMillis();
		String orderNo = "";
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int randnumber = random.nextInt(seed.length());
			String strRand = seed.substring(randnumber, randnumber + 1);
			orderNo += strRand;
		}
		return orderNo;
	}
	
	/**
	 * 补零
	 * 
	 * @param n
	 * @param length
	 * @return
	 */
	public String addZore(Integer n, int length) {
		StringBuffer t = new StringBuffer("");
		for (int i = 0; i < length - n.toString().length(); i++) {
			t.append("0");
		}
		return t.toString() + n.toString();
	}


	/**
	 * 分割字符
	 * 
	 * @param str
	 * @return
	 */
	private final static char splitChar = 6;

	public String[] splitStr(String str) {
		return str.split("" + splitChar);
	}


	public Date getAllDatePart(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(date);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Date getAllDatePart(String date, String pattern) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(date);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public Date TimestampToDate(java.sql.Timestamp tt) {
		GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
				.getInstance();
		gc.setTimeInMillis(tt.getTime());
		return gc.getTime();
	}


	public boolean isAllLetter(String inString) {
		Pattern p = Pattern.compile("[a-zA-Z]+");
		Matcher m = p.matcher(inString);
		return m.matches();
	}

	


	/**
	 * @return 得到工程当前目录
	 */
	public String getRealPath() {
		String path = this.getClass().getResource("").getPath();
		path = path.substring(1, path.indexOf("WEB-INF/"));
		return path;
	}

	/**
	 * 把文本编码为Html代码
	 * 
	 * @param target
	 * @return 编码后的字符
	 */
	public String htmEncode(String target) {
		StringBuffer stringbuffer = new StringBuffer();
		int j = target.length();
		for (int i = 0; i < j; i++) {
			char c = target.charAt(i);
			switch (c) {
			case 60:
				stringbuffer.append("&lt;");
				break;
			case 62:
				stringbuffer.append("&gt;");
				break;
			case 38:
				stringbuffer.append("&amp;");
				break;
			case 34:
				stringbuffer.append("&quot;");
				break;
			case 169:
				stringbuffer.append("&copy;");
				break;
			case 174:
				stringbuffer.append("&reg;");
				break;
			case 165:
				stringbuffer.append("&yen;");
				break;
			case 8364:
				stringbuffer.append("&euro;");
				break;
			case 8482:
				stringbuffer.append("&#153;");
				break;
			case 13:
				if (i < j - 1 && target.charAt(i + 1) == 10) {
					stringbuffer.append("<br>");
					i++;
				}
				break;
			case 32:
				if (i < j - 1 && target.charAt(i + 1) == ' ') {
					stringbuffer.append(" &nbsp;");
					i++;
					break;
				}
			default:
				stringbuffer.append(c);
				break;
			}
		}
		return new String(stringbuffer.toString());
	}

	/**
	 * 把时间转为标准形�?
	 * 
	 * @param dt
	 * @return 转换后的形式
	 */
	public String toString(Date dt, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String str = sdf.format(dt);
		return str;
	}

	public Date toDate(String dt) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = df.parse(dt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public Date getCurrenTime(){
		return java.util.Calendar.getInstance().getTime();
	}
	
	/**
	 * 
	 * 功能：将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处�?
	 * 
	 * @author zjy
	 * @param strIp
	 * @return
	 */
	public  long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位�?
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每�?.之间的字符串转换成整�?
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	/**
	 * 
	 * 功能：将十进制整数形式转换成127.0.0.1形式的ip地址
	 * 
	 * @author zjy
	 * @param longIp
	 * @return
	 */
	public  String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24�?
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右�?16�?
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右�?8�?
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}
	
	public static String buildCommandOrNo(String param, String commandOrNo, String channelNo) {
		if ("".equals(param)) {
			return commandOrNo;
		} else {
			return replaceParam(commandOrNo, param, channelNo);
		}
	}
	
	public static String replaceParam(String content, String param, String value) {
		return content.replace("${" + param + "}", value);
	}
	
	public static String extractParam(String param) {

		if (param.indexOf("${") != -1) {

			return param.substring(param.indexOf("${") + 2, param.indexOf("}"));
		} else {

			return "";
		}
	}
	
	public static String idDomain(String id){
		return "id("+id+")";
	}

	/**
	 * 格式化日�?
	 * yyyy-MM-dd-HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		return sdf.format(date);
	}
	
	public static String format(Calendar calendar) {
		return sdf1.format(calendar.getTime());
	}
	
	/**
     * 得到几天前的时间
     * 
     * @param d
     * @param day
     * @author xinwang.xu
     * @return
     */
    public Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }
    
    /**
     * 得到几周后的时间
     * 
     * @param d
     * @param day
     * @author xinwang.xu
     * @return
     */
    public Date getDateWeekAfter(Date d, int week) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + week);
        return now.getTime();
    }
    
    /**
     * 得到几周前的时间
     * 
     * @param d
     * @param day
     * @author xinwang.xu
     * @return
     */
    public Date getDateWeekBefore(Date d, int week) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR) - week);
        return now.getTime();
    }
    
    /**
     * 得到几天后的时间
     * 
     * @param d
     * @param day
     * @author xinwang.xu
     * @return
     */
    public Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }
    
    /**
     * 得到几小时前的时�?
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateHourBefore(Date d, int hour) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.HOUR, now.get(Calendar.HOUR) - hour);
        return now.getTime();
    }
    
    /**
     * 得到几小时后的时�?
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateHourAfter(Date d, int hour) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
        return now.getTime();
    }
    
    /**
     * 得到几个月后的时�?
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateMonthAfter(Date d, int month) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.MONTH, now.get(Calendar.MONTH) + month);
        return now.getTime();
    }
    
    /**
     * 得到几个月前的时�?
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateMonthBefore(Date d, int month) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.MONTH, now.get(Calendar.MONTH) - month);
        return now.getTime();
    }
    
    /**
     * 得到几秒前的时间
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateSecondBefore(Date d, int second) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.SECOND, now.get(Calendar.SECOND) - second);
        return now.getTime();
    }
    
    /**
     * 得到几秒后的时间
     * 
     * @param d
     * @param hour
     * @author xinwang.xu
     * @return
     */
    public Date getDateSecondAfter(Date d, int second) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.SECOND, now.get(Calendar.SECOND) + second);
        return now.getTime();
    }
    
    /**
  	 * 获取特定范围内的随机�?
  	 * @param min
  	 * @param max
  	 * @return
  	 */
  	public int getRandom(int min, int max) {
  		Random random = new Random();
  		return random.nextInt(max)%(max-min+1) + min;
  	}
  	
  	public double round(double value, int scale, int roundingMode) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(scale, roundingMode);  
        double d = bd.doubleValue();  
        bd = null;  
        return d;  
    }
  	
  	public float round(float value, int scale, int roundingMode) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(scale, roundingMode);  
        float d = bd.floatValue(); 
        bd = null;  
        return d;  
    }
  	
  	
	public List removeDuplicate(List list) {
		List retList = new ArrayList();
		HashSet h = new HashSet(list);
		retList.addAll(h);
		return retList;
	}
	
	public int daysBetween(Date early, Date late) {
        Calendar calst = Calendar.getInstance();  
        Calendar caled = Calendar.getInstance();  
        calst.setTime(early);  
        caled.setTime(late);  
        //设置时间�?0�?  
        calst.set(Calendar.HOUR_OF_DAY, 0);  
        calst.set(Calendar.MINUTE, 0);  
        calst.set(Calendar.SECOND, 0);  
        caled.set(Calendar.HOUR_OF_DAY, 0);  
        caled.set(Calendar.MINUTE, 0);  
        caled.set(Calendar.SECOND, 0);  
        //得到两个日期相差的天�?  
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst.getTime().getTime() / 1000)) / 3600 / 24;  
        return days;  
   }   
	
	public Long secondsBetween(Date early, Date late) {
        Calendar calst = Calendar.getInstance();  
        Calendar caled = Calendar.getInstance();  
        calst.setTime(early);  
        caled.setTime(late);  
        //得到两个日期相差的秒�?
        Long seconds = (caled.getTime().getTime() - calst.getTime().getTime())/1000;  
        return seconds;  
   }
	
	/** 
     * 将一�?2byte的数组转换成16的有符号short类型  (大端)
     *  
     * ＠param buf 
     *            bytes buffer 
     * ＠param byte[]中开端转换的地位 
     * ＠return convert result 
     */ 
    public short signed2ByteToShort(byte[] buf, int pos){
    	int firstByte = 0;  
        int secondByte = 0;  
        int index = pos;  
        firstByte = (0x000000FF & ((short) buf[index]));  
        secondByte = (0x000000FF & ((short) buf[index + 1]));
        return (short) (firstByte << 8 | secondByte);
    }
    
    /** 
     * �?16位的short转换成byte数组  (大端)
     *  
     * ＠param s 
     *            short 
     * ＠return byte[] 长度�?2 
     * */  
    public byte[] shortToByteArray(short s) {  
        byte[] targets = new byte[2];  
        for (int i = 0; i < 2; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }
    
    public String buildUrl(String baseUrl, Map<String, String> params){
    	StringBuffer urlBuf = new StringBuffer(baseUrl);
    	Iterator<String> keyIt = params.keySet().iterator();
    	while(keyIt.hasNext()){
    		String key = keyIt.next();
    		if(urlBuf.toString().contains("?")){
    			urlBuf.append("&");
    		}else{
    			urlBuf.append("?");
    		}
    		urlBuf.append(key).append("=").append(params.get(key));
    	}
    	return urlBuf.toString();
    }
    
    public String getBatchNo(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String batchNo = sdf.format(new Date())+getRandom(100000, 999999);
		return batchNo;
    }
	/**
	 * 文件流转字符�?
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static  String streamToString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	
 	

	/**
	 * 分割List
	 * @param list 待分割的list
	 * @param pageSize 每段list的大�?
	 * @return List<<List<T>> 
	 */
	public <T> List<List<T>> splitList(List<T> list, int pageSize) {

		int listSize = list.size(); // list的大�?
		int page = (listSize + (pageSize - 1)) / pageSize; // 页数

		List<List<T>> listArray = new ArrayList<List<T>>(); // 创建list数组
															// ,用来保存分割后的list
		for (int i = 0; i < page; i++) { // 按照数组大小遍历
			List<T> subList = new ArrayList<T>(); // 数组每一位放入一个分割后的list
			for (int j = 0; j < listSize; j++) { // 遍历待分割的list
				int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize; // 当前记录的页�?(第几�?)
				if (pageIndex == (i + 1)) { // 当前记录的页码等于要放入的页码时
					subList.add(list.get(j)); // 放入list中的元素到分割后的list(subList)
				}

				if ((j + 1) == ((j + 1) * pageSize)) { // 当放满一页时�?出当前循�?
					break;
				}
			}
			listArray.add(subList); // 将分割后的list放入对应的数组的位中
		}
		return listArray;
	}
	
	public <T> T[] listToArray(List<T> t){
		T tt[] = (T[]) new List[0];
		return t.toArray(tt);
	}
	
	public boolean checkIsValidDate(String date, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			sdf.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
    public String getUUID() {
       
    	//return UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return UUID.randomUUID().toString().toUpperCase();
}
    
    public long getIntervalTime(Date startday,Date endday) {
    	
    	if(startday!=null&&endday!=null){
    		long sl=startday.getTime();

        	long el=endday.getTime();

        	long ei=el-sl;
        	return ei;
    	}
    	return 0;
	}
    
}

