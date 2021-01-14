package org.anonymous;

import org.anonymous.constant.DataType;
import org.anonymous.constant.JsonDataConstant;
import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonMember;
import org.anonymous.data.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class AsJsonTest {

    private JsonNode jsonNode;

    private AsJson asJson;

    @BeforeMethod
    public void prepareData(){
        jsonNode=new JsonNode();
        jsonNode.setType(DataType.NULL);
        asJson=new AsJsonImpl();
    }

    @Test
    public void testParseNull(){
        Assert.assertEquals(ResultCode.OK, asJson.parse(jsonNode,"null"));
        Assert.assertEquals(DataType.NULL,jsonNode.getType());
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
        Assert.assertEquals(DataType.FALSE,jsonNode.getType());
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
        Assert.assertEquals(DataType.TRUE,jsonNode.getType());
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
        Assert.assertEquals(asJson.parse(jsonNode,"a\\\u0000a"),ResultCode.INVALID_VALUE);
    }

    @Test
    public void testUtf8(){
//        String str1="Hello World";
//        byte[] b1=str1.getBytes(Charset.forName("UTF8"));
//        for(byte b : b1){
//            System.out.print(String.format("0x%04X,",b));
//        }
//        byte[] b2=new byte[]{0x48,0x65,0x6C,0x6C,0x6F,0x20,0x57,0x6F,0x72,0x6C,0x64};
//        String str2=new String(b2,Charset.forName("UTF8"));
//        System.out.println(str2);


        String expectStr1="Hello World";
        byte[] bytes1=expectStr1.getBytes(Charset.forName("utf8"));
        String jsonStr1="\"\\u0048\\u0065\\u006C\\u006C\\u006F\\u0020\\u0057\\u006F\\u0072\\u006C\\u0064\"";
        checkUtf8Ok(bytes1, jsonStr1);

        byte[] bytes2=new byte[]{0x24};
        String jsonStr2="\"\\u0024\"";
        checkUtf8Ok(bytes2,jsonStr2);

        byte[] bytes3="\u00C2\u00A2".getBytes(Charset.forName("utf8"));
        String jsonStr3="\"\\u00A2\"";
        checkUtf8Ok(bytes3, jsonStr3);

        byte[] bytes4="\u00E2\u0082\u00AC".getBytes(Charset.forName("utf8"));
        String jsonStr4="\"\\u20AC\"";
        checkUtf8Ok(bytes4, jsonStr4);

        byte[] bytes5="\u00F0\u009D\u0084\u009E".getBytes(Charset.forName("utf8"));
        String jsonStr5="\"\\uD834\\uDD1E\"";
        checkUtf8Ok(bytes5, jsonStr5);

        byte[] bytes6="\u00F0\u009D\u0084\u009E".getBytes(Charset.forName("utf8"));
        String jsonStr6="\"\\ud834\\udd1e\"";
        checkUtf8Ok(bytes6, jsonStr6);
    }

    private void checkUtf8Ok(byte[] expect, String jsonStr){
        String expectStr=new String(expect,Charset.forName("utf8"));
        ResultCode resultCode=asJson.parse(jsonNode,jsonStr);
        Assert.assertEquals(resultCode,ResultCode.OK);
        Assert.assertEquals(jsonNode.getString(),expectStr);
        printSuccessful(expectStr);
    }

    private void checkUtf8NotOk(String jsonStr, ResultCode resultCode){
        ResultCode actualResultCode=asJson.parse(jsonNode,jsonStr);
        Assert.assertEquals(actualResultCode,resultCode);
        printFail(jsonStr);
    }

    private void printSuccessful(String str){
        System.out.println(String.format("%s check successful", str));
    }

    private void printFail(String str){
        System.out.println(String.format("%s check fail", str));
    }

    @Test
    public void testWrongUtf8(){
        ResultCode resultCode=ResultCode.PARSE_INVALID_UNICODE_HEX;
        List<String> jsonStrs=new ArrayList<>();

        jsonStrs.add("\"\\u\"");
        jsonStrs.add("\"\\u0\"");
        jsonStrs.add("\"\\u01\"");
        jsonStrs.add("\"\\u012\"");
        jsonStrs.add("\"\\u/00\"");
        jsonStrs.add("\"\\u0G00\"");
        jsonStrs.add("\"\\u00/0\"");
        jsonStrs.add("\"\\u00G0\"");
        jsonStrs.add("\"\\u000g\"");
        jsonStrs.add("\"\\u 123\"");

        for(String jsonStr : jsonStrs){
            checkUtf8NotOk(jsonStr,resultCode);
        }

    }


    @Test
    public void testParseArray(){
        // test empty array
        JsonNode expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        expect.setValue(new ArrayList<Object>());
        String arrayStr="[]";
        testParseCorrectArray(arrayStr,expect);

        // test number array
        expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        List<Object> nodes=new ArrayList<>();
        JsonNode arrayNode1=new JsonNode();
        arrayNode1.setType(DataType.NUMBER);
        arrayNode1.setValue(1.0);
        nodes.add(arrayNode1);
        expect.setValue(nodes);
        arrayStr="[1.0]";
        testParseCorrectArray(arrayStr,expect);

        //test number array
        expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        nodes=new ArrayList<>();
        arrayNode1=new JsonNode();
        arrayNode1.setType(DataType.NUMBER);
        arrayNode1.setValue(1.0);
        JsonNode arrayNode2=new JsonNode();
        arrayNode2.setType(DataType.NUMBER);
        arrayNode2.setValue(-1.5);
        nodes.add(arrayNode1);
        nodes.add(arrayNode2);
        expect.setValue(nodes);
        arrayStr="[1.0,-1.5]";
        testParseCorrectArray(arrayStr,expect);

        //test string array
        expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        nodes=new ArrayList<>();
        arrayNode1=new JsonNode();
        arrayNode1.setType(DataType.STRING);
        arrayNode1.setValue("abc");
        arrayNode2=new JsonNode();
        arrayNode2.setType(DataType.STRING);
        arrayNode2.setValue("\u00F0\u009D\u0084\u009E");

        nodes.add(arrayNode1);
        nodes.add(arrayNode2);
        expect.setValue(nodes);
        arrayStr="[\"abc\",\"\\ud834\\udd1e\"]";
        testParseCorrectArray(arrayStr,expect);

        //test true, false, null array
        expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        nodes=new ArrayList<>();
        arrayNode1=new JsonNode();
        arrayNode1.setType(DataType.TRUE);
        arrayNode1.setValue(true);
        arrayNode2=new JsonNode();
        arrayNode2.setType(DataType.FALSE);
        arrayNode2.setValue(false);
        JsonNode arrayNode3=new JsonNode();
        arrayNode3.setType(DataType.NULL);
        arrayNode3.setValue(JsonDataConstant.NULL);


        nodes.add(arrayNode1);
        nodes.add(arrayNode2);
        nodes.add(arrayNode3);
        expect.setValue(nodes);
        arrayStr="[true,false,null]";
        testParseCorrectArray(arrayStr,expect);

        //test nested array
        expect=new JsonNode();
        expect.setType(DataType.ARRAY);
        nodes=new ArrayList<>();
        arrayNode1.setType(DataType.TRUE);
        arrayNode1.setValue(true);
        arrayNode2.setType(DataType.ARRAY);
        List<Object> nodes1=new ArrayList<>();
        JsonNode nestedNode1=new JsonNode();
        nestedNode1.setType(DataType.TRUE);
        nestedNode1.setValue(true);
        JsonNode nestedNode2=new JsonNode();
        nestedNode2.setType(DataType.FALSE);
        nestedNode2.setValue(false);
        nodes1.add(nestedNode1);
        nodes1.add(nestedNode2);
        arrayNode2.setValue(nodes1);
        arrayNode3.setType(DataType.NULL);
        arrayNode3.setValue(JsonDataConstant.NULL);


        nodes.add(arrayNode1);
        nodes.add(arrayNode2);
        nodes.add(arrayNode3);
        expect.setValue(nodes);
        arrayStr="[true,[true,false],null]";
        testParseCorrectArray(arrayStr,expect);
    }

    private void testParseCorrectArray(String arrayStr,JsonNode expect){
        ResultCode resultCode=asJson.parse(jsonNode,arrayStr);
        Assert.assertEquals(resultCode,ResultCode.OK);
        Assert.assertEquals(jsonNode.toString(),expect.toString());
        System.out.println("check finish: "+expect);
    }

    @Test
    public void testParseWrongArray(){
        String jsonArray="[1,2";
        parseWrongArray(jsonArray,ResultCode.PARSE_MISS_COMMA_OR_CURLY_BRACKET);
    }

    private void parseWrongArray(String jsonArray,ResultCode expectResultCode){
        ResultCode resultCode=asJson.parse(jsonNode,jsonArray);
        Assert.assertEquals(resultCode,expectResultCode);
        System.out.println("check wrong string "+jsonArray+" finish.");
    }


}
