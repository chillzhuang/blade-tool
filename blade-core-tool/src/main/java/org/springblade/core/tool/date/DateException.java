package org.springblade.core.tool.date;

import org.springblade.core.tool.utils.StringUtil;

/**
 * 工具类异常
 * @author xiaoleilu
 */
public class DateException extends RuntimeException{
	private static final long serialVersionUID = 8247610319171014183L;

	public DateException(Throwable e) {
		super(StringUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage()), e);
	}
	
	public DateException(String message) {
		super(message);
	}
	
	public DateException(String messageTemplate, Object... params) {
		super(StringUtil.format(messageTemplate, params));
	}
	
	public DateException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public DateException(Throwable throwable, String messageTemplate, Object... params) {
		super(StringUtil.format(messageTemplate, params), throwable);
	}
}
