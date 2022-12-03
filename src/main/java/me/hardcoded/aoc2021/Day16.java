package me.hardcoded.aoc2021;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

public class Day16 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day16().run();
	}
	
	public Day16() {
		super(2021, 16);
	}
	
	public void run() throws Exception {
		String line = Utils.readAll(this).strip();
		
		String binary = String.join("", line.chars()
			.mapToObj(c -> "%4s".formatted(Integer.toBinaryString(Character.digit(c, 16))).replace(' ', '0')).toList());
		Packet packet = readPacket(new DataInputStream(new ByteArrayInputStream(binary.getBytes())));
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(packet));
		Utils.printf("PartTwo: %s\n", partTwo(packet));
	}
	
	private static class Packet {
		List<Packet> packets = new ArrayList<>();
		int version;
		int typeId;
		long value;
	}
	
	public Packet readPacket(DataInputStream stream) throws Exception {
		Packet packet = new Packet();
		packet.version = Integer.parseInt(new String(stream.readNBytes(3)), 2);
		packet.typeId = Integer.parseInt(new String(stream.readNBytes(3)), 2);
		
		if (packet.typeId == 4) {
			StringBuilder sb = new StringBuilder();
			byte[] pack;
			do {
				pack = stream.readNBytes(5);
				sb.append(new String(pack, 1, 4));
			} while (pack[0] != '0');
			packet.value = Long.parseLong(sb.toString(), 2);
		} else {
			if (stream.readUnsignedByte() == '0') {
				int mark = stream.available() - Integer.parseInt(new String(stream.readNBytes(15)), 2) - 15;
				while (stream.available() > mark) {
					packet.packets.add(readPacket(stream));
				}
			} else {
				int length = Integer.parseInt(new String(stream.readNBytes(11)), 2);
				for (int i = 0; i < length; i++) {
					packet.packets.add(readPacket(stream));
				}
			}
		}
		
		return packet;
	}
	
	private long sumPacketVersions(Packet packet) {
		long result = packet.version;
		
		for (Packet p : packet.packets) {
			result += sumPacketVersions(p);
		}
		
		return result;
	}
	
	private long calculateExpression(Packet packet) {
		long result = 0;
		
		result = packet.typeId == 4 ? packet.value:calculateExpression(packet.packets.get(0));
		for (int i = 1; i < packet.packets.size(); i++) {
			long value = calculateExpression(packet.packets.get(i));
			switch (packet.typeId) {
				case 0 -> result += value;
				case 1 -> result *= value;
				case 2 -> result = Math.min(result, value);
				case 3 -> result = Math.max(result, value);
				case 5 -> result = result > value ? 1:0;
				case 6 -> result = result < value ? 1:0;
				case 7 -> result = result == value ? 1:0;
			}
		}
		
		return result;
	}
	
	// Solve: 37 min
	public long partOne(Packet packet) throws Exception {
		return sumPacketVersions(packet);
	}
	
	// Solve 5 min
	public long partTwo(Packet packet) throws Exception {
		return calculateExpression(packet);
	}
}
