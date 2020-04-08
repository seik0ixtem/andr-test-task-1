package com.example.test.parsers;

import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> menuItems;
    private int eventType;

    enum TAG {
        PART,
        NAME,
        URL,
        VISIBLE_IN_MENU,
    }

    public Menu() {
        menuItems = new ArrayList<>();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public boolean parse(XmlPullParser parser) {
        boolean status = true;

        try {
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    new Part(parser);
                }
                eventType = parser.next();

            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    private class Part {
        String textValue = "";
        MenuItem currentItem;
        Part(XmlPullParser parser) throws Exception{

            currentItem = new MenuItem();
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

        private boolean endTag(String name){
            try {
                switch (TAG.valueOf(name)) {
                    case PART:
                        menuItems.add(currentItem);
                        return true;
                    case NAME:
                        currentItem.setName(textValue);
                        break;
                    case URL:
                        currentItem.setUrl(textValue);
                        break;
                    case VISIBLE_IN_MENU:
                        currentItem.setVisible(textValue.equals("1"));
                        break;
                }
            } catch (Exception ignored) {}
            return false;
        }
    }

    public static class MenuItem {
        private String name, url;
        private boolean visible;

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
}


