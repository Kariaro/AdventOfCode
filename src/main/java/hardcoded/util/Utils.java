package hardcoded.util;

import java.nio.file.Path;

public class Utils {
	public static Path of(String name) {
		return Path.of("src/main/resources/", name);
	}
}
