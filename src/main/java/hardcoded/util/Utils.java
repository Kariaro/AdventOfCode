package hardcoded.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {
	public static Path of(int year, String name) {
		return Path.of("src/main/resources/", "aoc%d/%s".formatted(year, name));
	}
	
	public static List<String> readAllLines(int year, String name) throws Exception {
		return Files.readAllLines(of(year, name));
	}

	public static String readAll(int year, String name) throws Exception {
		return new String(Files.readAllBytes(of(year, name)));
	}
	
	public static int[] readIntArray(int year, String name, String separator) throws Exception {
		return Arrays.asList(readAll(year, name).trim().split(separator)).stream().mapToInt(Integer::parseInt).toArray();
	}
	
	private static long nanos;
	public static void printf(String format, Object... args) {
		long start = System.nanoTime();
		System.out.printf(Locale.US, format, args);
		
		if(nanos != 0) {
			System.out.printf(Locale.US, "%.2f ms\n\n", (start - nanos) / 1000000.0);
		}
		
		nanos = System.nanoTime();
	}
}
