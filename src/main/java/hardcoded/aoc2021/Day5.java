package hardcoded.aoc2021;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hardcoded.util.Utils;

public class Day5 {
	static class Line {
		final int x1, y1, x2, y2;
		final int minX, minY, maxX, maxY;
		final boolean isAxisBounded;
		
		public Line(String value) {
			String[] parts = value.split("( -> )|,");
			this.x1 = Integer.parseInt(parts[0]);
			this.y1 = Integer.parseInt(parts[1]);
			this.x2 = Integer.parseInt(parts[2]);
			this.y2 = Integer.parseInt(parts[3]);
			this.minX = Math.min(x1, x2);
			this.minY = Math.min(y1, y2);
			this.maxX = Math.max(x1, x2);
			this.maxY = Math.max(y1, y2);
			this.isAxisBounded = (x1 == x2) || (y1 == y2);
		}
		
		void draw(Map<Long, Long> map) {
			if(y1 == y2) {
				for(int x = minX; x <= maxX; x++) {
					long point = getCoordinate(x, minY);
					map.put(point, map.getOrDefault(point, 0L) + 1L);
				}
			} else if(x1 == x2) {
				for(int y = minY; y <= maxY; y++) {
					long point = getCoordinate(minX, y);
					map.put(point, map.getOrDefault(point, 0L) + 1L);
				}
			} else {
				int xd = x1 < x2 ? 1:-1;
				int yd = y1 < y2 ? 1:-1;
				int dd = Math.max(maxX - minX, maxY - minY);
				for(int i = 0; i <= dd; i++) {
					long point = getCoordinate(x1 + xd * i, y1 + yd * i);
					map.put(point, map.getOrDefault(point, 0L) + 1L);
				}
			}
		}
	}
	
	private static long getCoordinate(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
	
	public static void main(String[] args) throws Exception {
		List<Line> lines = Utils.readAllLines(2021, "day5").stream().map(Line::new).toList();
		
		Utils.printf("Day 5\n");
		Utils.printf("PartOne: %d\n", partOne(lines));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	public static int partOne(List<Line> lines) throws Exception {
		final Map<Long, Long> system = new HashMap<>();
		
		for(Line line : lines) {
			if(line.isAxisBounded) {
				line.draw(system);
			}
		}
		
		int result = system.values().stream().mapToInt(i -> (i > 1) ? 1:0).sum();
		return result;
	}
	
	public static int partTwo(List<Line> lines) throws Exception {
		final Map<Long, Long> system = new HashMap<>();
		
		for(Line line : lines) {
			line.draw(system);
		}
		
		int result = system.values().stream().mapToInt(i -> (i > 1) ? 1:0).sum();
		return result;
	}
}
