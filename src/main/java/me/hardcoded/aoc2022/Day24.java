package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day24 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day24().run();
	}
	
	public Day24() {
		super(2022, 24);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static final int UP = 0;
	static final int DOWN = 1;
	static final int LEFT = 2;
	static final int RIGHT = 3;
	static final int[][] DELTAS = {
		{  0, -1 },
		{  0,  1 },
		{ -1,  0 },
		{  1,  0 }
	};
	
	static class World {
		Set<Long> walls = new HashSet<>();
		int width;
		int height;
	}
	
	static class Blizzard {
		int x;
		int y;
		int dir;
		
		Blizzard(int x, int y, int dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}
	}
	
	public Map<Integer, Set<Long>> BLOCKAGES = new HashMap<>();
	public Map<Integer, Set<Blizzard>> MINUTES = new HashMap<>();
	public Set<Long> getBlockages(World world, int minute) {
		if (!MINUTES.containsKey(minute)) {
			if (!MINUTES.containsKey(minute - 1)) {
				throw new RuntimeException("Previous minute has not been calculated");
			}
			
			Set<Long> blocks = new HashSet<>();
			Set<Blizzard> next = new HashSet<>();
			
			for (Blizzard blizzard : MINUTES.get(minute - 1)) {
				int[] delta = DELTAS[blizzard.dir];
				Blizzard n = new Blizzard(
					blizzard.x + delta[0],
					blizzard.y + delta[1],
					blizzard.dir
				);
				
				if (world.walls.contains(NumUtils.combine(n.x, n.y))) {
					switch (n.dir) {
						case RIGHT -> n.x = 1;
						case LEFT -> n.x = world.width - 2;
						case UP -> n.y = world.height - 2;
						case DOWN -> n.y = 1;
					}
				}
				
				next.add(n);
				blocks.add(NumUtils.combine(n.x, n.y));
			}
			
			MINUTES.put(minute, next);
			BLOCKAGES.put(minute, blocks);
		}
		
		return BLOCKAGES.get(minute);
	}
	
	public int getSteps(World world, int startX, int startY, int targetX, int targetY, int minute) {
		Set<Long> lastPositions = new HashSet<>();
		lastPositions.add(NumUtils.combine(startX, startY));
		
		while (true) {
			Set<Long> blocks = getBlockages(world, minute);
			Set<Long> newLocations = new HashSet<>(lastPositions);
			for (long player : lastPositions) {
				int px = NumUtils.getX(player);
				int py = NumUtils.getY(player);
				
				for (int dir = 0; dir < 4; dir++) {
					int[] delta = DELTAS[dir];
					if (py + delta[1] < 0) {
						continue;
					}
					
					newLocations.add(NumUtils.combine(px + delta[0], py + delta[1]));
				}
			}
			
			// Remove bad elements
			for (long point : blocks) {
				newLocations.remove(point);
			}
			
			for (long point : world.walls) {
				newLocations.remove(point);
			}
			
			if (newLocations.contains(NumUtils.combine(targetX, targetY))) {
				return minute;
			}
			
			lastPositions = newLocations;
			minute++;
		}
	}
	
	// Solve: 52 min
	public long partOne(List<String> lines) throws Exception {
		int sCol = lines.get(0).indexOf('.');
		int eCol = lines.get(lines.size() - 1).indexOf('.');
		
		World world = new World();
		world.width = lines.get(0).length();
		world.height = lines.size();
		
		Set<Long> first = new HashSet<>();
		Set<Blizzard> blizzards = new HashSet<>();
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				
				switch (c) {
					case '>' -> blizzards.add(new Blizzard(x, y, RIGHT));
					case '<' -> blizzards.add(new Blizzard(x, y, LEFT));
					case '^' -> blizzards.add(new Blizzard(x, y, UP));
					case 'v' -> blizzards.add(new Blizzard(x, y, DOWN));
					case '#' -> world.walls.add(NumUtils.combine(x, y));
				}
				
				if (c != '.' && c != '#') {
					first.add(NumUtils.combine(x, y));
				}
			}
		}
		
		BLOCKAGES.put(0, first);
		MINUTES.put(0, blizzards);
		
		return getSteps(world, sCol, 0, eCol, world.height - 1, 1);
	}
	
	// Solve: 3 min
	public long partTwo(List<String> lines) throws Exception {
		int sCol = lines.get(0).indexOf('.');
		int eCol = lines.get(lines.size() - 1).indexOf('.');
		
		World world = new World();
		world.width = lines.get(0).length();
		world.height = lines.size();
		
		Set<Long> first = new HashSet<>();
		Set<Blizzard> blizzards = new HashSet<>();
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				
				switch (c) {
					case '>' -> blizzards.add(new Blizzard(x, y, RIGHT));
					case '<' -> blizzards.add(new Blizzard(x, y, LEFT));
					case '^' -> blizzards.add(new Blizzard(x, y, UP));
					case 'v' -> blizzards.add(new Blizzard(x, y, DOWN));
					case '#' -> world.walls.add(NumUtils.combine(x, y));
				}
				
				if (c != '.' && c != '#') {
					first.add(NumUtils.combine(x, y));
				}
			}
		}
		
		BLOCKAGES.put(0, first);
		MINUTES.put(0, blizzards);
		
		int toCamp = getSteps(world, sCol, 0, eCol, world.height - 1, 1);
		int toStart = getSteps(world, eCol, world.height - 1, sCol, 0, toCamp);
		return getSteps(world, sCol, 0, eCol, world.height - 1, toStart);
	}
}
