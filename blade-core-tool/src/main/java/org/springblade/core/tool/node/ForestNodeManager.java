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
package org.springblade.core.tool.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 森林管理类
 *
 * @author Chill
 */
public class ForestNodeManager<T extends INode> {

	/**
	 * 森林的所有节点
	 */
	private List<T> list;

	/**
	 * 森林的父节点ID
	 */
	private List<Integer> parentIds = new ArrayList<>();

	public ForestNodeManager(List<T> items) {
		list = items;
	}

	/**
	 * 根据节点ID获取一个节点
	 *
	 * @param id 节点ID
	 * @return 对应的节点对象
	 */
	public INode getTreeNodeAT(int id) {
		for (INode forestNode : list) {
			if (forestNode.getId() == id) {
				return forestNode;
			}
		}
		return null;
	}

	/**
	 * 增加父节点ID
	 *
	 * @param parentId 父节点ID
	 */
	public void addParentId(Integer parentId) {
		parentIds.add(parentId);
	}

	/**
	 * 获取树的根节点(一个森林对应多颗树)
	 *
	 * @return 树的根节点集合
	 */
	public List<T> getRoot() {
		List<T> roots = new ArrayList<>();
		for (T forestNode : list) {
			if (forestNode.getParentId() == 0 || parentIds.contains(forestNode.getId())) {
				roots.add(forestNode);
			}
		}
		return roots;
	}

}
