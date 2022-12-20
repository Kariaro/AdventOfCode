package me.hardcoded.util;

/**
 * Base day class for Aoc
 */
public abstract class DayBase {
	public final int day;
	public final int year;
	public final Suffix suffix;
	public boolean slow;
	
	public DayBase(int year, int day) {
		this(year, day, Suffix.None, false);
	}
	
	public DayBase(int year, int day, Suffix suffix) {
		this(year, day, suffix, false);
	}
	
	public DayBase(int year, int day, Suffix suffix, boolean slow) {
		this.year = year;
		this.day = day;
		this.suffix = suffix;
		this.slow = slow;
	}
	
	public abstract void run() throws Exception;
	
	public enum Suffix {
		None,
		Post
	}
	
	@Override
	public String toString() {
		if (suffix != Suffix.None) {
			return "Aoc " + year + ", Day " + day + " " + suffix + (slow ? " [SLOW]" : "") + "\n\n";
		}
		
		return "Aoc " + year + ", Day " + day + (slow ? " [SLOW]" : "") + "\n\n";
	}
}
