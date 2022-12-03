package me.hardcoded.aoc2021;

import java.util.*;

import me.hardcoded.util.Utils;

public class Day21 {
	public static void main(String[] args) throws Exception {
		List<String> lines = Utils.readAllLines(2021, "day21");
		
		Utils.printf("Day 21\n");
		Utils.printf("PartOne: %d\n", partOne(lines));
		Utils.printf("PartTwo: %d\n", partTwo(lines));
	}
	
	private static final long[] AMOUNT = { 1, 3, 6, 7, 6, 3, 1 };
	private static final int SCORE_LENGTH = 31;
	private static final int SCORE_SEARCH = 21;
	private static final int PLAYER_LENGTH = 10;
	private static final int PLAYABLED = SCORE_SEARCH * PLAYER_LENGTH;
	private static final int DIMENSION = SCORE_LENGTH * PLAYER_LENGTH;
	
	// Solve: 17 min
	public static long partOne(List<String> lines) throws Exception {
		int player1 = Integer.parseInt(lines.get(0).substring(28));
		int player2 = Integer.parseInt(lines.get(1).substring(28));
		
		int score1 = 0;
		int score2 = 0;
		int die = 1;
		int dies = 0;
		
		while(true) {
			dies = 3 + ((die - 1) % 100) + (die % 100) + ((die + 1) % 100);
			die += 3;
			score1 += (((player1 += dies) - 1) % 10) + 1;
			if(score1 >= 1000) break;
			
			dies = 3 + ((die - 1) % 100) + (die % 100) + ((die + 1) % 100);
			die += 3;
			score2 += (((player2 += dies) - 1) % 10) + 1;
			if(score2 >= 1000) break;
		}
		
		return Math.min(score1, score2) * (die - 1);
	}
	
	// Solve: 62 min
	public static long partTwo(List<String> lines) throws Exception {
		int player1 = Integer.parseInt(lines.get(0).substring(28)) - 1;
		int player2 = Integer.parseInt(lines.get(1).substring(28)) - 1;
		
		long[][] states = new long[DIMENSION][DIMENSION];
		states[player1][player2] = 1;
		// We have a two dimensional state array representing the values
		// states[Player1 + score1 * PLAYER_LENGTH][Player2 + score2 * PLAYER_LENGTH] yeilds the current universes
		// If any of the scores are greater than 21 we do not process them
		// We keep doing this process until there are no more actions to be done
		
		boolean playerOne = true;
		boolean moreValues = true;
		while(moreValues) {
			long[][] next = new long[DIMENSION][DIMENSION];
			
			// Copy all winning games
			for(int idx1 = 0; idx1 < DIMENSION; idx1++) {
				for(int idx2 = 0; idx2 < DIMENSION; idx2++) {
					int score1 = idx1 / 10;
					int score2 = idx2 / 10;
					
					// Get the universes with these scores
					if(score1 >= SCORE_SEARCH || score2 >= SCORE_SEARCH) {
						next[idx1][idx2] += states[idx1][idx2];
					}
				}
			}
			
			if(playerOne) {
				for(int idx1 = 0; idx1 < PLAYABLED; idx1++) {
					int score1 = idx1 / 10;
					for(int idx2 = 0; idx2 < PLAYABLED; idx2++) {
						// Get the universes with these scores
						long universes = states[idx1][idx2];
						
						if(universes > 0) {
							for(int i = 0; i < 7; i++) {
								// Calculate the next player 1 position from the values
								// And calculate the next score value for the player and
								// Add the amount of universes times the amount that universe occurs
								int nextPlayer1 = (int)((idx1 + i + 3) % 10);
								int nextScore1 = score1 + nextPlayer1 + 1;
								next[nextPlayer1 + nextScore1 * PLAYER_LENGTH][idx2] += universes * AMOUNT[i];
							}
						}
					}
				}
			} else {
				for(int idx2 = 0; idx2 < PLAYABLED; idx2++) {
					int score2 = idx2 / 10;
					for(int idx1 = 0; idx1 < PLAYABLED; idx1++) {
						// Get the universes with these scores
						long universes = states[idx1][idx2];
						
						if(universes > 0) {
							for(int i = 0; i < 7; i++) {
								int nextPlayer2 = (int)((idx2 + i + 3) % 10);
								int nextScore2 = score2 + nextPlayer2 + 1;
								next[idx1][nextPlayer2 + nextScore2 * PLAYER_LENGTH] += universes * AMOUNT[i];
							}
						}
					}
				}
			}
			
			// Update parent states
			for(int i = 0; i < next.length; i++) {
				states[i] = next[i];
			}
			
			{
				moreValues = false;
				
				// Check if we have any more values
				beginLoop:
				for(int idx1 = 0; idx1 < PLAYABLED; idx1++) {
					for(int idx2 = 0; idx2 < PLAYABLED; idx2++) {
						if(states[idx1][idx2] > 0) {
							moreValues = true;
							break beginLoop;
						}
					}
				}
			}
			
			// Flip the player
			playerOne = !playerOne;
		}
		
		// Outside the loop we count all the player 1 wins
		long playerWins1 = 0;
		long playerWins2 = 0;
		
		for(int idx1 = 0; idx1 < DIMENSION; idx1++) {
			for(int idx2 = 0; idx2 < DIMENSION; idx2++) {
				int score1 = idx1 / 10;
				int score2 = idx2 / 10;
				
				long universes = states[idx1][idx2];
				
				// Get the universes with these scores
				if(score1 >= SCORE_SEARCH) {
					playerWins1 += universes;
				}
				
				if(score2 >= SCORE_SEARCH) {
					playerWins2 += universes;
				}
			}
		}
		
		return Math.max(playerWins1, playerWins2);
	}
}
