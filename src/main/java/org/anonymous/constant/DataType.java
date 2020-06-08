package org.anonymous.constant;

/**
 * seven types of json data
 */
public enum DataType {
    NULL("null"),
    FALSE("false"),
    TRUE("true"),
    NUMBER("number"),
    STRING("string"),
    ARRAY("array"),
    OBJECT("object");
    private String value;
    DataType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}
