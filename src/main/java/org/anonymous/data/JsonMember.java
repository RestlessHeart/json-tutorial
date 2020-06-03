package org.anonymous.data;

public class JsonMember {
    public String key;
    public JsonNode jsonNode;

    @Override
    public String toString() {
        return "JsonMember{" +
                "key='" + key + '\'' +
                ", jsonNode=" + jsonNode +
                '}';
    }
}
