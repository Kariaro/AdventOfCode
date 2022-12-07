package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Utils;

import java.util.*;
import java.util.function.Consumer;

public class Day7_Post extends DayBase {
	public static void main(String[] args) throws Exception {
		new Day7_Post().run();
	}
	
	public Day7_Post() {
		super(2022, 7, Suffix.Post);
	}
	
	public void run() throws Exception {
		SystemFile system = prepareData(Utils.readAllLines(this));
		
		Utils.startPrintf(toString());
		Utils.printf("PartOne: %s\n", partOne(system));
		Utils.printf("PartTwo: %s\n", partTwo(system));
	}
	
	static class SystemFile {
		private final Map<String, SystemFile> files;
		private final int size;
		
		SystemFile(int size) {
			this.size = size;
			this.files = size >= 0 ? null : new LinkedHashMap<>();
		}
		
		public boolean isDirectory() {
			return size < 0;
		}
		
		public long getSize() {
			if (!isDirectory()) {
				return size;
			}
			
			long total = 0;
			for (SystemFile file : files.values()) {
				total += file.getSize();
			}
			
			return total;
		}
		
		void add(List<String> paths, int index, String name, int size) {
			if (index == paths.size()) {
				files.put(name, new SystemFile(size));
				return;
			}
			
			String path = paths.get(index);
			SystemFile file = files.get(path);
			if (file == null) {
				files.put(path, file = new SystemFile(-1));
			}
			
			file.add(paths, index + 1, name, size);
		}
		
		public void traverse(Consumer<SystemFile> consumer) {
			consumer.accept(this);
			if (isDirectory()) {
				for (SystemFile file : files.values()) {
					file.traverse(consumer);
				}
			}
		}
	}
	
	public SystemFile prepareData(List<String> lines) {
		ListIterator<String> iter = lines.listIterator();
		
		SystemFile file = new SystemFile(-1);
		
		LinkedList<String> path = new LinkedList<>();
		while (iter.hasNext()) {
			String command = iter.next();
			
			if (command.equals("$ cd ..")) {
				path.removeLast();
			} else if (command.equals("$ cd /")) {
				path.clear();
				path.add("/");
			} else if (command.startsWith("$ cd ")) {
				String cdPath = command.substring(5);
				path.add(cdPath);
			} else if (command.equals("$ ls")) {
				while (iter.hasNext()) {
					command = iter.next();
					
					if (command.startsWith("$")) {
						iter.previous();
						break;
					}
					
					String[] part = command.split(" ");
					if (part[0].equals("dir")) {
						file.add(path, 0, part[1], -1);
					} else {
						file.add(path, 0, part[1], Integer.parseInt(part[0]));
					}
				}
			}
		}
		
		return file;
	}
	
	// Solve: 22 min
	public long partOne(SystemFile system) throws Exception {
		long[] result = { 0 };
		system.traverse(file -> {
			if (file.isDirectory()) {
				long size = file.getSize();
				
				if (size <= 100000) {
					result[0] += size;
				}
			}
		});
		
		return result[0];
	}
	
	// Solve: 6 min
	public long partTwo(SystemFile system) throws Exception {
		// If the disk is 70000000 and we need at least 30000000 free space
		// Then we need to know when 'currSize' - 'file' < 40000000
		long currSize = system.getSize();
		long[] result = { Long.MAX_VALUE };
		
		system.traverse(file -> {
			if (file.isDirectory()) {
				long size = file.getSize();
				if (currSize - size <= 40000000 && size < result[0]) {
					result[0] = size;
				}
			}
		});
		
		return result[0];
	}
}
