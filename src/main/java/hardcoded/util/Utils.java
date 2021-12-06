package hardcoded.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Utils {
	public static Path of(String name) {
		return Path.of("src/main/resources/", name);
	}
	
	public static List<String> readAllLines(String name) throws Exception {
		return Files.readAllLines(Path.of("src/main/resources/", name));
	}
	
	public static String readAll(String name) throws Exception {
		return new String(Files.readAllBytes(Path.of("src/main/resources/", name)));
	}
}
