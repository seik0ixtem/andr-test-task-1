package com.example.test.parsers.Tag;

import com.example.test.parsers.Element;

import org.xmlpull.v1.XmlPullParser;

class F {
    private String currentText;
    private XmlPullParser parser;
    private Element element;

    F(boolean column, XmlPullParser parser) throws Exception {
        this.parser = parser;
        if (column)
            fromColumn();
        else
            fromValues();
    }

    private void fromColumn() throws Exception {
        Element element = new Element(parser);
        element.parseAttributes();
        findText();
        element.setKeyName(getCurrentText());
        setElement(element);

    }

    private void fromValues() throws Exception {
        findText();
    }

    private void findText() throws Exception{
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_TAG) {
            if (eventType == XmlPullParser.TEXT)
                setCurrentText();
            eventType = parser.next();
        }
    }

    String getCurrentText() {
        if (currentText == null)
            currentText = "";
        return currentText;
    }

    private void setCurrentText() {
        currentText = parser.getText().trim();
    }

    Element getElement() {
        return element;
    }

    private void setElement(Element element) {
        this.element = element;
    }
}
