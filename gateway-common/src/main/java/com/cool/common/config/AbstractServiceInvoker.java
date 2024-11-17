package com.cool.common.config;

import lombok.Data;

/**
 * 抽象的服务调用接口实现类
 */
@Data
public class AbstractServiceInvoker implements ServiceInvoker {
	
	protected String invokerPath;
	
	protected int timeout = 5000;

}
