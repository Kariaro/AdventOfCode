package me.hardcoded.aoc2021;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.hardcoded.util.Utils;

public class Day8 {
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day8");
		
		Utils.printf("Day 8\n");
		Utils.printf("PartOne: %d\n", partOne(lines));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	public static Set<Character> toSet(String string) {
		return string.chars().mapToObj(i -> (Character)(char)i).collect(Collectors.toSet());
	}
	
	public static String getMatch(List<String> list, String digits) {
		Set<Character> digitSet = toSet(digits);
		return list.stream().filter(s -> toSet(s).containsAll(digitSet)).findFirst().orElse(null);
	}
	
	public static String getMatch(List<String> list, String a, String b) {
		Set<Character> digitSet = new HashSet<>(toSet(a));
		digitSet.removeAll(toSet(b));
		return list.stream().filter(s -> toSet(s).containsAll(digitSet)).findFirst().orElse(null);
	}
	
	public static List<Set<Character>> figureOutMapping(String[] array) {
		List<String> list = new ArrayList<>(Arrays.asList(array));
		
		String zero, two, three, five, six, nine;
		String one = null;
		String four = null;
		String seven = null;
		String eight = null;
		
		for(String str : list) {
			int len = str.length();
			switch(len) {
				case 2 -> one = str;
				case 3 -> seven = str;
				case 4 -> four = str;
				case 7 -> eight = str;
			}
		}
		
		// Remove previous patterns
		list.remove(one);
		list.remove(four);
		list.remove(seven);
		list.remove(eight);
		
		// If it matches Four we found Nine
		list.remove(nine = getMatch(list, four));
		
		// If it matches Eight minus One we found Six
		list.remove(six = getMatch(list, eight, one));
		
		// If it has 6 length we found Zero
		list.remove(zero = list.stream().filter(s -> s.length() == 6).findFirst().get());
		
		// If it matches Seven we found Three
		list.remove(three = getMatch(list, seven));
		
		// If it matches Four minus One we found Five
		list.remove(five = getMatch(list, four, one));
		
		// Last element is Two
		two = list.get(0);
		
		return List.of(zero, one, two, three, four, five, six, seven, eight, nine).stream().map(Day8::toSet).toList();
	}
	
	public static int partOne(List<String> lines) throws Exception {
		Set<Integer> match = Set.of(2, 3, 4, 7);
		return (int)lines.stream().flatMap(s -> Stream.of(s.split(" \\| ")[1].split(" "))).filter(s -> match.contains(s.length())).count();
	}
	
	public static int partTwo(List<String> lines) throws Exception {
		int total = 0;
		for(String line : lines) {
			String[] parts = line.split(" \\| ");
			List<Set<Character>> map = figureOutMapping(parts[0].split(" "));
			
			int result = 0;
			for(String str : parts[1].split(" ")) {
				Set<Character> strSet = toSet(str);
				for(int i = 0; i < 10; i++) {
					if(map.get(i).equals(strSet)) {
						result = result * 10 + i;
						break;
					}
				}
			}
			
			total += result;
		}
		
		return total;
	}
}
