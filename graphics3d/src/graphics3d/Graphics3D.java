package graphics3d;

import java.util.List;

import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Graphics3D implements AppInterface {
	
	public static void main(String[] args) {
		Engine game = new Engine("Graphics3D", new Window.WindowOptions(), new Graphics3D() );
		game.run();
	}
	
	Entity cubeEntity;
	float rotation;
	
	@Override
	public void init(Window window, Scene scene, Render render) {
		Texture texture = scene.getTextureCache().createTexture(TextureCache.DEFAULT_TEXTURE);
        Material material = new Material();
        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = Mesh.generateStar(0.5f, 0.5f, 0.5f, 0.25f);
        material.getMeshList().add(mesh);
        Model cubeModel = new Model("cube-model", materialList);
        scene.addModel(cubeModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
	}
	
	@Override
	public void input(Window window, Scene scene, long deltatime) {
        Vector3f displacement = new Vector3f(0,0,0);
        float scale = 0;
        if (window.isKeyPressed(GLFW_KEY_UP)) 
            displacement.y = 1;
        else if (window.isKeyPressed(GLFW_KEY_DOWN)) 
            displacement.y = -1;
        
        if (window.isKeyPressed(GLFW_KEY_LEFT)) 
            displacement.x = -1;
         else if (window.isKeyPressed(GLFW_KEY_RIGHT)) 
            displacement.x = 1;
        
        if (window.isKeyPressed(GLFW_KEY_A))
            displacement.z = -1;
         else if (window.isKeyPressed(GLFW_KEY_Q)) 
            displacement.z = 1;
        
        if (window.isKeyPressed(GLFW_KEY_Z))
            scale = -1;
        else if (window.isKeyPressed(GLFW_KEY_X))
            scale = 1;

        displacement.mul(deltatime / 1000.0f);

        Vector3f entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displacement.x + entityPos.x, displacement.y + entityPos.y, displacement.z + entityPos.z);
        cubeEntity.setScale(cubeEntity.getScale() + scale);
        cubeEntity.updateModelMatrix();
    }
	
	@Override
	public void update(Window window, Scene scene, long deltatime) {
		rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
	}
	
	@Override
	public void cleanup() {
		
	}

}
