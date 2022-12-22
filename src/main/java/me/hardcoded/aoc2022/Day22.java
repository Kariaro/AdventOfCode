package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.NumUtils;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.List;
import java.util.stream.LongStream;

public class Day22 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day22().run();
	}
	
	public Day22() {
		super(2022, 22);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class World {
		Map<Long, Position> moves = new LinkedHashMap<>();
		Set<Long> points = new LinkedHashSet<>();
		Set<Long> inside = new LinkedHashSet<>();
		long firstIndex = Long.MIN_VALUE;
		
		Position getPosition(int x, int y) {
			return moves.get(NumUtils.combine(x, y));
		}
	}
	
	static class Position {
		// Position of this position
		final int x, y;
		
		// Max steps in each direction. A negative value indicates an infinite loop
		final int[] steps = { 0, 0, 0, 0 };
		
		// How many tiles before a wrap. A negative value means there are no wraps
		final int[] wrap = { -1, -1, -1, -1 };
		
		// How large the world is in each direction. ( (minX, maxX), (minY, maxY) )
		final int[][] range = { { 0, 0 }, { 0, 0 } };
		
		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public long findWrap(Set<Long> inside, int x, int y, int dx, int dy) {
		OptionalLong value;
		if (dy == 0) {
			LongStream sameRow = inside.stream().filter(i -> NumUtils.getY(i) == y).mapToLong(NumUtils::getX);
			value = dx < 0 ? sameRow.max() : sameRow.min();
			return NumUtils.combine((int) value.orElseThrow(), y);
		} else {
			LongStream sameCol = inside.stream().filter(i -> NumUtils.getX(i) == x).mapToLong(NumUtils::getY);
			value = dy < 0 ? sameCol.max() : sameCol.min();
			return NumUtils.combine(x, (int) value.orElseThrow());
		}
	}
	
	static final int[][] DELTA = {
		{  1,  0 }, // Right
		{  0,  1 }, // Down
		{ -1,  0 }, // Left
		{  0, -1 }, // Up
	};
	
	public World constructMap(List<String> lines, boolean partOne) {
		World world = new World();
		
		int row = 0;
		for (String line : lines) {
			if (line.isEmpty()) {
				break;
			}
			
			int startColumn = line.lastIndexOf(' ') + 1;
			for (int i = startColumn; i < line.length(); i++) {
				long pos = NumUtils.combine(i, row);
				if (line.charAt(i) != '#') {
					if (world.firstIndex == Long.MIN_VALUE) {
						world.firstIndex = i;
					}
					
					world.points.add(pos);
				}
				
				world.inside.add(pos);
			}
			
			row++;
		}
		
		if (partOne) {
			for (long pos : world.points) {
				int x = NumUtils.getX(pos);
				int y = NumUtils.getY(pos);
				
				Position position = new Position(x, y);
				world.moves.put(pos, position);
				
				// Calculate how far up we can go
				// Outside wraps
				for (int i = 0; i < 4; i++) {
					int dx = DELTA[i][0];
					int dy = DELTA[i][1];
					int px = x;
					int py = y;
					
					// Set the ranges of the position
					{
						long wr = findWrap(world.inside, x, y, -dx, -dy);
						position.range[i & 1][(i >> 1) ^ 1] = (i & 1) == 0 ? NumUtils.getX(wr) : NumUtils.getY(wr);
					}
					
					int tries = 1000;
					int times = 0;
					while (tries-- > 0) {
						px += dx;
						py += dy;
						long playerPos = NumUtils.combine(px, py);
						if (!world.inside.contains(playerPos)) {
							// We are now outside
							long wrapTo = findWrap(world.inside, px, py, dx, dy);
							if (!world.points.contains(wrapTo)) {
								// We hit a wall. Write the move value
								position.steps[i] = times;
								position.wrap[i] = -1;
								break;
							}
							
							position.wrap[i] = times + 1;
							times--;
							px = NumUtils.getX(wrapTo) - dx;
							py = NumUtils.getY(wrapTo) - dy;
						} else if (!world.points.contains(playerPos)) {
							// We hit a wall. Write the move value
							position.steps[i] = times;
							break;
						}
						
						if (playerPos == pos) {
							position.steps[i] = -1;
							break;
						}
						
						times++;
					}
				}
			}
		}
		
		// System.out.println(NumUtils.getSpecificSet(walkablePoints, 0, 150, 0, 200, false, false));
		// System.out.println("FirstIndex: " + world.firstIndex);
		
		return world;
	}
	
	// Solve: 111 min
	public long partOne(List<String> lines) throws Exception {
		// First tile left most top
		// Facing right

		// If a tile takes you off you wrap to the opposite tile
		// 1. Make a list of opposite tiles

		World world = constructMap(lines, true);

		String data = lines.get(lines.size() - 1);
		System.out.println(data);

		String[] parts = data.replaceAll("([A-Z])", " $0 ").split("[ ]+", 0);
		System.out.println(Arrays.toString(parts));

		// Right, Down, Left, Up
		int x = NumUtils.getX(world.firstIndex);
		int y = NumUtils.getY(world.firstIndex);
		System.out.println(x + ", " + y);
		int dir = 0;
		for (String part : parts) {
			if (Character.isLetter(part.charAt(0))) {
				// Turn 90 degrees
				switch (part.charAt(0)) {
					case 'R' -> dir = (dir + 1) & 3;
					case 'L' -> dir = (dir - 1) & 3;
				}

				continue;
			}

			int dx = DELTA[dir][0];
			int dy = DELTA[dir][1];
			int input = Integer.parseInt(part);
			int tiles = Integer.parseInt(part);

			Position position = world.getPosition(x, y);
			if (position.wrap[dir] != -1 && tiles >= position.wrap[dir]) {
				// First remove
				tiles -= position.wrap[dir];

				// Then check if it's infinite
				if (position.steps[dir] == -1) {
					// Infinite steps
					if (dy == 0) {
						int size = position.range[0][1] - position.range[0][0];
						if (dx > 0) {
							x = position.range[0][0] + (tiles % size);
						} else {
							x = position.range[0][1] - (tiles % size);
						}
					} else {
						int size = position.range[1][1] - position.range[1][0];
						if (dy > 0) {
							y = position.range[1][0] + (tiles % size);
						} else {
							y = position.range[1][1] - (tiles % size);
						}
					}

//					System.out.println(x + ", " + y);
//					System.out.println(dx + ", " + dy);
//					System.out.println(position.steps[dir]);
//					System.out.println(position.wrap[dir]);
//					System.out.println(tiles);
//					System.out.println(Arrays.deepToString(position.range));
//					throw new RuntimeException("Handle");
				} else {
					int wrappedSteps = position.steps[dir] - position.wrap[dir];
					// Collide with walls
					tiles = Math.min(tiles, wrappedSteps);

					if (dy == 0) {
						if (dx > 0) {
							x = position.range[0][0] + tiles;
						} else {
							x = position.range[0][1] - tiles;
						}
					} else {
						if (dy > 0) {
							y = position.range[1][0] + tiles;
						} else {
							y = position.range[1][1] - tiles;
						}
					}
				}

//				System.out.println(x + ", " + y);
//				System.out.println(dx + ", " + dy);
//				System.out.println(position.steps[dir]);
//				System.out.println(position.wrap[dir]);
//				System.out.println(tiles);
//				System.out.println(Arrays.deepToString(position.range));
//				throw new RuntimeException("Fix wrap");
			} else {
				// Collision
				tiles = Math.min(tiles, position.steps[dir]);

				x += dx * tiles;
				y += dy * tiles;
			}

			System.out.printf("%-3d: %d, %d\n", input, x, y);
		}

		return 1000L * (y + 1) + 4L * (x + 1) + dir;
	}
	
	public World constructMap2(List<String> lines) {
		World world = new World();
		
		int row = 0;
		for (String line : lines) {
			if (line.isEmpty()) {
				break;
			}
			
			int startColumn = line.lastIndexOf(' ') + 1;
			for (int i = startColumn; i < line.length(); i++) {
				long pos = NumUtils.combine(i, row);
				if (line.charAt(i) != '#') {
					if (world.firstIndex == Long.MIN_VALUE) {
						world.firstIndex = i;
					}
					
					world.points.add(pos);
				}
				
				world.inside.add(pos);
			}
			
			row++;
		}
		
		return world;
	}
	
	record ASDFASDF(int x, int y, int dir) {};
	
	public ASDFASDF getPlace(int x, int y, int dir) {
		int dx = DELTA[dir][0];
		int dy = DELTA[dir][1];
		
		System.out.println("pos = " + x + ", " + y);
		System.out.println("delta = " + dx + ", " + dy);
		System.out.println("dir = " + dir);
		
		// Put all pieces in this formation
		//     + - +
		//     | 1 |
		// + - + - + - +
		// | 2 | 3 | 4 |
		// + - + - + - +
		//     | 5 |
		//     + - +
		//     | 6 |
		//     + - +
		
		int nx = x % 50;
		int ny = y % 50;
		boolean upd = false;
		
		final int RIGHT_0 = 0;
		final int DOWN_1 = 1;
		final int LEFT_2 = 2;
		final int UP_3 = 3;
		int pos = (x / 50) + (y / 50) * 3;
		
		int[] layout = {
			0, 1, 4,
			0, 3, 0,
			2, 5, 0,
			6, 0, 0
		};
		
		System.out.println("layout = " + layout[pos]);
		
		switch (layout[pos]) {
			case 1 -> {
				switch (dir) {
					case LEFT_2 -> {
						// [1] tl -> bl
						// [2] bl -> tl
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 0;
						y = 100 + (49 - ny);
						dir = RIGHT_0;
						upd = true;
					}
					case UP_3 -> {
						// [1] tl -> tr
						// [6] tl -> bl
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 0;
						y = 150 + nx;
						dir = RIGHT_0;
						upd = true;
					}
				}
			}
			case 2 -> {
				switch (dir) {
					case LEFT_2 -> {
						// [2] tl -> bl
						// [1] bl -> tl
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50;
						y = (49 - ny);
						dir = RIGHT_0;
						upd = true;
					}
					case UP_3 -> {
						// [2] tl -> tr
						// [3] tl -> bl
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50;
						y = 50 + nx;
						dir = RIGHT_0;
						upd = true;
					}
				}
			}
			case 3 -> {
				switch (dir) {
					case RIGHT_0 -> {
						// [3] tr -> br
						// [4] bl -> br
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 100 + ny;
						y = 49;
						dir = UP_3;
						upd = true;
					}
					case LEFT_2 -> {
						// [3] tl -> bl
						// [2] tl -> tr
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = ny;
						y = 100;
						dir = DOWN_1;
						upd = true;
					}
				}
			}
			case 4 -> {
				switch (dir) {
					case RIGHT_0 -> {
						// [4] tr -> br
						// [5] br -> tr
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50 + 49;
						y = 100 + (49 - ny);
						dir = LEFT_2;
						upd = true;
					}
					case DOWN_1 -> {
						// [4] bl -> br
						// [3] tr -> br
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50 + (49);
						y = 50 + nx;
						dir = LEFT_2;
						upd = true;
					}  /// CHECK
					case UP_3 -> {
						// [4] tl -> tr
						// [6] bl -> br
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = nx;
						y = 150 + 49;
						dir = UP_3;
						upd = true;
					}
				}
			}
			case 5 -> {
				switch (dir) {
					case RIGHT_0 -> {
						// [5] tr -> br
						// [4] br -> tr
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 100 + (49);
						y = 49 - ny;
						dir = LEFT_2;
						upd = true;
					}
					case DOWN_1 -> {
						// [5] bl -> br
						// [6] tr -> br
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 49;
						y = 150 + nx;
						dir = LEFT_2;
						upd = true;
					}
				}
			}
			case 6 -> {
				switch (dir) {
					case RIGHT_0 -> {
						// [6] tr -> br
						// [5] bl -> br
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50 + ny;
						y = 100 + (49);
						dir = UP_3;
						upd = true;
					}
					case DOWN_1 -> {
						// [6] bl -> br
						// [4] tl -> tr
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 100 + nx;
						y = 0;
						dir = DOWN_1;
						upd = true;
					}
					case LEFT_2 -> {
						// [6] tl -> bl
						// [1] tl -> tr
						
						// .##   .14
						// .#.   .3.
						// ##.   25.
						// #..   6..
						
						x = 50 + ny;
						y = 0;
						dir = DOWN_1;
						upd = true;
					}
				}
			}
		}
		
		// .##   .14
		// .#.   .3.
		// ##.   25.
		// #..   6..
		
		if (!upd) {
			System.out.println(x + ", " + y);
			throw new RuntimeException("Not updated " + layout[pos] + ", " + dir);
		}
		
		return new ASDFASDF(x, y, dir);
	}
	
	public static final String[] DIR_NAME = { "Right", "Down", "Left", "Up" };
	
	// Solve: 149 min
	public long partTwo(List<String> lines) throws Exception {
		// First tile left most top
		// Facing right
		
		// If a tile takes you off you wrap to the opposite tile
		// 1. Make a list of opposite tiles
		
		World world = constructMap2(lines);
		
		String data = lines.get(lines.size() - 1);
		String[] parts = data.replaceAll("([A-Z])", " $0 ").split("[ ]+", 0);
		
		// Right, Down, Left, Up
		int x = NumUtils.getX(world.firstIndex);
		int y = NumUtils.getY(world.firstIndex);
		//		System.out.println(x + ", " + y);
		
		//		WorldPart[] worldParts = createWorldParts(lines);
		//		draw(worldParts);
		
		// Have 4 rotations of each world
		
		// All pieces are equal in size
		//		long max = world.inside.stream().reduce(0L, (a, b) -> {
		//			int ax = NumUtils.getX(a);
		//			int ay = NumUtils.getY(a);
		//			int bx = NumUtils.getX(b);
		//			int by = NumUtils.getY(b);
		//			return NumUtils.combine(Math.max(ax, bx), Math.max(ay, by));
		//		});
		
		//		int[][] layout = {
		//			{ 0, 1, 4 },
		//			{ 0, 3, 0 },
		//			{ 2, 5, 0 },
		//			{ 6, 0, 0 }
		//		};
		
		// System.out.println(NumUtils.getX(max) + ", " + NumUtils.getY(max));
		
		//		int wor = 0;
		int dir = 0;
		for (String part : parts) {
			if (Character.isLetter(part.charAt(0))) {
				// Turn 90 degrees
				switch (part.charAt(0)) {
					case 'R' -> dir = (dir + 1) & 3;
					case 'L' -> dir = (dir - 1) & 3;
				}
				
				continue;
			}
			//			
			//			WorldPart current = worldParts[wor];
			
			int dx = DELTA[dir][0];
			int dy = DELTA[dir][1];
			int input = Integer.parseInt(part);
			int times = input;
			System.out.printf(">>> %-3d: %d, %d    %d\n", input, x, y, dir);
			
			int steps;
			for (steps = 0; steps < times; steps++) {
				long pos = NumUtils.combine(x + dx, y + dy);
				
				if (!world.inside.contains(pos)) {
					System.out.println("-".repeat(100));
					ASDFASDF val = getPlace(x, y, dir);
					
					long valpos = NumUtils.combine(val.x, val.y);
					System.out.println("valpos: " + NumUtils.getX(valpos) + ", " + NumUtils.getY(valpos));
					if (!world.points.contains(valpos)) {
						// Collided with a wall
						break;
					}
					
					x = val.x;
					y = val.y;
					dir = val.dir;
					dx = DELTA[dir][0];
					dy = DELTA[dir][1];
					
					System.out.printf("   %-3d: %d, %d    %d\n", input, x, y, dir);
					// throw new RuntimeException("Figure out wrapping");
				} else if (!world.points.contains(pos)) {
					// Collided with a wall
					break;
				} else {
					x += dx;
					y += dy;
				}
			}
			
			System.out.printf("%-3d: %d, %d    %s\n", input, x, y, DIR_NAME[dir]);
			
			// Set<Long> data2 = new HashSet<>(world.points);
			// data2.add(NumUtils.combine(x, y));
			// System.out.println(NumUtils.getSetInverse(data2, 0, 150, 0, 200, NumUtils.combine(x, y)));
			// System.in.read();
		}
		
		return 1000L * (y + 1) + 4L * (x + 1) + dir;
	}
}
