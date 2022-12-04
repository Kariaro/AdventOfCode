package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day4 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day4().run();
	}
	
	public Day4() {
		super(2022, 4);
	}
	
	record Range(long a, long b) {
		boolean contains(Range other) {
			return other.a >= a && other.b <= b;
		}
		
		boolean overlap(Range other) {
			return a <= other.b && b >= other.a;
		}
	}
	
	public void run() throws Exception {
		List<Range[]> lines = Utils.readAllLines(this).stream().map(line -> {
			int[] array = Arrays.stream(line.split("[,-]")).mapToInt(Integer::parseInt).toArray();
			return new Range[] { new Range(array[0], array[1]), new Range(array[2], array[3]) };
		}).toList();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	// Solve: 9 min
	public int partOne(List<Range[]> lines) throws Exception {
		int result = 0;
		for (Range[] array : lines) {
			Range a = array[0];
			Range b = array[1];
			
			if (a.contains(b) || b.contains(a)) {
				result ++;
			}
		}
		
		return result;
	}
	
	// Solve: 1 min
	public int partTwo(List<Range[]> lines) throws Exception {
		int result = 0;
		for (Range[] array : lines) {
			Range a = array[0];
			Range b = array[1];
			
			if (a.overlap(b) || b.overlap(a)) {
				result ++;
			}
		}
		
		return result;
	}
}
