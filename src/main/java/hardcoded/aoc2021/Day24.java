package hardcoded.aoc2021;

import java.util.*;

import hardcoded.util.Utils;

public class Day24 {
	enum Type {
		INP,
		ADD,
		MOD,
		MUL,
		DIV,
		EQL,
	}
	
	static class Input {
		public String buffer = "";
		int index;
		
		long read() {
			return buffer.charAt(index++) - '0';
		}
		
		void set(String buffer) {
			this.buffer = buffer;
			this.index = 0;
		}
	}
	
	static class Param {
		int regIdx;
		long num;
		
		Param(String value) {
			this.regIdx = -1;
			if(value.length() == 1) {
				char c = value.charAt(0);
				if(Character.isDigit(c)) {
					this.num = c - '0';
					return;
				}
				
				this.regIdx = c - 'w';
			} else {
				this.num = Long.parseLong(value);
			}
		}
		
		boolean isReg() {
			return regIdx >= 0;
		}
	}
	
	static class Inst {
		Type type;
		List<Param> values;
		String line = "";
		
		Inst(String line) {
			List<String> parts = Arrays.asList(line.split(" "));
			this.line = line;
			this.type = Type.valueOf(parts.get(0).toUpperCase());
			this.values = parts.subList(1, parts.size()).stream().map(Param::new).toList();
		}
		
		long resolve(long[] memory, int idx) {
			Param param = values.get(idx);
			
			if(param.isReg()) {
				return memory[param.regIdx];
			}
			
			return param.num;
		}
		
		void run(long[] memory, Input input) {
			int idx = values.get(0).regIdx;
			
			switch(type) {
				case INP -> memory[idx] = input.read();
				case ADD -> memory[idx] += resolve(memory, 1);
				case MUL -> memory[idx] *= resolve(memory, 1);
				case DIV -> memory[idx] /= resolve(memory, 1);
				case MOD -> memory[idx] %= resolve(memory, 1);
				case EQL -> memory[idx] = (memory[idx] == resolve(memory, 1)) ? 1:0;
			}
		}
	}
	
	static class Stage {
		List<long[]> outputs = new ArrayList<>();
		Input input = new Input();
		
		void process(List<Inst> prog) {
			List<long[]> next = new ArrayList<>();
			
			List<long[]> resolve = new ArrayList<>(outputs);
			if(resolve.isEmpty()) {
				resolve.add(new long[4]);
			}
			
			for(long[] test : resolve) {
				for(int i = 0; i < 9; i++) {
					long[] mem = test.clone();
					
					String val = "%d".formatted(i + 1);
					input.set(val);
					run(mem, prog, input);
					
					next.add(mem);
//					System.out.printf("%s -> %s\n", val, Arrays.toString(mem));
				}
			}
			
			this.outputs = next;
		}
	}
	
	static class Problem {
		final long A, B, C, D;
		
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
			11: add y <c>
			12: mul z y
			13: mul y 0
			14: add y w
			15: add y <d>
			16: mul y x
			17: add z y
			*/
			A = list.get(4).values.get(1).num;
			B = list.get(5).values.get(1).num;
			C = list.get(11).values.get(1).num;
			D = list.get(15).values.get(1).num;
		}
		
		long test(long[] mem) {
			long w = mem[0];
			long x = 0;
			long z = mem[3];
			
			x = (((z % 26) + B) != w) ? 1:0;
			z = (z / A) * (25 * x + C);
			z = z + (w + D) * x;
			
			return z;
		}
		
		long run(long w, long z) {
//			x = (((z % 26) + B) != w) ? 1:0;
//			z = (z / A) * (25 * x + C) + (w + D) * x;
			
			long x = (((z % 26) + B) != w) ? 1:0;
			z = (z / A) * (25 * x + C) + (w + D) * x;
			
			return z;
		}
		
		long test2(long w, long z) {
			
			// Find the equation for (z % 26) == (w - B)
			
			if((z % 26) == w - B) {
				z = (z / A) * C;
			} else {
				z = (z / A) * (25 + C) + (w + D);
			}
			
			return z;
		}
		
		boolean canDivide(long a, long b) {
			return (a % b) == 0;
		}
		
		void find_z_1e(long w, long za, long target, List<Long> list) {
			// We need to find all inputs that equals
			// Branch a is when ((z % 26) + B != w)
			
			// Go trough all possible combinations of z
			long bA = za * A;
			System.out.println("bA: " + bA);

			// We need to go from 0 to 25
			for(int i = -26; i < 26 * A; i++) {
				long t = bA + i;
				long d = (t / A);
				System.out.println("t: " + t + ", d: " + D);
				
				if(d > za) break;
				if((d == za) && ((t % 26) + B != w)) {
					list.add(t);
				}
			}
			
			System.out.println("l: " + list);
		}
		
