package me.hardcoded.aoc2021;

import java.util.stream.IntStream;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day7 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day7().run();
	}
	
	public Day7() {
		super(2021, 7);
	}
	
	public void run() throws Exception {
		int[] array = Utils.readIntArray(this, ",");
		int max = IntStream.of(array).max().getAsInt();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(array, max));
		Utils.printf("PartTwo: %s\n", partTwo(array, max));
	}
	
	public int partOne(int[] array, int max) throws Exception {
		int best_total = Integer.MAX_VALUE;
		for (int i = 0; i <= max; i++) {
			int total = 0;
			for (int j : array) {
				total += Math.abs(j - i);
			}

			if (total > best_total) break;
			best_total = total;
		}
		
		return best_total;
	}
	
	public int partTwo(int[] array, int max) throws Exception {
		int best_total = Integer.MAX_VALUE;
		for (int i = 0; i <= max; i++) {
			int total = 0;
			for (int j : array) {
				int n = Math.abs(j - i);
				total += (n * n + n) / 2;
			}
			
			if (total > best_total) break;
			best_total = total;
		}
		
		return best_total;
	}
}
