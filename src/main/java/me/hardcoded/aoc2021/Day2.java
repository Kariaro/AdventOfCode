package me.hardcoded.aoc2021;

import java.util.List;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day2 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day2().run();
	}
	
	public Day2() {
		super(2021, 2);
	}
	
	public void run() throws Exception {
		List<String> list = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(list));
		Utils.printf("PartTwo: %s\n", partTwo(list));
	}
	
	public int partOne(List<String> list) throws Exception {
		int horizontal_position = 0;
		int depth = 0;
		
		for (String line : list) {
			int value = 0;
			if (line.startsWith("forward ")) {
				value = Integer.parseInt(line.substring(8));
				horizontal_position += value;
			} else if (line.startsWith("down ")) {
				value = Integer.parseInt(line.substring(5));
				depth += value;
			} else if (line.startsWith("up ")) {
				value = Integer.parseInt(line.substring(3));
				depth -= value;
			}
		}
		
		int result = horizontal_position * depth;
		return result;
	}
	
	public int partTwo(List<String> list) throws Exception {
		int horizontal_position = 0;
		int aim = 0;
		int depth = 0;
		
		for (String line : list) {
			int value = 0;
			if (line.startsWith("forward ")) {
				value = Integer.parseInt(line.substring(8));
				horizontal_position += value;
				depth += aim * value;
			} else if (line.startsWith("down ")) {
				value = Integer.parseInt(line.substring(5));
				aim += value;
			} else if (line.startsWith("up ")) {
				value = Integer.parseInt(line.substring(3));
				aim -= value;
			}
		}
		
		int result = horizontal_position * depth;
		return result;
	}
}
