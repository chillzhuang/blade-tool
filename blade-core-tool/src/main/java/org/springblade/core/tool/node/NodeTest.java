package org.springblade.core.tool.node;

import org.springblade.core.tool.jackson.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blade.
 *
 * @author Chill
 */
public class NodeTest {

	public static void main(String[] args) {
		List<ForestNode> list = new ArrayList<>();
		list.add(new ForestNode(1, 0, "1"));
		list.add(new ForestNode(2, 0, "2"));
		list.add(new ForestNode(3, 1, "3"));
		list.add(new ForestNode(4, 2, "4"));
		list.add(new ForestNode(5, 3, "5"));
		list.add(new ForestNode(6, 4, "6"));
		list.add(new ForestNode(7, 3, "7"));
		list.add(new ForestNode(8, 5, "8"));
		list.add(new ForestNode(9, 6, "9"));
		list.add(new ForestNode(10, 9, "10"));
		List<ForestNode> tns = ForestNodeMerger.merge(list);
		tns.forEach(node ->
			System.out.println(JsonUtil.toJson(node))
		);
	}

}
