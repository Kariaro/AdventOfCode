package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day1 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day1().run();
	}
	
	public Day1() {
		super(2022, 1);
	}
	
	public void run() throws Exception {
		int[][] array = Arrays.stream(Utils.readAll(this).trim().split("\r\n\r\n"))
			.map(data -> data.trim().lines()
				.mapToInt(Integer::parseInt).toArray())
			.toArray(int[][]::new);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(array));
		Utils.printf("PartTwo: %s\n", partTwo(array));
	}
	
	// Solve: 15 min
	public int partOne(int[][] array) throws Exception {
		int highest = 0;
		for (int[] ints : array) {
			highest = Math.max(highest, IntStream.of(ints).sum());
		}
		
		return highest;
	}
	
	// Solve: 1 min
	public int partTwo(int[][] array) throws Exception {
		List<Integer> values = new ArrayList<>();
		for (int[] ints : array) {
			values.add(IntStream.of(ints).sum());
		}
		values.sort(Comparator.reverseOrder());
		
		return values.get(0) + values.get(1) + values.get(2);
	}
}
