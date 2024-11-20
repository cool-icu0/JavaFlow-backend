package com.cool.core.netty.processor;

import com.cool.core.context.HttpRequestWrapper;

public interface NettyProcessor {
    void process(HttpRequestWrapper httpRequestWrapper);
}
