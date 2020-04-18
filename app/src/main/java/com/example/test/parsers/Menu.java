package com.example.test.parsers;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> menuItems = new ArrayList<>();
    private boolean isParse;

    enum TAG {
        Part("PART"),
        Name("NAME"),
        Url("URL"),
        Visible("VISIBLE_IN_MENU");

        private String value;

        TAG(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public Menu(XmlPullParser parser) throws Exception {
        setParse(parse(parser));
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    private boolean parse(XmlPullParser parser) throws Exception {
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                menuItems.add(new MenuItem(parser));
            }
            eventType = parser.next();

        }
        return true;
    }

    public static class MenuItem {
        private String name, url;
        private boolean visible;
        String textValue = "";

        MenuItem (XmlPullParser parser) throws Exception{
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.TEXT:
                        textValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (endTag(parser.getName()))
                            return;
                        break;
                }
                eventType = parser.next();
            }
        }

        private boolean endTag(String name) {
            for (TAG tag : TAG.values())
                if (tag.getValue().equals(name)) {
                    switch (tag) {
                        case Part:
                            return true;
                        case Name:
                            setName(textValue);
                            break;
                        case Url:
                            setUrl(textValue);
                            break;
                        case Visible:
                            setVisible(textValue.equals("1"));
                            break;
                    }
                    break;
                }
            return false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url == null ? "" : url;
        }

        void setUrl(String url) {
            this.url = url;
        }

        public boolean isVisible() {
            return visible;
        }

        void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    public boolean isParse() {
        return isParse;
    }

    private void setParse(boolean parse) {
        isParse = parse;
    }
}


