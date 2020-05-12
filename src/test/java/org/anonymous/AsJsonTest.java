package org.anonymous;

import org.anonymous.constant.DataType;
import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AsJsonTest {

    private JsonNode jsonNode;

    private AsJson asJson;

    @BeforeMethod
    public void prepareData(){
        jsonNode=new JsonNode();
        jsonNode.setType(DataType.TRUE);
        asJson=new AsJsonImpl();
    }

    @Test
    public void testParseNull(){
        Assert.assertEquals(ResultCode.OK, asJson.parse(jsonNode,"null"));
        Assert.assertEquals(DataType.NULL,asJson.getType(jsonNode));
    }

    @Test
    public void testParseWrongNull(){
        String jsonStr=" nuil ";
        Assert.assertEquals(ResultCode.INVALID_VALUE, asJson.parse(jsonNode,jsonStr));
    }

    @Test
    public void testParseNullWithMoreSuffix(){
        Assert.assertEquals(ResultCode.ROOT_NOT_SINGULAR, asJson.parse(jsonNode,"  \n\t \r null a"));
    }

    @Test
    public void testParseFalse(){
        Assert.assertEquals(ResultCode.OK, asJson.parse(jsonNode," false "));
        Assert.assertEquals(DataType.FALSE,asJson.getType(jsonNode));
    }

    @Test
    public void testParseWrongFalse(){
        String jsonStr="  falae  ";
        Assert.assertEquals(ResultCode.INVALID_VALUE, asJson.parse(jsonNode,jsonStr));
    }

    @Test
    public void testParseFalseWithMoreSuffix(){
        Assert.assertEquals(ResultCode.ROOT_NOT_SINGULAR, asJson.parse(jsonNode," false a"));
    }

    @Test
    public void testParseTrue(){
        Assert.assertEquals(ResultCode.OK, asJson.parse(jsonNode," true "));
        Assert.assertEquals(DataType.TRUE,asJson.getType(jsonNode));
    }

    @Test
    public void testParseWrongTrue(){
        String jsonStr="  treu  ";
        Assert.assertEquals(ResultCode.INVALID_VALUE, asJson.parse(jsonNode,jsonStr));
    }

    @Test
    public void testParseTrueWithMoreSuffix(){
        Assert.assertEquals(ResultCode.ROOT_NOT_SINGULAR, asJson.parse(jsonNode," true a"));
    }

    @Test
    public void testParseExpectValueWithEmptyStr(){
        Assert.assertEquals(ResultCode.EXPECT_VALUE,asJson.parse(jsonNode,""));
    }

    @Test
    public void testParseExpectValueWithNull(){
        Assert.assertEquals(ResultCode.EXPECT_VALUE,asJson.parse(jsonNode,null));
    }

    private void testParseNumber(double expect, String jsonStr,JsonNode jsonNode){
        Assert.assertEquals(asJson.parse(jsonNode,jsonStr),ResultCode.OK);
        Assert.assertEquals(jsonNode.getType(),DataType.NUMBER);
        Assert.assertEquals(jsonNode.getNum(),expect);
        System.out.println(String.format("parse %s successfully",jsonStr));
    }

    private void testParseWrongNumber(String jsonStr,JsonNode jsonNode){
        Assert.assertEquals(asJson.parse(jsonNode,jsonStr),ResultCode.INVALID_VALUE);
        System.out.println(String.format("check %s successfully",jsonStr));
    }

    @Test
    public void testParseNumber(){
        testParseNumber(0.01,"0.01",jsonNode);
        testParseNumber(-0.01,"-0.01",jsonNode);
        testParseNumber(1.0,"1",jsonNode);
        testParseNumber(-1.0,"-1.0",jsonNode);
        testParseNumber(1.5,"1.5",jsonNode);
        testParseNumber(-1.5,"-1.5",jsonNode);
        testParseNumber(3.1416,"3.1416",jsonNode);
        testParseNumber(1E10,"1E10",jsonNode);
        testParseNumber(1e10,"1e10",jsonNode);
        testParseNumber(1E+10,"1E+10",jsonNode);
        testParseNumber(1E-10,"1E-10",jsonNode);
        testParseNumber(-1E10,"-1E10",jsonNode);
        testParseNumber(-1e10,"-1e10",jsonNode);
        testParseNumber(-1E+10,"-1E+10",jsonNode);
        testParseNumber(-1E-10,"-1E-10",jsonNode);
        testParseNumber(1.234E+10,"1.234E+10",jsonNode);
        testParseNumber(1.234E-10,"1.234E-10",jsonNode);
        testParseNumber(0.0,"1e-10000",jsonNode);
    }

    @Test
    public void testParseWrongNumber(){
        testParseWrongNumber("+0",jsonNode);
        testParseWrongNumber("01",jsonNode);
        testParseWrongNumber("-0",jsonNode);
        testParseWrongNumber("+0000",jsonNode);
        testParseWrongNumber("+01",jsonNode);
        testParseWrongNumber(".123",jsonNode);
        testParseWrongNumber("1.",jsonNode);
        testParseWrongNumber("INF",jsonNode);
        testParseWrongNumber("inf",jsonNode);
        testParseWrongNumber("NAN",jsonNode);
        testParseWrongNumber("nan",jsonNode);
    }

    @Test
    public void testParseString(){
        Assert.assertEquals(asJson.parse(jsonNode,"\"\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"");
        Assert.assertEquals(asJson.parse(jsonNode,"\"hello world\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"hello world");
        Assert.assertEquals(asJson.parse(jsonNode,"\"a\\na\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"a\na");
        Assert.assertEquals(asJson.parse(jsonNode,"\"a\\\"a\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"a\"a");
        Assert.assertEquals(asJson.parse(jsonNode,"\"a\\\"\\b\\f\\n\\r\\t\\/a\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"a\"\b\f\n\r\t/a");
        Assert.assertEquals(asJson.parse(jsonNode,"\"a\\\u005da\""),ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),"a\u005da");
    }

    @Test
    public void testParseWrongString(){
        Assert.assertEquals(asJson.parse(jsonNode,"a\\\u0020a"),ResultCode.INVALID_VALUE);
        Assert.assertEquals(asJson.parse(jsonNode,"a\\\u0020a"),ResultCode.INVALID_VALUE);
    }
}
