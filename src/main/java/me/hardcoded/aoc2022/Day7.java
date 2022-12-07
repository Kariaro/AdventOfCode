package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.function.Consumer;

public class Day7 extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day7().run();
	}
	
	public Day7() {
		super(2022, 7);
	}
	
	public void run() throws Exception {
		List<String> lines = Utils.readAllLines(this);
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(lines));
		Utils.printf("PartTwo: %s\n", partTwo(lines));
	}
	
	static class SystemFile {
		Map<String, SystemFile> files = new LinkedHashMap<>();
		String name;
		int size;
		
		SystemFile(String name, int size) {
			this.name = name;
			this.size = size;
		}
		
		void add(List<String> path, String name, int size) {
			if (path.isEmpty()) {
				files.put(name, new SystemFile(name, size));
			} else {
				SystemFile file = files.get(path.get(0));
				if (file == null) {
					file = new SystemFile(path.get(0), -1);
					files.put(path.get(0), file);
				}
				
				file.add(path.subList(1, path.size()), name, size);
			}
		}
		
		public long getSize() {
			long total = Math.max(size, 0);
			
			for (SystemFile file : files.values()) {
				total += file.getSize();
			}
			
			return total;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (name.isEmpty()) {
				for (var entry : files.entrySet()) {
					sb.append(entry.getValue().toString());
				}
				
				return sb.toString();
			}
			
			if (size < 0) {
				sb.append("- ").append(name).append(" (dir) (").append(getSize()).append(")\n");
			} else {
				sb.append("- ").append(name).append(" (file, size=").append(size).append(")\n");
			}
			
			for (var entry : files.entrySet()) {
				sb.append(entry.getValue().toString().indent(4));
			}
			
			return sb.toString();
		}
		
		public void traverse(Consumer<SystemFile> consumer) {
			consumer.accept(this);
			
			for (SystemFile file : files.values()) {
				file.traverse(consumer);
			}
		}
	}
	
	// Solve: 22 min
	public long partOne(List<String> lines) throws Exception {
		ListIterator<String> iter = lines.listIterator();
		
		SystemFile file = new SystemFile("", -1);
		
		LinkedList<String> path = new LinkedList<>();
		while (iter.hasNext()) {
			String command = iter.next().substring(2);
			List<String> output = new ArrayList<>();
			
			while (iter.hasNext()) {
				String line = iter.next();
				if (line.startsWith("$")) {
					iter.previous();
					break;
				}
				
				output.add(line);
			}
			
			if (command.equals("cd ..")) {
				path.removeLast();
			} else if (command.equals("cd /")) {
				path.clear();
				path.add("/");
			} else if (command.startsWith("cd ")) {
				String cdPath = command.substring(3);
				path.add(cdPath);
			}
			
			if (command.equals("ls")) {
				for (String str : output) {
					String[] part = str.split(" ");
					if (part[0].equals("dir")) {
						file.add(path, part[1], -1);
					} else {
						file.add(path, part[1], Integer.parseInt(part[0]));
					}
				}
			}
			
			// System.out.printf("%s -> [%s]\n", command, output);
		}
		
		System.out.println(file);
		
		long[] result = new long[1];
		file.traverse((item) -> {
			if (item.size < 0) {
				long size = item.getSize();
				
				if (size <= 100000) {
					result[0] += size;
				}
			}
		});
		
		return result[0];
	}
	
	// Solve: 6 min
	public long partTwo(List<String> lines) throws Exception {
		ListIterator<String> iter = lines.listIterator();
		
		SystemFile file = new SystemFile("", -1);
		
		LinkedList<String> path = new LinkedList<>();
		while (iter.hasNext()) {
			String command = iter.next().substring(2);
			List<String> output = new ArrayList<>();
			
			while (iter.hasNext()) {
				String line = iter.next();
				if (line.startsWith("$")) {
					iter.previous();
					break;
				}
				
				output.add(line);
			}
			
			if (command.equals("cd ..")) {
				path.removeLast();
			} else if (command.equals("cd /")) {
				path.clear();
				path.add("/");
			} else if (command.startsWith("cd ")) {
				String cdPath = command.substring(3);
				path.add(cdPath);
			}
			
			if (command.equals("ls")) {
				for (String str : output) {
					String[] part = str.split(" ");
					if (part[0].equals("dir")) {
						file.add(path, part[1], -1);
					} else {
						file.add(path, part[1], Integer.parseInt(part[0]));
					}
				}
			}
			
			// System.out.printf("%s -> [%s]\n", command, output);
		}
		
		System.out.println(file);
		
		List<Map.Entry<Long, SystemFile>> files = new ArrayList<>();
		file.traverse((item) -> {
			if (item.size < 0) {
				long size = item.getSize();
				files.add(Map.entry(size, item));
			}
		});
		
		long currSize = file.getSize();
		// size 70000000
		// atleast 30000000 unused
		// smallest below 40000000
		files.sort(Comparator.comparingLong(Map.Entry::getKey));
		
		for (int i = 0; i < files.size(); i++) {
			var item = files.get(i);
			if (currSize - item.getKey() <= 40000000) {
				return item.getKey();
			}
		}
		
		return -1;
	}
}
