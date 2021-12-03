package hardcoded.day1;

import java.nio.file.Files;

import hardcoded.util.Utils;

public class Day1 {
	public static void main(String[] args) throws Exception {
		System.out.println("Day 1\n");
		partOne();
		partTwo();
	}
	
	public static void partOne() throws Exception {
		int[] array = Files.readAllLines(Utils.of("day1/input")).stream().mapToInt(str -> Integer.parseInt(str)).toArray();
		
		int count = 0;
		
		int last = array[0];
		for(int i = 1; i < array.length; i++) {
			int value = array[i];
			if(value > last) {
				count ++;
			}
			
			last = value;
		}
		
		System.out.printf("PartOne: %d\n", count);
	}
	
	public static void partTwo() throws Exception {
		int[] array = Files.readAllLines(Utils.of("day1/input")).stream().mapToInt(str -> Integer.parseInt(str)).toArray();
		
		int count = 0;
		
		int last = Integer.MAX_VALUE;
		for(int i = 0; i < array.length - 2; i++) {
			int value = array[i] + array[i + 1] + array[i + 2];
			if(value > last) {
				count ++;
			}
			
			last = value;
		}
		
		System.out.printf("PartTwo: %d\n", count);
	}
}
