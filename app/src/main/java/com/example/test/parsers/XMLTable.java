package com.example.test.parsers;

import android.app.Activity;
import android.content.Context;

import com.example.test.parsers.Tag.D;
import com.example.test.parsers.Tag.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XMLTable {

    private Context context;
    private String url;
    private List<Element> elementList;
    private XmlPullParser parser;

    public enum TAG {
        D("d"),
        R("r"),
        F("f");

        private String value;

        TAG(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum AttributeName {
        Dt("dt"),
        HideIfValue("hide_if_value_same"),
        Hide("hide"),
        ShowIfShows("show_if_shows_any"),
        HideIfValueAll("hide_if_value_all");

        private String value;

        AttributeName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public XMLTable(String url, Activity context) throws Exception {
        this.context = context;
        this.url = url;
        this.elementList = new ArrayList<>();
        loadXMLTable();
    }

    private void loadXMLTable() throws Exception {
        parser = initParser();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                startTag();
            }
            eventType = parser.next();
        }
        readAttributes();
    }

    private void startTag() throws Exception {
        for (TAG tag : TAG.values())
            if (tag.getValue().equals(parser.getName())) {
                switch (tag) {
                    case D:
                        elementList = new D(parser).elementList; //список колонок
                        break;
                    case R:
                        new R(parser, elementList); // список значений
                        break;
                }
                break;
            }
    }

    private void readAttributes() {
        for (Element element : elementList)
            readAttributesForElement(element);
        for (Element element : elementList)
            attributeShowIfShow(element);
    }

    private void readAttributesForElement(Element element) { //при setHide(true) выходим из метода
        for (Attribute attribute : element.getAttributeList())
            switch (attribute.getName()) {
                case HideIfValue: //скрыть если значение элем. совпдают
                    boolean found = false;
                    String value = element.getValueList().get(0);
                    for (String list : element.getValueList()) {
                        if (!value.equals(list)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        element.setHide();
                        return;
                    }
                    break;
                case HideIfValueAll: //скрыть если все значения = ....
                    Scanner s = new Scanner(attribute.getValue()).useDelimiter("\\|");
                    found = false;
                    while (s.hasNext()) {
                        if (found)
                            found = false;
                        String item = s.next();
                        for (String current : element.getValueList()) {
                            if (!current.equalsIgnoreCase(item)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        element.setHide();
                        return;
                    }
                    break;
            }
        if (findEmptyElement(element))
            element.setHide();
    }

    private boolean findEmptyElement(Element element) {
        for (String current : element.getValueList())
            if (!current.isEmpty())
                return false;
        return true;
    }

    private void attributeShowIfShow(Element element) {
        for (Attribute attribute : element.getAttributeList())
            if (attribute.getName().equals(AttributeName.ShowIfShows)
                    && elementList.get(Integer.parseInt(attribute.getValue())).isHide()) {
                element.setHide();
                return;
            }
    }

    private XmlPullParser initParser() throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory
                .newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(context.getAssets().open(url + ".xml"), null);
        return parser;
    }

    public List<Element> getElementList(boolean isVisible) {
        List<Element> result;
        if (isVisible) {
            result = new ArrayList<>();
            for (Element element : elementList) {
                if (!element.isHide())
                    result.add(element);
            }
        } else
            result = elementList;
        return result;
    }
}