package com.cool.core.netty;

import com.cool.core.context.HttpRequestWrapper;

public interface NettyProcessor {
    void process(HttpRequestWrapper httpRequestWrapper);
}
