package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day22 {
	private static final int MODE_ADD = 0;
	private static final int MODE_SUB = 1;
	
	static class Cuboid {
		final int minX, maxX;
		final int minY, maxY;
		final int minZ, maxZ;
		final boolean init;
		final int mode;
		
		Cuboid(String line) {
			String[] parts = line.substring(3).trim().substring(2).split("(,[xyz]=)|(\\.\\.)");
			minX = Integer.parseInt(parts[0]);
			maxX = Integer.parseInt(parts[1]) + 1;
			minY = Integer.parseInt(parts[2]);
			maxY = Integer.parseInt(parts[3]) + 1;
			minZ = Integer.parseInt(parts[4]);
			maxZ = Integer.parseInt(parts[5]) + 1;
			mode = line.startsWith("on") ? MODE_ADD:MODE_SUB;
			init = !(
					Math.abs(minX) > 50
				|| Math.abs(maxX) > 51
				|| Math.abs(minY) > 50
				|| Math.abs(maxY) > 51
				|| Math.abs(minZ) > 50
				|| Math.abs(maxZ) > 51
			);
		}
		
		@Override
		public String toString() {
			return "x=%d..%d, y=%d..%d, z=%d..%d".formatted(minX, maxX, minY, maxY, minZ, maxZ);
		}
	}
	
	static class CuboidMesh {
		boolean[][][] fields;
		Set<Integer> x_set;
		Set<Integer> y_set;
		Set<Integer> z_set;
		int[] x_array;
		int[] y_array;
		int[] z_array;
		
		public CuboidMesh(List<Cuboid> shapes) {
			x_set = new TreeSet<>();
			y_set = new TreeSet<>();
			z_set = new TreeSet<>();
			
			for(Cuboid cuboid : shapes) {
				x_set.add(cuboid.minX);
				x_set.add(cuboid.maxX);
				y_set.add(cuboid.minY);
				y_set.add(cuboid.maxY);
				z_set.add(cuboid.minZ);
				z_set.add(cuboid.maxZ);
			}
			
			x_array = x_set.stream().mapToInt(i -> i).toArray();
			y_array = y_set.stream().mapToInt(i -> i).toArray();
			z_array = z_set.stream().mapToInt(i -> i).toArray();
			
			fields = new boolean[x_array.length - 1][y_array.length - 1][z_array.length - 1];
		}
		
		private int findIndex(int[] array, int start, int value) {
			for(int i = start; i < array.length; i++) {
				if(array[i] == value) return i;
			}
			
			return array.length - 1;
		}
		
		private void fill(Cuboid shape, boolean value) {
			// Find all spaces this cuboid has in the field
			int x_s = findIndex(x_array, 0, shape.minX);
			int x_e = findIndex(x_array, x_s, shape.maxX);
			int y_s = findIndex(y_array, 0, shape.minY);
			int y_e = findIndex(y_array, y_s, shape.maxY);
			int z_s = findIndex(z_array, 0, shape.minZ);
			int z_e = findIndex(z_array, z_s, shape.maxZ);
			
			for(int x = x_s; x < x_e; x++) {
				for(int y = y_s; y < y_e; y++) {
					for(int z = z_s; z < z_e; z++) {
						fields[x][y][z] = value;
					}
				}
			}
		}
		
		public void add(Cuboid shape) {
			fill(shape, true);
		}
		
		public void sub(Cuboid shape) {
			fill(shape, false);
		}
		
		public long volume() {
			long totalVolume = 0;
			for(int x = 0; x < x_array.length - 1; x++) {
				for(int y = 0; y < y_array.length - 1; y++) {
					for(int z = 0; z < z_array.length - 1; z++) {
						if(fields[x][y][z]) {
							long xx = x_array[x + 1] - x_array[x];
							long yy = y_array[y + 1] - y_array[y];
							long zz = z_array[z + 1] - z_array[z];
							totalVolume += xx * yy * zz;
						}
					}
				}
			}
			
			return totalVolume;
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day22");
		List<Cuboid> ranges = lines.stream().map(Cuboid::new).toList();
		
		Utils.printf("Day 22\n");
		Utils.printf("PartOne: %d\n", partOne(ranges));
		Utils.printf("PartTwo: %d\n", partTwo(ranges));
	}
	
	// Solve: 101 min
	public static long partOne(List<Cuboid> ranges) throws Exception {
		CuboidMesh mesh = new CuboidMesh(ranges.stream().filter(i -> i.init).toList());
		
		for(Cuboid box : ranges) {
			if(!box.init) continue;
			
			if(box.mode == MODE_ADD) {
				mesh.add(box);
			} else {
				mesh.sub(box);
			}
		}
		
		return mesh.volume();
	}
	
	// Solve: 7 min
	public static long partTwo(List<Cuboid> ranges) throws Exception {
		CuboidMesh mesh = new CuboidMesh(ranges);
		
		for(Cuboid box : ranges) {
			if(box.mode == MODE_ADD) {
				mesh.add(box);
			} else {
				mesh.sub(box);
			}
		}
		
		return mesh.volume();
	}
}
