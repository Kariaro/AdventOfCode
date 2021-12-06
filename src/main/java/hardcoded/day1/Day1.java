package hardcoded.day1;

import hardcoded.util.Utils;

public class Day1 {
	public static void main(String[] args) throws Exception {
		int[] array = Utils.readAllLines("day1/input").stream().mapToInt(Integer::parseInt).toArray();
		
		System.out.println("Day 1\n");
		System.out.printf("PartOne: %d\n", partOne(array));
		System.out.printf("PartTwo: %d\n", partTwo(array));
	}
	
	public static int partOne(int[] array) throws Exception {
		int count = 0;
		
		int last = array[0];
		for(int i = 1; i < array.length; i++) {
			int value = array[i];
			if(value > last) {
				count ++;
			}
			
			last = value;
		}
		
		return count;
	}
	
	public static int partTwo(int[] array) throws Exception {
		int count = 0;
		
		int last = Integer.MAX_VALUE;
		for(int i = 0; i < array.length - 2; i++) {
			int value = array[i] + array[i + 1] + array[i + 2];
			if(value > last) {
				count ++;
			}
			
			last = value;
		}
		
		return count;
	}
}
