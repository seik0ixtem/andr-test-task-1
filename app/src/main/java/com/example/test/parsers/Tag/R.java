package com.example.test.parsers.Tag;

import com.example.test.parsers.Element;
import com.example.test.parsers.XMLTable;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

public class R {
    public R(XmlPullParser parser, List<Element> elementList) throws Exception {
        int indexKey = 0;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG) {
            if (XMLTable.TAG.F.getValue().equals(parser.getName()))
                elementList.get(indexKey++).getValueList().add(new F(false, parser).getCurrentText());
            eventType = parser.next();
        }
    }
}
