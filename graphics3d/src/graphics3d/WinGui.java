package graphics3d;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

public class WinGui implements GuiInterface {
	private Scene scene;
	private Window window;
	String name;
	
	public WinGui(Scene scene, Window window) {
		this.scene = scene;
		this.window = window;
		scene.pause();
		window.getMouseInput().freeMouse();
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowSize(150, 104);
		ImGui.setNextWindowPos(window.getWidth()/2-150/2, window.getHeight()/2-104/2);
		ImGui.begin("You Win!", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		ImGui.text("Score: "+scene.getFlag("Score").getValue());
		if (ImGui.button("Restart", 134, 20)) {
			scene.getFlag("Time score").setValue(false);
			scene.getFlag("Lives").setValue(3);
			scene.getFlag("Score").setValue(0);
			scene.getFlag("Player auto walk").setValue(false);
			scene.getFlag("Player dies").setValue(false);
			scene.getFlag("Time score").setValue(false);
			scene.setGui(new GameGui(scene, window));
		}
		if (ImGui.button("Save Game", 134, 20)) {
			scene.setGui(new SaveQuitGui(scene, window));
		}
		ImGui.end();
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

        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        return consumed;
	}
}
