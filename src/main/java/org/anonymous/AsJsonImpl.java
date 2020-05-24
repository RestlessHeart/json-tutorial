package org.anonymous;

import org.anonymous.constant.DataType;
import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonNode;
import org.anonymous.data.ParseContext;

import java.util.ArrayList;
import java.util.List;

import static org.anonymous.constant.JsonDataConstant.FALSE;
import static org.anonymous.constant.JsonDataConstant.NULL;
import static org.anonymous.constant.JsonDataConstant.TRUE;
import static org.anonymous.constant.JsonDataConstant.VALID_NUMBER_START;
import static org.anonymous.constant.JsonDataConstant.whiteSpaceList;

public class AsJsonImpl implements AsJson {

    public ResultCode parse(JsonNode jsonNode, final String jsonStr) {
        ParseContext parseContext = new ParseContext();

        if (jsonStr == null) {
            return ResultCode.EXPECT_VALUE;
        }
        parseContext.jsonStr = jsonStr;
        jsonNode.setType(DataType.NULL);
        parseWhitespace(parseContext);
        if(jsonStr.length()==0){
            return ResultCode.EXPECT_VALUE;
        }
        return parseValue(jsonNode, parseContext);
    }

    public DataType getType(JsonNode jsonNode) {
        return jsonNode.getType();
    }

    public double getNumber(JsonNode jsonNode){
        return jsonNode.getNum();
    }

    private void parseWhitespace(ParseContext parseContext) {
        String jsonStr = parseContext.jsonStr;
        int index = 0;
        while (index < jsonStr.length()) {
            boolean findWhiteSpace=false;
            for(char c : whiteSpaceList){
                if(jsonStr.charAt(index)==c){
                    index++;
                    findWhiteSpace=true;
                    break;
                }
            }
            if(!findWhiteSpace){
                break;
            }
        }
        parseContext.jsonStr = jsonStr.substring(index);
    }

    private ResultCode parseValue(JsonNode jsonNode, ParseContext parseContext) {
        switch (parseContext.jsonStr.charAt(0)) {
            case 'n':
                return parseValue(jsonNode, parseContext,NULL,DataType.NULL);
            case 'f':
                return parseValue(jsonNode, parseContext,FALSE,DataType.FALSE);
            case 't':
                return parseValue(jsonNode, parseContext,TRUE,DataType.TRUE);
            case '"':
                return parseStringValue(jsonNode,parseContext);
            case '[':
                return parseArray(jsonNode,parseContext);
            default:
                return parseNumber(jsonNode,parseContext);
        }
    }

    private ResultCode parseValue(JsonNode jsonNode, ParseContext parseContext, String validStr, DataType dataType){
        String jsonStr = parseContext.jsonStr;
        for(int i=1; i<validStr.length();i++){
            if(jsonStr.charAt(i)!=validStr.charAt(i)){
                return ResultCode.INVALID_VALUE;
            }
        }
        parseContext.jsonStr = parseContext.jsonStr.substring(validStr.length());
        ResultCode suffixParseResult = parseSuffixWhiteSpace(jsonNode, parseContext);
        if (suffixParseResult != ResultCode.OK) {
            return suffixParseResult;
        }
        if(parseContext.jsonStr!=null&&parseContext.jsonStr.length()>0){
            if(parseContext.jsonStr.charAt(0)!=','&&parseContext.jsonStr.charAt(0)!=']'&&parseContext.jsonStr.charAt(0)!='}'){
                return ResultCode.ROOT_NOT_SINGULAR;
            }
        }
        jsonNode.setType(dataType);
        if(dataType==DataType.NULL){
            jsonNode.setValue(NULL);
        }else if(dataType==DataType.FALSE){
            jsonNode.setValue(Boolean.FALSE);
        }else if(dataType== DataType.TRUE){
            jsonNode.setValue(Boolean.TRUE);
        }
        return ResultCode.OK;
    }

    private ResultCode parseStringValue(JsonNode jsonNode, ParseContext parseContext){
        ResultCode resultCode=parseStringRawValue(parseContext);
        if(ResultCode.OK!=resultCode){
            return resultCode;
        }
        jsonNode.setType(DataType.STRING);
        jsonNode.setValue(parseContext.currentStr);
        parseContext.clearCurrentStr();
        return ResultCode.OK;
    }

