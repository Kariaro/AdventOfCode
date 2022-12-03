package me.hardcoded.aoc2021;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.hardcoded.util.Utils;

public class Day14 {
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day14");
		
		String line = lines.get(0);
		Map<String, Character> rules = lines.stream().skip(2).map(s -> s.split(" -> ")).collect(Collectors.toMap(v -> v[0], v -> v[1].charAt(0)));
		
		Utils.printf("Day 14\n");
		Utils.printf("PartOne: %d\n", partOne(line, rules));
		Utils.printf("PartTwo: %d\n", partTwo(line, rules));
	}
	
	private static long processCount(String line, Map<String, Character> rules, int count) {
		Map<String, Long> patterns = new HashMap<>();
		Map<String, Long> nextPatterns = new HashMap<>();
		long[] chars = new long[26];
		
		for(int i = 0; i < line.length(); i++) {
			if(i + 1 < line.length()) {
				String str = line.substring(i, i + 2);
				patterns.put(str, patterns.getOrDefault(str, 0L) + 1L);
			}
			chars[line.charAt(i) - 'A']++;
		}
		
		for(int i = 0; i < count; i++) {
			nextPatterns.clear();
			
			for(Map.Entry<String, Long> entry : patterns.entrySet()) {
				String str = entry.getKey();
				
				char add = rules.get(str);
				String aa = str.charAt(0) + "" + add;
				String bb = add + "" + str.charAt(1);

				long amount = entry.getValue();
				nextPatterns.put(aa, nextPatterns.getOrDefault(aa, 0L) + amount);
				nextPatterns.put(bb, nextPatterns.getOrDefault(bb, 0L) + amount);
				chars[add - 'A'] += amount;
			}
			
			patterns.clear();
			patterns.putAll(nextPatterns);
		}
		
		long max = Long.MIN_VALUE;
		long min = Long.MAX_VALUE;
		for(int i = 0; i < 26; i++) {
			max = Math.max(max, chars[i]);
			if(chars[i] != 0) min = Math.min(min, chars[i]);
		}
		
		return max - min;
	}
	
	// Solve: 9 min
	public static long partOne(String line, Map<String, Character> rules) throws Exception {
		return processCount(line, rules, 10);
	}
	
	// Solve 25 min
	public static long partTwo(String line, Map<String, Character> rules) throws Exception {
		return processCount(line, rules, 40);
	}
}
