package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.Utils;

public class Day23 {
	private static final Map<Character, Long> ENERGY = Map.of('A', 1L, 'B', 10L, 'C', 100L, 'D', 1000L);
	private static final Map<Character, Integer> TARGET = Map.of('A', 2, 'B', 4, 'C', 6, 'D', 8);
	private static final int MAX_DEPTH = 100;
	
	static class Move {
		int src;
		int dst;
		long energy;
		
		Move(int a, int b, long e) {
			this.src = a;
			this.dst = b;
			this.energy = e;
		}
		
		@Override
		public String toString() {
			return "%d -> %d : %d".formatted(src, dst, energy);
		}
	}
	
	static class Hallway {
		private static final Map<String, Long> SEEN = new HashMap<>();
		
		final char[] map;
		final int width;
		final int height;
		long energy;
		
		Hallway(List<String> input) {
			this.height = input.size() - 2;
			this.width = input.get(0).length() - 2;
			this.map = new char[width * height];
			
			for(int y = 0; y < height; y++) {
				String str = input.get(y + 1);
				for(int x = 0; x < width; x++) {
					char c = x + 2 > str.length() ? ' ':str.charAt(x + 1);
					if(c == '#') c = ' ';
					map[x + y * width] = c;
				}
			}
		}
		
		Hallway(Hallway hall) {
			this.height = hall.height;
			this.width = hall.width;
			this.map = new char[width * height];
			this.energy = hall.energy;
			System.arraycopy(hall.map, 0, map, 0, map.length);
		}
		
		boolean isSolved() {
			for(int i = 0; i < height - 1; i++) {
				for(int x = 0; x < 4; x++) {
					int target = 'A' + x;
					int xp = 2 + x * 2;
					if(map[xp + width * (i + 1)] != target) {
						return false;
					}
				}
			}
			
			return true;
		}
		
		String board() {
			return new String(map, 0, map.length);
		}
		
		List<Move> getMoves(int x, int y) {
			int idx = x + y * width;
			char piece = map[idx];

			int target = TARGET.get(piece);
			
			if(y > 0) {
				if(x == target) {
					boolean canStay = true;
					
					// We are already in our target area
					for(int i = y; i < height; i++) {
						// We check from the bottom that they are the correct value
						if(map[x + i * width] != piece) {
							canStay = false;
							break;
						}
					}
					
					if(canStay) {
						return List.of();
					}
				}
				
				for(int i = y - 1; i >= 1; i--) {
					if(map[x + i * width] != '.') {
						// We are blocked at some point and cannot continue
						return List.of();
					}
				}
				
				int start = y + 1;
				
				// We can see the hallway
				// Remember that we can only go to these squares
				//
				// xx.x.x.x.xx
				//   . . . @
				//   . . . .
				List<Move> moves = new ArrayList<>();
				
				long energy = ENERGY.get(piece);
				
				// We will first check left
				for(int i = x - 1, s = start; i >= 0; i--, s++) {
					char p = map[i];
					// If there are any point here that blocks we cannot go there
					if(p != '.') break;
					
					// If the point is not in the room area we can add it
					if((i & 1) == 1 || i == 0) {
						// How many squares did we move?
						// We went up one and then
						moves.add(new Move(idx, i, energy * s));
					}
				}
				
				// Then we check right
				for(int i = x + 1, s = start; i < width; i++, s++) {
					char p = map[i];
					// If there are any point here that blocks we cannot go there
					if(p != '.') break;
					
					// If the point is not in the room area we can add it
					if((i & 1) == 1 || i == width - 1) {
						// How many squares did we move?
						// We went up one and then
						moves.add(new Move(idx, i, energy * s));
					}
				}
				
				return moves;
			}
			
			List<Move> moves = new ArrayList<>();
			long energy = ENERGY.get(piece);
			// ..........@
			//   . . . .
			//   . . . .
			// We can only enter our own stall
			
			// We will first check left
			for(int i = x - 1, s = 2; i >= 0; i--, s++) {
				char p = map[i];
				// If there are any point here that blocks we cannot go there
				if(p != '.') break;
				
				// If the point is not in the room area we can add it
				if(i == target) {
					// Check if we can enter this place
					
					for(int j = height - 1; j >= 1; j--) {
						char block = map[i + j * width];
						if(block == '.') {
							// We have not seen an invalid shape and this is the first empty
							// place. We can more here
							moves.add(new Move(idx, i + width * j, energy * (s + j - 1)));
						} else if(block != piece) {
							// We saw an invalid shape and we cannot move here anymore
							break;
						}
					}
				}
			}
			
			// Then we check right
			for(int i = x + 1, s = 2; i < width; i++, s++) {
				char p = map[i];
				// If there are any point here that blocks we cannot go there
				if(p != '.') break;
				
				// If the point is not in the room area we can add it
				if(i == target) {
					// Check if we can enter this place
					for(int j = height - 1; j >= 1; j--) {
						char block = map[i + j * width];
						if(block == '.') {
							// We have not seen an invalid shape and this is the first empty
							// place. We can more here
							moves.add(new Move(idx, i + width * j, energy * (s + j - 1)));
						} else if(block != piece) {
							// We saw an invalid shape and we cannot move here anymore
							break;
						}
					}
				}
			}
			
			return moves;
		}
		
