package org.anonymous.constant;

public enum ResultCode {
    OK(0),
    EXPECT_VALUE(1),
    INVALID_VALUE(2),
    ROOT_NOT_SINGULAR(3);

    private int value;

    ResultCode(int Value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }
}
