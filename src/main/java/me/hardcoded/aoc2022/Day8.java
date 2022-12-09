package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.List;

public class Day8 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day8().run();
	}
	
	public Day8() {
		super(2022, 8);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	public boolean isVis(int x, int y, int w, int h, int[] trees) {
		int v = trees[x + y * w];
		
		for (int i = 0; i < w; i++) {
			int r = trees[i + y * w];
			
			if (i < x && r >= v) {
				i = x;
				continue;
			} else if (i > x && r >= v) {
				break;
			}
			
			if (i == x || i == w - 1) {
				return true;
			}
		}
		
		for (int i = 0; i < h; i++) {
			int r = trees[x + i * w];
			
			if (i < y && r >= v) {
				i = y;
				continue;
			} else if (i > y &&r >= v) {
				break;
			}
			
			if (i == y || i == h - 1) {
				return true;
			}
		}
		
		return false;
	}
	
	public int getScore(int x, int y, int w, int h, int[] trees) {
		int v = trees[x + y * w];
		int t = 1;
		
		for (int i = 0; i < 4; i++) {
			int dx = i < 2 ? ((i & 1) * 2 - 1) : 0;
			int dy = i < 2 ? 0 : ((i & 1) * 2 - 1);
			
			int sc = 0;
			int px = x;
			int py = y;
			while (true) {
				px += dx;
				py += dy;
				
				if (px < 0 || px >= w || py < 0 || py >= h) {
					break;
				}
				
				sc ++;
				
				if (trees[px + py * w] >= v) {
					break;
				}
			}
			
			if (sc < 1) {
				continue;
			}
			t *= sc;
		}
		
		return t;
	}
	
	// Solve: 11 min
	public int partOne(List<String> lines) throws Exception {
		int width = lines.get(0).length();
		int height = lines.size();
		int[] array = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				array[x + y * width] = lines.get(y).charAt(x) - '0';
			}
		}
		
		int total = width + width + height + height - 4;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				if (isVis(x, y, width, height, array)) {
					total ++;
				}
			}
		}
		
		return total;
	}
	
	// Solve: 12 min
	public int partTwo(List<String> lines) throws Exception {
		int width = lines.get(0).length();
		int height = lines.size();
		int[] array = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				array[x + y * width] = lines.get(y).charAt(x) - '0';
			}
		}
		
		int result = 0;
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				result = Math.max(result, getScore(x, y, width, height, array));
			}
		}
		
		return result;
	}
}
