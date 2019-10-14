package com.wuqi.facepay.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * @ClassName ReturnXMLParser
 * @Description XML文件转化工具类
 * @Author Luo Yi
 * @Date 2019/10/7 20:03
 */
public class ReturnXMLParser {
    public static String parseGetAuthInfoXML(InputStream is) throws Exception {
        String result=null;

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is,"UTF-8");

        int eventType = parser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("authinfo")) {
                        eventType = parser.next();
                        result=parser.getText();
                    }
            }
            eventType = parser.next();
        }

        return result;
    }

    public static String parseExpireInXML(InputStream is) throws Exception {
        String result=null;

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is,"UTF-8");

        int eventType = parser.getEventType();
        while(eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("expires_in")) {
                        eventType = parser.next();
                        result=parser.getText();
                    }
            }
            eventType = parser.next();
        }

        return result;
    }
}
