package org.anonymous.constant;

public enum ResultCode {
    OK(0),
    EXPECT_VALUE(1),
    INVALID_VALUE(2),
    ROOT_NOT_SINGULAR(3),
    PARSE_INVALID_UNICODE_HEX(4),
    PARSE_INVALID_UNICODE_SURROGATE(5);

    private int value;

    ResultCode(int Value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
