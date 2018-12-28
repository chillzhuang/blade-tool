package org.springblade.core.boot.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Spring boot 控制器 请求日志，方便代码调试
 *
 * @author L.cm
 */
@Slf4j
@Aspect
@Configuration
@Profile({"dev", "test"})
public class RequestLogAspect {

	/**
	 * AOP 环切 控制器 R 返回值
	 *
	 * @param point JoinPoint
	 * @return Object
	 * @throws Throwable 异常
	 */
	@Around(
		"execution(!static org.springblade.core.tool.api.R<*> *(..)) && " +
			"(@within(org.springframework.stereotype.Controller) || " +
			"@within(org.springframework.web.bind.annotation.RestController))"
	)
	public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		final Map<String, Object> paraMap = new HashMap<>(16);
		for (int i = 0; i < args.length; i++) {
			MethodParameter methodParam = ClassUtil.getMethodParameter(method, i);
			PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
			if (pathVariable != null) {
				continue;
			}
			RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
			Object object = args[i];
			// 如果是body的json则是对象
			if (requestBody != null && object != null) {
				paraMap.putAll(BeanUtil.toMap(object));
			} else {
				RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
				String paraName;
				if (requestParam != null && StringUtil.isNotBlank(requestParam.value())) {
					paraName = requestParam.value();
				} else {
					paraName = methodParam.getParameterName();
				}
				paraMap.put(paraName, object);
			}
		}
		HttpServletRequest request = WebUtil.getRequest();
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();
		// 处理 参数
		List<String> needRemoveKeys = new ArrayList<>(paraMap.size());
		paraMap.forEach((key, value) -> {
			if (value instanceof HttpServletRequest) {
				needRemoveKeys.add(key);
				paraMap.putAll(((HttpServletRequest) value).getParameterMap());
			} else if (value instanceof HttpServletResponse) {
				needRemoveKeys.add(key);
			} else if (value instanceof InputStream) {
				needRemoveKeys.add(key);
			} else if (value instanceof MultipartFile) {
				String fileName = ((MultipartFile) value).getOriginalFilename();
				paraMap.put(key, fileName);
			} else if (value instanceof InputStreamSource) {
				needRemoveKeys.add(key);
			} else if (value instanceof WebRequest) {
				needRemoveKeys.add(key);
				paraMap.putAll(((WebRequest) value).getParameterMap());
			}
		});
		needRemoveKeys.forEach(paraMap::remove);
		// 打印请求
		log.info("================  Request Start  ================");
		if (paraMap.isEmpty()) {
			log.info("===> {}: {}", requestMethod, requestURI);
		} else {
			log.info("===> {}: {} Parameters: {}", requestMethod, requestURI, JsonUtil.toJson(paraMap));
		}
		// 打印请求头
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = headers.nextElement();
			String headerValue = request.getHeader(headerName);
			log.info("===headers===  {} : {}", headerName, headerValue);
		}
		// 打印执行时间
		long startNs = System.nanoTime();
		try {
			Object result = point.proceed();
			log.info("===Result===  {}", JsonUtil.toJson(result));
			return result;
		} finally {
			long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
			log.info("<=== {}: {} ({} ms)", request.getMethod(), requestURI, tookMs);
			log.info("================   Request End   ================");
		}
	}

}
