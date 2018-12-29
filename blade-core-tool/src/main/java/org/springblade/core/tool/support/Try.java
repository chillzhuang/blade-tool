package org.springblade.core.tool.support;

import org.springblade.core.tool.utils.Exceptions;

import java.util.Objects;
import java.util.function.Function;

/**
 * 当 Lambda 遇上受检异常
 * https://segmentfault.com/a/1190000007832130
 *
 * @author Chill
 */
public class Try {

	public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
		Objects.requireNonNull(mapper);
		return t -> {
			try {
				return mapper.apply(t);
			} catch (Exception e) {
				throw Exceptions.unchecked(e);
			}
		};
	}

	@FunctionalInterface
	public interface UncheckedFunction<T, R> {
		/**
		 * 调用
		 * @param t t
		 * @return R
		 * @throws Exception Exception
		 */
		R apply(T t) throws Exception;
	}
}
