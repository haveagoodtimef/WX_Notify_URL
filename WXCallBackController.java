package com.jeefw.controller.sys;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeefw.core.BaseDao4;
import com.jeefw.core.Constant;
import com.jeefw.core.JavaEEFrameworkBaseController;
import com.jeefw.model.sys.Dict;
import com.jeefw.model.sys.RechargeRecordError;

import core.util.WXMD5;
import core.util.Xml_reader;


@Controller
@RequestMapping("/wx")
public class WXCallBackController extends JavaEEFrameworkBaseController<Dict> implements Constant {
	//通知微信不要在重复发送消息了
	private static boolean flag = true;
	
	private static String Attach;
	private static int status;
	private static double money;
	private static String wxId;
	private static String wxofferId;
	private static String mch_ID;
	private static String out_trade_no;
	
	private static org.hibernate.Session sessionss = null;
	
	@Resource
	private BaseDao4<RechargeRecordError> baseDao4;
	
	@RequestMapping(value = "/callback", method = { RequestMethod.POST, RequestMethod.GET })
	public void getDict(HttpServletRequest request, HttpServletResponse response) throws Exception {
			
			System.out.println("进入方法:......");
		    String result;//返回给微信的处理结果  
	        String inputLine;  
	        String notityXml = "";  
	        request.setCharacterEncoding("UTF-8");  
	        response.setCharacterEncoding("UTF-8");  
	        response.setContentType("text/html;charset=UTF-8"); 
	        //response.setContentType(“application/xml;charset=UTF-8”); xml数据
	        response.setHeader("Access-Control-Allow-Origin", "*");  
	        System.out.println("开始接受微信的返回信息.....");
	        //微信给返回的东西  
	        try {  
	            while ((inputLine = request.getReader().readLine()) != null) {  
	                notityXml += inputLine;  
	               // System.out.println(notityXml);
	            }  
	            request.getReader().close();  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            System.out.println("xml获取失败");
	            result = setXml("fail","xml获取失败");  
	        }  
	        if (StringUtils.isEmpty(notityXml)) {  
	        	System.out.println("xml为空");
	            result = setXml("fail","xml为空");  
	        } 
	        
	        System.out.println("开始创建MAP.....");
	        
	        // 解析各种数据  
	        Map<String,String> map = new Xml_reader().read(notityXml);  
	        System.out.println("开始解析MAP内容.....");
	        String appid = (String) map.get("appid");//应用ID  
	        String attach = (String) map.get("attach");//商家数据包  
	        String bank_type = (String) map.get("bank_type");//付款银行  
	        String cash_fee = (String) map.get("cash_fee");//现金支付金额  
	        String fee_type = (String) map.get("fee_type");//货币种类  
	        String is_subscribe = (String) map.get("is_subscribe");//是否关注公众账号  
	        String mch_id = (String) map.get("mch_id");//商户号  
	        String nonce_str = (String) map.get("nonce_str");//随机字符串  
	        String openid = (String) map.get("openid");//用户标识  
	        out_trade_no = (String) map.get("out_trade_no");// 获取商户订单号  
	        String result_code = (String) map.get("result_code");// 业务结果  
	        String return_code = (String) map.get("return_code");// SUCCESS/FAIL  
	        String sign = (String) map.get("sign");// 获取签名  
	        String time_end = (String) map.get("time_end");//支付完成时间  
	        String total_fee = (String) map.get("total_fee");// 获取订单金额  
	        String trade_type = (String) map.get("trade_type");//交易类型  
	        String transaction_id = (String) map.get("transaction_id");//微信支付订单号  
	        System.out.println("map数据提取完毕..");
	        //存入数据库中的数据.
	        
	        Attach = attach;
	        if("SUCCESS".equals(result_code)) {
	        	status = 1;
	        }else {
	        	status = 0;
	        }
	        money = Double.parseDouble(total_fee) / 100.00;//转变为分
	        wxId = openid;
	        wxofferId = transaction_id;
	        mch_ID = mch_id;
	        
	        
	        SortedMap<String, String> parameters = new TreeMap<String, String>();  
	        // 数据加密  
	        parameters.put("appid", appid);//应用ID  
	        parameters.put("attach", attach);//商家数据包  
	        parameters.put("bank_type", bank_type);//付款银行  
	        parameters.put("cash_fee", cash_fee);//现金支付金额  
	        parameters.put("fee_type", fee_type);//货币种类  
	        parameters.put("is_subscribe", is_subscribe);//是否关注公众账号  
	        parameters.put("mch_id", mch_id);//商户号  
	        parameters.put("nonce_str", nonce_str);//随机字符串  
	        parameters.put("openid", openid);//用户标识  
	        parameters.put("out_trade_no", out_trade_no);// 商户订单号  
	        parameters.put("result_code", result_code);// 业务结果  
	        parameters.put("return_code", return_code);// SUCCESS/FAIL  
	        parameters.put("time_end", time_end);// 支付完成时间  
	        parameters.put("total_fee", total_fee);// 获取订单金额  
	        parameters.put("trade_type", trade_type);//交易类型  
	        parameters.put("transaction_id", transaction_id);//微信支付订单号  
	        
	        //MD5加密  - 验证信息
	    	StringBuffer sb = new StringBuffer();
			for(Map.Entry<String, String> entry : parameters.entrySet()) {
				sb.append(entry.getKey()+"="+entry.getValue()+"&");
			}
			sb.append("key=qixiamajiang11335577992244668800");
			String miyao = sb.toString();
			System.out.println("miyao:+++++>>>>>>"+miyao);
			String endsign = WXMD5.MD5(miyao);
	        System.out.println("MD5.加密后的字符串...endsign===="+endsign);
	          
	        System.out.println("**************************************************************************************************");  
	        System.out.println(appid+"-------------------应用ID");  
	        System.out.println(attach+"-------------------商家数据包");  
	        System.out.println(bank_type+"-------------------付款银行");  
	        System.out.println(cash_fee+"-------------------现金支付金额");  
	        System.out.println(fee_type+"-------------------货币种类");  
	        System.out.println(is_subscribe+"-------------------是否关注公众账号");  
	        System.out.println(mch_id+"-------------------商户号");  
	        System.out.println(nonce_str+"-------------------随机字符串");  
	        System.out.println(openid+"-------------------用户标识");  
	        System.out.println(out_trade_no+"-------------------获取商户订单号");  
	        System.out.println(result_code+"-------------------业务结果");  
	        System.out.println(return_code+"------------------- SUCCESS/FAIL");  
	        System.out.println(sign+"-------------------获取签名-微信回调的签名");  
	        System.out.println(time_end+"-------------------支付完成时间");  
	        System.out.println(total_fee+"-------------------获取订单金额");  
	        System.out.println(trade_type+"-------------------交易类型");  
	        System.out.println(transaction_id+"-------------------微信支付订单号");  
	        System.out.println(endsign+"-------------------第二次加密sign");  
	        System.out.println("**************************************************************************************************");  
	          
	        // 验证签名  
	        if (sign.equals(endsign)) {  
	        	SessionFactory sf = baseDao4.getSessionFactory();
 				 sessionss = sf.openSession();
	        	 //如果成功写入数据库 ,调用具体执行的业务.
		        MsgService();
	            //result = setXml("SUCCESS", ""); 
		        System.out.println("开始给微信返回信息!");
		        result = "<xml>" + 
		        		"<return_code><![CDATA[SUCCESS]]></return_code>" + 
		        		"<return_msg><![CDATA[OK]]></return_msg>" + 
		        		"</xml>";
	            response.getWriter().write(result);//返回给微信的消息,防止重复验证.有人说直接返回success字符串即可
	            System.out.println("回调成功");  
		        System.out.println("----返回给微信的xml：" + result);  
	        } else {  
	            System.err.println("签名不一致！");  
	            result = setXml("fail", "签名不一致！");  
	            response.getWriter().write(result);
	            response.getWriter().flush();
	        } 
	    }  
	  
