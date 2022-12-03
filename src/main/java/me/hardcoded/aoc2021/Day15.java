package me.hardcoded.aoc2021;

import java.util.ArrayList;
import java.util.List;

import me.hardcoded.util.Djikstra;
import me.hardcoded.util.Utils;

public class Day15 {
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day15");
		
		List<long[]> board = lines.stream().map(s -> s.chars().mapToLong(i -> i - '0').toArray()).toList();
		int width = board.get(0).length;
		int height = board.size();
		
		Utils.printf("Day 15\n");
		Utils.printf("PartOne: %d\n", partOne(board, width, height));
		Utils.printf("PartTwo: %d\n", partTwo(board, width, height));
	}
	
	private static long djikstra(List<long[]> board, int width, int height) {
		Djikstra.Node[][] nodes = new Djikstra.Node[height][width];
		
		for(int i = 0; i < height; i++) {
			long[] values = board.get(i);
			for(int j = 0; j < width; j++) {
				nodes[i][j] = new Djikstra.Node(values[j]);
			}
		}
		
		for(int i = 0; i < height - 1; i++) {
			for(int j = 0; j < width - 1; j++) {
				Djikstra.Node n00 = nodes[i    ][j    ];
				Djikstra.Node n10 = nodes[i + 1][j    ];
				Djikstra.Node n01 = nodes[i    ][j + 1];
				Djikstra.Node n11 = nodes[i + 1][j + 1];
				
				// Horizontal
				n00.connectNodes(n10);
				n01.connectNodes(n11);
				
				// Vertical
				n00.connectNodes(n01);
				n10.connectNodes(n11);
			}
		}
		
		return Djikstra.getShortestDistance(nodes[0][0], nodes[height - 1][width - 1]);
	}
	
	// Solve: 86 min
	public static long partOne(List<long[]> board, int width, int height) throws Exception {
		return djikstra(board, width, height);
	}
	
	// Solve: 7 min
	public static long partTwo(List<long[]> board, int width, int height) throws Exception {
		List<long[]> largeBoard = new ArrayList<>();
		
		for(int yy = 0; yy < 5; yy++) {
			for(int y = 0; y < height; y++) {
				long[] newCol = new long[width * 5];
				long[] boardCol = board.get(y);
				
				for(int xx = 0; xx < 5; xx++) {
					for(int x = 0; x < width; x++) {
						newCol[xx * width + x] = ((boardCol[x] - 1 + xx + yy) % 9) + 1;
					}
				}
				
				largeBoard.add(newCol);
			}
		}

		return djikstra(largeBoard, width * 5, height * 5);
	}
}
