package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day10 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day10().run();
	}
	
	public Day10() {
		super(2022, 10);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("addx")) {
				lines.add(i, "noop");
				i++;
			}
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	// Solve: 9 min
	public int partOne(List<String> lines) throws Exception {
		int cycles = 0;
		int result = 0;
		int value = 1;
		
		for (String line : lines) {
			if ((++cycles % 40) == 20) {
				result += cycles * value;
			}
			
			if (line.startsWith("addx")) {
				value += Integer.parseInt(line.substring(5));
			}
		}
		
		return result;
	}
	
	// Solve: 13 min
	public String partTwo(List<String> lines) throws Exception {
		StringBuilder result = new StringBuilder();
		char[] row = ".".repeat(80).toCharArray();
		int cycles = 0;
		int value = 1;
		
		for (String line : lines) {
			if (line.startsWith("addx")) {
				value += Integer.parseInt(line.substring(5));
			}
			
			int crt = (++cycles) % 40;
			if (Math.abs(crt - value) <= 1) {
				row[crt * 2] = '#';
				row[crt * 2 + 1] = '#';
			}
			
			if (crt == 0) {
				result.append('\n').append(row);
				Arrays.fill(row, '.');
			}
		}
		
		return result.toString();
	}
}
