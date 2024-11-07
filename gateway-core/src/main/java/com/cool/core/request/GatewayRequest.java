package com.cool.core.request;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Getter;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.cookie.Cookie;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 网关请求对象
 */
public class GatewayRequest implements IGatewayRequest{

    /**
     * 服务ID
     */
    @Getter
    private final String uniquedId;

    /**
     * 请求进入网关时间
     */
    @Getter
    private final long beginTime;

    /**
     * 请求离开网关时间
     */
    @Getter
    private final long endTime;

    /**
     * 字符集不会变的
     */
    @Getter
    private final Charset charset;

    /**
     * 客户端的IP，主要用于做流控、黑白名单
     */
    @Getter
    private final String clientIp;

    /**
     * 请求的地址：IP：port
     */
    @Getter
    private final String host;

    /**
     *  请求的路径   /XXX/XXX/XX
     */
    @Getter
    private final String path;

    /**
     * URI：统一资源标识符，/XXX/XXX/XXX?attr1=value&attr2=value2
     * URL：统一资源定位符，它只是URI的子集一个实现
     */
    @Getter
    private final String uri;

    /**
     * 请求方法 post/put/GET
     */
    @Getter
    private final HttpMethod httpMethod;

    /**
     * 请求的格式
     */
    @Getter
    private final String contentType;

    /**
     * 请求头信息
     */
    @Getter
    private final HttpHeaders httpHeaders;

    /**
     * 参数解析器
     */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /**
     * FullHttpRequest
     */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /**
     * 请求体
     */
    @Getter
    private String body;

    /**
     * 请求Cookie
     */
    @Getter
    private Map<String,Cookie> cookieMap;

    /**
     * post请求定义的参数结合
     */
    @Getter
    private Map<String, List<String>> postParameters;


    /******可修改的请求变量***************************************/
    /**
     * 可修改的Scheme，默认是http://
     */
    private String modifyScheme;

    /**
     * 可修改的Host，默认是IP:port
     */
    private String modifyHost;

    /**
     * 可修改的Path，默认是/XXX/XXX/XXX
     */
    private String modifyPath;

    /**
     * 构建下游请求时的http请求构建器
     */
    private final RequestBuilder requestBuilder;

    /**
     * 构造器
     * @param uniquedId
     * @param charset
     * @param clientIp
     * @param host
     * @param uri
     * @param httpMethod
     * @param contentType
     * @param httpHeaders
     * @param fullHttpRequest
     */
    public GatewayRequest(String uniquedId, long beginTime,
                          long endTime, Charset charset,
                          String clientIp, String host,
                          String path, String uri,
                          HttpMethod httpMethod,
                          String contentType,
                          HttpHeaders httpHeaders,
                          QueryStringDecoder queryStringDecoder,
                          FullHttpRequest fullHttpRequest,
                          RequestBuilder requestBuilder) {
        this.uniquedId = uniquedId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.path = path;
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.contentType = contentType;
        this.httpHeaders = httpHeaders;
        this.queryStringDecoder = queryStringDecoder;
        this.fullHttpRequest = fullHttpRequest;
        this.requestBuilder = requestBuilder;
    }


    @Override
    public void setModifyHost(String host) {

    }

    @Override
    public String getModifyHost() {
        return "";
    }

    @Override
    public void setModifyPath(String path) {

    }

    @Override
    public String getModifyPath() {
        return "";
    }

    @Override
    public void addHeader(CharSequence name, String value) {

    }

    @Override
    public void setHeader(CharSequence name, String value) {

    }

    @Override
    public void addQueryParam(String name, String value) {

    }

    @Override
    public void addFormParam(String name, String value) {

    }

    @Override
    public void addOrReplaceCookie(Cookie cookie) {

    }

    @Override
    public void setRequestTimeout(int requestTimeout) {

    }

    @Override
    public void getFinalUrl() {

    }

    @Override
    public Request build() {
        return null;
    }
}
