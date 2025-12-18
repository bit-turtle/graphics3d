package graphics3d;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;

public class SaveQuitGui implements GuiInterface {
	private Scene scene;
	private Window window;
	private GameSave saveprogram;
	private float displayedPercent = 0.f;
	
	public SaveQuitGui(Scene scene, Window window) {
		this.scene = scene;
		this.window = window;
		saveprogram = new GameSave(scene);
		saveprogram.start();
	}
	
	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowSize(150, 80);
		ImGui.setNextWindowPos(window.getWidth()/2-150/2, window.getHeight()/2-104/2);
		ImGui.begin((displayedPercent != 1.0f) ? "Saving..." : "Saved!", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		displayedPercent = (saveprogram.percent()+displayedPercent)/2;
		if (displayedPercent != 1.0f) ImGui.progressBar(displayedPercent, Integer.toString((int) Math.round(saveprogram.percent()*100)) + "%");
		else if (ImGui.button("Resume", 134, 20)) {
			scene.setGui(new GameGui(scene, window));
		}
		if (displayedPercent != 1.0f) ImGui.text(saveprogram.activity());
		else if (ImGui.button("Quit", 134, 20)) {
			window.close();
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