		void find_z_1(long w, long za, long target, List<Long> list) {
			// We need to find all inputs that equals
			// Branch a is when ((z % 26) + B != w)
			
			// Go trough all possible combinations of z
			long bA = za * A;

			// We need to go from 0 to 25
			for(int i = -26; i < 26 * A; i++) {
				long t = bA + i;
				long d = (t / A);
				
				if(d > za) break;
				if((d == za) && ((t % 26) + B != w)) {
					list.add(t);
				}
			}
		}
		
		void find_z_2(long w, long za, long target, List<Long> list) {
			// We need to find all inputs that equals
			// Branch a is when ((z % 26) + B != w)
			
			// Go trough all possible combinations of z
			long bA = za * A;
			for(int i = -26; i < 26 * A; i++) {
				long t = bA + i;
				long d = (t / A);
				
				if(d > za) break;
				
				if((d == za) && ((t % 26) + B == w)) {
					// We found our z
					list.add(t);
				}
			}
		}
		
		List<Long> backtrace(long w, long target) {
			// W is a number [1, 9]
			// Z is whatever
			
			// Z is the only free variable
//			if((z % 26) + B != w) {
//				z = (z / A) * (25 + C) + (w + D);
//			} else {
//				z = (z / A) * C;
//			}
			
			Long branch_a = null;
			Long branch_b = null;
			
			// Try solve for branch_a
			{
				// First we remove (w + D)
				long bA = target - (w + D);
				// z = (z / A) * (25 + C)
				
				// Then we divide
				if(canDivide(bA, (25 + C))) {
					long bB = bA / (25 + C);
					// z = (z / A)
					branch_a = bB;
				}
			}
			
			// Try solve for branch_b
			{
				if(canDivide(target, C)) {
					long bA = target / C;
					// z = (z / A)
					branch_b = bA;
				}
			}
			
			List<Long> result = new ArrayList<>();
			// branch_a = (z / A)
			if(branch_a != null) {
				// We need to find all inputs that equals
				// Branch a is when ((z % 26) + B != w)
				
				find_z_1(w, branch_a, target, result);
			}
			
			// branch_b = (z / A)
			if(branch_b != null) {
				// We need to find all inputs that equals
				// Branch a is when ((z % 26) + B != w)
				
				find_z_2(w, branch_b, target, result);
			}
			
			return result;
		}
		
		List<Long> naive(long w, long target) {
			List<Long> result = new ArrayList<>();
			for(long i = -0x7fffff; i < 0x7fffff; i += 1L) {
				if(run(w, i) == target) {
					result.add((long)i);
				}
			}
			
			return result;
		}
		
