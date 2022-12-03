package me.hardcoded.aoc2021;


import java.util.*;

import me.hardcoded.util.Utils;

public class Day18 {
	static class Snailfish {
		Snailfish parent;
		Snailfish left;
		Snailfish right;
		int value;
		
		public Snailfish(int value) {
			this.value = value;
		}
		
		public Snailfish(Snailfish left, Snailfish right) {
			this.left = left;
			this.right = right;
			left.parent = this;
			right.parent = this;
		}
		
		int index() {
			return parent == null ? 0:(parent.left == this ? 0:1); 
		}
		
		boolean isNumber() {
			return left == null;
		}
		
		void set(int idx, Snailfish fish) {
			fish.parent = this;
			if(idx == 0) {
				left = fish;
			} else {
				right = fish;
			}
		}
		
		Snailfish get(int idx) {
			return idx == 0 ? left:right;
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day18");
		
		Utils.printf("Day 18\n");
		Utils.printf("PartOne: %d\n", partOne(lines));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	public static Snailfish getSnailfish(String line) {
		LinkedList<Snailfish> values = new LinkedList<Snailfish>();
		
		for(char c : line.toCharArray()) {
			if(c == '[' || c == ',') continue;
			if(c == ']') {
				Snailfish b = values.pop();
				Snailfish a = values.pop();
				values.push(new Snailfish(a, b));
			} else {
				values.push(new Snailfish(c - '0'));
			}
		}
		
		return values.pop();
	}
	
	public static Snailfish findSnailfish(Snailfish fish, int dir) {
		Snailfish current = fish;
		
		while(current.index() == dir) {
			current = current.parent;
			if(current.parent == null) return null;
		}
		
		current = current.parent.get(dir);
		if(current.isNumber()) {
			return current;
		}
		
		current = current.parent.get(dir);
		while(!current.isNumber()) {
			current = current.get(dir ^ 1);
		}
		
		return current;
	}
	
	public static long calculate(Snailfish fish) {
		return fish.isNumber() ? fish.value:(calculate(fish.left) * 3 + calculate(fish.right) * 2);
	}
	
	public static Snailfish reduce(Snailfish fish) {
		while(reduce(fish, 0, true) || reduce(fish, 0, false));
		return fish;
	}
	
	public static boolean reduce(Snailfish fish, int depth, boolean explode) {
		if(!fish.isNumber()) {
			if(reduce(fish.left, depth + 1, explode)
			|| reduce(fish.right, depth + 1, explode)
			) return true;
		} else {
			if(!explode && fish.value > 9) {
				int al = fish.value / 2;
				int ar = fish.value - al;
				fish.parent.set(fish.index(), new Snailfish(new Snailfish(al), new Snailfish(ar)));
				return true;
			}
			
			return false;
		}
		
		if(depth >= 4) {
			Snailfish left = findSnailfish(fish, 0);
			if(left != null) {
				left.value += fish.get(0).value;
			}
			
			Snailfish right = findSnailfish(fish, 1);
			if(right != null) {
				right.value += fish.get(1).value;
			}
			
			fish.parent.set(fish.index(), new Snailfish(0));
			return true;
		}
		
		return false;
	}
	
	// Solve 144 min
	public static long partOne(List<String> lines) throws Exception {
		Snailfish sum = getSnailfish(lines.get(0));
		for(int i = 1; i < lines.size(); i++) {
			sum = reduce(new Snailfish(sum, getSnailfish(lines.get(i))));
		}
		
		return calculate(sum);
	}
	
	// Solve: 10 min
	public static long partTwo(List<String> lines) throws Exception {
		long result = 0;
		for(int i = 0; i < lines.size(); i++) {
			for(int j = 0; j < lines.size(); j++) {
				if(i == j) continue;
				
				String a = lines.get(i);
				String b = lines.get(j);
				
				result = Math.max(result, calculate(reduce(new Snailfish(getSnailfish(a), getSnailfish(b)))));
				result = Math.max(result, calculate(reduce(new Snailfish(getSnailfish(b), getSnailfish(a)))));
			}
		}
		
		return result;
	}
}
