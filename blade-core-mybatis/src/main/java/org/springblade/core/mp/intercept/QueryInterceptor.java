/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */

package org.springblade.core.mp.intercept;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.Ordered;

/**
 * 自定义 mybatis plus 查询拦截器
 *
 * @author L.cm
 */
@SuppressWarnings({"rawtypes"})
public interface QueryInterceptor extends Ordered {

	/**
	 * 拦截处理
	 *
	 * @param executor
	 * @param ms
	 * @param parameter
	 * @param rowBounds
	 * @param resultHandler
	 * @param boundSql
	 */
	void intercept(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql);

	/**
	 * 排序
	 *
	 * @return int
	 */
	@Override
	default int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
