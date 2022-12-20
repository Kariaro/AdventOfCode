package me.hardcoded.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

public class RunnerYearly {
	public static void runAllDays(Package pack) throws Exception {
		List<DayBase> days = Reflection.findDays(pack);
		
		Utils.init();
		
		PrintStream out = System.out;
		System.setOut(new PrintStream(OutputStream.nullOutputStream()));
		
		for (DayBase day : days) {
			out.println("=".repeat(30));
			
			// Slow days take too long to run this for
			if (day.slow) {
				out.println(day);
				out.println("Day would take too long to run. Skipped");
				continue;
			}
			
			long time = System.nanoTime();
			day.run();
			out.printf(Locale.US, "Day took %.2f ms\n", (System.nanoTime() - time) / 1000000.0f);
		}
	}
}
