/**
 * Copyright (c) 2018-2099, DreamLu 卢春梦 (qq596392912@gmail.com).
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

package org.springblade.core.redis.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * ProtoStuff 序列化
 *
 * @author L.cm
 */
public class ProtoStuffSerializer implements RedisSerializer<Object> {
	private final Schema<BytesWrapper> schema;

	public ProtoStuffSerializer() {
		this.schema = RuntimeSchema.getSchema(BytesWrapper.class);
	}

	@Override
	public byte[] serialize(Object object) throws SerializationException {
		if (object == null) {
			return null;
		}
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			return ProtobufIOUtil.toByteArray(new BytesWrapper<>(object), schema, buffer);
		} finally {
			buffer.clear();
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		if (ObjectUtil.isEmpty(bytes)) {
			return null;
		}
		BytesWrapper<Object> wrapper = new BytesWrapper<>();
		ProtobufIOUtil.mergeFrom(bytes, wrapper, schema);
		return wrapper.getValue();
	}
}
