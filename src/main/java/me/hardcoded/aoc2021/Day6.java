package me.hardcoded.aoc2021;

import java.util.stream.LongStream;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day6 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day6().run();
	}
	
	public Day6() {
		super(2021, 6);
	}
	
	public void run() throws Exception {
		int[] days = Utils.readIntArray(this, ",");
		
		long[] scores = new long[9];
		for (int i : days) {
			scores[i]++;
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(scores));
		Utils.printf("PartTwo: %s\n", partTwo(scores));
	}
	
	public long calculateLanternFish(long[] array, int days) {
		for (int i = 0; i < days; i++) {
			long amount = array[0];
			
			// Shift all values by one
			System.arraycopy(array, 1, array, 0, array.length - 1);
			
			array[6] += amount;
			array[8] = amount;
		}
		
		return LongStream.of(array).sum();
	}
	
	public long partOne(long[] array) throws Exception {
		return calculateLanternFish(array.clone(), 80);
	}
	
	public long partTwo(long[] array) throws Exception {
		return calculateLanternFish(array.clone(), 256);
	}
}
