package graphics3d;

import java.util.concurrent.Callable;

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
	
	private Callable<Void> resize;
	
	public static class WindowOptions {
		public boolean compat = false;
		public int fps = 60;
		public int ups = 120;
		public int width = 900;
		public int height = 720;
    }
	
	public Window(String title, WindowOptions opts, Callable<Void> resize) {
		this.resize = resize;
		
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
		
		// Window Creation
		handle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (handle == NULL)
			throw new RuntimeException("Failed to create GLFW window");
		
		// Set Callbacks
		glfwSetFramebufferSizeCallback(handle,
			(_, w, h) -> {
				width = w;
				height = h;
				try { this.resize.call(); }
				catch (Exception e) {
					System.err.print("Error calling resize callback: ");
					System.err.println(e.getMessage());
				}
			}
		);
		
		glfwSetErrorCallback(
			(err, msg) -> {
				System.err.println("Error " + err + ": " + MemoryUtil.memUTF8(msg));
			}
		);
		
		glfwSetKeyCallback(handle,
			(_, key, _, action, _) -> {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(handle, true);
			}
		);
		
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
	
	public boolean isKeyPressed(int code) {
		return glfwGetKey(handle, code) == GLFW_PRESS;
	}
	
	public void pollEvents() {
		glfwPollEvents();
	}
	
	public void update() {
		glfwSwapBuffers(handle);
	}
	
	public boolean windowShouldClose() {
		return glfwWindowShouldClose(handle);
	}
	
}
