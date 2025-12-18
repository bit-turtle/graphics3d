package graphics3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarioLevelLoader {
	static ArrayList<Entity> loadLevel(Scene scene, String path, String textures[], Map<String, String> models) {
		ArrayList<Entity> level = new ArrayList<Entity>();
		List<String> leveldata = new ArrayList<String>();
		try {
			leveldata = Files.readAllLines(Path.of("resources/data/mariolevel.txt"));
		} catch (IOException e) {}
		int y = leveldata.size();
		for (String row : leveldata) {
			y--;
			for (int x = 0; x < row.length(); x++) {
				char c = row.charAt(x);
				if (c == 0x20)
					continue;
				Entity tile = new Entity(getBlockId(x, y), models.get(textures[c-0x30]));
				level.add(tile);
				tile.setPosition(x, y, 0);
				tile.setTextureVariant(textures[c-0x30]);
				tile.updateModelMatrix();
				scene.addEntity(tile);
			}
			
		}
		return level;
	}
	
	static String getBlockId(int x, int y) {
		return String.format("block %d %d", x, y);
	}
}
