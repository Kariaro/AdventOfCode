package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day11 {
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day11");
		int width = lines.get(0).length();
		int height = lines.size();
		
		int[] array = String.join("", lines).chars().map(i -> (i - '0')).toArray();
		
		Utils.printf("Day 11\n");
		Utils.printf("PartOne: %d\n", partOne(array.clone(), width, height));
		Utils.printf("PartTwo: %d\n", partTwo(array.clone(), width, height));
	}
	
	private static void spread(int[] array, Set<Integer> visited, int x, int y, int width, int height) {
		int index = x + y * width;
		if(!visited.add(index)) return;
		
		for(int i = 0; i < 9; i++) {
			int dy = (i / 3) - 1;
			int dx = (i % 3) - 1;
			if(dy != 0 || dx != 0) {
				int nx = x + dx;
				int ny = y + dy;
				
				if(nx >= 0 && nx < width && ny >= 0 && ny < height && ++array[nx + ny * width] > 9) {
					spread(array, visited, nx, ny, width, height);
				}
			}
		}
	}
	
	private static int performSteps(int[] array, int width, int height) {
		Set<Integer> visited = new HashSet<>();
		
		// First add one to all elements in the array.
		for(int i = 0, len = array.length; i < len; i++) {
			array[i]++;
		}
		
		// Then for all values that are greater than nine propagate the spread.
		for(int i = 0, len = array.length; i < len; i++) {
			if(array[i] > 9) {
				spread(array, visited, i % width, i / width, width, height);
			}
		}
		
		// All octopus that has flashed will be set to zero.
		for(int i : visited) {
			array[i] = 0;
		}
		
		return visited.size();
	}
	
	public static long partOne(int[] array, int width, int height) throws Exception {
		int score = 0;
		for(int i = 0; i < 100; i++) {
			score += performSteps(array, width, height);
		}
		return score;
	}
	
	public static long partTwo(int[] array, int width, int height) throws Exception {
		int index = 0;
		while(true) {
			int score = performSteps(array, width, height);
			index++;
			
			// If the whole board flashes it will have a score of the length of the array.
			if(score == array.length) {
				return index;
			}
		}
	}
}
