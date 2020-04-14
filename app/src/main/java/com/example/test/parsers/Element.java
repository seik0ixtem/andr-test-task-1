package com.example.test.parsers;

import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class Element {
    private String keyName;
    private List<String> valueList;
    private List<Attribute> attributeList;
    private boolean hide;
    private XmlPullParser parser;

    public Element(XmlPullParser parser) {
        this.attributeList = new ArrayList<>();
        this.valueList = new ArrayList<>();
        this.hide = false;
        this.parser = parser;
    }

    public void parseAttributes() {
        for (XMLTable.AttributeName attr : XMLTable.AttributeName.values()) {
            String AttributeValue = parser.getAttributeValue(null, attr.name());
            if (AttributeValue != null)
                attributeList.add(new Attribute(attr, AttributeValue));
        }
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
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
