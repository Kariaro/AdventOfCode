package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day19 {
	static class Point3d {
		int x, y, z;
		
		Point3d() {}
		Point3d(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		void set(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y, z);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Point3d that)) return false;
			return x == that.x && y == that.y && z == that.z;
		}
		
		@Override
		public String toString() {
			return "(%d, %d, %d)".formatted(x, y, z);
		}
	}
	
	static class Scanner {
		Map<Long, Set<Integer>> zeroPoints = new HashMap<>();
		Set<Point3d> points;
		int x, y, z, r;
		int index;
		
		List<Point3d> getTranslatedRotatedPoints() {
			List<Point3d> list = new ArrayList<>(points.size());
			for(Point3d point : points) {
				Point3d p = transform(point, r);
				p.x += x;
				p.y += y;
				p.z += z;
				list.add(p);
			}
			
			return list;
		}
		
		void addAllPoints() {
			for(Point3d point : points) {
				long coord = getCoordinate(point.x, point.y);
				zeroPoints.computeIfAbsent(coord, v -> new HashSet<>()).add(point.z);
			}
		}
		
		boolean contains(Point3d point) {
			Set<Integer> set = zeroPoints.get(getCoordinate(point.x, point.y));
			return set == null ? false:set.contains(point.z);
		}
		
		@Override
		public String toString() {
			return "scan(%d, %d, %d, rot: %d)".formatted(x, y, z, r);
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day19");
		
		List<Scanner> scanners = new ArrayList<>();
		Scanner scanner = new Scanner();
		
		for(String line : lines) {
			if(line.startsWith("---")) {
				scanner = new Scanner();
				scanner.points = new HashSet<>();
				scanner.index = scanners.size();
				scanners.add(scanner);
			} else if(!line.isBlank()) {
				String[] parts = line.split(",");
				scanner.points.add(new Point3d(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2])
				));
			}
		}
		
		Utils.printf("Day 19\n");
		Utils.printf("PartOne: %d\n", partOne(scanners));
		Utils.printf("PartTwo: %d\n", partTwo(scanners));
	}
	
	private static long getCoordinate(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
	
	private static int manhattanDistance(Scanner scanner, Scanner point) {
		int dx = Math.abs(scanner.x - point.x);
		int dy = Math.abs(scanner.y - point.y);
		int dz = Math.abs(scanner.z - point.z);
		return dx + dy + dz;
	}
	
	private static final int ROTATIONS = 8 * 6;
	private static Point3d transform(Point3d p, int i) {
		return transform(p, i, new Point3d());
	}
	
	private static Point3d transform(Point3d p, int i, Point3d result) {
		// 6 different rotations
		switch(i / 8) {
			case 0  -> result.set(p.x, p.y, p.z);
			case 1  -> result.set(p.x, p.z, p.y);
			case 2  -> result.set(p.y, p.x, p.z);
			case 3  -> result.set(p.y, p.z, p.x);
			case 4  -> result.set(p.z, p.x, p.y);
			default -> result.set(p.z, p.y, p.x);
		}
		
		// 8 different sign flips
		if((i & 1) != 0) result.x = -result.x;
		if((i & 2) != 0) result.y = -result.y;
		if((i & 4) != 0) result.z = -result.z;
		
		return result;
	}
	
	private static Point3d getScannerOriginPosition(Point3d a, Point3d b, int r) {
		// Calculate the offset of both points when b is rotated by 'r'
		
		Point3d b_origin = transform(b, r);
		b_origin.x = a.x - b_origin.x;
		b_origin.y = a.y - b_origin.y;
		b_origin.z = a.z - b_origin.z;
		// rotated = a - b * rotation
		
		// A      = (100, 0)
		// Br     = (-50, 0)
		// Origin = (150, 0)
		return b_origin;
	}
	
	private static boolean canSeeTwelveBeacons(Scanner center, Scanner scanner) {
		int beaconCount = 0;
		
		Point3d p = new Point3d();
		for(Point3d point : scanner.points) {
			transform(point, scanner.r, p);
			p.x += scanner.x;
			p.y += scanner.y;
			p.z += scanner.z;
			if(center.contains(p) && ++beaconCount >= 12) {
				return true;
			}
		}
		
		// This line is only here to solve tests cases with less than 12 beacons
		return beaconCount == scanner.points.size();
	}
	
	private static boolean calibrateScanner(Scanner center, Scanner scanner) {
		// Origin is at (0, 0, 0) and has the start rotation
		// All points that origin see is the correct points
		for(Point3d pivot : center.points) {
			// If this is our start pivot we check it against the first point of our scanner
			// We will check it for all rotations
			
			for(int k = 0; k < ROTATIONS; k++) {
				scanner.r = k;
				
				for(Point3d scannerPoint : scanner.points) {
					// Calculates the possible origin for the input 'scanner'
					Point3d origin = getScannerOriginPosition(pivot, scannerPoint, k);
					
					// Set the global offset and rotation
					scanner.x = origin.x;
					scanner.y = origin.y;
					scanner.z = origin.z;
					
					// If the scanner can see twelve beacons we can return
					if(canSeeTwelveBeacons(center, scanner)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static void calibrateScanners(List<Scanner> scanners) {
		// First scanner has the correct rotation we say
		List<Scanner> list = new ArrayList<>(scanners);
		list.remove(0);
		
		Scanner center = scanners.get(0);
		center.addAllPoints();
		
		while(!list.isEmpty()) {
			List<Scanner> keepList = new ArrayList<>();
			
			System.out.println("List: " + list);
			for(int i = 0; i < list.size(); i++) {
				Scanner scanner = list.get(i);
				if(calibrateScanner(center, scanner)) {
					System.out.printf("Scanner done: (%d) %s\n", scanner.index, scanner);
					center.points.addAll(scanner.getTranslatedRotatedPoints());
					center.addAllPoints();
				} else {
					keepList.add(scanner);
				}
			}
			
			list = keepList;
		}
		
		for(int i = 0; i < scanners.size(); i++) {
			System.out.printf("(%d) %s\n", i, scanners.get(i));
			System.out.println("-----------------------");
		}
	}
	
	// Solve: 209 min
	public static long partOne(List<Scanner> scanners) throws Exception {
		calibrateScanners(scanners);
		return scanners.get(0).points.size();
	}
	
	// Solve: 5 min
	public static long partTwo(List<Scanner> scanners) throws Exception {
		long longest = 0;
		for(int i = 0; i < scanners.size(); i++) {
			for(int j = i + 1; j < scanners.size(); j++) {
				longest = Math.max(longest, manhattanDistance(scanners.get(i), scanners.get(j)));
			}
		}
		
		return longest;
	}
}
