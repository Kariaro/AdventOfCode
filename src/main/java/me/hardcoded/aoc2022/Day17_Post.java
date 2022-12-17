package me.hardcoded.aoc2022;

import me.hardcoded.util.CircularIterator;
import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day17_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day17_Post().run();
	}
	
	public Day17_Post() {
		super(2022, 17, Suffix.Post);
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
	public static final List<Shape> SHAPES = List.of(MINUS, PLUS, HINGE, BAR, RECT);
	
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
	
	public void simulateNew(Set<Long> data, int turnOffset, int count, CircularIterator iter) {
		for (int turn = 0; turn < count; turn++) {
			// Pick the shape
			Shape shape = SHAPES.get((turn + turnOffset) % SHAPES.size());
			
			// First calculate the start point (3 units above the highest)
			int currentY = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 4;
			int currentX = 2; // left edge is two units away from the left wall
			
			// The shape begins falling
			while (true) {
				char c = iter.next();
				
				// We might do this in the wrong order
				int prevX = currentX;
				currentX += (c == '<' ? -1 : 1);
				
				if (isOutside(shape, currentX, currentY, 0, 7, -1, Integer.MAX_VALUE)
					|| doesCollide(data, shape, currentX, currentY)) {
					currentX = prevX;
				}
				
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
		Set<Long> data = new HashSet<>();
		
		Map<Shape, Map<Integer, Remember>> remember = new HashMap<>();
		Map<Shape, Set<Integer>> map = new HashMap<>();
		
		for (int turn = 0; turn < count; turn++) {
			Shape shape = SHAPES.get(turn % SHAPES.size());
			
			if (!map.computeIfAbsent(shape, s -> new HashSet<>()).add(iter.index())) {
				Remember rem = remember
					.computeIfAbsent(shape, s -> new HashMap<>())
					.computeIfAbsent(iter.index(), i -> new Remember(shape, iter.index()));
				
				int turnHeight = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
				if (rem.times > 1) {
					rem.turnEnd = turn;
					rem.turnGain = turnHeight - rem.turnHeight;
					return rem;
				} else {
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
				int prevX = currentX;
				currentX += c == '<' ? -1 : 1;
				
				if ((currentX < 0 || currentX + shape.w > 7)
				|| doesCollide(data, shape, currentX, currentY)) {
					currentX = prevX;
				}
				
				// Now we move down
				currentY--;
				boolean collides = doesCollide(data, shape, currentX, currentY);
				if (currentY < 0 || collides) {
					if (collides) {
						currentY ++;
					}
					
					currentY = Math.max(0, currentY);
					appendShape(data, shape, currentX, currentY);
					break;
				}
			}
		}
		
		return null;
	}
	
	public long simulateImprovedNew(long count, CircularIterator iter) {
		Remember rem = findSimulationRepeat(count, iter);
		iter.reset();
		
		int turnsUsed = rem.turnEnd - rem.turnStart;
		int turnsLeft = (int) ((count - rem.turnStart) % turnsUsed);
		long repeatsPlayed = (count - rem.turnStart) / turnsUsed;
		
		Set<Long> data = new HashSet<>();
		simulateNew(data, 0, rem.turnEnd, iter);
		
		simulateNew(data, SHAPES.indexOf(rem.shape), turnsLeft, iter);
		int turnHeightAfter = data.stream().mapToInt(NumUtils::getY).max().orElse(-1) + 1;
		int turnHeightGain = turnHeightAfter - (rem.turnHeight + rem.turnGain);
		
		long height = rem.turnHeight;
		height += rem.turnGain * repeatsPlayed;
		height += turnHeightGain;
		
		return height;
	}
	
	// Solve: 70 min
	public long partOne(List<String> lines) throws Exception {
		String command = lines.get(0);
		
		Set<Long> data = new HashSet<>();
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
