package me.hardcoded.aoc2021;

import java.util.ArrayList;
import java.util.List;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Dijkstra;
import me.hardcoded.util.Utils;

public class Day15 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day15().run();
	}
	
	public Day15() {
		super(2021, 15);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		List<long[]> board = lines.stream().map(s -> s.chars().mapToLong(i -> i - '0').toArray()).toList();
		int width = board.get(0).length;
		int height = board.size();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(board, width, height));
		Utils.printf("PartTwo: %s\n", partTwo(board, width, height));
	}
	
	private static long dijkstra(List<long[]> board, int width, int height) {
		Dijkstra.Node[][] nodes = new Dijkstra.Node[height][width];
		
		for (int i = 0; i < height; i++) {
			long[] values = board.get(i);
			for (int j = 0; j < width; j++) {
				nodes[i][j] = new Dijkstra.Node(values[j]);
			}
		}
		
		for (int i = 0; i < height - 1; i++) {
			for (int j = 0; j < width - 1; j++) {
				Dijkstra.Node n00 = nodes[i    ][j    ];
				Dijkstra.Node n10 = nodes[i + 1][j    ];
				Dijkstra.Node n01 = nodes[i    ][j + 1];
				Dijkstra.Node n11 = nodes[i + 1][j + 1];
				
				// Horizontal
				n00.connectNodes(n10);
				n01.connectNodes(n11);
				
				// Vertical
				n00.connectNodes(n01);
				n10.connectNodes(n11);
			}
		}
		
		return Dijkstra.getShortestDistance(nodes[0][0], nodes[height - 1][width - 1]);
	}
	
	// Solve: 86 min
	public long partOne(List<long[]> board, int width, int height) throws Exception {
		return dijkstra(board, width, height);
	}
	
	// Solve: 7 min
	public long partTwo(List<long[]> board, int width, int height) throws Exception {
		List<long[]> largeBoard = new ArrayList<>();
		
		for (int yy = 0; yy < 5; yy++) {
			for (int y = 0; y < height; y++) {
				long[] newCol = new long[width * 5];
				long[] boardCol = board.get(y);
				
				for (int xx = 0; xx < 5; xx++) {
					for (int x = 0; x < width; x++) {
						newCol[xx * width + x] = ((boardCol[x] - 1 + xx + yy) % 9) + 1;
					}
				}
				
				largeBoard.add(newCol);
			}
		}

		return dijkstra(largeBoard, width * 5, height * 5);
	}
}
