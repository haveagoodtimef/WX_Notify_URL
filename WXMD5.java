package core.util;

import java.security.MessageDigest;

/**
 * 微信的md5加密
 * @author feng-hong-zhang
 *
 * 2017年9月27日
 */
public class WXMD5 {
	 public static String MD5(String data) throws Exception {
	        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(data.getBytes("UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        for (byte item : array) {
	            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
	        }
	        return sb.toString().toUpperCase();
	  }
}
