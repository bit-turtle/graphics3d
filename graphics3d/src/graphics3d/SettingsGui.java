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
		ImGui.setNextWindowSize(150, 300);
		ImGui.setNextWindowPos(window.getWidth()/2-150/2, window.getHeight()/2-300/2);
		ImGui.begin("Settings", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		if (ImGui.button("Back", 134, 20)) {
			scene.setGui(new PauseScreen(scene, window));
		}
		
		if (ImGui.checkbox("Cube Spins", scene.getFlag("Cube Spins").enabled())) {
			scene.getFlag("Cube Spins").toggleValue();
		}
		
		if (ImGui.checkbox("Frustum Culling", scene.getFlag("Frustum Culling").enabled())) {
			scene.getFlag("Frustum Culling").toggleValue();
		}
		
		if (ImGui.checkbox("Show FPS", scene.getFlag("Show FPS").enabled())) {
			scene.getFlag("Show FPS").toggleValue();
		}
		
		if (ImGui.checkbox("Show Entities", scene.getFlag("Show Rendered Entity Count").enabled())) {
			scene.getFlag("Show Rendered Entity Count").toggleValue();
		}
		
		if (ImGui.checkbox("Show Camera", scene.getFlag("Show Camera Angle").enabled())) {
			scene.getFlag("Show Camera Angle").toggleValue();
		}
		
		if (ImGui.checkbox("Lock Camera", scene.getFlag("Lock Camera").enabled())) {
			scene.getFlag("Lock Camera").toggleValue();
		}
		
		if (ImGui.checkbox("Free Camera", scene.getFlag("Free Camera").enabled())) {
			scene.getFlag("Free Camera").toggleValue();
		}
		
		if (ImGui.checkbox("Show Flags", scene.getFlag("Show Flags").enabled())) {
			scene.getFlag("Show Flags").toggleValue();
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
        imGuiIO.addMouseWheelEvent(mouseInput.getScrollX(), mouseInput.getScrollY());

        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        return consumed;
	}
}
