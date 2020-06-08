package org.anonymous.utils;

import org.anonymous.data.JsonMember;
import org.anonymous.data.JsonNode;

import java.util.List;

public class JsonGenerator {
    public static String generate(JsonNode jsonNode){
        String result= generateValue(jsonNode, true);
        return String.format("{%s}",result);
    }

    private static String generateValue(JsonNode jsonNode,boolean isRoot){
        String result=null;
        switch(jsonNode.getType()){
            case NULL:
            case FALSE:
            case TRUE:
                result=generatePrimitiveType(jsonNode);
                break;
            case NUMBER:
                result=generateNumber(jsonNode);
                break;
            case STRING:
                result=String.format("\"%s\"",jsonNode.getString());
                break;
            case ARRAY:
                result=String.format("[%s]",generateArray(jsonNode));
                break;
            case OBJECT:
                if(isRoot){
                    result=generateObject(jsonNode);
                }else{
                    result=String.format("{%s}",generateObject(jsonNode));
                }
                break;
        }
        return result;
    }

    private static String generatePrimitiveType(JsonNode jsonNode){
        return jsonNode.getType().getValue();
    }

    private static String generateNumber(JsonNode jsonNode){
        return String.valueOf(jsonNode.getNum());
    }

    private static String generateArray(JsonNode jsonNode){
        List<JsonNode> jsonNodes=jsonNode.getArray();
        StringBuilder sb=new StringBuilder();
        for(JsonNode tmpJsonNode : jsonNodes){
            sb.append(generateValue(tmpJsonNode, false));
            sb.append(',');
        }
        return sb.substring(0,sb.length()-1);
    }

    private static String generateObject(JsonNode jsonNode){
        List<JsonMember> jsonMembers=jsonNode.getObject();
        StringBuilder sb=new StringBuilder();
        for(JsonMember jsonMember : jsonMembers){
            sb.append("\"");
            sb.append(jsonMember.key);
            sb.append("\"");
            sb.append(':');
            sb.append(generateValue(jsonMember.jsonNode,false));
            sb.append(',');
        }

        return sb.substring(0,sb.length()-1);
    }
}
