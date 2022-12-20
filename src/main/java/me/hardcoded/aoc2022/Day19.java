package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day19 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day19().run();
	}
	
	public Day19() {
		super(2022, 19, Suffix.None, true);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Sketch(int id, int[][] costs) {
		public Sketch(String[] parts) {
			this(
				Integer.parseInt(parts[1]),
				new int[][] {
					{ Integer.parseInt(parts[2]), 0, 0, 0 },
					{ Integer.parseInt(parts[3]), 0, 0, 0 },
					{ Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), 0, 0 },
					{ Integer.parseInt(parts[6]), 0, Integer.parseInt(parts[7]), 0 }
				}
			);
		}
	}
	
	static class Blueprint {
		final Sketch sketch;
		final int id;
		final int[] maximum;
		
		Blueprint(String[] parts) {
			this.sketch = new Sketch(parts);
			this.id = sketch.id;
			this.maximum = new int[4];
			
			// Calculate the max amount of each robot that we need to buy in order to get one new robot each day
			for (Type type : Type.FORWARD) {
				for (Type t : Type.FORWARD) {
					int am = sketch.costs[type.ordinal()][t.ordinal()];
					maximum[t.ordinal()] = Math.max(maximum[t.ordinal()], am);
				}
			}
			
			// Once we have reached this count of resources we do not need to buy that robot again
			// This is the multiple of time left
			
			// At a given robot count we can calculate 
			
			// if we have 7 minutes left and 3 robots
			// in those 7 minutes we can mine (21 ore + resources)
			// if that number is greater than what the most expensive recipe (Of all parts are)
			// Make in the remaining time we do not need to buy that generator
			
			maximum[3] = Integer.MAX_VALUE;
		}
		
		boolean isNeeded(Type type, State state, int minLeft) {
			// Max ore =
			long maxOre = state.getResource(type) + state.getRobots(type) * minLeft;
			return maxOre < (long) maximum[type.ordinal()] * minLeft;
		}
		
		int[] getCost(Type type) {
			return sketch.costs[type.ordinal()];
		}
	}
	
	public enum Type {
		Ore,
		Clay,
		Obsidian,
		Geode;
		
		public static final Type[] FORWARD = { Ore, Clay, Obsidian, Geode };
		public static final Type[] BACKWARDS = { Geode, Obsidian, Clay, Ore };
	}
	
	static class State {
		long[] data;
		long[] robots;
		
		State(State state) {
			this.data = state.data.clone();
			this.robots = state.robots.clone();
		}
		
		State() {
			this.data = new long[4];
			this.robots = new long[4];
		}
		
		void addRobots(Type type, long amount) {
			robots[type.ordinal()] += amount;
		}
		
		long getRobots(Type type) {
			return robots[type.ordinal()];
		}
		
		void addResource(Type type, long amount) {
			data[type.ordinal()] += amount;
		}
		
		void updateResources() {
			data[0] += robots[0];
			data[1] += robots[1];
			data[2] += robots[2];
			data[3] += robots[3];
		}
		
		long getResource(Type type) {
			return data[type.ordinal()];
		}
		
		boolean canBuy(Blueprint blueprint, Type type) {
			int[] cost = blueprint.getCost(type);
			for (Type t : Type.FORWARD) {
				if (cost[t.ordinal()] > getResource(t)) {
					return false;
				}
			}
			
			return true;
		}
		
		void buildRobot(Blueprint blueprint, Type type) {
			addRobots(type, 1);
			
			for (Type t : Type.FORWARD) {
				addResource(t, -blueprint.getCost(type)[t.ordinal()]);
			}
		}
		
		Type getHighestType() {
			if (getRobots(Type.Geode) != 0) return Type.Geode;
			if (getRobots(Type.Obsidian) != 0) return Type.Obsidian;
			if (getRobots(Type.Clay) != 0) return Type.Clay;
			return Type.Ore;
		}
	}
	
	
	static final Map<Integer, State> CUTTING = new HashMap<>();
	
	/*
	// This is similar to day 16
	public State simulateGolems(Blueprint blueprint, State parent, int minute) {
		if (minute >= 24) {
			return parent;
		}
		
		{
//			long minsToBuy = parent.minsToBuy(blueprint, Type.Geode);
//			if (minsToBuy > 0) {
//				int minLeft = 24 - minute;
//				// During the minutes left
//				// How many geodes could be created?
//				
//				// How many days to generate a geode * 2??
//				long total = minLeft / minsToBuy;
//				long count = (total * total + total / 2) * minsToBuy;
//				
//				// Not possible
//				long maxGeodes = CUTTING.computeIfAbsent(minute, i -> 0L);
//				if (count + parent.getResource(Type.Geode) < maxGeodes) {
//					return parent;
//				}
//			}
			
			// If no robot are greater than the CUTTED one
			// we return
			State cut = CUTTING.computeIfAbsent(minute, i -> parent);
			if (cut != parent) {
				boolean good = false;
				for (Type t : Type.FORWARD) {
					// We need to compare resources too
					if (parent.getRobots(t) > cut.getRobots(t)) {
						good = true;
						break;
					}
				}
				
				if (!good) {
					return parent;
				}
			}
			
//			if (minute > 16 && parent.getRobots(Type.Obsidian) == 0) {
//				return parent;
//			}
//			
//			if (minute > 20 && parent.getRobots(Type.Geode) == 0) {
//				return parent;
//			}
		}
		// Because we can only buy one geode a day if someone has less geode 
		// LowerBound = GEODES + (MinutesLeft ^ 2 + MinutesLeft / 2)
		
		State state = new State(parent);
		
		// Each day starts with constructing a robot and ends with it being finished
		
		// Each day our stats increase with the amount of robots
		for (Type type : Type.FORWARD) {
			long robots = state.getRobots(type);
			state.addResource(type, robots);
		}
		
		// Each turn we can only buy one robot
		// We have 4 options
		// Each minute has 5 options (Don't buy, Buy ore/clay/obsidian/geode)
		// We need to think smart about it
		// We want to maximize the ratio in which we can buy geodes
		
		
		// Maximize the ratio for robots we do not have
		
		// For clay we need obsidian
		
		//                   (Ore, Cla, Obs)
		// If geode requires (0, 5, 2)
		// Then we should aim for a ratio of 5/2 clay to obsidian and then pick one random of them
		// But this is not exact this might take some time
		// Always build the highest if possible
		// If we can find the formula this might work
		// Maximize ratio backwards
		
		// We have two states
		// Buy or not buy
		
		// First try without buying
		State woBuy = simulateGolems(blueprint, state, minute + 1);
		State wiBuy = null;
		
		for (Type type : Type.BACKWARDS) {
			// We check if the parent can buy because we are always late
			if (parent.getResource(Type.Geode) > 0 && type == Type.Ore) {
				continue;
			}
			
			// We don't need to buy more
			if (parent.getRobots(type) > blueprint.maximum[type.ordinal()]) {
				continue;
			}
			
			if (!blueprint.isNeeded(type, state, 24 - minute)) {
				continue;
			}
			
			if (parent.canBuy(blueprint, type)) {
				State wiState = new State(state);
				wiState.buildRobot(blueprint, type);
				
				State local = simulateGolems(blueprint, wiState, minute + 1);
				if (wiBuy == null) {
					wiBuy = local;
				} else {
					// Largest
					
					// Calculate ratio
					// When we are able to get geode we should not buy anymore ore
					wiBuy = wiBuy.getResource(Type.Geode) > local.getResource(Type.Geode) ? wiBuy : local;
				}
				
				// Always buy geode
				if (type == Type.Geode) {
					break;
				}
			}
		}
		
		State win = wiBuy == null || woBuy.getResource(Type.Geode) > wiBuy.getResource(Type.Geode)
			? woBuy
			: wiBuy;
		
		if (minute < 5) {
			System.out.println("== Minute " + minute + " == " + Arrays.toString(state.data) + ", " + Arrays.toString(state.robots));
			System.out.println("    == Minute " + minute + " == " + Arrays.toString(win.data) + ", " + Arrays.toString(win.robots));
		}
		
		return win;
	}
	*/
	
	public State simulateRobotsFaster(Blueprint blueprint, State parent, int minute) {
		if (minute == 24) {
			return parent;
		}
		
		{
			State cut = CUTTING.computeIfAbsent(minute, i -> parent);
			if (cut != parent) {
				boolean good = false;
				for (Type t : Type.FORWARD) {
					// We need to compare resources too
					if (parent.getRobots(t) > cut.getRobots(t)) {
						good = true;
						break;
					}
				}
				
				if (!good) {
					return parent;
				}
			}
		}
		
		State state = new State(parent);
		
		// Each day our stats increase with the amount of robots
		for (Type type : Type.FORWARD) {
			state.addResource(type, state.getRobots(type));
		}
		
		// First try without buying
		State woBuy = simulateRobotsFaster(blueprint, state, minute + 1);
		State wiBuy = null;
		
		for (Type type : Type.BACKWARDS) {
			// We check if the parent can buy because we are always late
			if (parent.getResource(Type.Geode) > 0 && type == Type.Ore) {
				continue;
			}
			
			// We don't need to buy more
			if (parent.getRobots(type) > blueprint.maximum[type.ordinal()]) {
				continue;
			}
			
			if (!blueprint.isNeeded(type, state, 24 - minute)) {
				continue;
			}
			
			if (parent.canBuy(blueprint, type)) {
				State wiState = new State(state);
				wiState.buildRobot(blueprint, type);
				
				State local = simulateRobotsFaster(blueprint, wiState, minute + 1);
				if (wiBuy == null) {
					wiBuy = local;
				} else {
					// Largest
					
					// Calculate ratio
					// When we are able to get geode we should not buy anymore ore
					wiBuy = wiBuy.getResource(Type.Geode) > local.getResource(Type.Geode) ? wiBuy : local;
				}
				
				// Always buy geode
				if (type == Type.Geode) {
					break;
				}
			}
		}
		
		State win = wiBuy == null || woBuy.getResource(Type.Geode) > wiBuy.getResource(Type.Geode)
			? woBuy
			: wiBuy;
		
		if (minute < 5) {
			System.out.println("== Minute " + minute + " == " + Arrays.toString(state.data) + ", " + Arrays.toString(state.robots));
			System.out.println("    == Minute " + minute + " == " + Arrays.toString(win.data) + ", " + Arrays.toString(win.robots));
		}
		
		// From win we got this state
		return win;
	}
	
	public State simulateRobotsUltra(Blueprint blueprint, State parent, int minute) {
		if (minute == 24) {
			return parent;
		}
		
		// 1. We do not have any collisions
		//    All states are unique and will not appear again
		
		// TODO: How do I know if a state is better than another state?
//		{
//			// maybe a score?
//			State cut = CUTTING.computeIfAbsent(minute, min -> parent);
//			if (cut != parent) {
//				boolean all = true;
//				boolean some = false;
//				for (Type type : Type.FORWARD) {
//					if (parent.getRobots(type) < cut.getRobots(type)) {
//						all = false;
//					} else {
//						some = true;
//					}
//				}
//				
//				if (all) {
//					CUTTING.put(minute, parent);
//				}
//				
//				if (!some) {
//					return parent;
//				}
//			}
//		}
		// If all resources and robots are greater or robots are equal and resources greater
//		{
//			// maybe a score?
//			State cut = CUTTING.computeIfAbsent(minute, min -> parent);
//			if (cut != parent) {
//				int sign = compare(blueprint, parent, cut);
//
//				if (sign > 0) {
//					CUTTING.put(minute, cut);
//				} else if (sign < 0) {
//					return parent;
//				}
//			}
//		}
		
//		if (minute > 20 && parent.getResource(Type.Geode) == 0) {
//			return parent;
//		}
		
		// Each day our stats increase with the amount of robots
		State state = new State(parent);
		state.updateResources();
		
		// First try without buying (528 extra states)
		// If we can buy geodes we do not do anything else
		State win = simulateRobotsUltra(blueprint, state, minute + 1);
		
		// We don't need to wait for space in that case
		for (Type type : Type.BACKWARDS) {
			// We don't need to buy more
			if (parent.getRobots(type) >= blueprint.maximum[type.ordinal()]) {
				continue;
			}
			
			// We check if the parent can buy because we are always late
			if (parent.getResource(Type.Geode) > 0 && type == Type.Ore) {
				continue;
			}
			
			if (!blueprint.isNeeded(type, state, 24 - minute)) {
				continue;
			}
			
			if (parent.canBuy(blueprint, type)) {
				State wiState = new State(state);
				wiState.buildRobot(blueprint, type);
				
				State local = simulateRobotsUltra(blueprint, wiState, minute + 1);
				win = win != null && win.getResource(Type.Geode) > local.getResource(Type.Geode) ? win : local;
			}
		}
		
//		if (minute < 5) {
//			System.out.println("== Minute " + minute + " == " + Arrays.toString(state.data) + ", " + Arrays.toString(state.robots));
//			System.out.println("    == Minute " + minute + " == " + Arrays.toString(win.data) + ", " + Arrays.toString(win.robots));
//		}
		
		return win;
	}
	
	// There should be a better way of solving this
	// 1 rule is always buy if we can buy geode
	
	// RULE 1: Once we have more ore robots than what it costs to buy anything we don't need to buy any more
	
	
	// New thought
	// Geode    costs (x, 0, y) and when we get our first obsidian we will need to wait at least (y) days
	// Obsidian costs()
	
	// 17:48
	// 19:25 - 701 too low
	// 19:35 - 1023 solve
	// Solve: 107 min
	public long partOne(List<String> lines) throws Exception {
//		List<Blueprint> blueprints = lines.stream().map(line -> line.split("[^0-9]+")).map(Blueprint::new).toList();
//
//		// 24 minutes
//
//		for (Blueprint bp : blueprints) {
//			System.out.println(bp.sketch);
//		}
//
//		// 100150 ms
//		long result = 0;
//		for (Blueprint bp : blueprints) {
//			CUTTING.clear();
//			State state = new State();
//			state.addRobots(Type.Ore, 1);
//			State res = simulateRobotsFaster(bp, state, 0);
//			System.out.println(res.getResource(Type.Geode) + ", " + bp.id);
//			result += res.getResource(Type.Geode) * bp.id;
//		}
//		System.out.println(result);

		return 0L;
	}
	
	// 20:25 - 7990 too low
	// 20:50 - still nothing
	// 21:18 - still nothing
	// 21:34 - looking into pattern but nothing
	// 21:50 - tried 20000 too high
	// Next day took 60181227 ms to compute
	// Solve: 135 min + (16.7 hours computation time)
	public long partTwo(List<String> lines) throws Exception {
		List<Blueprint> blueprints = lines.stream().map(line -> line.split("[^0-9]+")).map(Blueprint::new).toList();
		
		//  0:  1,  7,  0  (6 seconds)
		// -1:  2, 10,  0  (8 seconds)
		// -2:  4, 14,  0  (19)
		// -3:  6, 19,  1  (50)       -- fail 21750   115251
		// -4:  9, 23,  2  (250)      --      60219
		
		// Estimate
		// -5: (600)
		// -6: (1600)
		// -7: (4000)
		// -8: (~10000) seconds
		
		// This is the same almost like a cookie clicker hack or play
		
		// (1), (1), (1, 1), (1, 1) -> 351 - 26
		// (1), (1), (1, 1), (1, 2) -> 325 -  0
		// (1), (1), (1, 1), (1, 3) -> 325 - 25
		// (1), (1), (1, 1), (1, 4) -> 300 - 22
		// (1), (1), (1, 1), (1, 5) -> 278 - 20
		// (1), (1), (1, 1), (1, 6) -> 258 - 18
		// (1), (1), (1, 1), (1, 7) -> 240 - 20
		// (1), (1), (1, 1), (1, 8) -> 220
		
		// PartOne: 0
		// 0.02 ms
		// 
		// 26, 1
		// 52, 2
		// 10, 3
		// PartTwo: 13520
		// 60181227.76 ms
		// 8 hours
		// -8: 26, ?, ? 
		
		
		
//		for (Blueprint bp : blueprints) {
//			CUTTING.clear();
//			State state = new State();
//			state.addRobots(Type.Ore, 1);
//			State res = simulateRobotsUltra(bp, state, -8);
//			System.out.println(res.getResource(Type.Geode) + ", " + bp.id);
//		}
//		
//		System.exit(0);
		
		
		// Dp
		// [32] min
		// [12] obs [20] clay [4] ore [?] geode
		// [100] x [100] x [100] x [100]
		
		
		// This could take up to 6 hours to compute with my current code
		// Lets just make it run
		blueprints = blueprints.subList(0, 3);
		
		long result = 1;
		for (Blueprint bp : blueprints) {
			CUTTING.clear();
			State state = new State();
			state.addRobots(Type.Ore, 1);
			State res = simulateRobotsUltra(bp, state, -8);
			System.out.println(res.getResource(Type.Geode) + ", " + bp.id);
			result *= res.getResource(Type.Geode);
		}
		
		// [4, 11, 3, 1]
		return result;
	}
}
