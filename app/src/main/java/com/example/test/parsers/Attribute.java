package com.example.test.parsers;

class Attribute {
    private XMLTable.AttributeName name;
    private String value;

    Attribute(XMLTable.AttributeName name, String value) {
        this.name = name;
        this.value = value;
    }

    XMLTable.AttributeName getName() {
        return name;
    }

    String getValue() {
        return value;
    }
}
