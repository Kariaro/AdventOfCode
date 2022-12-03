package me.hardcoded.aoc2021;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day3 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day3().run();
	}
	
	public Day3() {
		super(2021, 3);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		int bitsInLine = lines.get(0).length();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines, bitsInLine));
		Utils.printf("PartTwo: %s\n", partTwo(lines, bitsInLine));
	}
	
	public int partOne(List<String> lines, int bitsInLine) throws Exception {
		int[] array = lines.stream().mapToInt(str -> Integer.parseInt(str, 2)).toArray();
		
		int common = 0;
		int epsilon = 0;
		for (int i = 0; i < bitsInLine; i++) {
			final int idx = i;
			int count = IntStream.of(array).map(v -> ((v >>> idx) & 1) * 2 - 1).sum();
			common  |= (count >= 0 ? 1:0) << i;
			epsilon |= (count >= 0 ? 0:1) << i;
		}
		
		return common * epsilon;
	}
	
	public int partTwo(List<String> lines, int bitsInLine) throws Exception {
		List<Integer> list = lines.stream().map(str -> Integer.parseInt(str, 2)).toList();
		List<Integer> oxy_list = new ArrayList<>(list);
		List<Integer> co2_list = new ArrayList<>(list);
		
		for (int i = bitsInLine - 1; i >= 0; i--) {
			final int idx = i;
			
			if (oxy_list.size() > 1) {
				int oxy_digit = oxy_list.stream().mapToInt(v -> ((v >>> idx) & 1) * 2 - 1).sum() >= 0 ? 1:0;
				oxy_list.removeIf(v -> ((v >> idx) & 1) == oxy_digit);
			}
			
			if (co2_list.size() > 1) {
				int co2_digit = co2_list.stream().mapToInt(v -> ((v >>> idx) & 1) * 2 - 1).sum() >= 0 ? 0:1;
				co2_list.removeIf(v -> ((v >> idx) & 1) == co2_digit);
			}
		}
		
		int oxy_value = oxy_list.get(0);
		int co2_value = co2_list.get(0);
		int life_support_rating = oxy_value * co2_value;
		
		return life_support_rating;
	}
}
