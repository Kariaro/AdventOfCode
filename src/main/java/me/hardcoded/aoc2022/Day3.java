package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Day3 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day3().run();
	}
	
	public Day3() {
		super(2022, 3);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	private int getPriority(char c) {
		if (c >= 'A' && c <= 'Z') {
			return 27 + (c - 'A');
		}
		
		return 1 + (c - 'a');
	}
	
	// Solve: 10 min
	public int partOne(List<String> lines) throws Exception {
		// item type is the first char of the line
		
		int result = 0;
		for (String line : lines) {
			// Calculate compartment
			String a = line.substring(0, line.length() / 2);
			String b = line.substring(line.length() / 2);
			
			System.out.println("---------");
			// System.out.println(line);
			// System.out.println(a);
			// System.out.println(" ".repeat(line.length() / 2) + b);
			
			// Find all chars that appear in both
			Set<Character> found = new HashSet<>();
			for (int i = 0; i < b.length(); i++) {
				if (a.indexOf(b.charAt(i)) != -1) {
					found.add(b.charAt(i));
				}
			}
			
			System.out.println(found);
			for (char c : found) {
				result += getPriority(c);
			}
		}
		
		return result;
	}
	
	// Solve: 4 min
	public int partTwo(List<String> lines) throws Exception {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		int result = 0;
		Iterator<String> iter = lines.iterator();
		
		while (iter.hasNext()) {
			String a = iter.next();
			String b = iter.next();
			String c = iter.next();
			
			// System.out.println("------------");
			// System.out.println(a);
			// System.out.println(b);
			// System.out.println(c);
			
			Set<Character> found = new HashSet<>();
			for (int i = 0; i < chars.length(); i++) {
				char d = chars.charAt(i);
				
				if (a.indexOf(d) != -1 && b.indexOf(d) != -1 && c.indexOf(d) != -1) {
					found.add(d);
				}
			}
			
			for (char d : found) {
				result += getPriority(d);
			}
		}
		
		return result;
	}
}