		List<Move> getMoves() {
			List<Move> result = new ArrayList<>();
			
			// Do not do moves for pieces that are on the correct squares
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					char piece = map[x + y * width];
					
					if(piece != '.' && piece != ' ') {
						result.addAll(getMoves(x, y));
					}
				}
			}
			
			return result;
		}
		
		Hallway play(Move move) {
			Hallway hall = new Hallway(this);
			hall.map[move.dst] = hall.map[move.src];
			hall.map[move.src] = '.';
			hall.energy += move.energy;
			return hall;
		}
		
		long smallestEnergy(int depth) {
			return smallestEnergy(depth, Long.MAX_VALUE);
		}
		
		long smallestEnergy(int depth, long smallest) {
			if(depth < 0) return smallest;
			
			for(Move move : getMoves()) {
				// If the computed hall energy is more than the smallest we skip it
				if(energy + move.energy > smallest) {
					continue;
				}
				
				Hallway hall = play(move);
				
				String hash = hall.board();
				long bestEnergy = SEEN.computeIfAbsent(hash, v -> Long.MAX_VALUE);
				if(bestEnergy > hall.energy) {
					// If we have reached this state and that state had a longer path
					// then we want to recompute it to get the smallest path possible
					SEEN.put(hash, hall.energy);
				} else {
					// If this board has already been played we do not try it again
					continue;
				}
				
				// Check if we solved the sorting
				if(hall.isSolved()) {
					if(hall.energy < smallest) {
						System.out.printf("depth: %d, small=%d\n", MAX_DEPTH - depth, hall.energy);
					}
					smallest = Math.min(hall.energy, smallest);
				} else {
					long small = hall.smallestEnergy(depth - 1, smallest);
					smallest = Math.min(small, smallest);
				}
			}
			
			return smallest;
		}
	}
	
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day23");
		
		Utils.printf("Day 23\n");
		Utils.printf("PartOne: %d\n", partOne(lines));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	// Solve: 86 min
	public static long partOne(List<String> lines) throws Exception {
		Hallway hall = new Hallway(lines);
		return hall.smallestEnergy(MAX_DEPTH);
	}
	
	// Solve: 27 min
	public static long partTwo(List<String> lines) throws Exception {
		lines.add(3, "  #D#C#B#A#");
		lines.add(4, "  #D#B#A#C#");
		
		Hallway hall = new Hallway(lines);
		return hall.smallestEnergy(MAX_DEPTH);
	}
}
