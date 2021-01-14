package org.anonymous;

import org.anonymous.data.JsonNode;
import org.anonymous.utils.CompareUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CompareUtilsTest {
    private JsonNode json1;
    private JsonNode json2;
    private AsJson asJson;

    @BeforeMethod
    public void setup(){
        json1=new JsonNode();
        json2=new JsonNode();
        asJson=new AsJsonImpl();
    }

    @Test
    public void testCompareEqual(){
        String jsonStr1="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"],\"k7\":{\"k71\":\"v71\",\"k72\":\"v72\"}}";
        String jsonStr2="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"],\"k7\":{\"k71\":\"v71\",\"k72\":\"v72\"}}";
        asJson.parse(json1,jsonStr1);
        asJson.parse(json2,jsonStr2);
        Assert.assertTrue(CompareUtils.equals(json1, json2));
    }
    @Test
    public void testCompareDiff(){
        String jsonStr1="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"],\"k7\":{\"k71\":\"v71\",\"k72\":\"v72\"}}";
        String jsonStr2="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"]," +
                "\"k7\":{\"k71\":\"v71\",\"k72\":\"v73\"}}";
        asJson.parse(json1,jsonStr1);
        asJson.parse(json2,jsonStr2);
        Assert.assertFalse(CompareUtils.equals(json1,json2));
    }
    @Test
    public void testCompareSame(){
        String jsonStr1="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"],\"k7\":{\"k71\":\"v71\",\"k72\":\"v72\"}}";
        String jsonStr2="{\"k1\":false,\"k2\":true,\"k3\":null,\"k4\":123,\"k5\":\"v5\",\"k6\":[\"v61\",\"v62\"],\"k7\":{\"k71\":\"v71\",\"k72\":\"v72\"}}";
        asJson.parse(json1,jsonStr1);
        asJson.parse(json2,jsonStr2);
        Assert.assertTrue(CompareUtils.same(json1, json1));
        Assert.assertFalse(CompareUtils.same(json1, json2));
    }
}
