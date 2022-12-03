package me.hardcoded.util;

/**
 * Base day class for Aoc
 */
public abstract class DayBase {
	public final int day;
	public final int year;
	public final Suffix suffix;
	
	public DayBase(int year, int day) {
		this(year, day, Suffix.None);
	}
	
	public DayBase(int year, int day, Suffix suffix) {
		this.year = year;
		this.day = day;
		this.suffix = suffix;
	}
	
	public abstract void run() throws Exception;
	
	public enum Suffix {
		None,
		Post
	}
	
	@Override
	public String toString() {
		if (suffix != Suffix.None) {
			return "Aoc " + year + ", Day " + day + " " + suffix + "\n\n";
		}
		
		return "Aoc " + year + ", Day " + day + "\n\n";
	}
}
