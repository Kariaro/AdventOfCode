package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day21 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day21().run();
	}
	
	public Day21() {
		super(2022, 21);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	enum Type {
		Add,
		Div,
		Mul,
		Sub,
		Set
	}
	
	static class Command {
		final String name;
		final String op1;
		final String op2;
		Type type;
		long value;
		
		Command(String line) {
			String[] part = line.split("[: ]+");
			name = part[0];
			if (part.length == 2) {
				op1 = null;
				op2 = null;
				type = Type.Set;
				value = Long.parseLong(part[1]);
			} else {
				op1 = part[1];
				type = switch (part[2]) {
					case "+" -> Type.Add;
					case "-" -> Type.Sub;
					case "*" -> Type.Mul;
					case "/" -> Type.Div;
					default -> throw new RuntimeException("Unknown op '" + part[2] + "'");
				};
				op2 = part[3];
				value = 0;
			}
		}
		
		public String toString(Map<String, Long> map) {
			if (type == Type.Set) {
				return name + ": " + value;
			}
			
			String ts = switch (type) {
				case Add -> " + ";
				case Sub -> " - ";
				case Div -> " / ";
				case Mul -> " * ";
				case Set -> "";
			};
			Long op1t = map.get(op1);
			Long op2t = map.get(op2);
			return name + ": " + (op1t == null ? op1 : op1t) + ts + (op2t == null ? op2 : op2t);
		}
	}
	
	// Solve: 14 min
	public long partOne(List<String> lines) throws Exception {
		Map<String, Long> complete = new HashMap<>();
		LinkedList<Command> commands = new LinkedList<>();
		for (String line : lines) {
			Command cmd = new Command(line);
			if (cmd.type == Type.Set) {
				complete.put(cmd.name, cmd.value);
			} else {
				commands.add(cmd);
			}
		}
		
		// Simulate
		int tries = commands.size() * 100;
		while (tries-- > 0 && !commands.isEmpty()) {
			Command cmd = commands.poll();
			Long a = complete.get(cmd.op1);
			Long b = complete.get(cmd.op2);
			
			if (a != null && b != null) {
				long res = switch (cmd.type) {
					case Add -> a + b;
					case Sub -> a - b;
					case Div -> a / b;
					case Mul -> a * b;
					case Set -> throw new RuntimeException();
				};
				complete.put(cmd.name, res);
			} else {
				commands.add(cmd);
			}
		}
		
		return complete.get("root");
	}
	
	static class Formula {
		final String name;
		final Formula left;
		final Formula right;
		Type type;
		long value;
		
		Formula(long val) {
			name = "?";
			value = val;
			left = null;
			right = null;
			type = Type.Set;
		}
		
		Formula(Command cmd, Map<String, Command> map, Map<String, Long> comp) {
			type = cmd.type;
			if (cmd.type == Type.Set) {
				name = cmd.name;
				value = cmd.value;
				left = null;
				right = null;
			} else {
				name = cmd.name;
				Long a = comp.get(cmd.op1);
				Long b = comp.get(cmd.op2);
				left = a != null
					? new Formula(a)
					: new Formula(map.get(cmd.op1), map, comp);
				right = b != null
					? new Formula(b)
					: new Formula(map.get(cmd.op2), map, comp);
			}
		}
		
		public String toString() {
			if (type == Type.Set) {
				return name + ": " + value;
			}
			
			String ts = switch (type) {
				case Add -> " + ";
				case Sub -> " - ";
				case Div -> " / ";
				case Mul -> " * ";
				case Set -> "";
			};
			
			return "(" + left + ts + right + ")";
		}
	}
	
	// Solve: 40 min
	public long partTwo(List<String> lines) throws Exception {
		LinkedList<Command> commands = new LinkedList<>();
		Map<String, Command> map = new HashMap<>();
		for (String line : lines) {
			Command cmd = new Command(line);
			commands.add(cmd);
			map.put(cmd.name, cmd);
		}
		
		// Fix constants
		Map<String, Long> complete = new HashMap<>();
		Iterator<Command> iter = commands.iterator();
		while (iter.hasNext()) {
			Command cmd = iter.next();
			if (cmd.op1 == null) {
				if (!"humn".equals(cmd.name)) {
					complete.put(cmd.name, cmd.value);
				}
				iter.remove();
			}
		}
		
		// Simulate
		int tries = commands.size() * 100;
		while (tries-- > 0 && !commands.isEmpty()) {
			Command cmd = commands.poll();
			Long a = complete.get(cmd.op1);
			Long b = complete.get(cmd.op2);
			
			if (a != null && b != null) {
				long res = switch (cmd.type) {
					case Add -> a + b;
					case Sub -> a - b;
					case Div -> a / b;
					case Mul -> a * b;
					case Set -> throw new RuntimeException();
				};
				complete.put(cmd.name, res);
				cmd.type = Type.Set;
				cmd.value = res;
			} else {
				commands.add(cmd);
			}
		}
		
		Formula formula = new Formula(map.get("root"), map, complete);
		long target = formula.right.value;
		
		Formula work = formula.left;
		tries = 1000;
		while (tries-- > 0) {
//			System.out.println("Resolve: " + work);
//			System.out.println("== " + target);
//			System.out.println();
			
			if (work.name.equals("humn")) {
				break;
			}
			
			boolean leftSet = work.left.type == Type.Set;
			if (leftSet == work.left.name.equals("humn")) {
				leftSet = false;
			}
			
			Formula form = leftSet ? work.left : work.right;
			switch (work.type) {
				case Add -> target -= form.value;
				case Sub -> {
					if (leftSet) {
						target = form.value - target;
					} else {
						target += form.value;
					}
				}
				case Div -> target *= form.value;
				case Mul -> target /= form.value;
				case Set -> throw new RuntimeException();
			}
			
			work = leftSet ? work.right : work.left;
		}
		
		return target;
	}
}
