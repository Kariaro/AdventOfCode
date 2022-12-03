package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day9 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day9().run();
	}
	
	public Day9() {
		super(2021, 9);
	}
	
	public void run() throws Exception {
		List<int[]> lines = Utils.readAllLines(this).stream().map(s -> s.chars().map(i -> (int)(i - '0')).toArray()).toList();
		int width = lines.get(0).length;
		int height = lines.size();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines, width, height));
		Utils.printf("PartTwo: %s\n", partTwo(lines, width, height));
	}
	
	public int getLowPoint(List<int[]> lines, int x, int y, int width, int height) {
		int value = lines.get(y)[x];
		
		return ((x > 0 && value >= lines.get(y)[x - 1])
		|| (x < width - 1 && value >= lines.get(y)[x + 1])
		|| (y > 0 && value >= lines.get(y - 1)[x])
		|| (y < height - 1 && value >= lines.get(y + 1)[x])) ? 0:(value + 1);
	}

	private long getCoordinate(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
	
	private int getBasinSize(List<int[]> lines, Set<Long> set, int x, int y, int width, int height) {
		int value = lines.get(y)[x];
		// A basin of value 9 does not count.
		if (value == 9) return 0;
		
		// This position has already been computed.
		if (!set.add(getCoordinate(x, y))) return 0;
		
		int count = 1;
		if (x > 0 && value < lines.get(y)[x - 1]) {
			count += getBasinSize(lines, set, x - 1, y, width, height);
		}
		
		if (x < width - 1 && value < lines.get(y)[x + 1]) {
			count += getBasinSize(lines, set, x + 1, y, width, height);
		}
		
		if (y > 0 && value < lines.get(y - 1)[x]) {
			count += getBasinSize(lines, set, x, y - 1, width, height);
		}
		
		if (y < height - 1 && value < lines.get(y + 1)[x]) {
			count += getBasinSize(lines, set, x, y + 1, width, height);
		}
		
		return count;
	}
	
	public int partOne(List<int[]> lines, int width, int height) throws Exception {
		int score = 0;
		for (int y = 0; y < lines.size(); y++) {
			for (int x = 0; x < width; x++) {
				score += getLowPoint(lines, x, y, width, height);
			}
		}
		
		return score;
	}
	
	public int partTwo(List<int[]> lines, int width, int height) throws Exception {
		List<Integer> basinSizes = new ArrayList<>();
		Set<Long> set = new HashSet<>();
		
		for (int y = 0; y < lines.size(); y++) {
			for (int x = 0; x < width; x++) {
				// If we found a low point we found a basin.
				int lowPoint = getLowPoint(lines, x, y, width, height);
				if (lowPoint > 0) {
					// Find all numbers next to this that is one above
					basinSizes.add(getBasinSize(lines, set, x, y, width, height));
					set.clear();
				}
			}
		}
		
		basinSizes.sort(Collections.reverseOrder());
		return basinSizes.get(0) * basinSizes.get(1) * basinSizes.get(2);
	}
}
