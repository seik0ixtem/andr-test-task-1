package com.example.test.parsers;

import android.app.Activity;
import android.content.Context;
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
    private List<Table.Element> elementList;

    private enum TAG {
        d,
        r,
        f
    }

    private enum AttributeName {
        hide_if_value_same,
        hide,
        show_if_shows_any,
        hide_if_value_all,
        dt
    }

    public XMLTable(String url, Activity context) throws Exception {
        this.context = context;
        this.url = url;
        loadXMLTable();
    }

    private void loadXMLTable() throws Exception {
        XmlPullParser parser = initParser();
        Table table = new Table(parser);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    table.startTag();
                    break;
                case XmlPullParser.TEXT:
                    table.setCurrentText();
                    break;
                case XmlPullParser.END_TAG:
                    table.endTag();
                    break;
            }
            eventType = parser.next();
        }
        table.readAttributes();
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
        is.read(buffer);
        is.close();
        result = new String(buffer);
        return result;
    }

    public class Table {
        private String currentText;
        XmlPullParser parser;
        boolean isKey, inEntry;
        int indexKey; //индекс ключа для значения

        Table(XmlPullParser parser) {
            this.parser = parser;
            elementList = new ArrayList<>();
        }

        String getCurrentText() {
            if (currentText == null)
                currentText = "";
            return currentText;
        }

        void setCurrentText() {
            if (inEntry)
                this.currentText = parser.getText().trim();
        }

        void startTag() {
            try {
                switch (TAG.valueOf(parser.getName())) {
                    case d:
                        isKey = true;
                        break;
                    case f:
                        inEntry = true;
                        if (isKey) {
                            Element element = new Element();
                            element.parseAttributes();
                            elementList.add(element);
                        }
                        break;
                    case r:
                        inEntry = true;
                        isKey = false;
                        indexKey = 0;
                        break;
                }
            } catch (Exception ignored) {
            }
        }

        void endTag() {
            try {
                if (inEntry) {
                    switch (TAG.valueOf(parser.getName())) {
                        case f:
                            if (isKey)
                                elementList.get(elementList.size() - 1).setKeyName(getCurrentText()); //получаем ласт элемент и задаем имя ключа
                            else
                                elementList.get(indexKey++).getValueList().add(getCurrentText());
                            break;
                        case r:
                        case d:
                            inEntry = false;
                            break;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        void readAttributes() {
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
            isKey = false;
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

        public class Element { //элемент таблицы: имя ключа, его аттрибуты и значения
            private String keyName;
            private List<String> valueList;
            private List<Attribute> attributeList;
            private boolean hide;

            Element() {
                this.attributeList = new ArrayList<>();
                this.valueList = new ArrayList<>();
                this.hide = false;
            }

            void parseAttributes() {
                for (AttributeName attr : AttributeName.values()) {
                    String AttributeValue = parser.getAttributeValue(null, attr.name());
                    if (AttributeValue != null)
                        attributeList.add(new Attribute(attr, AttributeValue));
                }
            }

            void setKeyName(String keyName) {
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

        private class Attribute {
            AttributeName name;
            String value;

            Attribute(AttributeName name, String value) {
                this.name = name;
                this.value = value;
            }

            AttributeName getName() {
                return name;
            }

            String getValue() {
                return value;
            }
        }
    }

    public List<Table.Element> getElementList() {
        return elementList;
    }
}