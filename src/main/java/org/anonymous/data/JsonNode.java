package org.anonymous.data;

import org.anonymous.constant.DataType;

import java.util.ArrayList;
import java.util.List;

public class JsonNode {
    DataType type;

    Object value;

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public double getNum() {
        assert type==DataType.NUMBER;
        return (Double)value;
    }

    public void setNum(double num) {
        this.value = num;
    }

    public String getString(){
        assert type==DataType.STRING;
        return (String)value;
    }

    public void setValue(String value){
        this.value=value;
    }

    public void setValue(Double value){
        this.value=value;
    }

    public void setValue(Boolean value){
        this.value=value;
    }

    public void setValue(List<Object> value){
        this.value=value;
    }

    public JsonNode getValue(String key){
        if(getType()!=DataType.OBJECT){
            throw new UnsupportedOperationException("You could not get value from a non-object json.");
        }
        for(JsonMember member:getObject()){
            if(key.equals(member.key)){
                return member.jsonNode;
            }
        }
        return null;
    }

    public List<JsonNode> getArray(){
        return (List<JsonNode>)this.value;
    }

    public Boolean getBoolean(){
        return (Boolean)this.value;
    }

    public List<JsonMember> getObject(){return (List<JsonMember>)this.value;}

    @Override
    public String toString() {
        return "JsonNode{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    // get object value
    public JsonNode getObjectValue(String key){
        for(JsonMember jsonMember:this.getObject()){
            if(key.equals(jsonMember.key)){
                return jsonMember.jsonNode;
            }
        }
        return null;
    }

    // get object keys
    public List<String> getObjectKeys(){
        List<String> keys=new ArrayList<>();
        for(JsonMember jsonMember: this.getObject()){
            keys.add(jsonMember.key);
        }
        return keys;
    }

    // equals
    public boolean equals(JsonNode jsonNode){
        if(type!=jsonNode.getType()){
            return false;
        }
        switch(type){
            case NULL:
            case TRUE:
            case FALSE:
                return true;
            case STRING:
                return this.getString()==jsonNode.getString();
            case NUMBER:
                return this.getNum()==jsonNode.getNum();
            case ARRAY:
                this.getArray().equals(jsonNode.getArray());
            case OBJECT:
                List<JsonMember> members1=this.getObject();
                List<JsonMember> members2=jsonNode.getObject();
                if(members1.size()!=members2.size()){
                    return false;
                }
                for(JsonMember jsonMember:members1){
                    if(!jsonMember.jsonNode.equals(jsonNode.getObjectValue(jsonMember.key))){
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}
