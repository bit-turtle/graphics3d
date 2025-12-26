package graphics3d;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private final long handle;
	
	private int width;
	private int height;
	
	private int measuredFPS;
	private int elapsedFrames;
	private long prevUpdate;
		
	private MouseInput mouse;
	private Engine engine;
	
	public static class WindowOptions {
		public int antiAliasing = 0;
		public boolean compat = false;
		public int fps = 0;
		public int ups = 120;
		public int width = 900;
		public int height = 720;
    }
	
	public Window(String title, WindowOptions opts, Engine engine) {
		this.engine = engine;
		
		if (!glfwInit())
        	throw new IllegalStateException("Unable to initialize GLFW");
		
		// GLFW Window Hints
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		
		// OpenGL Settings
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		if (opts.compat)
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		else {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		}
		
		// Window Size
		if (opts.width > 0 && opts.height > 0) {
			width = opts.width;
			height = opts.height;
		}
		else {
			glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
			GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			width = mode.width();
			height = mode.height();
		}
		
        glfwWindowHint(opts.antiAliasing, 4);
		
		// Window Creation
		handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (handle == NULL)
			throw new RuntimeException("Failed to create GLFW window");
		
		// Set Callbacks
		glfwSetFramebufferSizeCallback(handle,
			(_, w, h) -> {
				width = w;
				height = h;
				engine.resize();
			}
		);
		
		glfwSetErrorCallback(
			(err, msg) -> {
				System.err.println("Error " + err + ": " + MemoryUtil.memUTF8(msg));
			}
		);
		
		glfwSetKeyCallback(handle,
			(_, key, _, action, _) -> {
				engine.keyCallback(key, action);
			}
		);
		
		mouse = new MouseInput(handle);
		
		// Prepare Window
		
		glfwMakeContextCurrent(handle);
		
		// Vsync
		
		if (opts.fps > 0)
			glfwSwapInterval(0);
		else
			glfwSwapInterval(1);
		
		// Show Window
		
		glfwShowWindow(handle);
		
		
		// Get Framebuffer Size
		int[] fbwidth = new int[1];
		int[] fbheight = new int[1];
		glfwGetFramebufferSize(handle, fbwidth, fbheight);
		width = fbwidth[0];
		height = fbheight[0];
		
		prevUpdate = System.currentTimeMillis();
	}
	
	public void close() {
		glfwSetWindowShouldClose(handle, true);
	}
	
	public void cleanup() {
		glfwFreeCallbacks(handle);
		glfwDestroyWindow(handle);
		glfwTerminate();
		GLFWErrorCallback callback = glfwSetErrorCallback(null);
		if (callback != null)
			callback.free();
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public long getHandle() {
		return handle;
	}
	
	public MouseInput getMouseInput() {
		return mouse;
	}
	
	public boolean isKeyPressed(int code) {
		return glfwGetKey(handle, code) == GLFW_PRESS;
	}
	
	public void pollEvents() {
		glfwPollEvents();
	}
	
	public int getFPS() {
		return measuredFPS;
	}
	
	public void update() {
		glfwSwapBuffers(handle);
		elapsedFrames++;
		long currentTime = System.currentTimeMillis();
		if (currentTime-prevUpdate > 1000) {
			long timeDiff = currentTime-prevUpdate;
			measuredFPS = (int) (elapsedFrames/(timeDiff/1000));
			prevUpdate = currentTime;
			elapsedFrames = 0;
		}
	}
	
	public boolean windowShouldClose() {
		return glfwWindowShouldClose(handle);
	}
	

	public Engine getEngine() {
		return engine;
	}
	
}
