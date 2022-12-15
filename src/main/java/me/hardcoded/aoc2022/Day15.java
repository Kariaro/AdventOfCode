package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day15 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day15().run();
	}
	
	public Day15() {
		super(2022, 15);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	record Sensor(int x, int y) {}
	record Beacon(int x, int y) {}
	record Range(int s, int e) {}
	
	public int getManhattanDistance(Sensor sensor, Beacon beacon) {
		return Math.abs(beacon.x - sensor.x) + Math.abs(beacon.y - sensor.y);
	}
	
	public Range combine(Range a, Range b) {
		if ((a.s <= b.e && a.e >= b.s)
		|| (a.s >= b.s && a.e <= b.e)
		|| (b.s >= a.s && b.e <= a.e)) {
			return new Range(Math.min(a.s, b.s), Math.max(a.e, b.e));
		}
		
		return null;
	}
	
	public void collapseRanges(List<Range> list) {
		int lastSize;
		do {
			// Update the last list size
			lastSize = list.size();
			
			// Start by taking the first element from the list
			Range first = list.get(0);
			
			// Remove that element from the list
			list.remove(0);
			
			// Combine that range with all elements in the list
			for (int i = 0; i < list.size(); i++) {
				Range combine = combine(first, list.get(i));
				
				// If the ranges can be combined
				if (combine != null) {
					// Update the first element
					first = combine;
					
					// And remove the element that was combined from the list
					list.remove(i--);
				}
			}
			
			// In the end add the first element back into the list
			list.add(first);
		} while (lastSize != list.size());
	}
	
	// Solve: 26 min
	public long partOne(List<String> lines) throws Exception {
		List<Map.Entry<Sensor, Beacon>> list = new ArrayList<>();
		
		for (String line : lines) {
			String[] parts = line.replaceAll("[xy]=", "").substring(10).split(": closest beacon is at ");
			String[] sensorPart = parts[0].split(", ");
			String[] beaconPart = parts[1].split(", ");
			
			Sensor sensor = new Sensor(Integer.parseInt(sensorPart[0]), Integer.parseInt(sensorPart[1]));
			Beacon beacon = new Beacon(Integer.parseInt(beaconPart[0]), Integer.parseInt(beaconPart[1]));
			list.add(Map.entry(sensor, beacon));
		}
		
		int count = 1;
		for (int i = -1000000; i < 5000000; i++) {
			for (var item : list) {
				Sensor sensor = item.getKey();
				
				// Is it within the sensors manhattan distance
				int sensorDistance = getManhattanDistance(sensor, item.getValue());
				int dreamDistance = getManhattanDistance(sensor, new Beacon(i, 2000000));
				if (sensorDistance > dreamDistance) {
					count++;
					break;
				}
			}
		}
		
		return count;
	}
	
	// Solve: 36 min
	public long partTwo(List<String> lines) throws Exception {
		List<Map.Entry<Sensor, Beacon>> list = new ArrayList<>();
		
		for (String line : lines) {
			String[] parts = line.replaceAll("[xy]=", "").substring(10).split(": closest beacon is at ");
			String[] sensorPart = parts[0].split(", ");
			String[] beaconPart = parts[1].split(", ");
			
			Sensor sensor = new Sensor(Integer.parseInt(sensorPart[0]), Integer.parseInt(sensorPart[1]));
			Beacon beacon = new Beacon(Integer.parseInt(beaconPart[0]), Integer.parseInt(beaconPart[1]));
			list.add(Map.entry(sensor, beacon));
		}
		
		long result = 0;
		
		// For each y value from 0 to 4000000
		for (int y = 0; y < 4000000; y++) {
			// Define ranges for each sensor > beacon within manhattan distance
			// Calculate the intersection of each sensor > beacon and the line (y)
			List<Range> ranges = new ArrayList<>();
			for (var item : list) {
				Sensor sensor = item.getKey();
				int dist = getManhattanDistance(sensor, item.getValue());
				
				// Check if the sensor touches this line
				if (!(sensor.y - dist < y && sensor.y + dist > y)) {
					continue;
				}
				
				// Calculate the range at which the current line intersects the area of the sensor
				// Because: dist  =  Math.abs(beacon.x - sensor.x) + Math.abs(beacon.y - sensor.y)
				// Then   : dist  =  Math.abs((?) - sensor.x) + Math.abs((Y) - sensor.y)
				// Then   : (?)   =  sensor.x (+-) (dist - Math.abs((Y) - sensor.y))
				int diff = dist - Math.abs(y - sensor.y);
				ranges.add(new Range(sensor.x - diff, sensor.x + diff));
			}
			
			// Collapse all ranges
			collapseRanges(ranges);
			
			// If we have more than one range
			if (ranges.size() > 1) {
				// Sort the ranges from their ends
				ranges.sort(Comparator.comparingInt(aa -> aa.s));
				System.out.println(y + ", " + ranges);
				
				// Calculate the result
				int xp = ranges.get(0).e + 1;
				result = xp * 4000000L + y;
				break; 
			}
		}
		
		return result;
	}
}
