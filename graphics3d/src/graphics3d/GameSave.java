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
		for (float i = 0.f; i <= 1.f; i+=0.01f) {
			percent = i;
			if (i <= 0.25)
			activity = "Score: " + (scene.getFlag("Score").getValue());
			else if (i <= 0.5)
				activity = "Lives: " + (scene.getFlag("Lives").getValue());
			else if (i <= 0.75)
				activity = "Coins: " + (scene.getFlag("Coins").getValue());
			else 
				activity = "Complete!";

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		percent = 1.f;
	}
	
	
}
