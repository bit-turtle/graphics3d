package graphics3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {

	private Util() {}
	
	public static String readfile(String path) {
		try { return new String(Files.readAllBytes(Paths.get(path))); }
		catch (IOException e) { throw new RuntimeException("Error reading file [" + path + "]", e); }
	}
	
}
