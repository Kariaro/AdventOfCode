package me.hardcoded.util;

import java.util.*;

public interface Dijkstra {
	class Node {
		private final Map<Node, Long> adjacent = new LinkedHashMap<>();
		private long distance = Long.MAX_VALUE;
		private long value;
		
		public Node(long value) {
			this.value = value;
		}
		
		public Node() {
			
		}
		
		public void connectNodes(Node node) {
			this.adjacent.put(node, node.value);
			node.adjacent.put(this, this.value);
		}
		
		public void connectNode(Node node, long distance) {
			this.adjacent.put(node, distance);
		}
		
		public long getDistance() {
			return distance;
		}
		
		public void reset() {
			distance = Long.MAX_VALUE;
		}
	}
	
	static long getShortestDistance(Node start, Node end) {
		start.distance = 0;
		
		Set<Node> settled = new HashSet<>();
		Set<Node> unsettled = new HashSet<>();
		unsettled.add(start);

		while (unsettled.size() != 0) {
			Node current = getLowestDistance(unsettled);
			long currentDistance = current.distance;
			
			unsettled.remove(current);
			for (var entry : current.adjacent.entrySet()) {
				Node adjacent = entry.getKey();
				if (!settled.contains(adjacent)) {
					long nextDistance = currentDistance + entry.getValue();
					if (nextDistance < adjacent.distance) {
						adjacent.distance = nextDistance;
					}
					unsettled.add(adjacent);
				}
			}
			settled.add(current);
		}
		
		return end.getDistance();
	}
	
	private static Node getLowestDistance(Set<Node> unsettled) {
		Node lowestNode = null;
		long lowestDistance = Long.MAX_VALUE;
		for (Node node : unsettled) {
			long nodeDistance = node.distance;
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestNode = node;
			}
		}
		return lowestNode;
	}
}
