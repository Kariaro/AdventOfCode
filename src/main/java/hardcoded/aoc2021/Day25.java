package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day25 {
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
			// We move from bottom to top. left to right
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
		
		void print() {
			StringBuilder sb = new StringBuilder();
			for(int y = 0; y < map.length; y += width) {
				for(int x = 0; x < width; x++) {
					int pickle = map[x + y];
					sb.append(switch(pickle) {
						case SOUTH -> 'v';
						case EAST -> '>';
						default -> '.';
					});
				}
				sb.append('\n');
			}
			System.out.println(sb.toString());
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day25");
		
		Floor floor = new Floor(lines);
		
		Utils.printf("Day 25\n");
		Utils.printf("PartOne: %d\n", partOne(floor));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	// Start: 11:53
	// End: 12:20
	public static long partOne(Floor floor) throws Exception {
		floor.print();
		int index = 1;
//		for(int i = 0; i < 58; i++) {
//			System.out.printf("After %d steps:\n", i + 1);
//			floor.step();
//			floor.print();
//		}
		while(floor.step()) {
			System.out.printf("After %d steps:\n", index);
			index++;
//			floor.print();
		}
		
		floor.print();
		System.out.println(index);
		
		return 0;
	}
	
	public static long partTwo(List<String> lines) throws Exception {
		return 0;
	}
}
