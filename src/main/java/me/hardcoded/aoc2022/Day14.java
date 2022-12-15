package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day14 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day14().run();
	}
	
	public Day14() {
		super(2022, 14);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Part(int x, int y) {}
	
	private void drawLine(Set<Long> set, Part a, Part b) {
		if (a.x != b.x) {
			int min = Math.min(a.x, b.x);
			int max = Math.max(a.x, b.x);
			
			for (int i = min; i <= max; i++) {
				set.add(NumUtils.combine(i, a.y));
			}
		} else {
			int min = Math.min(a.y, b.y);
			int max = Math.max(a.y, b.y);
			
			for (int i = min; i <= max; i++) {
				set.add(NumUtils.combine(a.x, i));
			}
		}
	}
	
	public int simulate(Set<Long> data, int maxY, int x, int y) {
		if (data.contains(NumUtils.combine(x, y))) {
			return -1;
		}
		
		int count = 0;
		long pos;
		while (true) {
			count++;
			
			// Check if we are outside
			if (y >= maxY) {
				return -1;
			}
			
			// Check down
			pos = NumUtils.combine(x, y + 1);
			if (!data.contains(pos)) {
				y += 1;
				continue;
			}
			
			// Check left
			pos = NumUtils.combine(x - 1, y + 1);
			if (!data.contains(pos)) {
				x -= 1;
				y += 1;
				continue;
			}
			
			// Check right
			pos = NumUtils.combine(x + 1, y + 1);
			if (!data.contains(pos)) {
				x += 1;
				y += 1;
				continue;
			}
			
			// We have settled
			data.add(NumUtils.combine(x, y));
			return count;
		}
	}
	
	// Solve: 24 min
	public long partOne(List<String> lines) throws Exception {
		Set<Long> data = new HashSet<>();
		for (String line : lines) {
			List<Part> parts = Arrays.stream(line.split(" -> ")).map(i -> i.split(",")).map(i -> new Part(Integer.parseInt(i[0]), Integer.parseInt(i[1]))).toList();
			
			Part last = parts.get(0);
			for (Part part : parts) {
				drawLine(data, last, part);
				last = part;
			}
		}
		
		int maxY = data.stream().mapToInt(NumUtils::getY).max().orElseThrow();
		
		long result = 0;
		while (true) {
			int count = simulate(data, maxY, 500, 0);
			if (count < 0) {
				break;	
			}
			
			result ++;
		}
		
		return result;
	}
	
	// Solve: 5 min
	public long partTwo(List<String> lines) throws Exception {
		Set<Long> data = new HashSet<>();
		for (String line : lines) {
			List<Part> parts = Arrays.stream(line.split(" -> ")).map(i -> i.split(",")).map(i -> new Part(Integer.parseInt(i[0]), Integer.parseInt(i[1]))).toList();
			
			Part last = parts.get(0);
			for (Part part : parts) {
				drawLine(data, last, part);
				last = part;
			}
		}
		
		int maxY = data.stream().mapToInt(NumUtils::getY).max().orElseThrow() + 2;
		drawLine(data, new Part(0, maxY), new Part(1000, maxY));
		
		long result = 0;
		while (true) {
			int count = simulate(data, maxY, 500, 0);
			if (count < 0) {
				break;
			}
			
			result ++;
		}
		
		return result;
	}
}
