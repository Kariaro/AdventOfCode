package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.stream.LongStream;

public class Day22_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day22_Post().run();
	}
	
	public Day22_Post() {
		super(2022, 22, Suffix.Post);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class World {
		Set<Long> points = new LinkedHashSet<>();
		Set<Long> inside = new LinkedHashSet<>();
		long firstIndex = Long.MIN_VALUE;
	}
	
	public long findWrap(Set<Long> inside, int x, int y, int dx, int dy) {
		OptionalLong value;
		if (dy == 0) {
			LongStream sameRow = inside.stream().filter(i -> NumUtils.getY(i) == y).mapToLong(NumUtils::getX);
			value = dx < 0 ? sameRow.max() : sameRow.min();
			return NumUtils.combine((int) value.orElseThrow(), y);
		} else {
			LongStream sameCol = inside.stream().filter(i -> NumUtils.getX(i) == x).mapToLong(NumUtils::getY);
			value = dy < 0 ? sameCol.max() : sameCol.min();
			return NumUtils.combine(x, (int) value.orElseThrow());
		}
	}
	
	static final int[][] DELTA = {
		{  1,  0 }, // Right
		{  0,  1 }, // Down
		{ -1,  0 }, // Left
		{  0, -1 }, // Up
	};
	
	public World constructMap(List<String> lines) {
		World world = new World();
		
		int row = 0;
		for (String line : lines) {
			if (line.isEmpty()) {
				break;
			}
			
			int startColumn = line.lastIndexOf(' ') + 1;
			for (int i = startColumn; i < line.length(); i++) {
				long pos = NumUtils.combine(i, row);
				if (line.charAt(i) != '#') {
					if (world.firstIndex == Long.MIN_VALUE) {
						world.firstIndex = i;
					}
					
					world.points.add(pos);
				}
				
				world.inside.add(pos);
			}
			
			row++;
		}
		
		return world;
	}
	
	record CubePosition(int x, int y, int dir) {}
	
	public CubePosition getPlace(int x, int y, int dir) {
		int nx = x % 50;
		int ny = y % 50;
		boolean upd = false;
		
		final int RIGHT_0 = 0;
		final int DOWN_1 = 1;
		final int LEFT_2 = 2;
		final int UP_3 = 3;
		int pos = (x / 50) + (y / 50) * 3;
		
		int[] layout = {
			0, 1, 4,
			0, 3, 0,
			2, 5, 0,
			6, 0, 0
		};
		
		switch (layout[pos]) {
			case 1 -> {
				switch (dir) {
					case LEFT_2 -> {
						x = 0;
						y = 100 + (49 - ny);
						dir = RIGHT_0;
						upd = true;
					}
					case UP_3 -> {
						x = 0;
						y = 150 + nx;
						dir = RIGHT_0;
						upd = true;
					}
				}
			}
			case 2 -> {
				switch (dir) {
					case LEFT_2 -> {
						x = 50;
						y = (49 - ny);
						dir = RIGHT_0;
						upd = true;
					}
					case UP_3 -> {
						x = 50;
						y = 50 + nx;
						dir = RIGHT_0;
						upd = true;
					}
				}
			}
			case 3 -> {
				switch (dir) {
					case RIGHT_0 -> {
						x = 100 + ny;
						y = 49;
						dir = UP_3;
						upd = true;
					}
					case LEFT_2 -> {
						x = ny;
						y = 100;
						dir = DOWN_1;
						upd = true;
					}
				}
			}
			case 4 -> {
				switch (dir) {
					case RIGHT_0 -> {
						x = 50 + 49;
						y = 100 + (49 - ny);
						dir = LEFT_2;
						upd = true;
					}
					case DOWN_1 -> {
						x = 50 + (49);
						y = 50 + nx;
						dir = LEFT_2;
						upd = true;
					}
					case UP_3 -> {
						x = nx;
						y = 150 + 49;
						upd = true;
					}
				}
			}
			case 5 -> {
				switch (dir) {
					case RIGHT_0 -> {
						x = 100 + (49);
						y = 49 - ny;
						dir = LEFT_2;
						upd = true;
					}
					case DOWN_1 -> {
						x = 49;
						y = 150 + nx;
						dir = LEFT_2;
						upd = true;
					}
				}
			}
			case 6 -> {
				switch (dir) {
					case RIGHT_0 -> {
						x = 50 + ny;
						y = 100 + (49);
						dir = UP_3;
						upd = true;
					}
					case DOWN_1 -> {
						x = 100 + nx;
						y = 0;
						upd = true;
					}
					case LEFT_2 -> {
						x = 50 + ny;
						y = 0;
						dir = DOWN_1;
						upd = true;
					}
				}
			}
		}
		
		if (!upd) {
			throw new RuntimeException("Not updated " + layout[pos] + ", " + dir);
		}
		
		return new CubePosition(x, y, dir);
	}
	
	// Solve: 111 min
	public long partOne(List<String> lines) throws Exception {
		World world = constructMap(lines);
		
		String data = lines.get(lines.size() - 1);
		String[] parts = data.replaceAll("([A-Z])", " $0 ").split("[ ]+", 0);
		
		// Right, Down, Left, Up
		int x = NumUtils.getX(world.firstIndex);
		int y = NumUtils.getY(world.firstIndex);
		
		int dir = 0;
		for (String part : parts) {
			if (Character.isLetter(part.charAt(0))) {
				switch (part.charAt(0)) {
					case 'R' -> dir = (dir + 1) & 3;
					case 'L' -> dir = (dir - 1) & 3;
				}
				
				continue;
			}
			
			int dx = DELTA[dir][0];
			int dy = DELTA[dir][1];
			int input = Integer.parseInt(part);
			
			for (int steps = 0; steps < input; steps++) {
				long pos = NumUtils.combine(x + dx, y + dy);
				
				if (!world.inside.contains(pos)) {
					long point = findWrap(world.inside, x, y, dx, dy);
					if (!world.points.contains(point)) {
						// Collided with a wall
						break;
					}
					
					x = NumUtils.getX(point);
					y = NumUtils.getY(point);
				} else if (!world.points.contains(pos)) {
					// Collided with a wall
					break;
				} else {
					x += dx;
					y += dy;
				}
			}
		}
		
		return 1000L * (y + 1) + 4L * (x + 1) + dir;
	}
	
	// Solve: 149 min
	public long partTwo(List<String> lines) throws Exception {
		World world = constructMap(lines);
		
		String data = lines.get(lines.size() - 1);
		String[] parts = data.replaceAll("([A-Z])", " $0 ").split("[ ]+", 0);
		
		// Right, Down, Left, Up
		int x = NumUtils.getX(world.firstIndex);
		int y = NumUtils.getY(world.firstIndex);
		
		int dir = 0;
		for (String part : parts) {
			if (Character.isLetter(part.charAt(0))) {
				switch (part.charAt(0)) {
					case 'R' -> dir = (dir + 1) & 3;
					case 'L' -> dir = (dir - 1) & 3;
				}
				
				continue;
			}
			
			int dx = DELTA[dir][0];
			int dy = DELTA[dir][1];
			int input = Integer.parseInt(part);
			
			for (int steps = 0; steps < input; steps++) {
				long pos = NumUtils.combine(x + dx, y + dy);
				
				if (!world.inside.contains(pos)) {
					CubePosition val = getPlace(x, y, dir);
					if (!world.points.contains(NumUtils.combine(val.x, val.y))) {
						// Collided with a wall
						break;
					}
					
					x = val.x;
					y = val.y;
					dir = val.dir;
					dx = DELTA[dir][0];
					dy = DELTA[dir][1];
				} else if (!world.points.contains(pos)) {
					// Collided with a wall
					break;
				} else {
					x += dx;
					y += dy;
				}
			}
		}
		
		return 1000L * (y + 1) + 4L * (x + 1) + dir;
	}
}
