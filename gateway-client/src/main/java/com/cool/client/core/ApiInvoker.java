package com.cool.client.core;

import java.lang.annotation.*;

/**
 * 必须要在服务的方法上面强制声明
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiInvoker {
    String path();
}
