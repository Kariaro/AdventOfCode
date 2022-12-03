package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day25_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day25_Post();
	}
	
	public Day25_Post() {
		super(2021, 25, Suffix.Post);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		Floor floor = new Floor(lines);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(floor));
		Utils.printf("PartTwo: %s\n", partTwo(floor));
	}
	
	private static final int EAST  = 1;
	private static final int SOUTH = 2;
	
	static class Floor {
		final int[] map;
		final int width;
		final int height;
		
		Floor(List<String> lines) {
			this.width = lines.get(0).length();
			this.height = lines.size();
			this.map = new int[width * height];
			
			for (int y = 0; y < lines.size(); y++) {
				String line = lines.get(y);
				
				for (int x = 0; x < line.length(); x++) {
					int idx = x + y * width;
					map[idx] = switch (line.charAt(x)) {
						case '>' -> EAST;
						case 'v' -> SOUTH;
						default -> 0;
					};
				}
			}
		}
		
		boolean step() {
			boolean moved = false;
			
			for (int y = 0; y < height; y++) {
				boolean movedFirst = false;
				for (int x = 0; x < width; x++) {
					if (x == width - 1 && movedFirst) break;
					
					int thisIdx = x + y * width;
					int pickle = map[thisIdx];
					
					if (pickle == EAST) {
						int nextIdx = ((x + 1) % width) + y * width;
						int nextBlc = map[nextIdx];
						
						if (nextBlc == 0) {
							if (x == 0) movedFirst = true;
							
							// We can move
							map[thisIdx] = 0;
							map[nextIdx] = pickle;
							// We do not wanna process the same pickle twice
							x++;
							moved = true;
						}
					}
				}
			}
			
			for (int x = 0; x < width; x++) {
				boolean movedFirst = false;
				for (int y = 0; y < height; y++) {
					if (y == height - 1 && movedFirst) break;
					
					int thisIdx = x + y * width;
					int pickle = map[thisIdx];
					
					if (pickle == SOUTH) {
						int nextIdx = x + (((y + 1) % height)) * width;
						int nextBlc = map[nextIdx];
						
						if (nextBlc == 0) {
							if (y == 0) movedFirst = true;
							
							// We can move
							map[thisIdx] = 0;
							map[nextIdx] = pickle;
							// We do not wanna process the same pickle twice
							y++;
							moved = true;
						}
					}
				}
			}
			
			return moved;
		}
	}
	
	// Solve: 27 min
	public long partOne(Floor floor) throws Exception {
		int index = 1;
		
		while (floor.step()) {
			index++;
		}
		
		return index;
	}
	
	public long partTwo(Floor floor) throws Exception {
		return 0;
	}
}
