package graphics3d;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

public class SettingsGui implements GuiInterface {
	private Scene scene;
	private Window window;
	
	public SettingsGui(Scene scene, Window window) {
		this.scene = scene;
		this.window = window;
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowSize(150, 154);
		ImGui.setNextWindowPos(window.getWidth()/2-150/2, window.getHeight()/2-104/2);
		ImGui.begin("Settings", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		if (ImGui.button("Back", 134, 20)) {
			scene.setGui(new PauseScreen(scene, window));
		}
		
		if (ImGui.checkbox("Cube Spins", scene.getFlag("Cube Spins").getValue())) {
			scene.getFlag("Cube Spins").toggleValue();
		}
		
		if (ImGui.button("Reload Textures")) {
			scene.getTextureCache().reload();
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
