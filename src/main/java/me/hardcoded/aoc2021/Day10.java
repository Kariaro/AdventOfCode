package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day10 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day10().run();
	}
	
	public Day10() {
		super(2021, 10);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	public static final Map<Character, Character> CLOSES = Map.of('(', ')', '[', ']', '{', '}', '<', '>');
	public static final Map<Character, Long> FIRST_SCORE = Map.of(')', 3L, ']', 57L, '}', 1197L, '>', 25137L);
	public static final Map<Character, Long> CLOSE_SCORE = Map.of('(', 1L, '[', 2L, '{', 3L, '<', 4L);
	public static final Set<Character> CLOSE_CHAR = Set.of(')', ']', '}', '>');
	
	public long getFirstScore(String str) {
		LinkedList<Character> stack = new LinkedList<>();
		
		for (int i = 0, len = str.length(); i < len; i++) {
			char next = str.charAt(i);
			
			if (CLOSE_CHAR.contains(next)) {
				char prev = stack.pollLast();
				char expected = CLOSES.get(prev);
				
				if (next != expected) {
					return FIRST_SCORE.get(next);
				}
			} else {
				stack.add(next);
			}
		}
		
		return 0;
	}
	
	public long getCloseScore(String str) {
		LinkedList<Character> stack = new LinkedList<>();
		
		for (int i = 0, len = str.length(); i < len; i++) {
			char next = str.charAt(i);
			
			if (CLOSE_CHAR.contains(next)) {
				char prev = stack.pollLast();
				char expected = CLOSES.get(prev);
				
				if (next != expected) {
					return -1;
				}
			} else {
				stack.add(next);
			}
		}
		
		long score = 0;
		for (int i = stack.size() - 1; i >= 0; i--) {
			score = score * 5 + CLOSE_SCORE.get(stack.get(i));
		}
		
		return score;
	}
	
	public long partOne(List<String> lines) throws Exception {
		long score = 0;
		for (String line : lines) {
			score += getFirstScore(line);
		}
		return score;
	}
	
	public long partTwo(List<String> lines) throws Exception {
		List<Long> scores = new ArrayList<>();
		for (String line : lines) {
			long value = getCloseScore(line);
			if (value > 0) {
				scores.add(value);
			}
		}
		scores.sort(null);
		return scores.get(scores.size() / 2);
	}
}
