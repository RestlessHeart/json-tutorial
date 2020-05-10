package org.anonymous.constant;

/**
 * seven types of json data
 */
public enum DataType {
    NULL(0),
    FALSE(1),
    TRUE(2),
    NUMBER(3),
    STRING(4),
    ARRAY(5),
    OBJECT(6);
    private int value;
    DataType(int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
