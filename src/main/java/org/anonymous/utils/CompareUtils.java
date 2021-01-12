package org.anonymous.utils;

import org.anonymous.constant.DataType;
import org.anonymous.data.JsonMember;
import org.anonymous.data.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class CompareUtils {
    public static boolean equals(JsonNode firstNode, JsonNode secondNode){
        switch (firstNode.getType()){
            case NULL:
                if(secondNode.getType()== DataType.NULL){
                    return true;
                }else{
                    return false;
                }
            case NUMBER:
                if(secondNode.getType()==DataType.NUMBER&&firstNode.getNum()==secondNode.getNum()){
                   return true;
                }else{
                    return false;
                }
            case STRING:
                if(secondNode.getType()==DataType.STRING&&secondNode.getString().equals(firstNode.getString())){
                    return true;
                }else{
                    return false;
                }
            case TRUE:
                if(secondNode.getType()==DataType.TRUE){
                    return true;
                }else{
                    return false;
                }
            case FALSE:
                if(secondNode.getType()==DataType.FALSE){
                    return true;
                }else{
                    return false;
                }
            case ARRAY:
                if(secondNode.getType()==DataType.ARRAY){
                    List<JsonNode> firstArray=firstNode.getArray();
                    List<JsonNode> secondArray=secondNode.getArray();
                    if(firstArray.size()!=secondArray.size()){
                        return false;
                    }
                    for(int i=0;i<firstArray.size();i++){
                        if(!CompareUtils.equals(firstArray.get(i),secondArray.get(i))){
                            return false;
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            case OBJECT:
                if(secondNode.getType()==DataType.OBJECT){
                    List<JsonMember> sourceFirstObject=firstNode.getObject();
                    List<JsonMember> sourceSecondObject=secondNode.getObject();
                    if(sourceFirstObject.size()!=sourceSecondObject.size()){
                        return false;
                    }
                    List<JsonMember> firstObject=new ArrayList<>();
                    List<JsonMember> secondObject=new ArrayList<>();
                    sourceFirstObject.stream().forEach(jsonMember -> firstObject.add(jsonMember));
                    sourceSecondObject.stream().forEach(jsonMember -> secondObject.add(jsonMember));
                    firstObject.sort((jsonMember1,jsonMember2)->compare(jsonMember1,jsonMember2));
                    secondObject.sort((jsonMember1,jsonMember2)->compare(jsonMember1,jsonMember2));
                    for(int i=0; i<firstObject.size();i++){
                        JsonMember firstMem=firstObject.get(i);
                        JsonMember secondMem=secondObject.get(i);
                        if(firstMem.key.equals(secondMem.key)){
                            if(CompareUtils.equals(firstMem.jsonNode,secondMem.jsonNode)){
                                continue;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            default:
                return false;
        }
    }

    public static boolean same(JsonNode firstNode, JsonNode secondNode){
        switch (firstNode.getType()){
            case NULL:
                if(secondNode.getType()== DataType.NULL){
                    return true;
                }else{
                    return false;
                }
            case NUMBER:
                if(secondNode.getType()==DataType.NUMBER&&firstNode.getNum()==secondNode.getNum()){
                    return true;
                }else{
                    return false;
                }
            case STRING:
                if(secondNode.getType()==DataType.STRING&&secondNode.getString()==firstNode.getString()){
                    return true;
                }else{
                    return false;
                }
            case TRUE:
                if(secondNode.getType()==DataType.TRUE){
                    return true;
                }else{
                    return false;
                }
            case FALSE:
                if(secondNode.getType()==DataType.FALSE){
                    return true;
                }else{
                    return false;
                }
            case ARRAY:
                if(secondNode.getType()==DataType.ARRAY){
                    List<JsonNode> firstArray=firstNode.getArray();
                    List<JsonNode> secondArray=secondNode.getArray();
                    if(firstArray.size()!=secondArray.size()){
                        return false;
                    }
                    for(int i=0;i<firstArray.size();i++){
                        if(firstArray.get(i)!=secondArray.get(i)){
                            return false;
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            case OBJECT:
                if(secondNode.getType()==DataType.OBJECT){
                    List<JsonMember> sourceFirstObject=firstNode.getObject();
                    List<JsonMember> sourceSecondObject=secondNode.getObject();
                    if(sourceFirstObject.size()!=sourceSecondObject.size()){
                        return false;
                    }
                    List<JsonMember> firstObject=new ArrayList<>();
                    List<JsonMember> secondObject=new ArrayList<>();
                    sourceFirstObject.stream().forEach(jsonMember -> firstObject.add(jsonMember));
                    sourceSecondObject.stream().forEach(jsonMember -> secondObject.add(jsonMember));
                    firstObject.sort((jsonMember1,jsonMember2)->compare(jsonMember1,jsonMember2));
                    secondObject.sort((jsonMember1,jsonMember2)->compare(jsonMember1,jsonMember2));
                    for(int i=0; i<firstObject.size();i++){
                        JsonMember firstMem=firstObject.get(i);
                        JsonMember secondMem=secondObject.get(i);
                        if(firstMem.key.equals(secondMem.key)){
                            if(firstMem.jsonNode==secondMem.jsonNode){
                                continue;
                            }else{
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }
                    return true;
                }else{
                    return false;
                }
            default:
                return false;
        }
    }

    private static int compare(JsonMember jsonMember1, JsonMember jsonMember2){
        return jsonMember1.key.compareTo(jsonMember2.key);
    }
}
