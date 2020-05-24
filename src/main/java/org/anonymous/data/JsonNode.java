package org.anonymous.data;

import org.anonymous.constant.DataType;

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

    public void setValue(List<JsonNode> value){
        this.value=value;
    }

    public List<JsonNode> getArray(){
        return (List<JsonNode>)this.value;
    }

    public Boolean getBoolean(){
        return (Boolean)this.value;
    }

    @Override
    public String toString() {
        return "JsonNode{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
