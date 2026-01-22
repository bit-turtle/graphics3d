package graphics3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Util {

	private Util() {}
	
	public static String readfile(String path) {
		try { return new String(Files.readAllBytes(Paths.get(path))); }
		catch (IOException e) { throw new RuntimeException("Error reading file [" + path + "]", e); }
	}
	public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }
	
}