		void backtraceFast(long w, long target, List<Long> list) {
			Long branch_a = null;
			Long branch_b = null;
			
			long bA = target - (w + D);
			if(canDivide(bA, (25 + C))) {
				branch_a = bA / (25 + C);
			}
			
			if(canDivide(target, C)) {
				branch_b = target / C;
			}
			
			if(branch_a != null) {
				find_z_1(w, branch_a, target, list);
			}
			
			if(branch_b != null) {
				find_z_2(w, branch_b, target, list);
			}
			
//			if(list.size() != 1) {
//				for(long check : list) {
//					if(run(w, check) != target) {
//						throw new RuntimeException();
//					}
//				}
//				
//				List<Long> ch = naive(w, target);
//				if(list.size() != ch.size()) {
//					System.out.println("w: " + w);
//					System.out.println("t: " + target);
//					System.out.println("a: " + branch_a);
//					List<Long> test = new ArrayList<>();
//					find_z_1e(w, branch_a, target, test);
//					System.out.println("b: " + branch_b);
//					System.out.println("L: " + list);
//					System.out.println("T: " + test);
//					System.out.println("C: " + ch);
//					throw new RuntimeException();
//				}
//			}
		}
	}
	
	static class Pair {
		String digit;
		long value;
		
		Pair(String digit, long value) {
			this.digit = digit;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "'%s':%d".formatted(digit, value);
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day24");
		List<Inst> insts = lines.stream().map(Inst::new).toList();
		
		Utils.printf("Day 24\n");
		Utils.printf("PartOne: %d\n", partOne(insts));
		Utils.printf("PartTwo: %d\n", partTwo(insts));
	}
	
	private static List<List<Inst>> split(List<Inst> insts) {
		List<List<Inst>> result = new ArrayList<>();
		List<Inst> list = null;
		for(Inst inst : insts) {
			if(inst.type == Type.INP) {
				list = new ArrayList<>();
				result.add(list);
			}
			
			list.add(inst);
		}
		
		return result;
	}
	
	private static long[] run(long[] mem, List<Inst> insts, Input input) {
		for(Inst inst : insts) {
			inst.run(mem, input);
		}
		return mem;
	}
	
//	private static Map<Character, Pair> getPairs2(Problem prog, long target, long roof) {
//		Map<Character, Pair> result = new HashMap<>();
//		
//		int start = (int)(Math.max(0, roof - 0x7ffff));
//		int end = (int)(roof + 0x7ffffff);
//		for(int j = start; j < end; j++) {
//			for(int i = 0; i < 9; i++) {
//				long ret = prog.test2(i + 1, j);
//				
//				if(ret == target) {
//					char digit = (char)('1' + i);
//					result.put(digit, new Pair(Character.toString(digit), j));
//					
//					if(result.size() == 9) {
//						return result;
//					}
//				}
//			}
//		}
//		
//		return result;
//	}
	
//	private static final char[] DIGITS = "987654321".toCharArray();
//	private static boolean generate2(List<Problem> lines, int index, long target, LinkedList<Character> list) {
//		if(index < 0) {
//			return true;
//		}
//
//		// Get this program
//		// Calculate the digit pairs that return the target Z value
//		Problem prog = lines.get(index);
//		Map<Character, Pair> pairs = getPairs2(prog, target, target);
//		
//		boolean found = false;
//		
//		if(index < 100) {
//			System.out.println(list);
//		}
//		
//		// For each digit check the sub digits
//		for(char digit : DIGITS) {
//			Pair pair = pairs.get(digit);
//			if(pair == null) continue;
//			
//			list.addLast(digit);
//			
//			if(generate2(lines, index - 1, pair.value, list)) {
//				// We found the largest solution
//				
//				System.out.println("Found");
//				System.out.println(list);
//				return true;
//			}
//			list.removeLast();
//		}
//		
//		return found;
//	}
	
	private static int lowestIndex = 100;
	private static boolean backtrace(List<Problem> probs, int index, long target, LinkedList<Integer> stack, LinkedList<Long> test) {
		if(index < 0) {
			return target == 0;
		}
		
		test.addLast(target);
		if(index < lowestIndex) {
			lowestIndex = index;
			System.out.println("Lowest: " + lowestIndex);
			System.out.println("Target: " + target);
			System.out.println(String.join("", stack.stream().map(v -> "" + v).toList()));
			System.out.println(test);
		}
		
//		if(index == 0) {
//			System.out.println(stack);
//			System.out.println("TRG: " + target);
//		}
		
		// Get the current problem
		Problem prob = probs.get(index);
		List<Long> list = new ArrayList<>();
		
		// Iterrate from 9 to 1 because we want the highest
		for(int i = 1; i < 10; i++) {
			list.clear();
			// Calculate what values could have given this number
			prob.backtraceFast(i, target, list);
			
//			if(index == 0) {
//				System.out.printf("%d -> %s\n", i, list);
//			}
			
			stack.push(i);
			// Check if we can use the current digit
			for(int j = 0; j < list.size(); j++) {
				long solution = list.get(j);
				if(backtrace(probs, index - 1, solution, stack, test)) {
					// Return from the method
					return true;
				} else {
//					System.out.println("Could not find: " + index);
//					System.out.println("sol: " + solution);
//					System.out.println("w  : " + i);
//					System.out.println("idx: " + index);
//					System.out.println("stc: " + stack);
				}
			}
			
			stack.pop();
		}
		
		test.removeLast();
		
		return false;
	}
	
	private static long testRun(List<Problem> list, long start, String input) {
		final int len = input.length();
		final int str = list.size() - input.length();
		
		// 11961194979999
		long z = start;
		for(int i = 0; i < len; i++) {
			Problem prob = list.get(str + i);
			long w = input.charAt(i) - '0';
			z = prob.run(w, z);
			System.out.println(z + ", w=" + w + ", " + prob.backtrace(w, z));
		}
		
		return z;
	}

	// Day: 1, 14:22 -> 16:38 end
	
	//  19:00 -> 20:24
	public static long partOne(List<Inst> lines) throws Exception {
		List<Problem> parts = split(lines).stream().map(Problem::new).toList();
		
		LinkedList<Long> targets = new LinkedList<>();
		LinkedList<Integer> stack = new LinkedList<>();
		backtrace(parts, 13, 0, stack, targets);
		System.out.println(stack);
		System.out.println(targets);
		
		String str = "";
		for(int i = 0; i < stack.size(); i++) {
			str += stack.get(i);
		}
		
		System.out.println(parts.get(13).backtrace(9, 0));
		System.out.println(parts.get(13).run(9, 9));
		
		
		long test = testRun(parts, 0, str);
		test = testRun(parts, 0, "36969794979199");
		System.out.printf("'%s' -> %d\n", str, test);
		
		
//		LinkedList<Character> test = new LinkedList<>();
//		// Solve
//		generate2(parts, 13, 0, test);
//		Problem problem = parts.get(0);
//		long bw = 0;
//		for(int i = 0; i < 16; i++) {
//			long bz = i;
//			
//			long ta = problem.run(bw, bz);
//			
//			List<Long> result = problem.backtrace(bw, ta);
//			System.out.printf("in : w=%d, z=%d\n", bw, bz);
//			System.out.printf("out: v=%d\n", ta);
//			System.out.printf("rev: %s\n", result);
//		}
		
		// 11419161313147
		return 0;
	}
	
	// 20:47
	public static long partTwo(List<Inst> lines) throws Exception {
		return 0;
	}
}
