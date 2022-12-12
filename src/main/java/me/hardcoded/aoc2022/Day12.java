package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Dijkstra;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day12 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day12().run();
	}
	
	public Day12() {
		super(2022, 12);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	private int getPoints(List<String> lines, int x, int y) {
		return getPoints(lines.get(y).charAt(x));
	}
	
	private int getPoints(char c) {
		return switch (c) {
			case 'S' -> 0;
			case 'E' -> 'z' - 'a';
			default -> c - 'a';
		};
	}
	
	// Solve: 17 min
	public long partOne(List<String> lines) throws Exception {
		int width = lines.get(0).length();
		int height = lines.size();
		
		Dijkstra.Node[] nodes = new Dijkstra.Node[width * height];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Dijkstra.Node(1);
		}
		
		int[][] diffs = {
			{  0, -1 }, // up
			{  0,  1 }, // down
			{ -1,  0 }, // left
			{  1,  0 }, // right
		};
		
		Dijkstra.Node start = null;
		Dijkstra.Node end = null;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				char c = lines.get(y).charAt(x);
				
				if (c == 'S') start = nodes[x + y * width];
				if (c == 'E') end = nodes[x + y * width];
				
				Dijkstra.Node currNode = nodes[x + y * width];
				int currValue = getPoints(lines, x, y);
				for (int[] pair : diffs) {
					int dx = x + pair[0];
					int dy = y + pair[1];
					
					if (dx < 0 || dy < 0 || dx >= width || dy >= height) {
						continue;
					}
					
					Dijkstra.Node adjNode = nodes[dx + dy * width];
					int adjValue = getPoints(lines, dx, dy);
					if (adjValue <= currValue + 1) {
						currNode.connectNode(adjNode, 1);
					}
				}
			}
		}
		
		return Dijkstra.getShortestDistance(start, end);
	}
	
	// Solve: 4 min
	public long partTwo(List<String> lines) throws Exception {
		int width = lines.get(0).length();
		int height = lines.size();
		
		Dijkstra.Node[] nodes = new Dijkstra.Node[width * height];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Dijkstra.Node(1);
		}
		
		int[][] diffs = {
			{  0, -1 }, // up
			{  0,  1 }, // down
			{ -1,  0 }, // left
			{  1,  0 }, // right
		};
		
		Dijkstra.Node end = null;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				char c = lines.get(y).charAt(x);
				if (c == 'E') end = nodes[x + y * width];
				
				Dijkstra.Node currNode = nodes[x + y * width];
				int currValue = getPoints(lines, x, y);
				for (int[] pair : diffs) {
					int dx = x + pair[0];
					int dy = y + pair[1];
					if (dx < 0 || dy < 0 || dx >= width || dy >= height) {
						continue;
					}
					
					Dijkstra.Node adjNode = nodes[dx + dy * width];
					int adjValue = getPoints(lines, dx, dy);
					if (adjValue <= currValue + 1) {
						currNode.connectNode(adjNode, 1);
					}
				}
			}
		}
		
		long result = Long.MAX_VALUE;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int points = getPoints(lines, x, y);
				if (points != 0) {
					continue;
				}
				
				for (Dijkstra.Node node : nodes) {
					node.reset();
				}
				
				Dijkstra.Node currNode = nodes[x + y * width];
				long distance = Dijkstra.getShortestDistance(currNode, end);
				if (distance <= result) {
					result = distance;
				}
			}
		}
		
		return result;
	}
}
