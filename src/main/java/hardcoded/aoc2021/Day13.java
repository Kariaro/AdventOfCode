package hardcoded.aoc2021;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import hardcoded.util.Utils;

public class Day13 {
	public static class Fold {
		public boolean xAxis;
		public int value;
		
		public Fold(String str) {
			if(str.startsWith("x=")) {
				xAxis = true;
				value = Integer.parseInt(str.substring(2));
			} else {
				value = Integer.parseInt(str.substring(2));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day13");
		
		List<Point> dots = new ArrayList<>();
		List<Fold> folds = new ArrayList<>();
		
		int maxX = 0;
		int maxY = 0;
		
		for(String line : lines) {
			if(line.startsWith("fold along ")) {
				folds.add(new Fold(line.substring(11)));
			} else if(!line.isEmpty()) {
				String[] parts = line.split(",");
				Point point = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				dots.add(point);
				
				maxX = Math.max(maxX, point.x + 1);
				maxY = Math.max(maxY, point.y + 1);
			}
		}
		
		int[] board = new int[maxX * maxY];
		for(Point p : dots) {
			board[p.x + p.y * maxX]++;
		}
		
		Utils.printf("Day 13\n");
		Utils.printf("PartOne: %d\n", partOne(board.clone(), folds, maxX, maxY));
		Utils.printf("PartTwo: %d\n", partTwo(board.clone(), folds, maxX, maxY));
	}
	
	private static void foldPaper(int[] paper, Fold fold, int maxX, int maxY) {
		if(fold.xAxis) {
			for(int x = 0; x <= fold.value; x++) {
				int dx = 2 * fold.value - x;
				
				for(int y = 0; y < maxY; y++) {
					paper[x + y * maxX] += paper[dx + y * maxX];
					paper[dx + y * maxX] = 0;
				}
			}
		} else {
			for(int y = 0; y <= fold.value; y++) {
				int dy = 2 * fold.value - y;
				
				for(int x = 0; x < maxX; x++) {
					paper[x + y * maxX] += paper[x + dy * maxX];
					paper[x + dy * maxX] = 0;
				}
			}
		}
	}
	
	private static int countDots(int[] paper) {
		return IntStream.of(paper).map(i -> i > 0 ? 1:0).sum();
	}
	
	private static void printBoard(int[] paper, int maxX) {
		for(int y = 0; y < paper.length; y += maxX) {
			String test = ":";
			for(int x = 0; x < maxX; x++) {
				test += paper[x + y] > 0 ? "#.":"  ";
			}
			
			test = test.trim().substring(1);
			if(y > 10 && test.isEmpty()) {
				continue;
			}
			
			System.out.println(test);
		}
	}
	
	// Solve 20 min
	public static long partOne(int[] paper, List<Fold> folds, int maxX, int maxY) throws Exception {
		foldPaper(paper, folds.get(0), maxX, maxY);
		return countDots(paper);
	}
	
	// Solve 1 min
	public static long partTwo(int[] paper, List<Fold> folds, int maxX, int maxY) throws Exception {
		for(Fold fold : folds) {
			foldPaper(paper, fold, maxX, maxY);
		}
		printBoard(paper, maxX);
		return 0;
	}
}
