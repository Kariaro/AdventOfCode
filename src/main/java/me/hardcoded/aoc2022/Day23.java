package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Day23 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day23().run();
	}
	
	public Day23() {
		super(2022, 23);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static int[][] DELTAS = {
		{  0, -1 }, // N
		{  1, -1 }, // NE
		{ -1, -1 }, // NW
		
		{  0,  1 }, // S
		{  1,  1 }, // SE
		{ -1,  1 }, // SW
		
		{ -1,  0 }, // W
		{ -1, -1 }, // NW
		{ -1,  1 }, // SW
		
		{  1,  0 }, // E
		{  1, -1 }, // NE
		{  1,  1 }, // SE
	};
	
	static class Elf {
		int startIndex;
		int x;
		int y;
		
		Elf(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		long getPosition() {
			return NumUtils.combine(x, y);
		}
	}
	
	public Set<Long> createSet(Set<Elf> elves) {
		return elves.stream().map(Elf::getPosition).collect(Collectors.toSet());
	}
	
	public Set<Elf> simulate(Set<Elf> data, boolean[] anyMove) {
		Set<Elf> result = new LinkedHashSet<>();
		
		Map<Long, Elf> proposed = new HashMap<>();
		Set<Long> points = createSet(data);
		
		for (Elf elf : data) {
			boolean isBlocked = false;
			for (int[] delta : DELTAS) {
				if (points.contains(NumUtils.combine(elf.x + delta[0], elf.y + delta[1]))) {
					isBlocked = true;
					break;
				}
			}
			
			// If we didn't find any moves add the elf to the result
			if (!isBlocked) {
				result.add(elf);
				continue;
			}
			
			boolean found = false;
			for (int i = 0; i < 4; i++) {
				int elfIndex = (i + elf.startIndex) % 4;
				isBlocked = false;
				for (int j = 0; j < 3; j++) {
					int[] delta = DELTAS[elfIndex * 3 + j];
					if (points.contains(NumUtils.combine(elf.x + delta[0], elf.y + delta[1]))) {
						isBlocked = true;
						break;
					}
				}
				
				// If there are no elves in here move to the direction
				if (!isBlocked) {
					int[] delta = DELTAS[elfIndex * 3];
					long value = NumUtils.combine(elf.x + delta[0], elf.y + delta[1]);
					
					if (!proposed.containsKey(value)) {
						proposed.put(value, elf);
					} else {
						Elf last = proposed.put(value, null);
						if (last != null) {
							result.add(last);
						}
						
						result.add(elf);
					}
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				result.add(elf);
			}
		}
		
		for (var entry : proposed.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			
			Elf elf = entry.getValue();
			elf.x = NumUtils.getX(entry.getKey());
			elf.y = NumUtils.getY(entry.getKey());
			result.add(elf);
			anyMove[0] = true;
		}
		
		for (Elf elf : result) {
			elf.startIndex ++;
		}
		
		if (result.size() != data.size()) {
			throw new RuntimeException();
		}
		
		return result;
	}
	
	// Solve: 52 min
	public long partOne(List<String> lines) throws Exception {
		Set<Elf> data = new LinkedHashSet<>();
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				
				if (c == '#') {
					data.add(new Elf(x, y));
				}
			}
		}
		
		boolean[] anyMove = { true };
		for (int i = 0; i < 10; i++) {
			data = simulate(data, anyMove);
		}
		
		int xMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;
		for (Elf elf : data) {
			xMin = Math.min(xMin, elf.x);
			xMax = Math.max(xMax, elf.x);
			yMin = Math.min(yMin, elf.y);
			yMax = Math.max(yMax, elf.y);
		}
		
		int width = xMax - xMin + 1;
		int height = yMax - yMin + 1;
		return (long) width * height - data.size();
	}
	
	// Solve: 3 min
	public long partTwo(List<String> lines) throws Exception {
		Set<Elf> data = new LinkedHashSet<>();
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				
				if (c == '#') {
					data.add(new Elf(x, y));
				}
			}
		}
		
		boolean[] anyMove = { true };
		int round = 0;
		while (anyMove[0]) {
			anyMove[0] = false;
			data = simulate(data, anyMove);
			round ++;
		}
		
		return round;
	}
}
