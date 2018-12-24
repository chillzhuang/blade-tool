/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE;
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
package org.springblade.core.tool.support;

import java.util.*;


public class CaseInsensitiveHashMap<K,V> extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 9178606903603606031L;

    private final Map<String, String> lowerCaseMap = new HashMap<String, String>();

    @Override
    public boolean containsKey(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
        return super.containsKey(realKey);
    }

    @Override
    public Object get(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
        return super.get(realKey);
    }

    @Override
    public Set keySet() {
        return lowerCaseMap.keySet();
    }

    @Override
    public Object put(String key, Object value) {
        Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
        Object oldValue = super.remove(oldKey);
        super.put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            this.put(key, value);
        }
    }

    @Override
    public Object remove(Object key) {
        Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
        return super.remove(realKey);
    }


}
