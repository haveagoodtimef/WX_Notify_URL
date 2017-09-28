package core.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 解析xml
 * @author feng-hong-zhang
 *
 * 2017年9月28日
 */
public class Xml_reader {

	public static Map<String, String> read(String xmlStr) {
		 Map<String,String> map = null;
		try {
			Document document = DocumentHelper.parseText(xmlStr);
			 //获取根节点元素对象  
	        Element root = document.getRootElement(); 
	        //遍历  
	       map =  listNodes(root);  
		} catch (DocumentException e) {
			System.out.println("创建dom对象错误!");
			e.printStackTrace();
		}  
		return map;
	}
	
	 //遍历当前节点下的所有节点  
    public static Map<String,String> listNodes(Element node){  
    	Map<String,String> map = new TreeMap<>(); 
        //System.out.println("当前节点的名称：" + node.getName());  
        //首先获取当前节点的所有属性节点  
        List<Attribute> list = node.attributes();  
        //遍历属性节点  
//        for(Attribute attribute : list){  
//            System.out.println("11111111属性"+attribute.getName() +":" + attribute.getValue());  
//        }  
        //如果当前节点内容不为空，则输出  
        if(!(node.getTextTrim().equals(""))){  
             //System.out.println( "222222222"+node.getName() + "：" + node.getText());    
        	  //map.put(node.getName(), node.getText());
        	 // map.put("1", "3");
        	  
        	 // System.out.println("添加map");
        	  
        }  
        //同时迭代当前节点下面的所有子节点  
        //使用递归  
        Iterator<Element> iterator = node.elementIterator();  
        while(iterator.hasNext()){  
            Element e = iterator.next();  
            map.put(e.getName(),e.getStringValue());
            listNodes(e);  
        }  
        return map;
    }
}
