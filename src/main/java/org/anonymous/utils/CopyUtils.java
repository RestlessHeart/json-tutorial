package org.anonymous.utils;

import org.anonymous.constant.DataType;
import org.anonymous.data.JsonMember;
import org.anonymous.data.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class CopyUtils {
    public static void copy(JsonNode source, JsonNode dest) {
        switch(source.getType()){
            case OBJECT:
                dest.setType(DataType.OBJECT);
                List<Object> memberList=new ArrayList<>();
                dest.setValue(memberList);
                for(JsonMember jsonMember:source.getObject()){
                    JsonNode sourceNode=jsonMember.jsonNode;
                    JsonMember member=new JsonMember();
                    member.key=jsonMember.key;
                    JsonNode destNode=new JsonNode();
                    CopyUtils.copy(sourceNode, destNode);
                    member.jsonNode=destNode;
                    memberList.add(member);
                }
                break;
            case FALSE:
                dest.setType(DataType.FALSE);
                dest.setValue(false);
                break;
            case TRUE:
                dest.setType(DataType.TRUE);
                dest.setValue(true);
                break;
            case NULL:
                dest.setType(DataType.NULL);
                break;
            case NUMBER:
                dest.setType(DataType.NUMBER);
                dest.setValue(source.getNum());
                break;
            case STRING:
                dest.setType(DataType.STRING);
                dest.setValue(source.getString());
                break;
            case ARRAY:
                dest.setType(DataType.ARRAY);
                List<Object> jsonNodes=new ArrayList<>();
                for(JsonNode sourceNode:source.getArray()){
                    JsonNode destNode=new JsonNode();
                    CopyUtils.copy(sourceNode,destNode);
                    jsonNodes.add(destNode);
                }
                dest.setValue(jsonNodes);
                break;
            default:
                dest.setType(DataType.NULL);
        }
    }
}
