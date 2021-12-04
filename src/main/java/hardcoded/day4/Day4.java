package hardcoded.day4;

import java.nio.file.Files;
import java.util.*;

import hardcoded.util.Utils;

public class Day4 {
	static class Board {
		final int[] array;
		final int size;
		
		public Board(List<String> board, int size) {
			this.array = Arrays.asList(String.join(" ", board).trim().split("\\s+"))
				.stream().mapToInt(Integer::parseInt).toArray();
			this.size = size;
		}
		
		boolean hasBingo(Set<Integer> set) {
			for(int i = 0; i < size; i++) {
				boolean vertical = true;
				boolean horizontal = true;
				
				for(int j = 0; j < size; j++) {
					vertical &= set.contains(array[i + j * size]);
					horizontal &= set.contains(array[j + i * size]);
				}
				
				if(vertical || horizontal) {
					return true;
				}
			}
			
			return false;
		}
		
		int sumOfUnmarked(Set<Integer> set) {
			int result = 0;
			for(int i : array) {
				if(!set.contains(i)) {
					result += i;
				}
			}
			
			return result;
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Day 4\n");
		List<String> lines = Files.readAllLines(Utils.of("day4/input"));
		
		int[] random = Arrays.asList(lines.get(0).split(",")).stream().mapToInt(Integer::parseInt).toArray();
		
		List<Board> boards = new ArrayList<>();
		for(int i = 2; i < lines.size(); i += 6) {
			boards.add(new Board(lines.subList(i, i + 5), 5));
		}
		
		System.out.printf("PartOne: %d\n", partOne(random, boards));
		System.out.printf("PartTwo: %d\n", partTwo(random, boards));
	}
	
	public static int partOne(int[] randomNumbers, List<Board> boards) throws Exception {
		Set<Integer> values = new HashSet<>();
		
		for(int i = 0; i < randomNumbers.length; i++) {
			int number = randomNumbers[i];
			values.add(number);
			
			for(int j = 0; j < boards.size(); j++) {
				Board board = boards.get(j);
				
				if(board.hasBingo(values)) {
					int unmarkedSum = board.sumOfUnmarked(values);
					int winningNumber = number;
					
					return winningNumber * unmarkedSum;
				}
			}
		}
		
		throw new RuntimeException("Failed to find a winning board");
	}
	
	public static int partTwo(int[] randomNumbers, List<Board> boards) throws Exception {
		List<Board> boardCopy = new ArrayList<>(boards);
		Set<Integer> values = new HashSet<>();
		
		for(int i = 0; i < randomNumbers.length; i++) {
			int number = randomNumbers[i];
			values.add(number);
			
			for(int j = 0; j < boardCopy.size(); j++) {
				Board board = boardCopy.get(j);
				
				if(board.hasBingo(values)) {
					if(boardCopy.size() == 1) {
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
