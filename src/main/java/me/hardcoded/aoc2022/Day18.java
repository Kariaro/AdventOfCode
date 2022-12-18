package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day18 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day18().run();
	}
	
	public Day18() {
		super(2022, 18);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Point(int x, int y, int z) {
		public Point(String[] parts) {
			this(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		}
		
		public Point withOffset(int x, int y, int z) {
			return new Point(this.x + x, this.y + y, this.z + z);
		}
	}
	
	record Box(Point min, Point max) {
		public boolean isOutside(Point p) {
			return p.x < min.x || p.x > max.x
				|| p.y < min.y || p.y > max.y
				|| p.z < min.z || p.z > max.z;
		}
	}
	
	public int countFaces(Set<Point> points, Point point) {
		int[][] offsets = {
			{  0,  1,  0 }, // Up
			{  0, -1,  0 }, // Down
			{  1,  0,  0 }, // Left
			{ -1,  0,  0 }, // Right
			{  0,  0,  1 }, // Front
			{  0,  0, -1 }, // Back
		};
		
		int count = 0;
		for (int[] offset : offsets) {
			if (!points.contains(point.withOffset(offset[0], offset[1], offset[2]))) {
				count ++;
			}
		}
		
		return count;
	}
	
	public boolean findInternal(Set<Point> points, Set<Point> searched, Point point, Box box) {
		int[][] offsets = {
			{  0,  1,  0 }, // Up
			{  0, -1,  0 }, // Down
			{  1,  0,  0 }, // Left
			{ -1,  0,  0 }, // Right
			{  0,  0,  1 }, // Front
			{  0,  0, -1 }, // Back
		};
		
		searched.add(point);
		
		for (int[] offset : offsets) {
			Point next = point.withOffset(offset[0], offset[1], offset[2]);
			
			if (points.contains(next) || searched.contains(next)) {
				continue;
			}
			
			if (box.isOutside(next) || !findInternal(points, searched, next, box)) {
				return false;
			}
		}
		
		return true;
	}
	
	// Solve: 6 min
	public long partOne(List<String> lines) throws Exception {
		Set<Point> points = new HashSet<>();
		for (String line : lines) {
			points.add(new Point(line.split(",")));
		}
		
		long result = 0;
		for (Point point : points) {
			result += countFaces(points, point);
		}
		
		return result;
	}
	
	// Solve: 20 min
	public long partTwo(List<String> lines) throws Exception {
		Set<Point> points = new HashSet<>();
		for (String line : lines) {
			Point p = new Point(line.split(","));
			points.add(p);
		}
		
		Point min = points.stream().reduce(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), (a, b) -> new Point(
			Math.min(a.x, b.x),
			Math.min(a.y, b.y),
			Math.min(a.z, b.z)
		));
		
		Point max = points.stream().reduce(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE), (a, b) -> new Point(
			Math.max(a.x, b.x),
			Math.max(a.y, b.y),
			Math.max(a.z, b.z)
		));
		
		Box box = new Box(min, max);
		for (int x = min.x; x < max.x; x++) {
			for (int y = min.y; y < max.y; y++) {
				for (int z = min.z; z < max.z; z++) {
					Point p = new Point(x, y, z);
					
					if (points.contains(p)) {
						continue;
					}
					
					Set<Point> set = new HashSet<>();
					if (findInternal(points, set, p, box)) {
						points.addAll(set);
					}
				}
			}
		}
		
		long result = 0;
		for (Point point : points) {
			result += countFaces(points, point);
		}
		
		return result;
	}
}
