package com.example.test.parsers.Tag;

import com.example.test.parsers.Element;
import com.example.test.parsers.XMLTable;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class D {
    private List<Element> elementList;

    public D(XmlPullParser parser) throws Exception {
        elementList = new ArrayList<>();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG) {
            if (eventType == XmlPullParser.START_TAG
                    && parser.getName().equals(XMLTable.TAG.f.toString()))
                elementList.add(new F(true, parser).getElement());
            eventType = parser.next();
        }
    }

    public List<Element> getElementList() {
        return elementList;
    }
}
