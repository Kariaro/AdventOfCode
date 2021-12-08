package hardcoded.aoc2021;

import java.util.stream.LongStream;

import hardcoded.util.Utils;

public class Day6 {
	public static void main(String[] args) throws Exception {
//		int[] days = Arrays.asList(Utils.readAll(2021, "day6").trim().split(",")).stream().mapToInt(Integer::parseInt).toArray();
		int[] days = Utils.readIntArray(2021, "day6", ",");
		
		long[] scores = new long[9];
		for(int i : days) {
			scores[i]++;
		}
		
		Utils.printf("Day 6\n");
		Utils.printf("PartOne: %d\n", partOne(scores));
		Utils.printf("PartTwo: %d\n", partTwo(scores));
	}
	
	public static long calculateLanternFish(long[] array, int days) {
		for(int i = 0; i < days; i++) {
			long amount = array[0];
			
			// Shift all values by one
			System.arraycopy(array, 1, array, 0, array.length - 1);
			
			array[6] += amount;
			array[8] = amount;
		}
		
		return LongStream.of(array).sum();
	}
	
	public static long partOne(long[] array) throws Exception {
		return calculateLanternFish(array.clone(), 80);
	}
	
	public static long partTwo(long[] array) throws Exception {
		return calculateLanternFish(array.clone(), 256);
	}
}
