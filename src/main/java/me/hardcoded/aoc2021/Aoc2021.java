package me.hardcoded.aoc2021;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Reflection;
import me.hardcoded.util.Utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class Aoc2021 {
	public static void main(String[] args) throws Exception {
		List<DayBase> days = Reflection.findDays(Aoc2021.class.getPackage());
		
		Utils.init();
		
		PrintStream out = System.out;
		System.setOut(new PrintStream(OutputStream.nullOutputStream()));
		
		for (DayBase day : days) {
			out.println("=".repeat(30));
			day.run();
		}
	}
}