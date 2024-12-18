package com.cool.core.response;

import com.cool.common.enums.ResponseCode;
import com.cool.common.utils.JSONUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;


/**
 * 网关响应
 * 网关回复消息对象
 */
@Data
public class GatewayResponse {

    //	响应头
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    //	额外的响应结果
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    //	返回的响应内容
    private String content;

    //  返回响应状态码
    private HttpResponseStatus httpResponseStatus;

    //如果是异步的，我们是不是需要有一个异步的返回对象
    private Response futureResponse;

    public GatewayResponse(){

    }
    /**
     * 设置响应头信息
     * @param key
     * @param val
     */
    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    /**
     * 构建异步网关响应对象
     * @param futureResponse
     * @return
     */
    public static GatewayResponse buildGatewayResponse(org.asynchttpclient.Response futureResponse) {
        GatewayResponse response = new GatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }

    /**
     * 返回一个json类型的响应信息，失败时候使用
     * @param code
     * @param args
     * @return
     */
    public static GatewayResponse buildGatewayResponse(ResponseCode code, Object... args) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, code.getStatus().code());
        objectNode.put(JSONUtil.CODE, code.getCode());
        objectNode.put(JSONUtil.MESSAGE, code.getMessage());
        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }

    /**
     * 返回一个json类型的响应信息, 成功时候使用
     * @param data
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Object data) {
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.CODE, ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA, data);
        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }


}
