package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.List;

public class Day2 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day2().run();
	}
	
	public Day2() {
		super(2022, 2);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	public enum Out {
		Lose,
		Draw,
		Win
	}
	
	public enum RPS {
		Rock,
		Paper,
		Scissors;
		
		public Out getResult(RPS other) {
			if (other == this) {
				return Out.Draw;
			}
			
			return switch (this) {
				case Rock -> other == Paper;
				case Paper -> other == Scissors;
				case Scissors -> other == Rock;
			} ? Out.Lose : Out.Win;
		}
		
		public RPS getMove(Out outcome) {
			return switch (outcome) {
				case Draw -> this;
				case Win -> switch (this) {
					case Rock -> Paper;
					case Paper -> Scissors;
					case Scissors -> Rock;
				};
				case Lose -> switch (this) {
					case Rock -> Scissors;
					case Paper -> Rock;
					case Scissors -> Paper;
				};
			};
		}
	}
	
	// Solve: 10 min
	public int partOne(List<String> lines) throws Exception {
		int points = 0;
		for (String line : lines) {
			char player = line.charAt(0);
			char move = line.charAt(2);
			
			// Win or loose
			RPS op = RPS.values()[player - 'A'];
			RPS mv = RPS.values()[move - 'X'];
			
			Out result = mv.getResult(op);
			
			int movePoint = switch (mv) {
				case Rock -> 1;
				case Paper -> 2;
				case Scissors -> 3;
			};
			
			int resultPoint = switch (result) {
				case Lose -> 0;
				case Win -> 6;
				case Draw -> 3;
			};
			
			points += movePoint + resultPoint;
		}
		
		return points;
	}
	
	// Solve: 5 min
	public int partTwo(List<String> lines) throws Exception {
		int points = 0;
		for (String line : lines) {
			char player = line.charAt(0);
			char move = line.charAt(2);
			
			// Win or loose
			RPS op = RPS.values()[player - 'A'];
			Out result = Out.values()[move - 'X'];
			RPS mv = op.getMove(result);
			
			int movePoint = switch (mv) {
				case Rock -> 1;
				case Paper -> 2;
				case Scissors -> 3;
			};
			
			int resultPoint = switch (result) {
				case Lose -> 0;
				case Win -> 6;
				case Draw -> 3;
			};
			
			points += movePoint + resultPoint;
		}
		
		return points;
	}
}
