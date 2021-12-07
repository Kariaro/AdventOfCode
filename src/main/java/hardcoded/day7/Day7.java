package hardcoded.day7;

import java.util.*;
import java.util.stream.IntStream;

import hardcoded.util.Utils;

public class Day7 {
	public static void main(String[] args) throws Exception {
		int[] array = Arrays.asList(Utils.readAll("day7/input").trim().split(",")).stream().mapToInt(Integer::parseInt).toArray();
		int max = IntStream.of(array).max().getAsInt();
		
		System.out.println("Day 7\n");
		System.out.printf("PartOne: %d\n", partOne(array, max));
		System.out.printf("PartTwo: %d\n", partTwo(array, max));
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
