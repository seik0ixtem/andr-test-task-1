package com.example.test.parsers;

import android.util.Log;

import com.example.test.MainActivity;

import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<MenuItem> menuItems;
    private boolean isParse;

    enum TAG {
        PART,
        NAME,
        URL,
        VISIBLE_IN_MENU,
    }

    public Menu(XmlPullParser parser){
        menuItems = new ArrayList<>();
        try {
            setParse(parse(parser));
        } catch (Exception e) {
            setParse(false);
            Log.d(MainActivity.logTag, e.getMessage());
        }
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
            try {
                switch (TAG.valueOf(name)) {
                    case PART:
                        return true;
                    case NAME:
                        setName(textValue);
                        break;
                    case URL:
                        setUrl(textValue);
                        break;
                    case VISIBLE_IN_MENU:
                        setVisible(textValue.equals("1"));
                        break;
                }
            } catch (Exception ignored) {
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


