package graphics3d;

import java.util.Map.Entry;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

public class GameGui implements GuiInterface {
	
	Scene scene;
	Window window;
	
	public GameGui(Scene scene, Window window) {
		window.getMouseInput().captureMouse();
		scene.resume();
		this.scene = scene;
		this.window = window;
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(window.getWidth(), window.getHeight());
		ImGui.begin("Data Window", ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoInputs | ImGuiWindowFlags.NoMouseInputs | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar);
		ImGui.beginTable("Game Info", 4);
		ImGui.tableNextRow();
		ImGui.tableSetColumnIndex(0);
		ImGui.text("SCORE: "+scene.getFlag("Score").getValue());
		ImGui.tableSetColumnIndex(1);
		ImGui.text("COINS: "+scene.getFlag("Coins").getValue());
		ImGui.tableSetColumnIndex(2);
		ImGui.text("LIVES: "+scene.getFlag("Lives").getValue());
		ImGui.tableSetColumnIndex(3);
		ImGui.text("TIME: "+scene.getFlag("Time").getValue());
		ImGui.endTable();
		if (scene.getFlag("Show FPS").enabled())
			ImGui.text("FPS: " + window.getFPS());
		if (scene.getFlag("Show Rendered Entity Count").enabled())
			ImGui.text("Rendered Entities: " + scene.getFlag("Current Rendered Entity Count").getValue());
		if (scene.getFlag("Show Camera Angle").enabled()) {
			Quaternionf angle = scene.getCamera().getRotation();
			ImGui.text(String.format("Camera: %.2f, %.2f, %.2f, %.2f", angle.x, angle.y, angle.z, angle.w));
		}
		if (scene.getFlag("Show Flags").enabled()) {
			ImGui.text("Flags:");
			for (Entry<String, Flag> flag : scene.getFlags().entrySet()) {
				ImGui.text("- " + flag.getKey() + ": " + flag.getValue().getValue());
			}
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
