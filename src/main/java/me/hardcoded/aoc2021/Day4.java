package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day4 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day4().run();
	}
	
	public Day4() {
		super(2021, 4);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		int[] random = Arrays.asList(lines.get(0).split(",")).stream().mapToInt(Integer::parseInt).toArray();
		
		List<Board> boards = new ArrayList<>();
		for (int i = 2; i < lines.size(); i += 6) {
			boards.add(new Board(lines.subList(i, i + 5), 5));
		}
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(random, boards));
		Utils.printf("PartTwo: %s\n", partTwo(random, boards));
	}
	
	static class Board {
		final int[] array;
		final int size;
		
		public Board(List<String> board, int size) {
			this.array = Arrays.asList(String.join(" ", board).trim().split("\\s+"))
				.stream().mapToInt(Integer::parseInt).toArray();
			this.size = size;
		}
		
		boolean hasBingo(Set<Integer> set) {
			for (int i = 0; i < size; i++) {
				boolean vertical = true;
				boolean horizontal = true;
				
				for (int j = 0; j < size; j++) {
					vertical &= set.contains(array[i + j * size]);
					horizontal &= set.contains(array[j + i * size]);
				}
				
				if (vertical || horizontal) {
					return true;
				}
			}
			
			return false;
		}
		
		int sumOfUnmarked(Set<Integer> set) {
			int result = 0;
			for (int i : array) {
				if (!set.contains(i)) {
					result += i;
				}
			}
			
			return result;
		}
	}
	
	public int partOne(int[] randomNumbers, List<Board> boards) throws Exception {
		Set<Integer> values = new HashSet<>();
		
		for (int i = 0; i < randomNumbers.length; i++) {
			int number = randomNumbers[i];
			values.add(number);
			
			for (int j = 0; j < boards.size(); j++) {
				Board board = boards.get(j);
				
				if (board.hasBingo(values)) {
					int unmarkedSum = board.sumOfUnmarked(values);
					int winningNumber = number;
					
					return winningNumber * unmarkedSum;
				}
			}
		}
		
		throw new RuntimeException("Failed to find a winning board");
	}
	
	public int partTwo(int[] randomNumbers, List<Board> boards) throws Exception {
		List<Board> boardCopy = new ArrayList<>(boards);
		Set<Integer> values = new HashSet<>();
		
		for (int i = 0; i < randomNumbers.length; i++) {
			int number = randomNumbers[i];
			values.add(number);
			
			for (int j = 0; j < boardCopy.size(); j++) {
				Board board = boardCopy.get(j);
				
				if (board.hasBingo(values)) {
					if (boardCopy.size() == 1) {
						int unmarkedSum = board.sumOfUnmarked(values);
						int winningNumber = number;
						
						return winningNumber * unmarkedSum;
					}
					
					boardCopy.remove(j--);
				}
			}
		}
		
		throw new RuntimeException("Failed to find the last winning board");
	}
}
