package me.hardcoded.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Reflection utility class for running all tests
 */
public class Reflection {
	public static <T> List<Class<?>> findClasses(Class<T> base, Package pack) throws Exception {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(pack.getName().replace('.', '/'));
		
		try (stream) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			// Get a list of all classes that follows this pattern
			return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> forName(line, pack))
				.filter(Objects::nonNull)
				.filter(base::isAssignableFrom)
				.collect(Collectors.toList());
		}
	}
	
	public static List<DayBase> findDays(Package pack) throws Exception {
		List<Class<?>> list = findClasses(DayBase.class, pack);
		List<DayBase> result = new ArrayList<>();
		
		// First construct the list of all days
		for (Class<?> clazz : list) {
			result.add((DayBase) clazz.getConstructor().newInstance());
		}
		
		// Then sort the list
		result.sort(Comparator.comparingInt((DayBase a) -> a.day).thenComparing(a -> a.suffix));
		
		return result;
	}
	
	public static Class<?> forName(String className, Package pack) {
		try {
			return Class.forName(pack.getName() + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
		
		return null;
	}
}
