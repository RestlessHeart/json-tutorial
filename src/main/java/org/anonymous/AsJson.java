package org.anonymous;

import org.anonymous.constant.ResultCode;
import org.anonymous.data.JsonNode;

public interface AsJson {
    /**
     * @param jsonNode root json node
     * @param jsonStr json string to be parsed
     * @return result code
     */
    ResultCode parse(JsonNode jsonNode, final String jsonStr);

    /**
     *  generate json string from json object
     * @param jsonNode root json node
     * @return json format string for jsonNode
     */
    String generate(JsonNode jsonNode);
}
