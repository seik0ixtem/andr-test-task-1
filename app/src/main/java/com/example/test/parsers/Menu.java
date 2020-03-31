package com.example.test.parsers;

import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> menuItems;

    private static class TAG {
        static final String PART = "PART"; // start/end tag
        static final String NAME = "NAME";
        static final String URL = "URL";
        static final String VISIBLE_IN_MENU = "VISIBLE_IN_MENU";
        static final String ORDER_NO = "ORDER_NO";
    }

    public Menu() {
        menuItems = new ArrayList<>();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public boolean parse(XmlPullParser xpp) {
        boolean status = true;
        MenuItem currentItem = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("PART".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentItem = new MenuItem();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            switch (tagName) {
                                case TAG.PART:
                                    menuItems.add(currentItem);
                                    inEntry = false;
                                    break;
                                case TAG.NAME:
                                    currentItem.setName(textValue);
                                    break;
                                case TAG.URL:
                                    currentItem.setUrl(textValue);
                                    break;
                                case TAG.VISIBLE_IN_MENU:
                                    currentItem.setVisible(textValue.equals("1"));
                                    break;
                                case TAG.ORDER_NO:
                                    currentItem.setOrder(Integer.parseInt(textValue));
                                    break;
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    public static class MenuItem {
        private String name, url;
        private boolean visible;
        private int order;

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

        public int getOrder() {
            return order;
        }

        void setOrder(int order) {
            this.order = order;
        }
    }
}


