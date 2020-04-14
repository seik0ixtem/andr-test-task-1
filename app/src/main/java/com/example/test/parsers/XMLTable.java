package com.example.test.parsers;

import android.app.Activity;
import android.content.Context;
import com.example.test.parsers.Tag.D;
import com.example.test.parsers.Tag.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XMLTable {

    private Context context;
    private String url;
    private List<Element> elementList;
    private XmlPullParser parser;

    public enum TAG {
        d,
        r,
        f
    }

    enum AttributeName {
        hide_if_value_same,
        hide,
        show_if_shows_any,
        hide_if_value_all,
        dt
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

    private void startTag() {
        try {
            switch (TAG.valueOf(parser.getName())) {
                case d:
                    elementList = new D(parser).getElementList(); //список колонок
                    break;
                case r:
                    new R(parser, elementList); // список значений
                    break;
            }
        } catch (Exception ignored) {
        }
    }

    private void readAttributes() {
        for (Element element : elementList)
            readAttributesForElement(element);
        for (Element element : elementList)
            attributeShowIfShow(element);
        removeHideElement();
    }

    private void removeHideElement() { //удаляем скрытые элементы и очищаем аттрибуты, т.к. больше они не понадобятся
        int shift = 0;
        int size = elementList.size();
        for (int i = 0; i < size; i++) {
            Element element = elementList.get(i - shift);
            if (element.isHide()) {
                elementList.remove(i - shift);
                shift++;
            } else
                element.getAttributeList().clear();
        }
    }

    private void readAttributesForElement(Element element) { //при setHide(true) выходим из метода
        boolean isKey = false;
        for (Attribute attribute : element.getAttributeList())
            switch (attribute.getName()) {
                case hide:
                    if (attribute.getValue().equalsIgnoreCase("yes")) {
                        element.setHide();
                        return;
                    }
                    break;
                case hide_if_value_same: //скрыть если значение элем. совпдают
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
                case hide_if_value_all: //скрыть если все значения = ....
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
                case dt:
                    isKey = true;
                    break;
            }
        if (!isKey || findEmptyElement(element))
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
            if (attribute.getName().equals(AttributeName.show_if_shows_any)
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
        parser.setInput(new StringReader(loadFromAsset(url + ".xml", context).replaceAll("<br\\s?/>", "")));
        return parser;
    }

    private static String loadFromAsset(String url, Context context) throws Exception {
        String result;
        InputStream is = context.getAssets().open(url);
        int size = is.available();
        byte[] buffer = new byte[size];
        //noinspection ResultOfMethodCallIgnored
        is.read(buffer);
        is.close();
        result = new String(buffer);
        return result;
    }

    public List<Element> getElementList() {
        return elementList;
    }
}