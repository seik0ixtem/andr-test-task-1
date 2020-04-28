package com.example.test.parsers;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class Element {
    private String keyName;
    private final List<String> valueList;
    private final List<Attribute> attributeList;
    private boolean hide;
    private final XmlPullParser parser;

    public Element(XmlPullParser parser) {
        this.parser = parser;
        this.attributeList = new ArrayList<>();
        this.valueList = new ArrayList<>();
        this.hide = parseAttributes();
    }

    private boolean parseAttributes() {
        boolean hide = true;
        for (XMLTable.AttributeName attr : XMLTable.AttributeName.values()) {
            String attributeValue = parser.getAttributeValue(null, attr.getValue());
            if (attributeValue != null) {
                switch (attr) {
                    case Dt:
                        hide = false;
                        break;
                    case Hide:
                        if (attributeValue.equals("yes"))
                            hide = true;
                        else
                            attributeList.add(new Attribute(attr, attributeValue));
                        break;
                    case HideIfValue:
                    case HideIfValueAll:
                    case ShowIfShows:
                        hide = false;
                        attributeList.add(new Attribute(attr, attributeValue));
                        break;
                }
            }
        }
        return hide;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public List<String> getValueList() {
        return valueList;
    }

    List<Attribute> getAttributeList() {
        return attributeList;
    }

    boolean isHide() {
        return hide;
    }

    void setHide() {
        this.hide = true;
    }
}
