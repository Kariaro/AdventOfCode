package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day20 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day20().run();
	}
	
	public Day20() {
		super(2022, 20);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Element(long value, int index) {}
	
	public int findElementWithIndex(List<Element> list, int index) {
		int idx = 0;
		for (Element elm : list) {
			if (elm.index == index) {
				return idx;
			}
			idx ++;
		}
		
		return -1;
	}
	
	public int findElementWithValue(List<Element> list, long value) {
		int idx = 0;
		for (Element elm : list) {
			if (elm.value == value) {
				return idx;
			}
			idx ++;
		}
		
		return -1;
	}
	
	public int wrapIndex(List<Element> list, long index) {
		int len = list.size();
		return (int) (((index % len) + len) % len);
	}
	
	// Solve: 17 min
	public long partOne(List<String> lines) throws Exception {
		LinkedList<Element> list = new LinkedList<>();
		for (int i = 0; i < lines.size(); i++) {
			list.add(new Element(Long.parseLong(lines.get(i)), i));
		}
		
		for (int i = 0; i < list.size(); i++) {
			int idx = findElementWithIndex(list, i);
			
			Element value = list.get(idx);
			list.remove(idx);
			
			int wrap = wrapIndex(list, idx + value.value);
			if (wrap == 0) {
				list.add(value);
			} else {
				list.add(wrap, value);
			}
		}
		
		int zeroIndex = findElementWithValue(list, 0);
		long a = list.get(wrapIndex(list, zeroIndex + 1000)).value;
		long b = list.get(wrapIndex(list, zeroIndex + 2000)).value;
		long c = list.get(wrapIndex(list, zeroIndex + 3000)).value;
		return a + b + c;
	}
	
	// Solve: 7 min
	public long partTwo(List<String> lines) throws Exception {
		LinkedList<Element> list = new LinkedList<>();
		for (int i = 0; i < lines.size(); i++) {
			list.add(new Element(Long.parseLong(lines.get(i)) * 811589153, i));
		}
		
		for (int times = 0; times < 10; times ++) {
			for (int i = 0; i < list.size(); i++) {
				int idx = findElementWithIndex(list, i);
				
				Element value = list.get(idx);
				list.remove(idx);
				
				int wrap = wrapIndex(list, idx + value.value);
				if (wrap == 0) {
					list.add(value);
				} else {
					list.add(wrap, value);
				}
			}
		}
		
		int zeroIndex = findElementWithValue(list, 0);
		long a = list.get(wrapIndex(list, zeroIndex + 1000)).value;
		long b = list.get(wrapIndex(list, zeroIndex + 2000)).value;
		long c = list.get(wrapIndex(list, zeroIndex + 3000)).value;
		return a + b + c;
	}
}
