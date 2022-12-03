package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day24_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day24_Post().run();
	}
	
	public Day24_Post() {
		super(2021, 24, Suffix.Post);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		List<Inst> insts = lines.stream().map(Inst::new).toList();
		List<Problem> parts = split(insts).stream().map(Problem::new).toList();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(parts));
		Utils.printf("PartTwo: %s\n", partTwo(parts));
	}
	
	static class Inst {
		String first;
		long[] values;
		
		Inst(String line) {
			List<String> parts = Arrays.asList(line.split(" "));
			this.first = parts.get(0);
			this.values = parts.subList(1, parts.size()).stream().mapToLong(v -> {
				// We just want to get the numbers we do not need any fancy checking
				try {
					return Long.parseLong(v);
				} catch (NumberFormatException ignored) {
					return 0;
				}
			}).toArray();
		}
	}
	
	static class Problem {
		final long A, B, C;
		
		Problem(List<Inst> list) {
			/**
			 0: inp w
			 1: mul x 0
			 2: add x z
			 3: mod x 26
			 4: div z <a>
			 5: add x <b>
			 6: eql x w
			 7: eql x 0
			 8: mul y 0
			 9: add y 25
			10: mul y x
			11: add y 1
			12: mul z y
			13: mul y 0
			14: add y w
			15: add y <c>
			16: mul y x
			17: add z y
			*/
			A = list.get(4).values[1];
			B = list.get(5).values[1];
			C = list.get(15).values[1];
		}
		
		void findBranchA(long w, long z, List<Long> list) {
			long bW = w - B;
			long s = z * A;
			long e = s + A;
			for (long p = s; p < e; p++) {
				if ((p % 26) != bW) {
					list.add(p);
				}
			}
		}
		
		void findBranchB(long w, long z, List<Long> list) {
			long bW = w - B;
			long s = z * A;
			long e = s + A;
			for (long p = s; p < e; p++) {
				if ((p % 26) == bW) {
					list.add(p);
					// We can skip 25 because we loop every
					p += 25;
				}
			}
		}
		
		void backtraceFast(long w, long target, List<Long> list) {
			long tz = target - (w + C);
			if ((tz % 26) == 0) {
				findBranchA(w, tz / 26, list);
			}
			
			findBranchB(w, target, list);
		}
	}
	
	private List<List<Inst>> split(List<Inst> insts) {
		List<List<Inst>> result = new ArrayList<>();
		List<Inst> list = null;
		for (Inst inst : insts) {
			if (inst.first.equals("inp")) {
				list = new ArrayList<>();
				result.add(list);
			}
			
			list.add(inst);
		}
		
		return result;
	}
	
	private boolean backtraceMax(List<Problem> probs, int index, long target, LinkedList<Integer> stack) {
		if (index < 0) {
			return target == 0;
		}
		
		// Get the current problem
		Problem prob = probs.get(index);
		List<Long> list = new ArrayList<>();
		
		for (int i = 9; i > 0; i--) {
			list.clear();
			prob.backtraceFast(i, target, list);
			
			stack.push(i);
			for (int j = 0; j < list.size(); j++) {
				long solution = list.get(j);
				if (backtraceMax(probs, index - 1, solution, stack)) {
					return true;
				}
			}
			stack.pop();
		}
		
		return false;
	}
	
	private boolean backtraceMin(List<Problem> probs, int index, long target, LinkedList<Integer> stack) {
		if (index < 0) {
			return target == 0;
		}
		
		// Get the current problem
		Problem prob = probs.get(index);
		List<Long> list = new ArrayList<>();
		
		for (int i = 1; i < 10; i++) {
			list.clear();
			prob.backtraceFast(i, target, list);
			
			stack.push(i);
			for (int j = 0; j < list.size(); j++) {
				long solution = list.get(j);
				if (backtraceMin(probs, index - 1, solution, stack)) {
					return true;
				}
			}
			stack.pop();
		}
		
		return false;
	}
	
	// time: 14:22 -> 16:38
	// time: 19:00 -> 20:24
	// Solve: 220 min
	public long partOne(List<Problem> parts) throws Exception {
		LinkedList<Integer> stack = new LinkedList<>();
		backtraceMax(parts, 13, 0, stack);
		
		String modelNumber = String.join("", stack.stream().map(v -> "" + v).toList());
		return Long.parseLong(modelNumber);
	}
	
	// Solve: 3 min
	public long partTwo(List<Problem> parts) throws Exception {
		LinkedList<Integer> stack = new LinkedList<>();
		backtraceMin(parts, 13, 0, stack);
		
		String modelNumber = String.join("", stack.stream().map(v -> "" + v).toList());
		return Long.parseLong(modelNumber);
	}
}
