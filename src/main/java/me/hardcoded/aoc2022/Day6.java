package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day6 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day6().run();
	}
	
	public Day6() {
		super(2022, 6);
	}
	
	public void run() throws Exception {
		String line = Utils.readAll(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(line));
		Utils.printf("PartTwo: %s\n", partTwo(line));
	}
	
	public int findFirstUniqueGroup(String input, int length) {
		for (int i = 0; i < input.length() - length; i++) {
			boolean isUnique = true;
			Set<Character> unique = new HashSet<>();
			for (int j = 0; j < length; j++) {
				char c = input.charAt(i + j);
				if (!unique.add(c)) {
					isUnique = false;
					break;
				}
			}
			
			if (isUnique) {
				return i + length;
			}
		}
		
		return -1;
	}
	
	// Solve: 5 min
	public int partOne(String line) throws Exception {
		return findFirstUniqueGroup(line, 4);
	}
	
	// Solve: 0 min
	public int partTwo(String line) throws Exception {
		return findFirstUniqueGroup(line, 14);
	}
}
