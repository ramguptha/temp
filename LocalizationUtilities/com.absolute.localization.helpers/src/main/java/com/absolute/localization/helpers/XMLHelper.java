package com.absolute.localization.helpers;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


//GE: code from com.absoluteapps.mdm.XMLHelper - with changes!
//removed escaping of all characters other than the angle brackets
public class XMLHelper {
	
		private final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			  "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";
		private final static String 	XML_PLIST_START_TAG		= "<plist version=\"1.0\">";
		private final static String 	XML_PLIST_END_TAG		= "</plist>";
		private final static String 	XML_DICT_START_TAG		= "<dict>";
		private final static String 	XML_DICT_END_TAG		= "</dict>";
		private final static String 	XML_KEY_START_TAG		= "<key>";
		private final static String 	XML_KEY_END_TAG			= "</key>";
		private final static String 	XML_STRING_START_TAG	= "<string>";
		private final static String 	XML_STRING_END_TAG		= "</string>";
		private static final String 	XML_REAL_START_TAG 		= "<real>";
		private static final String 	XML_REAL_END_TAG 		= "</real>";
		private static final String 	XML_ARRAY_START_TAG		= "<array>";
		private static final String 	XML_ARRAY_END_TAG		= "</array>";
		private static final String 	XML_EMPTY_ARRAY_TAG 	= "<array/>";
		private static final String 	XML_INTEGER_START_TAG 	= "<integer>";
		private static final String 	XML_INTEGER_END_TAG 	= "</integer>";
		private static final String 	XML_DATE_START_TAG 		= "<date>";
		private static final String 	XML_DATE_END_TAG 		= "</date>";
		private static final String 	XML_DATA_START_TAG 		= "<data>";
		private static final String 	XML_DATA_END_TAG 		= "</data>";
		
		
		public static String XMLHeader() {
			return XML_HEADER;
		}
		
		public static String startPList() {
			return XML_PLIST_START_TAG;
		}
		public static String endPList() {
			return XML_PLIST_END_TAG;
		}
		
		public static String startDict() {
			return XML_DICT_START_TAG;
		}
		
		public static String endDict() {
			return XML_DICT_END_TAG;
		}
		
		public static String startArray() {
			return XML_ARRAY_START_TAG;
		}
		
		public static String endArray() {
			return XML_ARRAY_END_TAG;
		}
		
		public static String emptyArray() {
			return XML_EMPTY_ARRAY_TAG;
		}
		
		
		public static String keyElement(String value) {
			return XML_KEY_START_TAG+makeXmlSavvy(value)+XML_KEY_END_TAG;
		}
		
		public static String stringElement(String value) {
			return XML_STRING_START_TAG+makeXmlSavvy(value)+XML_STRING_END_TAG;
		}
		
		public static String integerElement(String value) {
			return XML_INTEGER_START_TAG+makeXmlSavvy(value)+XML_INTEGER_END_TAG;
		}
		
		public static String realElement(String value) {
			return XML_REAL_START_TAG+makeXmlSavvy(value)+XML_REAL_END_TAG;
		}
		
		public static String dateElement(String value) {
			return XML_DATE_START_TAG+makeXmlSavvy(value)+XML_DATE_END_TAG;
		}
		
		public static String dataElement(String value, String indent) {
			return XML_DATA_START_TAG+"\n"+indent+makeXmlSavvy(value)+indent+XML_DATA_END_TAG;
		}
		
		public static String makeXmlSavvy(String value) {
			if(value != null) {
				value = value.replaceAll("<", "&lt;");
				value = value.replaceAll(">", "&gt;");
				value = value.replaceAll("&", "&amp;");
				//reverse if double-escaped (if already escaped in the original plist file)
				value = value.replaceAll("&amp;amp;", "&amp;");
				value = value.replaceAll("\"", "&quot;");
				value = value.replaceAll("'", "&apos;");
			}
			return value;
		}
		
		public static Element getRootElementFromResponse(String response) {
			
			if(response != null && response.length() > 0) {
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setCoalescing(true);
				dbf.setIgnoringComments(true);
				DocumentBuilder db = null;
				Document dom;
				try {
					db = dbf.newDocumentBuilder();
					dom = db.parse(new ByteArrayInputStream(response.getBytes()));
				} catch (Exception e) {
					System.err.println(e.getMessage());
					return null;
				}
			
				Element rootEle = dom.getDocumentElement();
				if(rootEle != null) {
					removeEmptyTextNodes(rootEle);
				}
				return rootEle;
			}
			
			return null;
		}
		
		private static void removeEmptyTextNodes(Node node) {
			
			if(node.getNodeName().equalsIgnoreCase("string")) {
				return;
			}
			
			NodeList nodeList = node.getChildNodes();
			Node childNode;
			for (int x = nodeList.getLength() - 1; x >= 0; x--) {
				childNode = nodeList.item(x);
				if (childNode.getNodeType() == Node.TEXT_NODE) {
					if (childNode.getNodeValue().trim().equals("")) {
						node.removeChild(childNode);
					}
				} else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					removeEmptyTextNodes(childNode);
				}
			}
		}
		
		public static String booleanElement(boolean b) {
			return "<"+Boolean.toString(b)+"/>";
		}

}