    private ResultCode parseStringRawValue(ParseContext parseContext){
        String jsonStr=parseContext.jsonStr;
        int strSize=0;
        int pos=1;
        while(pos<jsonStr.length()&&jsonStr.charAt(pos)!='"'){
            strSize++;
            switch (jsonStr.charAt(pos)){
                case '\\':
                    if(pos==jsonStr.length()-1){
                        parseContext.clearN(strSize);
                        return ResultCode.INVALID_VALUE;
                    }
                    pos++;
                    switch (jsonStr.charAt(pos)) {
                        case 'u':
                            pos++;
                            Integer result=parseHex4(pos,jsonStr);
                            if(result==null){
                                return ResultCode.PARSE_INVALID_UNICODE_HEX;
                            }
                            if(result>= 0xD800&& result<=0xDBFF){
                                pos+=4;
                                if(pos>=jsonStr.length()){
                                    return ResultCode.PARSE_INVALID_UNICODE_HEX;
                                }
                                if(jsonStr.charAt(pos)!='\\'){
                                    return ResultCode.PARSE_INVALID_UNICODE_HEX;
                                }
                                if(jsonStr.charAt(pos+1)!='u'){
                                    return ResultCode.PARSE_INVALID_UNICODE_HEX;
                                }
                                pos+=2;
                                Integer resultLow=parseHex4(pos,jsonStr);
                                if(resultLow==null){
                                    return ResultCode.PARSE_INVALID_UNICODE_HEX;
                                }
                                if(resultLow<=0xDC00||resultLow>=0xDFFF){
                                    return ResultCode.PARSE_INVALID_UNICODE_SURROGATE;
                                }
                                result=(((result-0xD800)<<10)|(resultLow-0xDC00))+0x10000;
                            }
                            Integer count=encodeUtf8(result,parseContext);
                            if(count==0){
                                return ResultCode.PARSE_INVALID_UNICODE_HEX;
                            }
                            strSize+=(count-1);
                            pos+=3;
                            break;
                        case '"':
                            parseContext.pushChar('"');
                            break;
                        case '\\':
                            parseContext.pushChar('\\');
                            break;
                        case '/':
                            parseContext.pushChar('/');
                            break;
                        case 'b':
                            parseContext.pushChar('\b');
                            break;
                        case 'f':
                            parseContext.pushChar('\f');
                            break;
                        case 'n':
                            parseContext.pushChar('\n');
                            break;
                        case 'r':
                            parseContext.pushChar('\r');
                            break;
                        case 't':
                            parseContext.pushChar('\t');
                            break;
                        default:
                            if(jsonStr.charAt(pos)<0x20){
                                parseContext.clearN(strSize);
                                return ResultCode.INVALID_VALUE;
                            }
                            parseContext.pushChar(jsonStr.charAt(pos));
                            break;
                    }
                    break;
                default:
                    parseContext.pushChar(jsonStr.charAt(pos));
            }
            pos++;
        }
        if(pos>jsonStr.length()||jsonStr.charAt(pos)!='"'){
            return ResultCode.INVALID_VALUE;
        }else{
            pos++;
        }
        parseContext.jsonStr=jsonStr.substring(pos);
        parseWhitespace(parseContext);
        parseContext.currentStr=parseContext.popString(strSize);
        return ResultCode.OK;
    }

    private ResultCode parseArray(JsonNode jsonNode, ParseContext parseContext){
        String jsonStr=parseContext.jsonStr;
        int pos=0;
        List<JsonNode> jsonNodeList=new ArrayList<>();
        if(jsonStr.charAt(pos)=='['&&jsonStr.charAt(pos+1)==']'){
            jsonNode.setType(DataType.ARRAY);
            jsonNode.setValue(jsonNodeList);
            return ResultCode.OK;
        }
        jsonStr=jsonStr.substring(pos+1);
        parseContext.jsonStr=jsonStr;
        while(true){
            JsonNode tmpJsonNode=new JsonNode();
            ResultCode resultCode=parseValue(tmpJsonNode,parseContext);
            if(ResultCode.OK!=resultCode){
                return resultCode;
            }
            jsonNodeList.add(tmpJsonNode);
            jsonStr=parseContext.jsonStr;
            if(jsonStr.charAt(0)==']'){
                jsonStr=jsonStr.substring(1);
                parseContext.jsonStr=jsonStr;
                break;
            }
            if(jsonStr.length()==0){
                return ResultCode.INVALID_VALUE;
            }
            if(jsonStr.charAt(0)==','){
                jsonStr=jsonStr.substring(1);
                parseContext.jsonStr=jsonStr;
                continue;
            }else{
                return ResultCode.INVALID_VALUE;
            }
        }
        jsonNode.setType(DataType.ARRAY);
        jsonNode.setValue(jsonNodeList);

        return ResultCode.OK;
    }

    private ResultCode parseSuffixWhiteSpace(JsonNode jsonNode, ParseContext parseContext) {
        parseWhitespace(parseContext);
        return ResultCode.OK;
    }
    private ResultCode parseNumber(JsonNode jsonNode, ParseContext parseContext){
        String jsonStr=parseContext.jsonStr;
        int subIndex=jsonStr.indexOf(',');
        if(subIndex==-1){
            subIndex=jsonStr.indexOf(']');
            if(subIndex==-1){
                subIndex=jsonStr.indexOf("}");
            }
        }
        if(subIndex!=-1){
            jsonStr=jsonStr.substring(0,subIndex);
        }
        jsonStr=jsonStr.trim();
        if(ResultCode.OK!=checkNumber(jsonStr)){
            return ResultCode.INVALID_VALUE;
        }

        try{
            double result=Double.valueOf(jsonStr);
            jsonNode.setNum(result);
            jsonNode.setType(DataType.NUMBER);
            if(subIndex!=-1){
                parseContext.jsonStr=parseContext.jsonStr.substring(subIndex);
            }
            return ResultCode.OK;
        }catch (Exception e){
            return ResultCode.INVALID_VALUE;
        }

    }

