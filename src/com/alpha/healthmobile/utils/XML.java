package com.alpha.healthmobile.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * 解析xml文件
 * 
 * @author SongJian E-mail:1129574214@qq.com
 * @date 2015-12-12 下午4:57:06
 * @version 1.0
 * @parameter
 * @return
 */
public class XML {
	public static Map<String, String> decodeXml(String content)
			throws XmlPullParserException, IOException {
		Map<String, String> xmlMap = new HashMap<String, String>();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(new StringReader(content));
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			String nodeName = parser.getName();
			switch (event) {
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:
				if ("xml".equals(nodeName) == false) {
					xmlMap.put(nodeName, parser.nextText());
				}
			case XmlPullParser.END_TAG:
				break;
			default:
				break;
			}
			event = parser.next();
			return xmlMap;
		}
		return null;
	}
}
