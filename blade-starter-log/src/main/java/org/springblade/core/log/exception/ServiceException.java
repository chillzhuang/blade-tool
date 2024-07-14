/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.log.exception;

import lombok.Getter;
import org.springblade.core.tool.api.IResultCode;
import org.springblade.core.tool.api.ResultCode;


/**
 * 业务异常
 *
 * @author Chill
 */
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 2359767895161832954L;

	@Getter
	private final IResultCode resultCode;

	public ServiceException(String message) {
		super(message);
		this.resultCode = ResultCode.FAILURE;
	}

	public ServiceException(IResultCode resultCode) {
		super(resultCode.getMessage());
		this.resultCode = resultCode;
	}

	public ServiceException(IResultCode resultCode, Throwable cause) {
		super(cause);
		this.resultCode = resultCode;
	}

	/**
	 * 提高性能
	 *
	 * @return Throwable
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

	public Throwable doFillInStackTrace() {
		return super.fillInStackTrace();
	}

}
