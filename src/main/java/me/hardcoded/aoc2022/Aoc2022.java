package me.hardcoded.aoc2022;

import me.hardcoded.util.DayBase;
import me.hardcoded.util.Reflection;
import me.hardcoded.util.Utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public class Aoc2022 {
	public static void main(String[] args) throws Exception {
		List<DayBase> days = Reflection.findDays(Aoc2022.class.getPackage());
		
		Utils.init();
		
		PrintStream out = System.out;
		System.setOut(new PrintStream(OutputStream.nullOutputStream()));
		
		for (DayBase day : days) {
			out.println("=".repeat(30));
			long time = System.nanoTime();
			day.run();
			out.printf(Locale.US, "Day took %.2f ms\n", (System.nanoTime() - time) / 1000000.0f);
		}
	}
}
