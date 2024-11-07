package com.cool.core.request;


import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * 网关请求类接口
 * 提供可修改的请求对象（Request参数操作接口）
 */
public interface IGatewayRequest {

    /**
     * 修改域名（目标服务主机地址）
     * @param host
     */
    void setModifyHost(String host);

    /**
     * 获取域名（目标服务主机地址）
     * @return
     */
    String getModifyHost();

    /**
     * 设置目标服务路径
     * @param path
     */
    void setModifyPath(String path);

    /**
     * 获取目标服务路径
     * @return
     */
    String getModifyPath();

    /**
     * 添加请求头信息
     * @param name
     * @param value
     */
    void addHeader(CharSequence name, String value);

    /**
     * 设置请求头信息
     * @param name
     * @param value
     */
    void setHeader(CharSequence name, String value);

    /**
     * get请求参数
     * @param name
     * @param value
     */
    void addQueryParam(String name, String value);

    /**
     * post（表单）请求参数
     * @param name
     * @param value
     */
    void addFormParam(String name, String value);

    /**
     * 添加或者替换Cookie
     * @param cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 设置请求超时时间
     * @param requestTimeout
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 获取最终的请求路径（包含请求参数，Http://localhost:8080/api/admin?name=111）
     */
    String getFinalUrl();

    /**
     * 构造最终的请求对象
     * @return
     */
    Request build();
}
