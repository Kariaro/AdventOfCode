package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day5 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day5().run();
	}
	
	public Day5() {
		super(2022, 5);
	}
	
	record Command(int count, int from, int to) {}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		List<LinkedList<Character>> boxes = new ArrayList<>();
		List<Command> commands = new ArrayList<>();
		
		int header = 0;
		for (; header < lines.size(); header++) {
			if (lines.get(header).isBlank()) {
				break;
			}
		}
		
		int groups = (lines.get(0).length() + 1) / 4;
		for (int i = 0; i < groups; i++) {
			LinkedList<Character> box = new LinkedList<>();
			boxes.add(box);
			
			for (int j = 0; j < header - 1; j++) {
				int column = 1 + 4 * i;
				char c = lines.get(j).charAt(column);
				
				if (c != ' ') {
					box.add(c);
				}
			}
		}
		
		for (int i = header + 1; i < lines.size(); i++) {
			Matcher matcher = Pattern.compile("move ([0-9]+) from ([0-9]+) to ([0-9]+)").matcher(lines.get(i));
			matcher.find();
			commands.add(new Command(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))));
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(copy(boxes), commands));
		Utils.printf("PartTwo: %s\n", partTwo(copy(boxes), commands));
	}
	
	private List<LinkedList<Character>> copy(List<LinkedList<Character>> list) {
		List<LinkedList<Character>> result = new ArrayList<>();
		for (var item : list) {
			result.add(new LinkedList<>(item));
		}
		
		return result;
	}
	
	// Solve: 17 min
	public String partOne(List<LinkedList<Character>> boxes, List<Command> commands) throws Exception {
		for (Command cmd : commands) {
			for (int i = 0; i < cmd.count; i++) {
				char item = boxes.get(cmd.from - 1).removeFirst();
				boxes.get(cmd.to - 1).addFirst(item);
			}
		}
		
		String result = "";
		for (var item : boxes) {
			result += item.getFirst();
		}
		
		return result;
	}
	
	// Solve: 2 min
	public String partTwo(List<LinkedList<Character>> boxes, List<Command> commands) throws Exception {
		for (Command cmd : commands) {
			List<Character> removed = new ArrayList<>();
			for (int i = 0; i < cmd.count; i++) {
				char item = boxes.get(cmd.from - 1).removeFirst();
				removed.add(item);
			}
			
			for (int i = removed.size() - 1; i >= 0; i--) {
				boxes.get(cmd.to - 1).addFirst(removed.get(i));
			}
		}
		
		String result = "";
		for (var item : boxes) {
			result += item.getFirst();
		}
		
		return result;
	}
}
