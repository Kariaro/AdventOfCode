package me.hardcoded.aoc2022;

import me.hardcoded.util.CircularIterator;
import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day17 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day17().run();
	}
	
	public Day17() {
		super(2022, 17);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class Shape {
		final int[] shape;
		final int w;
		final int h;
		final char n;
		
		Shape(char n, int[] shape, int w) {
			this.n = n;
			this.shape = shape;
			this.w = w;
			this.h = shape.length / w;
		}
	}
	
	public static final Shape MINUS = new Shape('-', new int[] { 1, 1, 1, 1 }, 4);
	public static final Shape PLUS  = new Shape('+', new int[] { 0, 1, 0, 1, 1, 1, 0, 1, 0 }, 3);
	public static final Shape HINGE = new Shape('L', new int[] { 0, 0, 1, 0, 0, 1, 1, 1, 1 }, 3);
	public static final Shape BAR   = new Shape('|', new int[] { 1, 1, 1, 1 }, 1);
	public static final Shape RECT  = new Shape('#', new int[] { 1, 1, 1, 1 }, 2);
	public static final Shape[] SHAPES = { MINUS, PLUS, HINGE, BAR, RECT };
	
	public boolean doesCollide(Set<Long> data, Shape shape, int x, int y) {
		// Always calculated from the bottom right
		for (int i = 0; i < shape.shape.length; i++) {
			int xp = i % shape.w;
			int yp = i / shape.w;
			boolean block = shape.shape[i] != 0;
			
			if (block && data.contains(NumUtils.combine(x + xp, y + shape.h - yp - 1))) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isOutside(Shape shape, int x, int y, int minX, int maxX, int minY, int maxY) {
		// Always calculated from the bottom right
		for (int i = 0; i < shape.shape.length; i++) {
			int xp = i % shape.w;
			int yp = i / shape.w;
			boolean block = shape.shape[i] != 0;
			
			if (block) {
				int shapeX = x + xp;
				int shapeY = y + shape.h - yp - 1;
				
				if (shapeX < minX || shapeX >= maxX || shapeY < minY || shapeY >= maxY) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void appendShape(Set<Long> data, Shape shape, int x, int y) {
		for (int i = 0; i < shape.shape.length; i++) {
			int xp = i % shape.w;
			int yp = i / shape.w;
			boolean block = shape.shape[i] != 0;
			
			if (block) {
				long point = NumUtils.combine(x + xp, y + shape.h - yp - 1);
				data.add(point);
			}
		}
	}
	
	public void debug(Set<Long> data, Shape shape, int x, int y) {
		int highestY = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 4;
		
		Set<Long> testD = new HashSet<>(data);
		appendShape(testD, shape, x, y);
		
		 System.out.println(NumUtils.getSpecificSet(testD, 0, 7, 0, highestY + shape.h, false, true));
		 System.out.println();
	}
	
	public void debugNew(Set<Long> data) {
		int y = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
		System.out.println(NumUtils.getSpecificSet(data, 0, 7, y - 10, y + 1, false, true));
		System.out.println();
	}
	
	public void simulateNew(Set<Long> data, int turnOffset, int count, CircularIterator iter) {
		// How do we find a pattern
		// After SHAPES.length * patternLength we will loop
		// But we need to take care for this to work
		
		// Suppose we find the length we gain by doing X operations
		// We need to find when we repeat those
		
		
		for (int turn = 0; turn < count; turn++) {
			// Pick the shape
			Shape shape = SHAPES[(turn + turnOffset) % SHAPES.length];
			
			// First calculate the start point (3 units above the highest)
			int currentY = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 4;
			int currentX = 2; // left edge is two units away from the left wall
			
			// The shape begins falling
			while (true) {
				char c = iter.next();
				
				// We might do this in the wrong order
				int prevX = currentX;
				
				if (c == '<') {
					currentX--;
				} else {
					currentX++;
				}
				
				boolean reverse = false;
				if (isOutside(shape, currentX, currentY, 0, 7, -1, Integer.MAX_VALUE)
					|| doesCollide(data, shape, currentX, currentY)) {
					// We cannot do this operation
					currentX = prevX;
					reverse = true;
				}
				
				// Now we move down
				currentY--;
				
				boolean collides = doesCollide(data, shape, currentX, currentY);
				
				if (currentY < 0 || collides) {
					if (collides) {
						currentY ++;
					}
					
					if (currentY < 0) {
						// Consume input
						currentY = 0;
					}
					
					// Solidify and update shape
					appendShape(data, shape, currentX, currentY);
					break;
				}
			}
		}
	}
	
	static class Remember {
		Shape shape;
		int index;
		int times;
		
		int turnHeight;
		int turnStart;
		int turnGain;
		int turnEnd;
		
		Remember(Shape shape, int index) {
			this.shape = shape;
			this.index = index;
		}
	}
	
	// Find a point were the simulation repeats
	public Remember findSimulationRepeat(long count, CircularIterator iter) {
		// How do we find a pattern
		// After SHAPES.length * patternLength we will loop
		// But we need to take care for this to work
		
		// Suppose we find the length we gain by doing X operations
		// We need to find when we repeat those
		
		// At some point this pattern will repeat
		// And within that repetition there will be a number X (characters consumed)
		// And a number Y (height gained)
		
		// Simulate until we reach a point were we have already been
		// There will always be a start
		// And then a middle that can be repeated (INF)
		// And then the last part that needs to be manually
		// Computed
		
		Set<Long> data = new HashSet<>();
		
		Map<Shape, Map<Integer, Remember>> remember = new HashMap<>();
		Map<Shape, Set<Integer>> map = new HashMap<>();
		
		Remember selected = null;
		// Find how many turns it takes to complete a cycle
		for (int turn = 0; turn < count; turn++) {
			// Pick the shape
			Shape shape = SHAPES[turn % SHAPES.length];
			
			if (!map.computeIfAbsent(shape, s -> new HashSet<>()).add(iter.index())) {
				// System.out.println("This shape has been seen here before: turn = " + turn + ", " + iter.index() + ", " + shape.n);
				
				Remember rem = remember
					.computeIfAbsent(shape, s -> new HashMap<>())
					.computeIfAbsent(iter.index(), i -> new Remember(shape, iter.index()));
				
				int turnHeight = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
				if (rem == selected || (selected == null && rem.times > 1)) {
					rem.turnEnd = turn;
//					System.out.println(rem.shape.n);
//					System.out.println(rem.index);
//					System.out.println(rem.times);
//					System.out.printf("Start: %d, End: %d\n", rem.turnStart, rem.turnEnd);
					
					selected = rem;
//					debugNew(data);
					
//					int turnsUsed = rem.turnEnd - rem.turnStart;
//					int turnsLeft = (int) (count - rem.turnStart) % turnsUsed;
//					int turnGain = turnHeight - rem.turnHeight;
					
					rem.turnGain = turnHeight - rem.turnHeight;
//					System.out.println("== Steps ==");
//					System.out.println("Simulate until: " + rem.turnStart);
//					System.out.println("Repeat gives height: " + turnGain);
//					System.out.println("Repeat rest: " + turnsLeft);
					
					return selected;
				} else if (selected == null) {
					rem.turnStart = turn;
					rem.times ++;
					rem.turnHeight = turnHeight;
				}
			}
			
			// First calculate the start point (3 units above the highest)
			int currentY = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 4;
			int currentX = 2; // left edge is two units away from the left wall
			
			// The shape begins falling
			while (true) {
				char c = iter.next();
				
				// We might do this in the wrong order
				int prevX = currentX;
				currentX += c == '<' ? -1 : 1;
				
				if (isOutside(shape, currentX, currentY, 0, 7, -1, Integer.MAX_VALUE)
					|| doesCollide(data, shape, currentX, currentY)) {
					// We cannot do this operation
					currentX = prevX;
				}
				
				// Now we move down
				currentY--;
				
				boolean collides = doesCollide(data, shape, currentX, currentY);
				
				if (currentY < 0 || collides) {
					if (collides) {
						currentY ++;
					}
					
					if (currentY < 0) {
						// Consume input
						currentY = 0;
					}
					
					// Solidify and update shape
					appendShape(data, shape, currentX, currentY);
					break;
				}
			}
		}
		
		return null;
	}
	
	public long simulateImprovedNew(long count, CircularIterator iter) {
		// How do we find a pattern
		// After SHAPES.length * patternLength we will loop
		// But we need to take care for this to work
		
		// Suppose we find the length we gain by doing X operations
		// We need to find when we repeat those
		
		// At some point this pattern will repeat
		// And within that repetition there will be a number X (characters consumed)
		// And a number Y (height gained)
		
		// Simulate until we reach a point were we have already been
		
		Remember rem = findSimulationRepeat(count, iter);
		iter.reset();
		
		System.out.println(rem.shape.n);
		System.out.println(rem.index);
		System.out.println(rem.times);
		System.out.printf("Start: %d, End: %d\n", rem.turnStart, rem.turnEnd);
		
		int turnsUsed = rem.turnEnd - rem.turnStart;
		int turnsLeft = (int) ((count - rem.turnStart) % turnsUsed);
		long repeatsPlayed = (count - rem.turnStart) / turnsUsed;
		
		System.out.println("== Steps ==");
		System.out.println("Simulate until: " + rem.turnStart);
		System.out.println("Repeat gives height: " + rem.turnGain);
		System.out.println("Repeat plays: " + repeatsPlayed);
		System.out.println("Repeat rest: " + turnsLeft);
		
		Set<Long> data = new HashSet<>();
		simulateNew(data, 0, rem.turnEnd, iter);
		
		System.out.println("== Debug ==");
		System.out.println("iter.index: " + iter.index());
		
		debugNew(data);
		
		// Lets apply all the repeat stuff
		
		// Keep simulating until we reach the end
		// Find what ID our shape has
		int shapeOffset = 0;
		for (shapeOffset = 0; shapeOffset < SHAPES.length; shapeOffset ++) {
			if (SHAPES[shapeOffset] == rem.shape) {
				break;
			}
		}
		
		int turnHeightBefore = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
		simulateNew(data, shapeOffset, turnsLeft, iter);
		int turnHeightAfter = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
		
		int turnHeightGain = turnHeightAfter - turnHeightBefore;
		
		long height = rem.turnHeight;
		height += rem.turnGain * repeatsPlayed;
		height += turnHeightGain;
		
		System.out.println("From turn: " + (rem.turnStart + repeatsPlayed * turnsUsed + turnsLeft));
		System.out.println("Height   : " + height);
		
		return height;
	}
	
	// Solve: 70 min
	public long partOne(List<String> lines) throws Exception {
		String command = lines.get(0);
		
		Set<Long> data = new HashSet<>();
		// 13:22 not 2768
		//       not 2769
		simulateNew(data, 0, 2022, new CircularIterator(command));
		
		return data.stream().mapToInt(NumUtils::getY).max().orElseThrow() + 1;
	}
	
	// Solve: 68 min
	public long partTwo(List<String> lines) throws Exception {
		// We need to find were the pattern repeats
		String command = lines.get(0);
		return simulateImprovedNew(1000000000000L, new CircularIterator(command));
	}
}
