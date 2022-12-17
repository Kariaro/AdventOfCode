package me.hardcoded.util;

import java.util.Arrays;
import java.util.Set;

public class NumUtils {
	public static long combine(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
	
	public static int getX(long combined) {
		return (int) combined;
	}
	
	public static int getY(long combined) {
		return (int) (combined >> 32L);
	}
	
	public static String getSet(Set<Long> data, int x0, int x1, int y0, int y1) {
		int w = x1 - x0;
		int h = y1 - y0;
		char[] buffer = new char[w * h];
		Arrays.fill(buffer, '.');
		
		for (long point : data) {
			int x = NumUtils.getX(point) - x0;
			int y = NumUtils.getY(point) - y0;
			
			if (x < 0 || x >= w || y < 0 || y >= h) {
				continue;
			}
			
			buffer[x + y * w] = '#';
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < h; i++) {
			sb.append(new String(buffer, i * w, w)).append('\n');
		}
		
		return sb.toString();
	}
	
	public static String getSpecificSet(Set<Long> data, int x0, int x1, int y0, int y1, boolean flipH, boolean flipV) {
		int w = x1 - x0;
		int h = y1 - y0;
		char[] buffer = new char[w * h];
		Arrays.fill(buffer, '.');
		
		for (long point : data) {
			int x = NumUtils.getX(point) - x0;
			int y = NumUtils.getY(point) - y0;
			
			if (x < 0 || x >= w || y < 0 || y >= h) {
				continue;
			}
			
			buffer[(flipH ? (w - x - 1) : x) + (flipV ? (h - y - 1) : y) * w] = '#';
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < h; i++) {
			sb.append(new String(buffer, i * w, w)).append('\n');
		}
		
		return sb.toString();
	}
	
	public static void drawLine(Set<Long> set, int x0, int y0, int x1, int y1) {
		if (x0 != x1) {
			int min = Math.min(x0, x1);
			int max = Math.max(x0, x1);
			
			for (int i = min; i <= max; i++) {
				set.add(NumUtils.combine(i, y0));
			}
		} else {
			int min = Math.min(y0, y1);
			int max = Math.max(y0, y1);
			
			for (int i = min; i <= max; i++) {
				set.add(NumUtils.combine(x0, i));
			}
		}
	}
}
