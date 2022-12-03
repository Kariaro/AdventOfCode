package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day12 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day12().run();
	}
	
	public Day12() {
		super(2021, 12);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Map<String, List<String>> routes = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split("-");
			List<String> a = routes.computeIfAbsent(parts[0], v -> new ArrayList<>());
			List<String> b = routes.computeIfAbsent(parts[1], v -> new ArrayList<>());
			if (!a.contains(parts[1])) a.add(parts[1]);
			if (!b.contains(parts[0])) b.add(parts[0]);
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(routes));
		Utils.printf("PartTwo: %s\n", partTwo(routes));
	}
	
	private int distinctRoutes(Map<String, List<String>> routes, String path, Set<String> smallCaves, String pickedTwice, boolean hasTwo) {
		int paths = 0;
		
		for (String option : routes.get(path)) {
			// If we reached the end we continue.
			if (option.equals("end")) {
				paths++;
				continue;
			} else if (option.equals("start")) {
				continue;
			}
			
			if (option.toLowerCase().equals(option)) {
				if (smallCaves.contains(option)) {
					// We have already found this
					if (!hasTwo) {
						paths += distinctRoutes(routes, option, smallCaves, option, true);
					}
				} else {
					smallCaves.add(option);
					paths += distinctRoutes(routes, option, smallCaves, pickedTwice, hasTwo);
					smallCaves.remove(option);
				}
			} else {
				paths += distinctRoutes(routes, option, smallCaves, pickedTwice, hasTwo);
			}
		}
		
		return paths;
	}

	// Solve: 17 min
	public long partOne(Map<String, List<String>> routes) throws Exception {
		return distinctRoutes(routes, "start", new HashSet<>(), null, true);
	}
	
	// Solve: 47 min
	public long partTwo(Map<String, List<String>> routes) throws Exception {
		return distinctRoutes(routes, "start", new HashSet<>(), null, false);
	}
}
