package hardcoded.day2;

import java.nio.file.Files;
import java.util.List;

import hardcoded.util.Utils;

public class Day2 {
	public static void main(String[] args) throws Exception {
		System.out.println("Day 2\n");
		partOne();
		partTwo();
	}
	
	public static void partOne() throws Exception {
		List<String> list = Files.readAllLines(Utils.of("day2/input"));
		
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
		
		System.out.printf("PartOne: %d\n", result);
	}
	
	public static void partTwo() throws Exception {
		List<String> list = Files.readAllLines(Utils.of("day2/input"));
		
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
		
		System.out.printf("PartTwo: %d\n", result);
	}
}
