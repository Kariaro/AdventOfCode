package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;

public class Day13 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day13().run();
	}
	
	public Day13() {
		super(2022, 13);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class Packet {
		private final List<Packet> packets = new ArrayList<>();
		private Long value;
		
		Packet(long value) {
			this.value = value;
		}
		
		Packet(Packet packet) {
			if (packet != null) {
				packets.add(packet);
			}
		}
		
		public boolean isValue() {
			return value != null;
		}
		
		public int size() {
			return packets.size();
		}
	}
	
	private Packet processPacket(String line) {
		Packet root = new Packet(null);
		
		Stack<Packet> packets = new Stack<>();
		packets.add(root);
		
		Scanner scanner = new Scanner(line);
		while (scanner.hasNext()) {
			String str = scanner.findInLine("[0-9]+|.");
			switch (str) {
				case "[" -> {
					Packet parent = packets.peek();
					Packet next = new Packet(null);
					parent.packets.add(next);
					packets.push(next);
				}
				case "]" -> packets.pop();
				case "," -> {}
				default -> packets.peek().packets.add(new Packet(Long.parseLong(str)));
			}
		}
		
		return root.packets.get(0);
	}
	
	private int compare(Packet a, Packet b) {
		// If a is value or b is value
		if (a.isValue() != b.isValue()) {
			return a.isValue()
				? compare(new Packet(a), b)
				: compare(a, new Packet(b));
		}
		
		// If a is value and b is value
		if (a.isValue()) {
			return Long.compare(a.value, b.value);
		}
		
		// Compare all elements of a and b
		int result = Integer.compare(a.size(), b.size());
		for (int i = 0, len = Math.min(a.size(), b.size()); i < len; i++) {
			int cmp = compare(a.packets.get(i), b.packets.get(i));
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return result;
	}
	
	// Solve: 31 min
	public long partOne(List<String> lines) throws Exception {
		long result = 0;
		int count = 0;
		
		Iterator<String> iter = lines.iterator();
		while (iter.hasNext()) {
			Packet a = processPacket(iter.next());
			Packet b = processPacket(iter.next());
			
			count++;
			if (compare(a, b) < 1) {
				result += count;
			}
			
			if (iter.hasNext()) {
				iter.next();
			}
		}
		
		return result;
	}
	
	// Solve: 7 min
	public int partTwo(List<String> lines) throws Exception {
		List<Packet> packets = new ArrayList<>();
		
		Iterator<String> iter = lines.iterator();
		while (iter.hasNext()) {
			packets.add(processPacket(iter.next()));
			packets.add(processPacket(iter.next()));
			if (iter.hasNext()) {
				iter.next();
			}
		}
		
		Packet div2 = new Packet(new Packet(new Packet(2)));
		Packet div6 = new Packet(new Packet(new Packet(6)));
		packets.add(div2);
		packets.add(div6);
		packets.sort(this::compare);
		
		return (packets.indexOf(div2) + 1) * (packets.indexOf(div6) + 1);
	}
}
