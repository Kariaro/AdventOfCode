package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day11 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day11().run();
	}
	
	public Day11() {
		super(2022, 11);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		List<Monkey> monkeys = new ArrayList<>();
		ListIterator<String> iter = lines.listIterator();
		while (iter.hasNext()) {
			monkeys.add(new Monkey(iter));
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(monkeys));
		
		monkeys = new ArrayList<>();
		iter = lines.listIterator();
		while (iter.hasNext()) {
			monkeys.add(new Monkey(iter));
		}
		
		Utils.printf("PartTwo: %s\n", partTwo(monkeys));
	}
	
	static class Monkey {
		final List<Long> items;
		final String operation;
		final int divisibilityCheck;
		final int divisibilityTrue;
		final int divisibilityFalse;
		long inspections = 0;
		
		Monkey(ListIterator<String> iter) {
			iter.next();
			items = new ArrayList<>(Arrays.stream(iter.next().substring(18).split(", ")).map(Long::parseLong).toList());
			operation = iter.next().substring(23).replaceAll("old", "o").replaceAll(" ", "");
			divisibilityCheck = Integer.parseInt(iter.next().substring(21));
			divisibilityTrue = Integer.parseInt(iter.next().substring(29));
			divisibilityFalse = Integer.parseInt(iter.next().substring(30));
			
			if (iter.hasNext()) {
				iter.next();
			}
		}
	}
	
	public long performOperation(String opr, long worryLevel) {
		long rightValue = opr.charAt(1) == 'o'
			? worryLevel
			: Integer.parseInt(opr.substring(1));
		
		return switch (opr.charAt(0)) {
			case '+' -> rightValue + worryLevel;
			case '*' -> rightValue * worryLevel;
			default -> throw new RuntimeException("Unknown operation '" + opr + "'");
		};
	}
	
	// Solve: 24 min
	public long partOne(List<Monkey> lines) throws Exception {
		for (int round = 1; round <= 20; round ++) {
			for (Monkey monkey : lines) {
				Iterator<Long> iter = monkey.items.iterator();
				
				while (iter.hasNext()) {
					long nextValue = performOperation(monkey.operation, iter.next()) / 3;
					int nextMonkey = ((nextValue % monkey.divisibilityCheck) == 0) ? monkey.divisibilityTrue : monkey.divisibilityFalse;
					iter.remove();
					lines.get(nextMonkey).items.add(nextValue);
					monkey.inspections++;
				}
			}
		}
		
		List<Long> inspections = new ArrayList<>();
		for (Monkey monkey : lines) {
			inspections.add(monkey.inspections);
		}
		inspections.sort(Comparator.reverseOrder());
		
		return inspections.get(0) * inspections.get(1);
	}
	
	// Solve: 17 min
	public long partTwo(List<Monkey> lines) throws Exception {
		// The number should only check if it's divisible by the following factors
		long factors = 1;
		for (Monkey monkey : lines) {
			factors *= monkey.divisibilityCheck;
		}
		
		for (int round = 1; round <= 10000; round ++) {
			for (Monkey monkey : lines) {
				Iterator<Long> iter = monkey.items.iterator();
				
				while (iter.hasNext()) {
					long nextValue = performOperation(monkey.operation, iter.next()) % factors;
					int nextMonkey = ((nextValue % monkey.divisibilityCheck) == 0) ? monkey.divisibilityTrue : monkey.divisibilityFalse;
					iter.remove();
					lines.get(nextMonkey).items.add(nextValue);
					monkey.inspections++;
				}
			}
		}
		
		List<Long> inspections = new ArrayList<>();
		for (Monkey monkey : lines) {
			inspections.add(monkey.inspections);
		}
		inspections.sort(Comparator.reverseOrder());
		
		return inspections.get(0) * inspections.get(1);
	}
}
