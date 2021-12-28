package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day25_Post {
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
			
			for(int y = 0; y < lines.size(); y++) {
				String line = lines.get(y);
				
				for(int x = 0; x < line.length(); x++) {
					int idx = x + y * width;
					map[idx] = switch(line.charAt(x)) {
						case '>' -> EAST;
						case 'v' -> SOUTH;
						default -> 0;
					};
				}
			}
		}
		
		boolean step() {
			boolean moved = false;
			
			for(int y = 0; y < height; y++) {
				boolean movedFirst = false;
				for(int x = 0; x < width; x++) {
					if(x == width - 1 && movedFirst) break;
					
					int thisIdx = x + y * width;
					int pickle = map[thisIdx];
					
					if(pickle == EAST) {
						int nextIdx = ((x + 1) % width) + y * width;
						int nextBlc = map[nextIdx];
						
						if(nextBlc == 0) {
							if(x == 0) movedFirst = true;
							
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
			
			for(int x = 0; x < width; x++) {
				boolean movedFirst = false;
				for(int y = 0; y < height; y++) {
					if(y == height - 1 && movedFirst) break;
					
					int thisIdx = x + y * width;
					int pickle = map[thisIdx];
					
					if(pickle == SOUTH) {
						int nextIdx = x + (((y + 1) % height)) * width;
						int nextBlc = map[nextIdx];
						
						if(nextBlc == 0) {
							if(y == 0) movedFirst = true;
							
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
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day25");
		Floor floor = new Floor(lines);
		
		Utils.printf("Day 25\n");
		Utils.printf("PartOne: %d\n", partOne(floor));
		Utils.printf("PartTwo: %d\n", partTwo(floor));
	}
	
	// Solve: 27 min
	public static long partOne(Floor floor) throws Exception {
		int index = 1;
		
		while(floor.step()) {
			index++;
		}
		
		return index;
	}
	
	public static long partTwo(Floor floor) throws Exception {
		return 0;
	}
}
