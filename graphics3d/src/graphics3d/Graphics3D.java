package graphics3d;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

public class Graphics3D implements AppInterface, GuiInterface {
	
	private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
	
	public static void main(String[] args) {
		Engine game = new Engine("Graphics3D", new Window.WindowOptions(), new Graphics3D() );
		game.run();
	}
	
	List<Entity> level;
	List<Entity> enemies;
	// Block Texture Variants
	String texture_variants[] = {
			"resources/models/block/block.jpg",
			"resources/models/block/brick.png",
			"resources/models/block/question.jpg",	// Question with coins
			"resources/models/block/pipe.jpg",
			"resources/models/block/brick.png",	// Brick with coins
			"resources/models/block/question.jpg",	// Question with mushroom/flower
			"resources/models/block/brick.png",	// Brick with star
			"resources/models/block/air.png",	// Air with 1 up
	};
	Map<String, String> model_variants;
		
	@Override
	public void init(Window window, Scene scene, Render render) {
		scene.setGui(this);
		// Mario Block default model
		Model blockModel = ModelLoader.loadModel("mario block", "resources/models/block/block.obj",
                scene.getTextureCache());
		scene.addModel(blockModel);
		// Load Texture Variants
		for (String variant : texture_variants)
			scene.getTextureCache()
			.createTexture(variant);
		// Load Model Variants
		model_variants = new HashMap<String, String>();
		for (int i = 0; i < texture_variants.length; i++)
			model_variants.put(texture_variants[i], blockModel.getId());
		
		this.level = MarioLevelLoader.loadLevel(scene, "resources/mariolevel.txt", texture_variants, model_variants);		
		
		scene.getCamera().setPosition(5, 5, 5);
        
        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));
        
        scene.setFlag("Cube Spins", new Flag(false));
        scene.setGui(new GameGui(scene, window));
	}
	
	@Override
	public void escape(Window window, Scene scene) {
		window.getMouseInput().freeMouse();
		if (!scene.paused()) {
			glfwSetCursorPos(window.getHandle(), window.getWidth()/2, window.getHeight()/2);
        	scene.setGui(new PauseScreen(scene, window));
		}
	}
	
	@Override
	public void input(Window window, Scene scene, long deltatime, boolean consumed) {
		if (consumed)
			return;
		float move = deltatime * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        Vector2f displVec = mouseInput.getDisplVec();
        if (window.getMouseInput().captured()) {
        	camera.addRotation(
        			(float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
        			(float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY)
        	);
        }
    }
	
	@Override
    public void drawGui() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean handleInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());
        imGuiIO.addMouseWheelEvent(mouseInput.getScrollX(), mouseInput.getScrollY());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }
	
	@Override
	public void update(Window window, Scene scene, long deltatime) {
		scene.getModelMap().forEach((String _, Model model) -> {
			model.getEntitiesList().forEach((Entity entity) -> {
				if (scene.getFlag("Cube Spins").getValue())
					entity.setRotation(entity.getRotation().rotateXYZ(deltatime*0.01f, deltatime*0.01f, deltatime*0.01f));
				else
					entity.setRotation(new Quaternionf());
				entity.updateModelMatrix();
			});
		});
	}
	
	@Override
	public void cleanup() {
		
	}

}
