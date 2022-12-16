package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Dijkstra;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day16_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day16_Post().run();
	}
	
	public Day16_Post() {
		super(2022, 16, Suffix.Post);
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
		
		// Clean up all moves into zero flow
		for (Valve valve : valves) {
			valve.valves.keySet().removeIf(next -> valvesMap.get(next).flow == 0);
			valve.valves.keySet().removeIf(valve.name::equals);
		}
	}
	
	public long solveBetter(Map<String, Valve> valves, Valve valve, Set<String> openValves, long valvePressure, int minute) {
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
		
		return pressure;
	}
	
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
