package hardcoded.aoc2021;

import java.util.stream.IntStream;

import hardcoded.util.Utils;

public class Day7 {
	public static void main(String[] args) throws Exception {
		int[] array = Utils.readIntArray(2021, "day7", ",");
		int max = IntStream.of(array).max().getAsInt();
		
		Utils.printf("Day 7\n");
		Utils.printf("PartOne: %d\n", partOne(array, max));
		Utils.printf("PartTwo: %d\n", partTwo(array, max));
	}
	
	public static int partOne(int[] array, int max) throws Exception {
		int best_total = Integer.MAX_VALUE;
		for(int i = 0; i <= max; i++) {
			int total = 0;
			for(int j : array) {
				total += Math.abs(j - i);
			}

			if(total > best_total) break;
			best_total = total;
		}
		
		return best_total;
	}
	
	public static int partTwo(int[] array, int max) throws Exception {
		int best_total = Integer.MAX_VALUE;
		for(int i = 0; i <= max; i++) {
			int total = 0;
			for(int j : array) {
				int n = Math.abs(j - i);
				total += (n * n + n) / 2;
			}
			
			if(total > best_total) break;
			best_total = total;
		}
		
		return best_total;
	}
}
