package graphics3d;

import java.util.concurrent.Callable;

public class Engine {
	
	private final AppInterface logic;
	private final Window window;
	private Render render;
	private Scene scene;
	private int targetFps;
	private int targetUps;
	private boolean running;
	
	public Engine(String title, Window.WindowOptions opts, AppInterface logic) {
		Callable<Void> resizeCallback = () -> { resize(); return null; };
		window = new Window(title, opts, resizeCallback);
		this.logic = logic;
		render = new Render(window);
		scene = new Scene(window.getWidth(), window.getHeight());
		targetFps = opts.fps;
		targetUps = opts.ups;
		logic.init(window, scene, render);
		running = true;
	}
		
	private void cleanup() {
		logic.cleanup();
		render.cleanup();
		scene.cleanup();
		window.cleanup();
	}
	
	private void resize() {
		scene.resize(window.getWidth(), window.getHeight());
	}
	
	public void run() {
		long initTime = System.currentTimeMillis();
		float timeU = 1000.f / targetUps;
		float timeR = targetFps > 0 ? 1000.f / targetFps : 0;
		float deltaud = 0;
		float deltafps = 0;
		
		long updateTime = initTime;
		GuiInterface gui = scene.getGui();
		while (running && ! window.windowShouldClose()) {
			window.pollEvents();
			
			long now = System.currentTimeMillis();
			long time = now - initTime;
			deltaud += time/timeU;
			deltafps += time/timeR;
						
			if (deltaud >= 1) {
				long timediff = now - updateTime;
				window.getMouseInput().input();
				window.getMouseInput().input();
                boolean inputConsumed = gui != null && gui.handleInput(scene, window);
                logic.input(window, scene, now - initTime, inputConsumed);
				logic.update(window, scene, timediff);
				updateTime = now;
				deltaud--;
			}
			
			if (targetFps <= 0 || deltafps >= 1) {
				render.render(window, scene);
				deltafps--;
				window.update();
			}
			
			initTime = now;
		}
		
		cleanup();
	}
	
	public void start() {
		running = true;
		run();
	}
	
	public void stop() {
		running = false;
	}
	
}
