package me.hardcoded.aoc2021;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day1 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day1().run();
	}
	
	public Day1() {
		super(2021, 1);
	}
	
	public void run() throws Exception {
		int[] array = Utils.readAllLines(this).stream().mapToInt(Integer::parseInt).toArray();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(array));
		Utils.printf("PartTwo: %s\n", partTwo(array));
	}
	
	public int partOne(int[] array) throws Exception {
		int count = 0;
		
		int last = array[0];
		for (int i = 1; i < array.length; i++) {
			int value = array[i];
			if (value > last) {
				count ++;
			}
			
			last = value;
		}
		
		return count;
	}
	
	public int partTwo(int[] array) throws Exception {
		int count = 0;
		
		int last = Integer.MAX_VALUE;
		for (int i = 0; i < array.length - 2; i++) {
			int value = array[i] + array[i + 1] + array[i + 2];
			if (value > last) {
				count ++;
			}
			
			last = value;
		}
		
		return count;
	}
}
