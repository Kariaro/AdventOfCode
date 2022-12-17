package me.hardcoded.util;

public class CircularIterator {
	private final String string;
	private int index;
	
	public CircularIterator(String string) {
		if (string == null || string.isEmpty()) {
			throw new RuntimeException("string cannot be null or empty");
		}
		
		this.string = string;
	}
	
	public int index() {
		return index;
	}
	
	public int setIndex(int index) {
		int prev = this.index;
		this.index = ((index % string.length()) + string.length()) % string.length();
		return prev;
	}
	
	public char previous() {
		int idx = setIndex(index - 1);
		return string.charAt(idx);
	}
	
	public char next() {
		int idx = setIndex(index + 1);
		return string.charAt(idx);
	}
	
	public void reset() {
		setIndex(0);
	}
}