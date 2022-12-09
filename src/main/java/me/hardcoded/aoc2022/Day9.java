package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Day9 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day9().run();
	}
	
	public Day9() {
		super(2022, 9);
	}
	
	public void run() throws Exception {
		List<Command> lines = Utils.readAllLines(this).stream().map(Command::new).toList();
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Command(char dir, int steps) {
		public Command(String line) {
			this(line.charAt(0), Integer.parseInt(line.substring(2)));
		}
	}
	
	public void updatedPoint(Point head, Point tail, Point delta) {
		// Greater than sqrt 2 (diagonal)
		if (tail.distance(head) > 1.5) {
			int dy = head.y - tail.y;
			int dx = head.x - tail.x;
			int ax = Math.abs(dx);
			int ay = Math.abs(dy);
			
			if (ax == 1 && ay == 2) {
				delta.x = dx;
			}
			if (ax == 2 && ay == 1) {
				delta.y = dy;
			}
			if ((ax == 2 && ay == 2) || (ax == 2 && ay == 0) || (ay == 2 && ax == 0)) {
				delta.x = dx / 2;
				delta.y = dy / 2;
			}
			
			tail.x += delta.x;
			tail.y += delta.y;
		} else {
			delta.x = 0;
			delta.y = 0;
		}
	}
	
	// Solve: 18 min
	public int partOne(List<Command> lines) throws Exception {
		Set<Long> set = new HashSet<>();
		Point head = new Point();
		Point tail = new Point();
		Point delta = new Point();
		
		for (Command cmd : lines) {
			for (int i = 0; i < cmd.steps; i++) {
				switch (cmd.dir) {
					case 'U' -> delta.setLocation(0, 1);
					case 'D' -> delta.setLocation(0, -1);
					case 'L' -> delta.setLocation(-1, 0);
					case 'R' -> delta.setLocation(1, 0);
				}
				
				head.x += delta.x;
				head.y += delta.y;
				updatedPoint(head, tail, delta);
				
				set.add(NumUtils.combine(tail.x, tail.y));
			}
		}
		
		return set.size();
	}
	
	// Solve: 42 min
	public int partTwo(List<Command> lines) throws Exception {
		Set<Long> set = new HashSet<>();
		Point delta = new Point();
		
		Point[] knot = new Point[10];
		for (int i = 0; i < knot.length; i++) {
			knot[i] = new Point();
		}
		
		for (Command cmd : lines) {
			for (int i = 0; i < cmd.steps; i++) {
				switch (cmd.dir) {
					case 'U' -> delta.setLocation(0, 1);
					case 'D' -> delta.setLocation(0, -1);
					case 'L' -> delta.setLocation(-1, 0);
					case 'R' -> delta.setLocation(1, 0);
				}
				
				knot[0].x += delta.x;
				knot[0].y += delta.y;
				for (int j = 0; j < knot.length - 1; j++) {
					updatedPoint(knot[j], knot[j + 1], delta);
				}
				
				set.add(NumUtils.combine(knot[knot.length - 1].x, knot[knot.length - 1].y));
			}
		}
		
		return set.size();
	}
}
