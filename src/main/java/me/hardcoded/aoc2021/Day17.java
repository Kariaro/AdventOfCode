package me.hardcoded.aoc2021;


import java.awt.Rectangle;

import me.hardcoded.util.Utils;

public class Day17 {
	public static void main(String[] args) throws Exception {
		String line = Utils.readAll(2021, "day17").strip();

		String[] parts = line.substring(13).split(", ");
		String[] xparts = parts[0].substring(2).split("\\.\\.");
		String[] yparts = parts[1].substring(2).split("\\.\\.");
		int xMin = Integer.parseInt(xparts[0]);
		int xMax = Integer.parseInt(xparts[1]) + 1;
		int yMin = Integer.parseInt(yparts[0]);
		int yMax = Integer.parseInt(yparts[1]) + 1;
		Rectangle area = new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		
		Utils.printf("Day 17\n");
		Utils.printf("PartOne: %d\n", partOne(area, xMax));
		Utils.printf("PartTwo: %d\n", partTwo(area, xMax));
	}
	
	public static int hitTarget(int vx, int vy, Rectangle rect) {
		int x = 0;
		int y = 0;
		int ly = 0;
		while(true) {
			if(rect.contains(x, y)) {
				return ly;
			}
			
			if(y < rect.y || x > rect.x + rect.width) {
				return -1;
			}
			
			x += vx--;
			y += vy--;
			ly = Math.max(ly, y);
			if(vx < 0) vx = 0;
		}
	}
	
	// Solve: 14 min
	public static long partOne(Rectangle area, int xMax) throws Exception {
		int largest_y = 0;
		for(int y = -1000; y < 1000; y++) {
			for(int x = 0; x < xMax; x++) {
				largest_y = Math.max(largest_y, hitTarget(x, y, area));
			}
		}
		
		return largest_y;
	}
	
	// Solve: 6 min
	public static long partTwo(Rectangle area, int xMax) throws Exception {
		int times = 0;
		for(int y = -1000; y < 1000; y++) {
			for(int x = 0; x < xMax; x++) {
				times += (hitTarget(x, y, area) >= 0) ? 1:0;
			}
		}
		
		return times;
	}
}
