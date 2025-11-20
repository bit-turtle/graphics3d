package graphics3d;

import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Render {

	private SceneRender render;
	private GuiRender gui;
	
	public Render(Window window) {
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
		render = new SceneRender();
		gui = new GuiRender(window);
	}
		
	public void render(Window window, Scene scene) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glViewport(0,0, window.getWidth(), window.getHeight());
		render.render(scene);
		gui.render(scene);
	}
	
	public void cleanup() {
		render.cleanup();
		gui.cleanup();
	}
	
	public void resize(int width, int height) {
		gui.resize(width, height);
	}
	
}
