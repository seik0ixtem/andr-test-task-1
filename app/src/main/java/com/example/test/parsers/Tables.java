package com.example.test.parsers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Tables {



    public static String loadFromAsset(String url, Context context) {
        String result;
        try {
            InputStream is = context.getAssets().open(url);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    public static class xmlTable {
        ArrayList<String> keys, values;
        Activity context;
        String url;

        public xmlTable(String url, Activity context) {
            this.context = context;
            this.url = url;
            loadXMLTable();
        }

        private static class TAG {
            static final String D = "d";
            static final String R = "r";
            static final String F = "f";
        }

        static final class Attrs {
            static final String hide_if_value_same = "hide_if_value_same";
            static final String hide = "hide";
            static final String show_if_shows_any = "show_if_shows_any";
            static final String hide_if_value_all = "hide_if_value_all";
            static final String dt = "dt";
        }

        private static final List<String> AttrList = Arrays.asList(Attrs.hide_if_value_same, Attrs.hide, Attrs.show_if_shows_any, Attrs.hide_if_value_all, Attrs.dt);

        private static class Attribute {
            String name;
            String value;

            Attribute(String name, String value){
                this.name = name;
                this.value = value;
            }

            String getName() {
                return name;
            }

            String getValue() {
                return value;
            }
        }

        private void loadXMLTable() {
            try {
                this.keys = new ArrayList<>(); // ключи
                ArrayList<String> value = new ArrayList<>(); //значения строки
                ArrayList<ArrayList<String>> values = new ArrayList<>(); // массив всех строк
                ArrayList<ArrayList<Attribute>> attributes = new ArrayList<>(); //атрибуты ключей
                ArrayList<Attribute> attribute = new ArrayList<>(); //атрибут 1 ключа
                boolean inEntry = false, key = false; //в теге? колонка?
                String textValue = ""; // текст в теге
                XmlPullParser parser = initParser();
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            switch (tagName) {
                                case TAG.D:
                                    key = true;
                                    break;
                                case TAG.F:
                                    inEntry = true;
                                    if (key){
                                        attribute = new ArrayList<>();
                                        for (String attr : AttrList) {
                                            String A_value = parser.getAttributeValue(null, attr);
                                            if (A_value != null)
                                                attribute.add(new Attribute(attr, A_value));
                                        }
                                    }
                                    break;
                                case TAG.R:
                                    inEntry = true;
                                    value = new ArrayList<>();
                                    key = false;
                                    break;
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (inEntry)
                                textValue = parser.getText().trim();
                            break;
                        case XmlPullParser.END_TAG:
                            if (inEntry) {
                                switch (tagName) {
                                    case TAG.F:
                                        if (key) {
                                            keys.add(textValue); //добавление ключа, если есть атрибут DT
                                            attributes.add(attribute);
                                        }
                                        else
                                            value.add(textValue);
                                        break;
                                    case TAG.R:
                                        if (value.size() > 0)
                                            values.add(value);
                                        inEntry = false;
                                        break;
                                    case TAG.D:
                                        inEntry = false;

                                        break;
                                }
                            }
                            break;
                    }
                    eventType = parser.next();
                }
                readAttribute(attributes, values);
                this.values = clearEmptyColumn(keys, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private XmlPullParser initParser() {
            XmlPullParser parser = null;
            try {
                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                parser = factory.newPullParser();
                parser.setInput(new StringReader(loadFromAsset(url + ".xml", context).replaceAll("<br\\s?/>", "")));
            } catch (Exception e) {
                Toast.makeText(context, "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
            }
            return parser;
        }

        private void readAttribute(ArrayList<ArrayList<Attribute>> attributes, ArrayList<ArrayList<String>> values) {
            boolean key;
            for (int i = 0; i < attributes.size(); i++) {
                key = false;
                for (Attribute attribute : attributes.get(i))
                    switch (attribute.getName()) {
                        case Attrs.hide_if_value_same: //скрыть если значение след. элем. совпдают
                            boolean found = false;
                            for (ArrayList<String> list : values) {
                                if (!list.get(i + 1).contains(list.get(i))) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                                keys.set(i, "");
                            break;
                        case Attrs.hide: //скрыть
                            if (attribute.getValue().equalsIgnoreCase("yes"))
                                keys.set(i, "");
                            break;
                        case Attrs.hide_if_value_all: //скрыть если все значения = ....
                            Scanner s = new Scanner(attribute.getValue()).useDelimiter("\\|");
                            found = false;
                            while (s.hasNext()) {
                                if (found)
                                    found = false;
                                String item = s.next();
                                for (ArrayList<String> list : values) {
                                    if (!list.get(i).equalsIgnoreCase(item)) {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (!found)
                                keys.set(i, "");
                            break;
                        case Attrs.dt:
                            key = true;
                    }
                if (!key)
                    keys.set(i, "");
            }
            for (int i = 0; i < attributes.size(); i++)
                for (Attribute attribute : attributes.get(i))
                    if (attribute.getName().equals(Attrs.show_if_shows_any) && keys.get(Integer.parseInt(attribute.getValue())).isEmpty()) //скрыть, если скрыто
                        keys.set(i, "");
        }

        private ArrayList<String> clearEmptyColumn(ArrayList<String> keys, ArrayList<ArrayList<String>> values) {
            boolean found;
            for (int m = 0; m < keys.size(); m++) {
                found = false;
                for (int i = 0; i < values.size(); i++) {
                    if (!values.get(i).get(m).isEmpty()) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    keys.set(m, "");
            }

            ArrayList<Integer> emptyColumns = new ArrayList<>();
            int shift = 0;
            for (int i = 0; i < keys.size(); i++) { //получаем индексы пустых ключей
                if (keys.get(i).isEmpty()) {
                    emptyColumns.add(i);
                }
            }
            for (int del : emptyColumns) { //удаляем ключи и соотв. знач.
                keys.remove(del - shift);
                for (int i = 0; i < values.size(); i++)
                    values.get(i).remove(del - shift);
                shift++;
            }

            ArrayList<String> result = new ArrayList<>();
            for (ArrayList<String> curList : values)
                result.add(convertToJSON(curList).toString());
            return result;
        }

        private JSONArray convertToJSON(ArrayList<String> item) {
            JSONArray jsonArray = new JSONArray();
            for (String cur : item)
                jsonArray.put(cur);
            return jsonArray;
        }

        public ArrayList<String> getKeys() {
            return keys;
        }

        public ArrayList<String> getValues() {
            return values;
        }
    }
}
