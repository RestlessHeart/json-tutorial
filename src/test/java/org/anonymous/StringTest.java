package org.anonymous;

import org.testng.annotations.Test;

public class StringTest {
    @Test
    public void testStringCopy(){
        String s1="old world";
        String s2=s1;
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());

    }
}
