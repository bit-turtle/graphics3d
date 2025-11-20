package graphics3d;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;

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
	
	Entity cubeEntity;
	float rotation;
	
	@Override
	public void init(Window window, Scene scene, Render render) {
		scene.setGui(this);
		Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cottage/cottage.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);
        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
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
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        Vector2f displVec = mouseInput.getDisplVec();
        camera.addRotation(
    		(float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
            (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY)
        );
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

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }
	
	@Override
	public void update(Window window, Scene scene, long deltatime) {
		rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        //cubeEntity.setRotation(0, 1, 0, (float) Math.toRadians(rotation));
        cubeEntity.updateModelMatrix();
	}
	
	@Override
	public void cleanup() {
		
	}

}
