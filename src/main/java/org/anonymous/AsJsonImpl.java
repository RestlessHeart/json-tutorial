package org.anonymous;

import org.anonymous.constant.DataType;
import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonNode;
import org.anonymous.data.ParseContext;

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

    private ResultCode parseSuffixWhiteSpace(JsonNode jsonNode, ParseContext parseContext) {
        parseWhitespace(parseContext);
        String jsonStr = parseContext.jsonStr;
        if (jsonStr.length() != 0) {
            return ResultCode.ROOT_NOT_SINGULAR;
        } else {
            return ResultCode.OK;
        }
    }
    private ResultCode parseNumber(JsonNode jsonNode, ParseContext parseContext){
        String jsonStr=parseContext.jsonStr;

        if(ResultCode.OK!=checkNumber(jsonStr)){
            return ResultCode.INVALID_VALUE;
        }

        try{
            double result=Double.valueOf(jsonStr);
            jsonNode.setNum(result);
            jsonNode.setType(DataType.NUMBER);
            return ResultCode.OK;
        }catch (Exception e){
            return ResultCode.INVALID_VALUE;
        }

    }

    private ResultCode parseDoubleNumber(JsonNode jsonNode, ParseContext parseContext){
        String jsonStr=parseContext.jsonStr;
        int positive=1;
        int numberPos=0;
        double result=0;
        double index=1E0;
        if(jsonStr.charAt(0)=='-'){
            positive=-1;
            numberPos++;
        }
        if(jsonStr.charAt(0)=='+'){
            numberPos++;
        }
        if(jsonStr.charAt(numberPos)=='.'||jsonStr.charAt(numberPos)=='-'||jsonStr.charAt(numberPos)=='+'){
            return ResultCode.INVALID_VALUE;
        }
        boolean findPoint=false;
        boolean findIndex=false;
        double decimal=1;
        double mantissa=0.0;
        while(numberPos<jsonStr.length()){
            if(jsonStr.charAt(numberPos)!='e'&&jsonStr.charAt(numberPos)!='E'){
                if(jsonStr.charAt(numberPos)>='0'&&jsonStr.charAt(numberPos)<='9'){
                    if(findPoint){

                        decimal*=0.1;
                        mantissa+=decimal*(jsonStr.charAt(numberPos)-'0');
                    }else{
                        mantissa=mantissa*10+(jsonStr.charAt(numberPos)-'0');
                    }
                }else{
                    if(jsonStr.charAt(numberPos)=='.'){
                        if(numberPos==0||numberPos==jsonStr.length()-1){
                            return ResultCode.INVALID_VALUE;
                        }
                        findPoint=true;
                    }else{
                        return ResultCode.INVALID_VALUE;
                    }
                }
                numberPos++;
            }else{
                if(!findIndex){
                    findIndex=true;
                    numberPos++;
                    break;
                }else{
                    return ResultCode.INVALID_VALUE;
                }
            }
        }
        Double.valueOf("");
        double exp=0.0;
        int expPositive=1;
        boolean isFirst=true;
        if(findIndex){
            while(numberPos<jsonStr.length()){
                if(isFirst){
                    if(jsonStr.charAt(numberPos)=='0'){
                        return ResultCode.INVALID_VALUE;
                    }else{
                        if(jsonStr.charAt(numberPos)=='-'){
                            expPositive=-1;
                            numberPos++;
                            isFirst=false;
                            continue;
                        }else if(jsonStr.charAt(numberPos)=='+'){
                            expPositive=1;
                            numberPos++;
                            isFirst=false;
                            continue;
                        }
                        isFirst=false;
                    }
                }
                if(jsonStr.charAt(numberPos)>='0'&&jsonStr.charAt(numberPos)<='9'){
                    exp=exp*10+(jsonStr.charAt(numberPos)-'0');
                }else{
                    return ResultCode.INVALID_VALUE;
                }
                numberPos++;
            }
        }
        exp=exp*expPositive;
        parseContext.jsonStr=jsonStr.substring(numberPos);
        ResultCode suffixParseResult = parseSuffixWhiteSpace(jsonNode, parseContext);
        if (suffixParseResult != ResultCode.OK) {
            return suffixParseResult;
        }

        result=(mantissa*positive)*Math.pow(10.0,exp);
        jsonNode.setNum(result);
        jsonNode.setType(DataType.NUMBER);
        return ResultCode.OK;
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

}