	    //通过xml 发给微信消息  
	    public static String setXml(String return_code, String return_msg) {  
	        return "<xml><return_code><![CDATA[" + return_code + "]]>" +   
	                "</return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";  
	    }  
	    
	    /**
	     * 具体执行的业务
	     */
	    public static void MsgService() {
 	        	System.out.println("正在写入数据库:........");
 	        	try {
 					sessionss.doWork(new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							 CallableStatement cs = connection.prepareCall("{CALL GSP_GR_Distributor_A(?,?,?,?,?,?,?,?)}");
 							 cs.setString(1, Attach);//传来的userid
 		                	 cs.setInt(2, status);//成功为1.失败为0
 		                	 cs.setDouble(3, money);//金额    
 		                	 cs.setString(4, wxId);//openid    
 		                	 cs.setString(5, wxofferId);//微信的订单号    
 		                	 cs.setString(6, mch_ID);//商户号  
 		                	 cs.setString(7, "");//随机订单号
 		                	 cs.registerOutParameter(8, java.sql.Types.VARCHAR);
 		                	 cs.execute();
 		                	 String s =  cs.getString(8);
 		                	 System.out.println(s);
 		                	 System.out.println("数据库写入完毕....");
 						}
 					});
 				} catch (Exception e) {
 					e.printStackTrace();
 				}finally {
 					sessionss.close();
 				}
 				
	 	  }
}

