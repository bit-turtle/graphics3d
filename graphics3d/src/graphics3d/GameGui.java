package graphics3d;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;

public class GameGui implements GuiInterface {
	
	public GameGui(Scene scene, Window window) {
		window.getMouseInput().captureMouse();
		scene.resume();
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
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
