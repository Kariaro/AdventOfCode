package me.hardcoded.util;

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
}
