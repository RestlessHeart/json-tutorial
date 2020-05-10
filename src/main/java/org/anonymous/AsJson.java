package org.anonymous;

import org.anonymous.constant.DataType;
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
     * @param jsonNode json data node
     * @return the data type of this node
     */
    DataType getType(final JsonNode jsonNode);
    /**
     *
     */
    double getNumber(final JsonNode jsonNode);
}
