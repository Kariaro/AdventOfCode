package hardcoded.day2;

import java.util.List;

import hardcoded.util.Utils;

public class Day2 {
	public static void main(String[] args) throws Exception {
		List<String> list = Utils.readAllLines("day2/input");
		
		System.out.println("Day 2\n");
		System.out.printf("PartOne: %d\n", partOne(list));
		System.out.printf("PartTwo: %d\n", partTwo(list));
	}
	
	public static int partOne(List<String> list) throws Exception {
		int horizontal_position = 0;
		int depth = 0;
		
		for(String line : list) {
			int value = 0;
			if(line.startsWith("forward ")) {
				value = Integer.parseInt(line.substring(8));
				horizontal_position += value;
			} else if(line.startsWith("down ")) {
				value = Integer.parseInt(line.substring(5));
				depth += value;
			} else if(line.startsWith("up ")) {
				value = Integer.parseInt(line.substring(3));
				depth -= value;
			}
		}
		
		int result = horizontal_position * depth;
		
		return result;
	}
	
	public static int partTwo(List<String> list) throws Exception {
		int horizontal_position = 0;
		int aim = 0;
		int depth = 0;
		
		for(String line : list) {
			int value = 0;
			if(line.startsWith("forward ")) {
				value = Integer.parseInt(line.substring(8));
				horizontal_position += value;
				depth += aim * value;
			} else if(line.startsWith("down ")) {
				value = Integer.parseInt(line.substring(5));
				aim += value;
			} else if(line.startsWith("up ")) {
				value = Integer.parseInt(line.substring(3));
				aim -= value;
			}
		}
		
		int result = horizontal_position * depth;
		
		return result;
	}
}
