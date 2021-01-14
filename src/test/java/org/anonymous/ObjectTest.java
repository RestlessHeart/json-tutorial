package org.anonymous;

import org.anonymous.constant.DataType;
import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonMember;
import org.anonymous.data.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ObjectTest {
    private JsonNode jsonNode;

    private AsJson asJson;

    @BeforeMethod
    public void prepareData(){
        jsonNode=new JsonNode();
        jsonNode.setType(DataType.NULL);
        asJson=new AsJsonImpl();
    }

    @Test
    public void testParseCorrectObject(){
        String arrayStr="{\"m1\":\"v1\",\"m2\":\"v2\"}";
        JsonNode expect=new JsonNode();
        List<Object> members=new ArrayList<>();
        JsonMember member1=new JsonMember();
        member1.key="m1";
        JsonNode value1= new JsonNode();
        member1.jsonNode=value1;
        value1.setType(DataType.STRING);
        value1.setValue("v1");
        members.add(member1);
        JsonMember member2=new JsonMember();
        member2.key="m2";
        JsonNode value2= new JsonNode();
        member2.jsonNode=value2;
        value2.setType(DataType.STRING);
        value2.setValue("v2");
        members.add(member2);
        expect.setType(DataType.OBJECT);
        expect.setValue(members);
        testParseCorrectObject(arrayStr,expect);
    }

    private void testParseCorrectObject(String objectStr, JsonNode expect){
        ResultCode resultCode=asJson.parse(jsonNode, objectStr);
        Assert.assertEquals(resultCode,ResultCode.OK);
        Assert.assertEquals(jsonNode.toString(),expect.toString());
        System.out.println("check finish "+expect);
    }

    @Test
    public void testParseWrongObject(){
        String objectStr="{\"a\"}";
        testParseWrongObject(objectStr,ResultCode.PARSE_MISS_COLON);

        objectStr="{:\"a\"}";
        testParseWrongObject(objectStr,ResultCode.PARSE_MISS_KEY);

        objectStr="{\"a\":\"b\"";
        testParseWrongObject(objectStr,ResultCode.PARSE_MISS_COMMA_OR_CURLY_BRACKET);

        objectStr="{\"a\":\"b\"1";
        testParseWrongObject(objectStr,ResultCode.PARSE_MISS_COMMA_OR_CURLY_BRACKET);
    }

    private void testParseWrongObject(String objectStr, ResultCode expectCode){
        ResultCode resultCode=asJson.parse(jsonNode,objectStr);
        Assert.assertEquals(resultCode,expectCode);
    }

    @Test
    public void testSuccessfulGenerate(){
        String expect="{\"a\":null}";
        asJson.parse(jsonNode,expect);
        testSuccessfulGenerate(expect);

        expect="{\"a\":[null,false,true,1.5,\"a\",[\"a1\",\"a2\"],{\"na\":\"na\",\"nb\":\"nb\"}],\"b\":{\"c\":\"cValue\"}}";
        asJson.parse(jsonNode,expect);
        testSuccessfulGenerate(expect);
    }

    private void testSuccessfulGenerate(String expect){
        String result=asJson.generate(jsonNode);
        Assert.assertEquals(result,expect);
        System.out.println("successful generate: "+expect);
    }

    @Test
    public void testGetValue(){
        String jsonStr="{\"k1\":\"m1\",\"k2\":{\"k3\":123}}";
        asJson.parse(jsonNode,jsonStr);

        Assert.assertEquals(jsonNode.getObjectValue("k1").getString(),"m1");
    }

    @Test
    public void testGetKeys(){
        String jsonStr="{\"k1\":\"m1\",\"k2\":{\"k3\":123}}";
        asJson.parse(jsonNode,jsonStr);
        List<String> keys=new ArrayList<>();
        keys.add("k1");
        keys.add("k2");
        Assert.assertEquals(jsonNode.getObjectKeys(),keys);
    }
}
