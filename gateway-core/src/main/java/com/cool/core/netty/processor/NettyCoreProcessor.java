package com.cool.core.netty.processor;

import com.cool.common.enums.ResponseCode;
import com.cool.common.exception.BaseException;
import com.cool.common.exception.ConnectException;
import com.cool.common.exception.ResponseException;
import com.cool.core.ConfigLoader;
import com.cool.core.context.GatewayContext;
import com.cool.core.context.HttpRequestWrapper;
import com.cool.core.helper.AsyncHttpHelper;
import com.cool.core.helper.RequestHelper;
import com.cool.core.helper.ResponseHelper;
import com.cool.core.response.GatewayResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {
    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
        FullHttpRequest fullHttpRequest = httpRequestWrapper.getFullHttpRequest();
        ChannelHandlerContext ctx = httpRequestWrapper.getCtx();

        try {
            // 上下文处理
            GatewayContext gatewayContext = RequestHelper.doContext(fullHttpRequest, ctx);
            route(gatewayContext);
        }catch (BaseException e){
            // 异常
            log.error("process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, fullHttpRequest, fullHttpResponse);
        } catch (Throwable t) {
            // 未知异常
            log.error("process unkown error", t);
            FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, fullHttpRequest, fullHttpResponse);
        }

    }


    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, FullHttpResponse fullHttpResponse) {
        ctx.writeAndFlush(fullHttpResponse)
                .addListener(ChannelFutureListener.CLOSE); //释放资源后关闭channel
        ReferenceCountUtil.release(fullHttpRequest);
    }

    private void route(GatewayContext gatewayContext) {
        Request request = gatewayContext.getRequest().build();

        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete= ConfigLoader.getConfig().isWhenComplete();

        if(whenComplete){
            future.whenComplete((response, throwable) -> {
                complete(request,response,throwable,gatewayContext);
            });
        }else{
            future.whenCompleteAsync(((response, throwable) -> {
                complete(request,response,throwable,gatewayContext);
            }));
        }
    }
    public void complete(Request request ,
                         Response response,
                         Throwable throwable,
                         GatewayContext gatewayContext){
        gatewayContext.releaseRequest();

        try {
            if (Objects.nonNull(throwable)) {
                // 异常
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    // 超时
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    // 异常
                    gatewayContext.setThrowable(
                            new ConnectException(throwable,
                                    gatewayContext.getUniqueId(),
                                    url, ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                // 正常响应
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        }catch (Throwable t){
            // 意外的情况
            log.error("complete error",t);
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
        }finally {
            //兜底，优雅关机
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);
        }

    }
}
