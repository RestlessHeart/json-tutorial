package org.anonymous.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JsonDataConstant {
    public static final char[] whiteSpaceList=new char[]{' ','\n','\t','\r'};

    public static final String NULL="null";

    public static final String FALSE="false";

    public static final String TRUE="true";

    public static final Set<Character> VALID_NUMBER_START=new HashSet<Character>();

    public static final Set<Character> VALID_NUMBER=new HashSet<Character>();

    static {
        VALID_NUMBER_START.add('-');
        VALID_NUMBER_START.add('+');
        for(int i=0;i<10;i++){
            VALID_NUMBER_START.add((char)('0'+i));
        }
        for(int i=0; i<10; i++){
            VALID_NUMBER.add((char)('0'+i));
        }
    }

}