    private ResultCode checkNumber(String jsonStr){
        if(!VALID_NUMBER_START.contains(jsonStr.charAt(0))){
            return ResultCode.INVALID_VALUE;
        }
        if(jsonStr.length()==1&&(jsonStr.charAt(0)=='-'||jsonStr.charAt(0)=='+')){
            return ResultCode.INVALID_VALUE;
        }
        boolean findPoint=false;
        boolean findExp =false;
        int expIndex=0;
        boolean findFirstDigit=false;
        int nDigit=0;
        int nLeadZero=0;
        int numberPos=0;
        while(numberPos<jsonStr.length()){
            if(jsonStr.charAt(numberPos)=='-'||jsonStr.charAt(numberPos)=='+'){
                if(numberPos==0){
                    numberPos++;
                    continue;
                }else{
                    if(numberPos==expIndex+1){
                        numberPos++;
                        continue;
                    }
                    return ResultCode.INVALID_VALUE;
                }
            }
            if(jsonStr.charAt(numberPos)>='0'&&jsonStr.charAt(numberPos)<='9'){
                if(jsonStr.charAt(numberPos)!='0'){
                    if(!findFirstDigit){
                        findFirstDigit=true;
                        if(!findPoint&&nLeadZero>0){
                            return ResultCode.INVALID_VALUE;
                        }else{
                            if(findPoint){
                                nLeadZero=0;
                            }
                        }
                    }else{
                        if(nLeadZero!=0){
                            return ResultCode.INVALID_VALUE;
                        }
                    }
                }else{
                    if(findFirstDigit){
                        nLeadZero=0;
                    }else{
                        nLeadZero++;
                    }
                }
                numberPos++;
                nDigit++;
            }else{
                if(jsonStr.charAt(numberPos)=='.'){
                    if(findPoint){
                        return ResultCode.INVALID_VALUE;
                    }else{
                        if(numberPos==0){
                            return ResultCode.INVALID_VALUE;
                        }
                        findPoint=true;
                        numberPos++;
                        nDigit=0;
                        findFirstDigit=false;
                        continue;
                    }
                }
                if(jsonStr.charAt(numberPos)=='e'||jsonStr.charAt(numberPos)=='E'){
                    if(findExp){
                        return ResultCode.INVALID_VALUE;
                    }else{
                        if(numberPos==0){
                            return ResultCode.INVALID_VALUE;
                        }
                        expIndex=numberPos;
                        findExp =true;
                        nDigit=0;
                        numberPos++;
                        continue;
                    }
                }
                return ResultCode.INVALID_VALUE;
            }
        }
        if(nLeadZero>0){
            if(!(findPoint&&nLeadZero==1)) {
                return ResultCode.INVALID_VALUE;
            }
        }
        if(findExp ||findPoint){
            if(nDigit==0){
                return ResultCode.INVALID_VALUE;
            }
        }
        return ResultCode.OK;
    }

    private Integer parseHex4(int pos, String jsonStr){
        Integer result=null;
        int codePoint=0;
        for(int i=0; i<4;i++){
            if(pos>=jsonStr.length()){
                return result;
            }
            codePoint<<=4;
            char c=jsonStr.charAt(pos+i);
            if((c>='0'&&c<='9')){
                codePoint|=(c-'0');
            }else if((c>='A'&&c<='F')){
                codePoint|=(c-'A'+10);
            }else if(c>='a'&&c<='f'){
                codePoint|=(c-'a'+10);
            }else{
                return result;
            }
        }
        result=codePoint;
        return result;
    }

    private Integer encodeUtf8(Integer codePoint, ParseContext parseContext){
        Integer pushCount=0;
        if(codePoint >=0&& codePoint <=0x007F){
            parseContext.pushChar((char)codePoint.byteValue());
            pushCount=1;
        }else if(codePoint<=0x07FF){
            parseContext.pushChar((char)(0xC0|(codePoint>>6)&0xFF));
            parseContext.pushChar((char)(0x80|(codePoint)&0x3F));
            pushCount=2;
        }else if(codePoint<=0xFFFF){
            parseContext.pushChar((char)(0xE0|((codePoint >> 12)&0xFF)));
            parseContext.pushChar((char)(0x80|((codePoint >> 6)&0x3F)));
            parseContext.pushChar((char)(0x80|(codePoint&0x3F)));
            pushCount=3;
        }else if(codePoint<=0x10FFFF){
            parseContext.pushChar((char)(0xF0|(codePoint>>18)&0x07));
            parseContext.pushChar((char)(0x80|(codePoint>>12)&0x3F));
            parseContext.pushChar((char)(0x80|(codePoint>>6)&0x3F));
            parseContext.pushChar((char)(0x80|(codePoint)&0x3F));
            pushCount=4;
        }
        return pushCount;
    }
}
