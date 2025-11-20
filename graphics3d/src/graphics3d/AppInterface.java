package graphics3d;

public interface AppInterface {

	void init(Window window, Scene scene, Render render);
	
	void input(Window window, Scene scene, long deltatime, boolean consumed);
	
	void update(Window window, Scene scene, long deltatime);
	
	void cleanup();
	
}
