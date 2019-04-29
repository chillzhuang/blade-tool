package org.springblade.core.log.feign.fallback;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.log.feign.ILogClient;
import org.springblade.core.log.model.LogApi;
import org.springblade.core.log.model.LogError;
import org.springblade.core.log.model.LogUsual;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

/**
 * @Auther: jiang
 * @Date: 2019/04/26 23:04
 */
@Component
@Slf4j
public class ILogClientHystric implements ILogClient {

	@Override
	public R<Boolean> saveUsualLog(LogUsual log) {
		//注：这里如果使用log.toString()来打印日志的话，只能打印出该对象自身的属性值，无法输出父类的属性值
		this.log.error("usual log send fail:{}", JSON.toJSONString(log));
		return R.fail("usual log send fail");
	}

	@Override
	public R<Boolean> saveApiLog(LogApi log) {
		this.log.error("api log send fail:{}", JSON.toJSONString(log));
		return R.fail("api log send fail");
	}

	@Override
	public R<Boolean> saveErrorLog(LogError log) {
		this.log.error("error log send fail:{}", JSON.toJSONString(log));
		return R.fail("error log send fail");
	}
}
