package graphics3d;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

public class PauseScreen implements GuiInterface {
	private Scene scene;
	private Window window;
	
	public PauseScreen(Scene scene, Window window) {
		this.scene = scene;
		this.window = window;
		scene.pause();
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowSize(150, 104);
		ImGui.setNextWindowPos(window.getWidth()/2-150/2, window.getHeight()/2-104/2);
		ImGui.begin("Game Paused", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		if (ImGui.button("Resume", 134, 20)) {
			scene.setGui(new GameGui(scene, window));
		}
		if (ImGui.button("Settings", 134, 20)) {
			scene.setGui(new SettingsGui(scene, window));
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
