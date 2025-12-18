package graphics3d;

public class GameSave extends Thread {
	private Scene scene;
	
	private float percent = 0;
	private String activity = "Loading...";
	
	public float percent() {
		return percent;
	}
	
	public String activity() {
		return activity;
	}
	
	public GameSave(Scene scene) {
		this.scene = scene;
	}
	
	@Override
	public void run() {
		for (float i = 0.f; i <= 1.f; i+=0.1f) {
			percent = i;
			activity = "Test";
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		percent = 1.f;
	}
	
	
}
