package graphics3d;

import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Render {

	private SceneRender render;
	
	public Render() {
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		render = new SceneRender();
	}
		
	public void render(Window window, Scene scene) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glViewport(0,0, window.getWidth(), window.getHeight());
		render.render(scene);
	}
	
	public void cleanup() {
		render.cleanup();
	}
	
}
