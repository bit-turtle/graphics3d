package graphics3d;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2i;

public class MarioLevelLoader {
	static HashMap<String, Entity> loadLevel(Scene scene, String path, String textures[], String models[], Map<String, String> enemyTextures) {
		HashMap<String, Entity> level = new HashMap<String, Entity>();
		List<String> leveldata = new ArrayList<String>();
		int id = 0;
		try {
			leveldata = Files.readAllLines(Path.of(path));
		} catch (IOException e) {
			System.out.println("Failed to load mario level");
		}
		int y = leveldata.size();
		for (String row : leveldata) {
			y--;
			for (int x = 0; x < row.length(); x++) {
				char c = row.charAt(x);
				if (c == 0x20)
					continue;
				if (c <= 0x40) {
					Entity tile = new Entity(getBlockId(x, y), models[c-0x30]);
					tile.setPosition(x, y, 0);
					tile.setTextureVariant(textures[c-0x30]);
					tile.setType(String.format("%c", c));
					tile.updateModelMatrix();
					scene.addEntity(tile);
					level.put(tile.getId(), tile);
					if (c == '<') {
						Entity flag = new Entity("flag", "flag");
						flag.setPosition(x, y, 0);
						flag.setType("flag");
						flag.updateModelMatrix();
						scene.addEntity(flag);
					}
				}
				else {
					Entity enemy = null;
					
					switch (c) {
						case 'g':
							enemy = new Entity(String.format("enemy %d", id++), "goomba");
							enemy.setType("goomba");
							enemy.setTextureVariant(enemyTextures.get("goomba"));
							break;
					}
					
					if (enemy == null)
						continue;
					enemy.setPosition(x, y ,0);
					enemy.updateModelMatrix();
					scene.addEntity(enemy);
				}
			}
			
		}
		return level;
	}
	
	static String getBlockId(int x, int y) {
		return String.format("block %d %d", x, y);
	}
	
	static Vector2i getBlockPos(String blockId) {
		Vector2i pos = new Vector2i(0, 0);
		
		String sections[] = blockId.split(" ");
		if (sections.length == 3) {
			pos.x = Integer.parseInt(sections[1]);
			pos.y = Integer.parseInt(sections[2]);
		}
		
		return pos;
	}
}
