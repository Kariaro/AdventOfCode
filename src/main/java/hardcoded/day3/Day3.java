package hardcoded.day3;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import hardcoded.util.Utils;

public class Day3 {
	public static void main(String[] args) throws Exception {
		System.out.println("Day 3\n");
		System.out.printf("PartOne: %d\n", partOne());
		System.out.printf("PartTwo: %d\n", partTwo());
	}
	
	public static int partOne() throws Exception {
		List<String> lines = Files.readAllLines(Utils.of("day3/input"));
		int bitsInLine = lines.get(0).length();
		int[] array = lines.stream().mapToInt(str -> Integer.parseInt(str, 2)).toArray();
		
		int common = 0;
		for(int i = 0; i < bitsInLine; i++) {
			int ones = 0;
			int zeros = 0;
			for(int j = 0; j < array.length; j++) {
				int value = (array[j] >>> i) & 1;
				
				if(value == 0) {
					zeros ++;
				} else {
					ones ++;
				}
			}
			
			int digit = ones >= zeros ? 1:0;
			
			common |= (digit << i);
		}
		
		int epsilon = (~common) & ((1 << bitsInLine) - 1);
		
		int result = common * epsilon;
		
		return result;
	}
	
	public static int partTwo() throws Exception {
		List<String> lines = Files.readAllLines(Utils.of("day3/input"));
		int bitsInLine = lines.get(0).length();
		List<Integer> list = lines.stream().map(str -> Integer.parseInt(str, 2)).toList();
		
		List<Integer> oxy_list = new ArrayList<>(list);
		List<Integer> co2_list = new ArrayList<>(list);
		
		for(int i = bitsInLine - 1; i >= 0; i--) {
			final int idx = i;
			
			if(oxy_list.size() != 1) {
				int oxy_digit = oxy_list.stream().mapToInt(value -> ((value >>> idx) & 1) * 2 - 1).sum() >= 0 ? 1:0;
				oxy_list.removeIf(value -> ((value >> idx) & 1) == oxy_digit);
			}
			
			if(co2_list.size() != 1) {
				int co2_digit = co2_list.stream().mapToInt(value -> ((value >>> idx) & 1) * 2 - 1).sum() >= 0 ? 0:1;
				co2_list.removeIf(value -> ((value >> idx) & 1) == co2_digit);
			}
		}
		
		int oxy_value = oxy_list.get(0);
		int co2_value = co2_list.get(0);
		int life_support_rating = oxy_value * co2_value;
		
		return life_support_rating;
	}
}
