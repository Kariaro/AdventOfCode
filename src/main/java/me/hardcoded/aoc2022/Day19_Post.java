package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class Day19_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day19_Post().run();
	}
	
	public Day19_Post() {
		super(2022, 19, Suffix.Post, true);
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
			for (int type = 0; type < 4; type++) {
				maximum[0] = Math.max(maximum[0], sketch.costs[type][0]);
				maximum[1] = Math.max(maximum[1], sketch.costs[type][1]);
				maximum[2] = Math.max(maximum[2], sketch.costs[type][2]);
				maximum[3] = Math.max(maximum[3], sketch.costs[type][3]);
			}
			
			maximum[3] = 1000000;
		}
		
		boolean canRemove(int type, int[] state, int minLeft) {
			int resources = state[type];
			int robots = state[4 + type];
			int maxProduced = resources + robots * minLeft;
			return maxProduced >= maximum[type] * minLeft;
		}
	}
	
	static boolean cannotBuy(Blueprint blueprint, int[] state, int type) {
		int[] cost = blueprint.sketch.costs[type];
		return cost[0] > state[0]
			|| cost[1] > state[1]
			|| cost[2] > state[2]
			|| cost[3] > state[3];
	}
	
	static void buildRobot(Blueprint blueprint, int[] state, int type) {
		int[] cost = blueprint.sketch.costs[type];
		state[4 + type] += 1;
		state[0] -= cost[0];
		state[1] -= cost[1];
		state[2] -= cost[2];
		state[3] -= cost[3];
	}
	
	public long simulateRobots(Blueprint blueprint, int[] parent, int minute) {
		if (minute == 24) {
			return parent[3];
		}
		
		// Each day our stats increase with the amount of robots
		int[] state = parent.clone();
		state[0] += state[4];
		state[1] += state[5];
		state[2] += state[6];
		state[3] += state[7];
		
		long win = simulateRobots(blueprint, state, minute + 1);
		
		// We don't need to wait for space in that case
		for (int i = 0; i < 4; i++) {
			if (parent[4 + i] >= blueprint.maximum[i]
				|| blueprint.canRemove(i, state, 24 - minute)
				|| cannotBuy(blueprint, parent, i)) {
				continue;
			}
			
			int[] wiState = state.clone();
			buildRobot(blueprint, wiState, i);
			win = Math.max(win, simulateRobots(blueprint, wiState, minute + 1));
		}
		
		return win;
	}
	
	public void generateAllStates(Blueprint blueprint, int[] parent, int minute, int depth, Set<int[]> states) {
		if (minute == depth) {
			states.add(parent);
			return;
		}
		
		// Each day our stats increase with the amount of robots
		int[] state = parent.clone();
		state[0] += state[4];
		state[1] += state[5];
		state[2] += state[6];
		state[3] += state[7];
		generateAllStates(blueprint, state, minute + 1, depth, states);
		
		// We don't need to wait for space in that case
		for (int i = 0; i < 4; i++) {
			if (parent[4 + i] >= blueprint.maximum[i]
				|| blueprint.canRemove(i, state, 24 - minute)
				|| cannotBuy(blueprint, parent, i)) {
				continue;
			}
			
			int[] wiState = state.clone();
			buildRobot(blueprint, wiState, i);
			generateAllStates(blueprint, wiState, minute + 1, depth, states);
		}
	}
	
	private long getThreadedRobots(Blueprint blueprint, int depth, int minutes, boolean quiet) throws InterruptedException {
		final int threadedDepth = depth + (24 - minutes);
		int[] state = { 0, 0, 0, 0, 1, 0, 0, 0 };
		
		Set<int[]> set = new HashSet<>();
		generateAllStates(blueprint, state, 0, depth, set);
		
		Collection<Long> out = runThreaded(16, set, item -> simulateRobots(blueprint, item, threadedDepth), quiet);
		return out.stream().mapToLong(i -> i).max().orElse(0);
	}
	
	private <R, T> Collection<R> runThreaded(int noThreads, Collection<T> input, Function<T, R> func, boolean quiet) throws InterruptedException {
		ThreadGroup group = new ThreadGroup("threaded-run");
		Thread[] array = new Thread[noThreads];
		
		ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>(input);
		List<R> result = Collections.synchronizedList(new ArrayList<>());
		
		for (int i = 0; i < noThreads; i++) {
			array[i] = new Thread(group, () -> {
				T item;
				while ((item = queue.poll()) != null) {
					result.add(func.apply(item));
				}
			}, "thread-" + i);
		}
		
		for (int i = 0; i < noThreads; i++) {
			array[i].start();
		}
		
		long start = System.nanoTime();
		int last = -1;
		do {
			int read = input.size() - result.size();
			
			if (last != read && !quiet) {
				last = read;
				long elapsed = System.nanoTime() - start;
				
				double time = read * ((elapsed / 1000000.0) / (double) Math.max(1, input.size() - read));
				System.out.printf(Locale.US, "Time (%14.3f ms) : %5d / %5d\teta %.3f sec\n", elapsed / 1000000.0, read, input.size(), time / 1000.0);
			} else {
				Thread.sleep(100);
			}
		} while (result.size() != input.size());
		
		for (Thread thread : array) {
			thread.join();
		}
		
		return result;
	}
	
	// Solve: 107 min
	public long partOne(List<String> lines) throws Exception {
		List<Blueprint> blueprints = lines.stream().map(line -> line.split("[^0-9]+")).map(Blueprint::new).toList();

		long result = 0;
		for (Blueprint bp : blueprints) {
			long res = getThreadedRobots(bp, 12, 24, true);
			
			System.out.println(res + ", " + bp.id);
			result += res * bp.id;
		}
		
		return result;
	}
	
	// Solve: 135 min + (16.7 hours computation time)
	public long partTwo(List<String> lines) throws Exception {
		List<Blueprint> blueprints = lines.stream().map(line -> line.split("[^0-9]+")).map(Blueprint::new).toList();
		blueprints = blueprints.subList(0, 3);
		
		// 24:  1,  7,  0  (1)
		// 25:  2, 10,  0  (2)
		// 26:  4, 14,  0  (6)
		// 27:  6, 18,  1  (15)
		// 28:  9, 23,  2  (48)
		
		// 2 hours to run this
		long result = 1;
		for (Blueprint bp : blueprints) {
			long res = getThreadedRobots(bp, 12, 32, false);
			
			System.out.println(res + ", " + bp.id);
			result *= res;
		}
		
		return result;
	}
}
