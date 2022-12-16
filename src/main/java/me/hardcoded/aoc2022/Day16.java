package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Dijkstra;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day16 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day16().run();
	}
	
	public Day16() {
		super(2022, 16);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class Valve {
		final String name;
		final int flow;
		final Map<String, Integer> valves;
		
		public Valve(String[] parts) {
			name = parts[0];
			flow = Integer.parseInt(parts[1]);
			valves = new LinkedHashMap<>();
			for (String name : parts[2].split(", ")) {
				valves.put(name, 1);
			}
		}
		
		public String toString() {
			return name + " > " + valves.keySet();
		}
	}
	
	public long recursive(Map<String, Valve> valves, Valve valve, Set<Valve> openValves, long valvePressure, int minute) {
		if (minute > 30) {
			return valvePressure;
		}
		
		// System.out.println("== Minute " + minute + " ==");
		
		if (minute < 15) {
			System.out.println("== Minute " + minute + " ==" + valvePressure);
		}
		
		// How many steps does it take to get to a valve
		// Find the optimal path in steps (1 cost) that when multiplied by its flow
		// Gives the greatest value
		
		
		// Calculate all paths through the network
		// Open them for each bit and then check O(2^N * N^3)
		
		
		
		// a
		
		/*
		if (pressure == 0) {
			System.out.println("No valves are open.");
		} else {
			System.out.println("Valves " + openValves + " are open, releasing " + pressure + " pressure.");
		}
		*/
		
		if (valve.flow != 0 && !openValves.contains(valve)) {
			// Check max by opening
			openValves.add(valve);
			recursive(valves, valve, openValves, valvePressure + ((long) valve.flow * minute), minute + 1);
			openValves.remove(valve);
		}
		
		// We think the solution is kind and we can never get less
		
		// Check if all tunnels are closed
		boolean open = false;
		for (String next : valve.valves.keySet()) {
			if (!openValves.contains(valves.get(next))) {
				open = true;
				break;
			}
		}
		
		if (!open) {
			return valvePressure;
		}
		
		// Check by moving
		for (String next : valve.valves.keySet()) {
			// Go though all paths and find the minimum
			long result = recursive(valves, valves.get(next), openValves, valvePressure, minute + 1);
			valvePressure = Math.max(result, valvePressure);
		}
		
		return valvePressure;
	}
	
	public void optimizeValves(List<Valve> valves) {
		// Valve 0 is always preserved
		// Note 1: We always move from tunnels with flow 0
		//         This means that we can compact the network into only flow tubes
		//         but with different times to get there
		
		Map<String, Valve> valvesMap = new HashMap<>();
		Map<String, Dijkstra.Node> nodes = new HashMap<>();
		for (Valve valve : valves) {
			nodes.put(valve.name, new Dijkstra.Node());
			valvesMap.put(valve.name, valve);
		}
		
		for (Valve valve : valves) {
			Dijkstra.Node node = nodes.get(valve.name);
			for (var item : valve.valves.entrySet()) {
				node.connectNode(nodes.get(item.getKey()), 1);
			}
		}
		
		// Now we have a connected network
		// Go though all values and add the shortest distance to all other nodes
		
		for (Valve valve : valves) {
			// Each one of the valves connections take 1 minute to walk to
			// What if we added how long time it took to walk to any node in the network?
			
			// Reset
			for (Dijkstra.Node node : nodes.values()) {
				node.reset();
			}
			
			for (Valve next : valves) {
				Dijkstra.Node a = nodes.get(valve.name);
				Dijkstra.Node b = nodes.get(next.name);
				
				final int distance = (int) Dijkstra.getShortestDistance(a, b);
				int lastA = valve.valves.computeIfAbsent(next.name, i -> distance);
				int lastB = next.valves.computeIfAbsent(valve.name, i -> distance);
				
				int maxDistance = Math.max(lastA, lastB);
				valve.valves.put(next.name, maxDistance);
				next.valves.put(valve.name, maxDistance);
			}
		}
		
//		System.out.println();
//		for (Valve valve : valves) {
//			System.out.println(valve.valves);
//		}
		
		// Clean up all moves into zero flow
		for (Valve valve : valves) {
			valve.valves.keySet().removeIf(next -> valvesMap.get(next).flow == 0);
			valve.valves.keySet().removeIf(valve.name::equals);
		}
		
		System.out.println();
		for (Valve valve : valves) {
			System.out.println(valve.name + " > " + valve.valves);
		}
	}
	
	// A recursive solution is not suitable for this we need a better one
	public long solveBetter(Map<String, Valve> valves, Valve valve, Set<String> openValves, long valvePressure, int minute) {
		// 19:39 first try 
		// 19:59 solved it. STARTING ROOM WAS AA not first xD
		// 2359
		
		long resultPressure = valvePressure;
		for (String nextName : valve.valves.keySet()) {
			int newTime = minute + valve.valves.get(nextName) + 1;
			if (openValves.contains(nextName) || (newTime > 30)) {
				continue;
			}
			
			Valve next = valves.get(nextName);
			
			long newPressure = valvePressure + ((long) next.flow * (30 - newTime));
			
			openValves.add(next.name);
			long result = solveBetter(valves, next, openValves, newPressure, newTime);
			openValves.remove(next.name);
			
			resultPressure = Math.max(resultPressure, result);
		}
		
		return resultPressure;
	}
	
	// 18:26
	// 19:59
	// Solve: 93 min
	public long partOne(List<String> lines) throws Exception {
		Map<String, Valve> map = new LinkedHashMap<>();
		List<Valve> valves = new ArrayList<>();
		for (String line : lines) {
			line = line.substring(6).replaceAll("( has flow rate=|; tunnel[s]? lead[s]? to valve[s]? )", ":").trim();
			Valve valve = new Valve(line.split(":"));
			map.put(valve.name, valve);
			valves.add(valve);
		}
		
		optimizeValves(valves);
		
		// Open valves by moving
		// No point in going to already solved valves
		
		// 2488 is not correct
		
		return solveBetter(map, map.get("AA"), new HashSet<>(), 0, 0);
	}
	
	static class Piece {
		// How much time it takes to move
		int time;
		
		// What the valve this piece is approaching
		Valve valve;
		
		Piece(int time, Valve valve) {
			this.time = time;
			this.valve = valve;
		}
		
		Piece(Piece p) {
			this(p.time, p.valve);
		}
	}
	
	public long solveBetterWithHelp(Map<String, Valve> valves, Piece[] pieces, Set<String> openValves, long valvePressure, int minute) {
		if (minute > 30) {
			return valvePressure;
		}
		
		// Open all valves if possible
		for (Piece piece : pieces) {
			if (piece.time == 0) {
				openValves.add(piece.valve.name);
			}
		}
		
		// Update pressure value (each minute)
		for (String open : openValves) {
			valvePressure += valves.get(open).flow;
		}
		
		// Create state for this minute
		Piece a = new Piece(pieces[0]);
		Piece b = new Piece(pieces[1]);
		
		long resultPressure = valvePressure;
		if (a.time == 0 && b.time == 0) {
			Map<String, Integer> aMap = a.valve.valves;
			Map<String, Integer> bMap = b.valve.valves;
			
			// This would double the time for (A.value == B.value) but this is negligible
			for (var aItem : aMap.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				for (var bItem : bMap.entrySet()) {
					if (openValves.contains(bItem.getKey())) {
						continue;
					}
					
					if (aItem.getKey().equals(bItem.getKey())) {
						// We cannot move to the same item
						continue;
					}
					
					// Setup A and B values
					Piece na = new Piece(
						a.valve.valves.get(aItem.getKey()), // Add one because of open
						valves.get(aItem.getKey())
					);
					Piece nb = new Piece(
						b.valve.valves.get(bItem.getKey()), // Add one because of open
						valves.get(bItem.getKey())
					);
					
					long result = solveBetterWithHelp(valves, new Piece[] { na, nb }, openValves, valvePressure, minute + 1);
					resultPressure = Math.max(resultPressure, result);
				}
			}
		} else if (a.time == 0 || b.time == 0) {
			// The order of A and B does not matter
			// Only A or B has time zero
			if (b.time == 0) {
				// Swap places
				Piece tmp = a;
				a = b;
				b = tmp;
			}
			
			// B time should tick down
			b.time --;
			
			// Apply those actions to A
			for (String nextName : a.valve.valves.keySet()) {
				if (openValves.contains(nextName)) {
					continue;
				}
				
				// Don't go to the same valve as B
//				if (b.valve == valves.get(nextName)) {
//					continue;
//				}
				
				// Setup A value
				Piece na = new Piece(
					a.valve.valves.get(nextName), // Add one because of open
					valves.get(nextName)
				);
				
				long result = solveBetterWithHelp(valves, new Piece[] { na, b }, openValves, valvePressure, minute + 1);
				resultPressure = Math.max(resultPressure, result);
			}
		} else {
			// A and B time should tick down
			a.time --;
			b.time --;
			
			long result = solveBetterWithHelp(valves, new Piece[] { a, b }, openValves, valvePressure, minute + 1);
			resultPressure = Math.max(resultPressure, result);
		}
		
		// Close all values if possible
		for (Piece piece : pieces) {
			if (piece.time == 0) {
				openValves.remove(piece.valve.name);
			}
		}
		
		// if (minute < 8) {
		// 	System.out.println("== Minute " + minute + " == " + resultPressure);
		// }
		
		return resultPressure;
	}
	
	
	// Test
	public long solveBetterWithHelp2(Map<String, Valve> valves, Piece a, Piece b, Set<String> openValves, long valvePressure, int minute) {
		final int CostOfOpen = 1;
		
		// Create state for this minute
		a = new Piece(a);
		b = new Piece(b);
		
		// At the start of each minute add the pressure
		for (String open : openValves) {
			valvePressure += valves.get(open).flow;
		}
		
		if (minute >= 26) {
			return valvePressure;
		}
		
		long resultPressure = valvePressure;
		if (a.time == 0 && b.time == 0) {
			Map<String, Integer> aMap = a.valve.valves;
			Map<String, Integer> bMap = b.valve.valves;
			
			// This would double the time for (A.value == B.value) but this is negligible
			for (var aItem : aMap.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				for (var bItem : bMap.entrySet()) {
					if (openValves.contains(bItem.getKey())) {
						continue;
					}
					
					if (aItem.getKey().equals(bItem.getKey())) {
						// We cannot move to the same item
						continue;
					}
					
					openValves.add(a.valve.name);
					openValves.add(b.valve.name);
					
					// Setup A and B values
					Piece na = new Piece(
						a.valve.valves.get(aItem.getKey()) + CostOfOpen, // Add one because of open
						valves.get(aItem.getKey())
					);
					Piece nb = new Piece(
						b.valve.valves.get(bItem.getKey()) + CostOfOpen, // Add one because of open
						valves.get(bItem.getKey())
					);
					
					long result = solveBetterWithHelp2(valves, na, nb, openValves, valvePressure, minute + 1);
					resultPressure = Math.max(resultPressure, result);
					
					openValves.remove(a.valve.name);
					openValves.remove(b.valve.name);
				}
			}
		} else if (a.time == 0 || b.time == 0) {
			// The order of A and B does not matter
			// Only A or B has time zero
			if (b.time == 0) {
				// Swap places
				Piece tmp = a;
				a = b;
				b = tmp;
			}
			
			b.time --;
			
			// Apply those actions to A
			for (String nextName : a.valve.valves.keySet()) {
				if (openValves.contains(nextName)) {
					continue;
				}
				
				// Don't go to the same valve as B
				// TODO: This line is the one that is not working figure out why!!!!!
				if (b.valve == valves.get(nextName)) {
					continue;
				}
				
				openValves.add(a.valve.name);
				
				// Setup A value
				Piece na = new Piece(
					a.valve.valves.get(nextName) + CostOfOpen, // Add one because of open
					valves.get(nextName)
				);
				
				long result = solveBetterWithHelp2(valves, na, b, openValves, valvePressure, minute + 1);
				resultPressure = Math.max(resultPressure, result);
				
				openValves.remove(a.valve.name);
			}
		} else {
			a.time --;
			b.time --;
			
			long result = solveBetterWithHelp2(valves, a, b, openValves, valvePressure, minute + 1);
			resultPressure = Math.max(resultPressure, result);
		}
		
		return resultPressure;
	}
	
	
	public long solveBetterWithHelp3(Map<String, Valve> valves, Piece a, Piece b, Set<String> openValves, long valvePressure, int minute) {
		if (minute > 26) {
			return valvePressure;
		}
		
		final int CostOfOpen = 0;
		
		// Create state for this minute
		a = new Piece(a);
		b = new Piece(b);
		
		// At the start of each minute add the pressure
		for (String open : openValves) {
			valvePressure += valves.get(open).flow;
		}
		
		long resultPressure = valvePressure;
		if (a.time == 0 && b.time == 0) {
			Map<String, Integer> aMap = a.valve.valves;
			Map<String, Integer> bMap = b.valve.valves;
			
			// This would double the time for (A.value == B.value) but this is negligible
			for (var aItem : aMap.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				for (var bItem : bMap.entrySet()) {
					if (openValves.contains(bItem.getKey())) {
						continue;
					}
					
					if (aItem.getKey().equals(bItem.getKey())) {
						// We cannot move to the same item
						continue;
					}
					
					openValves.add(a.valve.name);
					openValves.add(b.valve.name);
					
					// Setup A and B values
					Piece na = new Piece(
						a.valve.valves.get(aItem.getKey()) + CostOfOpen, // Add one because of open
						valves.get(aItem.getKey())
					);
					Piece nb = new Piece(
						b.valve.valves.get(bItem.getKey()) + CostOfOpen, // Add one because of open
						valves.get(bItem.getKey())
					);
					
					long result = solveBetterWithHelp3(valves, na, nb, openValves, valvePressure, minute + 1);
					resultPressure = Math.max(resultPressure, result);
					
					openValves.remove(a.valve.name);
					openValves.remove(b.valve.name);
				}
			}
		} else if (a.time == 0 || b.time == 0) {
			// The order of A and B does not matter
			// Only A or B has time zero
			if (b.time == 0) {
				// Swap places
				Piece tmp = a;
				a = b;
				b = tmp;
			}
			
			b.time --;
			
			// Apply those actions to A
			for (String nextName : a.valve.valves.keySet()) {
				if (openValves.contains(nextName)) {
					continue;
				}
				
				// Don't go to the same valve as B
				// TODO: This line is the one that is not working figure out why!!!!!
				if (b.valve == valves.get(nextName)) {
					continue;
				}
				
				openValves.add(a.valve.name);
				
				// Setup A value
				Piece na = new Piece(
					a.valve.valves.get(nextName) + CostOfOpen, // Add one because of open
					valves.get(nextName)
				);
				
				long result = solveBetterWithHelp3(valves, na, b, openValves, valvePressure, minute + 1);
				resultPressure = Math.max(resultPressure, result);
				
				openValves.remove(a.valve.name);
			}
		} else {
			a.time --;
			b.time --;
			
			long result = solveBetterWithHelp3(valves, a, b, openValves, valvePressure, minute + 1);
			resultPressure = Math.max(resultPressure, result);
		}
		
		return resultPressure;
	}
	
	
	public long solveFaster(Map<String, Valve> valves, Piece a, Piece b, Set<String> openValves, long valvePressure, int minute) {
		final int CostOfOpen = 0;
		
		if (minute >= 26) {
			return valvePressure;
		}
		
		// Overwrite state
		if (a.time == 0) openValves.add(a.valve.name);
		if (b.time == 0) openValves.add(b.valve.name);
		
		// 1: At the start of each minute add the valve pressure
		for (String open : openValves) {
			valvePressure += valves.get(open).flow;
		}
		
		long pressure = valvePressure;
		
		boolean moved = false;
		
		// 2: Check if A and B has time zero
		if (a.time == 0 && b.time == 0) {
			// 2.1: Iterate through all possible combinations that A and B can jump to
			for (var aItem : a.valve.valves.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				for (var bItem : b.valve.valves.entrySet()) {
					if (openValves.contains(bItem.getKey())) {
						continue;
					}
					
					if (aItem.getKey().equals(bItem.getKey())) {
						continue;
					}
					
					// 2.2: Check if there is any larger pressure powers
					Piece na = new Piece(a.valve.valves.get(aItem.getKey()) + CostOfOpen, valves.get(aItem.getKey()));
					Piece nb = new Piece(b.valve.valves.get(bItem.getKey()) + CostOfOpen, valves.get(bItem.getKey()));
					pressure = Math.max(pressure, solveFaster(valves, na, nb, openValves, valvePressure, minute + 1));
					
					moved = true;
				}
			}
		} else
		// 3: Check if A or B has time zero
		if (a.time == 0 || b.time == 0) {
			// 3.1: Always make A the time zero value
			if (b.time == 0) {
				Piece t = a;
				a = b;
				b = t;
			}
			
			// 3.2: B is always moved down one step
			Piece nb = new Piece(b);
			nb.time --;
			
			// 3.3: Iterate though all possible values of A
			for (var aItem : a.valve.valves.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				if (nb.valve.name.equals(aItem.getKey())) {
					continue;
				}
				
				// 3.4: Check if there is any larger pressure powers
				Piece na = new Piece(a.valve.valves.get(aItem.getKey()) + CostOfOpen, valves.get(aItem.getKey()));
				pressure = Math.max(pressure, solveFaster(valves, na, nb, openValves, valvePressure, minute + 1));
				
				moved = true;
			}
		}
		
		// 4: Just tick down A and B
		if (!moved) {
			Piece na = new Piece(a);
			Piece nb = new Piece(b);
			na.time --;
			nb.time --;
			
			// 4.1: Check if there is any larger pressure powers
			pressure = Math.max(pressure, solveFaster(valves, na, nb, openValves, valvePressure, minute + 1));
		}
		
		if (a.time == 0) openValves.remove(a.valve.name);
		if (b.time == 0) openValves.remove(b.valve.name);
		
		if (minute < 6) {
			System.out.println("== Minute " + minute + " == " + pressure);
		}
		return pressure;
	}
	
	
	public static long MAX_FLOW = 0;
	public static long HIGHEST = 0;
	public long solveFasterCutting(Map<String, Valve> valves, Piece a, Piece b, Set<String> openValves, long valvePressure, int minute) {
		final int CostOfOpen = 0;
		
		if (valvePressure + MAX_FLOW * (26 - minute) < HIGHEST) {
			// This will never beat the highest score
			return 0;
		}
		
		HIGHEST = Math.max(HIGHEST, valvePressure);
		
		if (minute >= 26) {
			return valvePressure;
		}
		
		// Overwrite state
		if (a.time == 0) openValves.add(a.valve.name);
		if (b.time == 0) openValves.add(b.valve.name);
		
		// 1: At the start of each minute add the valve pressure
		for (String open : openValves) {
			valvePressure += valves.get(open).flow;
		}
		
		long pressure = valvePressure;
		
		boolean moved = false;
		
		// 2: Check if A and B has time zero
		if (a.time == 0 && b.time == 0) {
			// 2.1: Iterate through all possible combinations that A and B can jump to
			for (var aItem : a.valve.valves.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				for (var bItem : b.valve.valves.entrySet()) {
					if (openValves.contains(bItem.getKey())) {
						continue;
					}
					
					if (aItem.getKey().equals(bItem.getKey())) {
						continue;
					}
					
					// 2.2: Check if there is any larger pressure powers
					Piece na = new Piece(a.valve.valves.get(aItem.getKey()) + CostOfOpen, valves.get(aItem.getKey()));
					Piece nb = new Piece(b.valve.valves.get(bItem.getKey()) + CostOfOpen, valves.get(bItem.getKey()));
					pressure = Math.max(pressure, solveFasterCutting(valves, na, nb, openValves, valvePressure, minute + 1));
					
					moved = true;
				}
			}
		} else
		// 3: Check if A or B has time zero
		if (a.time == 0 || b.time == 0) {
			// 3.1: Always make A the time zero value
			if (b.time == 0) {
				Piece t = a;
				a = b;
				b = t;
			}
			
			// 3.2: B is always moved down one step
			Piece nb = new Piece(b);
			nb.time --;
			
			// 3.3: Iterate though all possible values of A
			for (var aItem : a.valve.valves.entrySet()) {
				if (openValves.contains(aItem.getKey())) {
					continue;
				}
				
				if (nb.valve.name.equals(aItem.getKey())) {
					continue;
				}
				
				// 3.4: Check if there is any larger pressure powers
				Piece na = new Piece(a.valve.valves.get(aItem.getKey()) + CostOfOpen, valves.get(aItem.getKey()));
				pressure = Math.max(pressure, solveFasterCutting(valves, na, nb, openValves, valvePressure, minute + 1));
				
				moved = true;
			}
		}
		
		// 4: Just tick down A and B
		if (!moved) {
			Piece na = new Piece(a);
			Piece nb = new Piece(b);
			na.time --;
			nb.time --;
			
			// 4.1: Check if there is any larger pressure powers
			pressure = Math.max(pressure, solveFasterCutting(valves, na, nb, openValves, valvePressure, minute + 1));
		}
		
		if (a.time == 0) openValves.remove(a.valve.name);
		if (b.time == 0) openValves.remove(b.valve.name);
		
//		if (minute < 6) {
//			System.out.println("== Minute " + minute + " == " + pressure);
//		}
		
		return pressure;
	}
	
	// 20:00
	
	// 21:17 still not solved
	// 21:53 almost have a solution but does not work fully
	// 22:22 solved but might take some time to run
	
	// 22:32
	
	// Solve: 152 min
	public long partTwo(List<String> lines) throws Exception {
		Map<String, Valve> map = new LinkedHashMap<>();
		List<Valve> valves = new ArrayList<>();
		for (String line : lines) {
			line = line.substring(6).replaceAll("( has flow rate=|; tunnel[s]? lead[s]? to valve[s]? )", ":").trim();
			Valve valve = new Valve(line.split(":"));
			map.put(valve.name, valve);
			valves.add(valve);
			
			MAX_FLOW += valve.flow;
		}
		
		optimizeValves(valves);
		
		Piece me = new Piece(0, map.get("AA"));
		Piece elephant = new Piece(0, map.get("AA"));
		
		return solveFasterCutting(map, me, elephant, new HashSet<>(), 0, 0);
	}
}
