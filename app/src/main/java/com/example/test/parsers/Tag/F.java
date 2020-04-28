package com.example.test.parsers.Tag;

import com.example.test.parsers.Element;

import org.xmlpull.v1.XmlPullParser;

class F {
    private String currentText;
    private XmlPullParser parser;
    final Element element;

    F(boolean column, XmlPullParser parser) throws Exception {
        this.parser = parser;
        element = column ? fromColumn() : fromValues();
    }

    private Element fromColumn() throws Exception {
        Element element = new Element(parser);
        findText();
        element.setKeyName(getCurrentText());
        return element;
    }

    private Element fromValues() throws Exception {
        findText();
        return null;
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
}
