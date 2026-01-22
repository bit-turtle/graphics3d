package graphics3d;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.ImGuiIO;

public class Graphics3D implements AppInterface, GuiInterface {
	
	private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.008f;
    private static final float MOVEMENT_ACCEL = 0.00001f;
    
    private static final float GRAVITY = 0.00005f;
    private static final float BLOCK_BUMP = 0.0075f;
    private static final float BLOCK_GRAVITY = 0.00008f;
    private static final float COIN_BOUNCE = 0.015f;
    private static final float JUMP = 0.021f;
    private static final float HOP = 0.01f;
    private static final float SCORE_TEXT_SPEED = 0.005f;
    private static final float POWERUP_RISE_SPEED = 0.002f;
    
    private static final long SCORE_TEXT_TIME = 500;
    private static final long MULTIPLE_COIN_BRICK_TIME = 3800;
    
    private static final float SCORE_MODEL_SCALE = 0.8f;
    private static final float COIN_MODEL_SCALE = 0.5f;
    
    private static final float POWERUP_HEIGHT = 2.0f;
    private static final float MUSHROOM_SPEED = 0.0025f;
    
    private static final float POWERUP_OFFSET = 0.50f;
    
    private static final float FELL_OFF_A_CLIFF = -32.f;
    
    private static final long TIME = 400;
    private static final float TIME_SCALE = 2.5f;
    
    private static final float HITBOX_WIDTH = 0.8f;
    private static final float HITBOX_HEIGHT = 1.0f;
    
    private static final Vector3f RESPAWN_LOCATION = new Vector3f(20, 5, 0);
    
    private static final float DIE_BOUNCE = 0.030f;
    private static final float DIE_ZVEL = 0.002f;
    
    private static final long INVINCIBLE = 3000;
    
    private static final float BLOCK_ZVEL = 0.003f;
    
    private static final long BIG_ANIMATION_TIME = 500;
    
    private static final float ENEMY_BOUNCE = 0.01f;
    
    private static final float GOOMBA_SPEED = 0.003f;
    
    private static final float ENEMY_ACTIVATION_DIST = 12.f;
    
    private static final float POLE_5000 = 12.f;
    private static final float POLE_4000 = 10.f;
    private static final float POLE_2000 = 9.f;
    private static final float POLE_800 = 8.f;
    private static final float POLE_400 = 7.f;
    
    private static final long SQUISH_TIME = 2000;
    
    private static final long STAR_TIME = 8000;
    private static final float STAR_BOUNCE = 0.018f;
    
    private int nextid = 0;
	
	public static void main(String[] args) {
		Engine game = new Engine("Graphics3D", new Window.WindowOptions(), new Graphics3D() );
		game.run();
	}
	
	Vector3f cameraPosition = new Vector3f(0, 10, 0);
	float cameraZoom = 15.f;
	
	Entity player;
	
	Map<String, Entity> level;
	// Block Texture Variants
	String texture_variants[] = {
			/*0*/ "resources/models/block/block.jpg",
			/*1*/ "resources/models/block/brick.png",
			/*2*/ "resources/models/block/question.jpg",	// Question with coins
			/*3*/ "resources/models/pipe/pipe.png",
			/*4*/ "resources/models/block/brick.png",	// Brick with coins
			/*5*/ "resources/models/block/question.jpg",	// Question with mushroom/flower
			/*6*/ "resources/models/block/brick.png",	// Brick with star
			/*7*/ "resources/models/block/air.png",	// Air with 1 up
			/*8*/ "resources/models/pipe/pipe.png",	// Pipe top
			/*9*/ "resources/models/block/stair.png",	// Stairs
			/*:*/ "resources/models/block/air.png",	// : Invisible block
			/*;*/ "resources/models/flag/pole.png",	// ; Pole
			/*<*/ "resources/models/flag/poletop.png",	// < Pole Top
			/*=*/ "resources/models/block/blank.png",	// Blank question
	};
	String model_variants[];
	Map<String, String> enemy_texture_variants;
	
	String score_models[] = {
			"100",
			"200",
			"300",
			"400",
			"500",
			"600",
			"700",
			"800",
			"900",
			"1000",
			"2000",
			"3000",
			"4000",
			"5000",
			"6000",
			"7000",
			"8000",
			"9000",
			"10000",
			"1UP"
	};
		
	@Override
	public void init(Window window, Scene scene, Render render) {
		scene.setGui(this);		
		// Mario Block default model
		Model blockModel = ModelLoader.loadModel("mario block", "resources/models/block/block.obj", scene.getTextureCache(), false);
		scene.addModel(blockModel);
		Model airModel = ModelLoader.loadModel("air block", "resources/models/block/air.obj", scene.getTextureCache(), false);
		scene.addModel(airModel);
		Model groundModel = ModelLoader.loadModel("ground block", "resources/models/block/ground.obj", scene.getTextureCache(), false);
		scene.addModel(groundModel);
		Model pipeModel = ModelLoader.loadModel("pipe block", "resources/models/pipe/pipe.obj", scene.getTextureCache(), false);
		scene.addModel(pipeModel);
		Model toppipeModel = ModelLoader.loadModel("pipetop block", "resources/models/pipe/pipetop.obj", scene.getTextureCache(), false);
		scene.addModel(toppipeModel);
		Model poleModel = ModelLoader.loadModel("pole block", "resources/models/flag/pole.obj", scene.getTextureCache(), false);
		scene.addModel(poleModel);
		Model toppoleModel = ModelLoader.loadModel("poletop block", "resources/models/flag/poletop.obj", scene.getTextureCache(), false);
		scene.addModel(toppoleModel);
		Model coinModel = ModelLoader.loadModel("coin", "resources/models/coin/coin.obj", scene.getTextureCache(), false);
		scene.addModel(coinModel);
		Model flagModel = ModelLoader.loadModel("flag", "resources/models/flag/flag.obj", scene.getTextureCache(), false);
		scene.addModel(flagModel);
		Model mushroomModel = ModelLoader.loadModel("mushroom", "resources/models/mushroom/mushroom.obj", scene.getTextureCache(), false);
		scene.addModel(mushroomModel);
		Model upshroomModel = ModelLoader.loadModel("upshroom", "resources/models/mushroom/upshroom.obj", scene.getTextureCache(), false);
		scene.addModel(upshroomModel);
		Model goombaModel = ModelLoader.loadModel("goomba", "resources/models/flat/flat.obj", scene.getTextureCache(), false);
		scene.addModel(goombaModel);
		Model starModel = ModelLoader.loadModel("star", "resources/models/flat/flat.obj", scene.getTextureCache(), false);
		scene.addModel(starModel);
		// Load Score Models
		for (String modelId : score_models) {
			Model scoreModel = ModelLoader.loadModel(modelId, "resources/models/score/" + modelId + ".obj", scene.getTextureCache(), false);
			scene.addModel(scoreModel);
		}
		// Load Texture Variants
		for (String variant : texture_variants)
			scene.getTextureCache()
			.createTexture(variant);
		// Load Model Variants
		model_variants = new String[texture_variants.length];
		for (int i = 0; i < texture_variants.length; i++)
			model_variants[i] = blockModel.getId();
		model_variants[0] = groundModel.getId();
		model_variants[3] = pipeModel.getId();
		model_variants[7] = airModel.getId();
		model_variants[8] = toppipeModel.getId();
		model_variants[10] = airModel.getId();
		model_variants[11] = poleModel.getId();
		model_variants[12] = toppoleModel.getId();
		// Load enemy texture variants
		enemy_texture_variants = new HashMap<String, String>();
		enemy_texture_variants.put("goomba", "resources/models/flat/goomba.png");
		enemy_texture_variants.forEach((String _, String texture) -> {
			scene.getTextureCache()
			.createTexture(texture);
		});
		
		
		this.level = MarioLevelLoader.loadLevel(scene, "resources/data/mariolevel.txt", texture_variants, model_variants, enemy_texture_variants);		
		
		scene.getCamera().setPosition(5, 5, 5);
        
        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.6f);
        sceneLights.getDirectionalLight().setDirection(new Vector3f(0, 0.5f, 1));
        sceneLights.getDirectionalLight().setIntensity(0.5f);
        scene.setLights(sceneLights);
        
        // Setup Flags
        scene.setFlag("Cube Spins", new Flag(false));
        scene.setFlag("Frustum Culling", new Flag(true));
        scene.setFlag("Show FPS", new Flag(false));
        scene.setFlag("Show Rendered Entity Count", new Flag(false));
        scene.setFlag("Current Rendered Entity Count", new Flag(0));
        scene.setFlag("Show Camera Angle", new Flag(false));
        scene.setFlag("Player on Ground", new Flag(false));
        scene.setFlag("Lock Camera", new Flag(true));
        scene.setFlag("Free Camera", new Flag(false));
        scene.setFlag("Player auto walk", new Flag(false));
        scene.setFlag("Player flagpole slide", new Flag(false));
        scene.setFlag("Player dies", new Flag(false));
        scene.setFlag("Show Flags", new Flag(false));
        scene.setFlag("Coins", new Flag(0));
        scene.setFlag("Score", new Flag(0));
        scene.setFlag("Lives", new Flag(3));
        scene.setFlag("Timer", new Flag(0));
        scene.setFlag("Time", new Flag(0));
        scene.setFlag("Enemies Bounced", new Flag(0));
        

        scene.setFlag("Big mario", new Flag(false));
        scene.setFlag("Fire mario", new Flag(false));
        scene.setFlag("Invincibility", new Flag(0));
        scene.setFlag("Level reset", new Flag(false));
        scene.setFlag("Time score", new Flag(false));
        scene.setFlag("Mario star", new Flag(0));
        scene.setGui(new GameGui(scene, window));
        
        player = new Entity("player entity", toppoleModel.getId());
        player.setTextureVariant("resources/models/block/question.jpg");
        player.setType("player");
        player.setPosition(RESPAWN_LOCATION.x, RESPAWN_LOCATION.y, RESPAWN_LOCATION.z);
        scene.addEntity(player);
        
        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(2099999999);
        scene.setSky(skyBox);
	}
	
	public void die(Scene scene, Window window) {
		scene.getFlag("Lives").increment(-1);
        player.setPosition(RESPAWN_LOCATION.x, RESPAWN_LOCATION.y, RESPAWN_LOCATION.z);
        player.setVelocity(new Vector3f());
        cameraPosition.x = RESPAWN_LOCATION.x;
        scene.getFlag("Timer").setValue(0);
        scene.getFlag("Time").setValue(400);
        scene.getFlag("Time score").setValue(false);
        scene.getFlag("Big mario").setValue(false);
        scene.getFlag("Fire mario").setValue(false);
        scene.getFlag("Level reset").setValue(true);
		if (scene.getFlag("Player auto walk").enabled()) {
			scene.pause();
			scene.setGui(new WinGui(scene, window));
		}
	} 
	public void fall(Scene scene) {
		if (scene.getFlag("Big mario").enabled() && !scene.getFlag("Player auto walk").enabled()) {
			scene.getFlag("Big mario").setValue(false);
			scene.getFlag("Fire mario").setValue(false);
			scene.getFlag("Invincibility").setValue((int)INVINCIBLE);
		}
		if (scene.getFlag("Invincibility").getValue() <= 0) {
			player.setVelocity(new Vector3f(0, DIE_BOUNCE, 0));
			scene.getFlag("Player dies").setValue(true);
		}
	}
	
	@Override
	public void escape(Window window, Scene scene) {
		window.getMouseInput().freeMouse();
		if (!scene.paused()) {
			glfwSetCursorPos(window.getHandle(), window.getWidth()/2, window.getHeight()/2);
        	scene.setGui(new PauseScreen(scene, window));
		}
	}
	
	@Override
	public void input(Window window, Scene scene, long deltatime, boolean consumed) {
		if (consumed)
			return;
		Vector3f velocity = player.getVelocity();
		float move = deltatime * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        }
        
        if (!scene.getFlag("Player auto walk").enabled() && !scene.getFlag("Player dies").enabled()) {
	        if (window.isKeyPressed(GLFW_KEY_A)) {
	            velocity.x -= MOVEMENT_ACCEL * deltatime;
	            if (velocity.x < -MOVEMENT_SPEED && !window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
	            	velocity.x = -MOVEMENT_SPEED;
	            }
	        } else if (window.isKeyPressed(GLFW_KEY_D)) {
	            velocity.x += MOVEMENT_ACCEL * deltatime;
	            if (velocity.x > MOVEMENT_SPEED && !window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
	            	velocity.x = MOVEMENT_SPEED;
	            }
	        }
	        else {
	        	float initvel = velocity.x;
	        	if (velocity.x > 0) {
	        		velocity.x -= MOVEMENT_ACCEL*deltatime;
	        	}
	        	else if (velocity.x < 0) {
	        		velocity.x += MOVEMENT_ACCEL*deltatime;
	        	}
	        	if ((velocity.x > 0) ^ (initvel > 0))
	        		velocity.x = 0;
	        }
	        if (window.isKeyPressed(GLFW_KEY_SPACE) && scene.getFlag("Player on Ground").enabled()) {
	           velocity.y = JUMP;
	        }
        }
        player.setVelocity(velocity);

        MouseInput mouseInput = window.getMouseInput();
        Vector2f displVec = mouseInput.getDisplVec();
        if (window.getMouseInput().captured() && !scene.getFlag("Lock Camera").enabled()) {
        	camera.addRotation(
        			(float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
        			(float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY)
        	);
        	cameraZoom += mouseInput.getScrollY();
        	if (cameraZoom < 1.f)
        		cameraZoom = 1.f;
        }
    }
	
	@Override
    public void drawGui() {
        ImGui.newFrame();
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
        imGuiIO.addMouseWheelEvent(mouseInput.getScrollX(), mouseInput.getScrollY());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }
	
	@Override
	public void update(Window window, Scene scene, long deltatime) {
		scene.getFlag("Mario star").time(-deltatime);
		if (scene.getFlag("Mario star").getValue() < 0) {
			scene.getFlag("Mario star").setValue(0);
			player.setHue(0);
		}
		else
			player.setHue((int)(player.getHue()+deltatime));
		SceneLights lights = scene.getLights();
		lights.getPointLights().clear();
		lights.getSpotLights().clear();
		ArrayList<Entity> toBeRemoved = new ArrayList<Entity>();
		ArrayList<Entity> toBeAdded = new ArrayList<Entity>();
		if (!scene.getFlag("Player auto walk").enabled()) {
			scene.getFlag("Timer").time(deltatime);
			scene.getFlag("Time").setValue((int)(TIME-scene.getFlag("Timer").getValue()*TIME_SCALE/1000));
		}
		else if (scene.getFlag("Time score").enabled() && scene.getFlag("Time").getValue() > 0) {
			scene.getFlag("Time").increment(-1);
			scene.getFlag("Score").increment(10);
		}
		if (scene.getFlag("Invincibility").getValue() > 0) scene.getFlag("Invincibility").increment((int)-deltatime);
		scene.getModelMap().forEach((String _, Model model) -> {
			model.getEntitiesList().forEach((Entity entity) -> {
				// Spin
				if (scene.getFlag("Cube Spins").enabled())
					entity.setRotation(entity.getRotation().rotateXYZ(deltatime*0.01f, deltatime*0.01f, deltatime*0.01f));
				
				if (entity.getType().equals("coin")) {
					Vector3f position = entity.getPosition();
					Vector3f velocity = entity.getVelocity();
					velocity.y -= GRAVITY*deltatime;
					position.y += velocity.y*deltatime;
					entity.setVelocity(velocity);
					entity.setPosition(position.x, position.y, position.z);
					entity.setRotation(entity.getRotation().rotateY(deltatime));
					if (velocity.y <= 0)
						entity.setScale(0.5f*(1-velocity.y/(-COIN_BOUNCE/1.5f)));
					if (velocity.y <= -COIN_BOUNCE/1.5) {
						toBeRemoved.add(entity);
						scene.getFlag("Score").increment(200);
						Entity score = new Entity(entity.getId()+" score", "200");
						score.setType("score");
						scene.setFlag(score.getId() + " time", new Flag(0));
						score.setPosition(position.x, position.y, position.z);
						score.setRotation(new Quaternionf().rotateX(90));
						score.setScale(SCORE_MODEL_SCALE);
						scene.addEntity(score);
					}
				}
				else if (entity.getType().equals("score")) {
					Vector3f position = entity.getPosition();
					scene.getFlag(entity.getId() + " time").time(deltatime);
					position.y += SCORE_TEXT_SPEED*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					if (scene.getFlag(entity.getId()+ " time").getValue() > SCORE_TEXT_TIME) {
						toBeRemoved.add(entity);
						scene.getFlags().remove(entity.getId() + " time");
					}
				}
				else if (entity == player && scene.getFlag("Player flagpole slide").enabled()) {
					Vector3f position = entity.getPosition();
					Vector3f velocity = entity.getVelocity();
					
					velocity.y = -MOVEMENT_SPEED;
					
					entity.setVelocity(velocity);
					
					position.y += velocity.y * deltatime;
					
					if (position.y <= 5.0f) {
						if (scene.getFlag("Player flagpole slide").getValue() == 1) {
							position.x+=0.75f;
							scene.getFlag("Player flagpole slide").setValue(2);
						}
						position.y = 5.0f;
					}
					
					entity.setPosition(position.x, position.y, position.z);
				}
				else if (entity.getType().equals("flag") && scene.getFlag("Player flagpole slide").enabled()) {
					Vector3f position = entity.getPosition();
					Vector3f velocity = entity.getVelocity();
					
					velocity.y = -MOVEMENT_SPEED;
					
					entity.setVelocity(velocity);
					
					position.y += velocity.y * deltatime;
					
					if (position.y <= 5.0f) {
						position.y = 5.0f;
						player.setVelocity(new Vector3f(MOVEMENT_SPEED, HOP, 0.f));
						player.setPosition(entity.getPosition().x+1.f, 5.f, 0.f);
						scene.getFlag("Player flagpole slide").setValue(false);
					}
					
					entity.setPosition(position.x, position.y, position.z);
				}
				else if (entity.getType().equals("powerup")) {
					String id[] = entity.getId().split(" ");
					float targetHeight = Float.parseFloat(id[1]);
					Vector3f position = entity.getPosition();
					position.y+=POWERUP_RISE_SPEED*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					if (position.y >= targetHeight+POWERUP_HEIGHT) {
						position.y = targetHeight+POWERUP_HEIGHT+0.0001f;
						entity.setPosition(position.x, position.y, position.z);
						entity.setType(id[0]);
					}
				}
				else if (entity.getType().equals("squished")) {
					scene.getFlag(entity.getId()+" time").time(deltatime);
					long time = scene.getFlag(entity.getId()+" time").getValue();
					if (time>SQUISH_TIME)
						toBeRemoved.add(entity);
					float squish = 0.25f;
					entity.setScaleVector(new Vector3f(1, squish, 1));
				}
				else if (entity.getType().length() > 1) {
					
					
					Vector3f position = entity.getPosition();
					Vector3f velocity = entity.getVelocity();
					
					if (entity.getType().equals("goomba")) {
						if (velocity.x == 0 && player.getPosition().x+ENEMY_ACTIVATION_DIST >= position.x) {
							velocity.x = -GOOMBA_SPEED;
						}
						entity.flipTextureX((scene.getFlag("Time").getValue()%2 == 0));
					}
					// Physics
					velocity.y -= GRAVITY*deltatime;
					
					entity.setVelocity(velocity);
					
					if (entity.getType().equals("player") && scene.getFlag("Player dies").enabled() ) {
						velocity.z = DIE_ZVEL;
						position.z += velocity.z*deltatime;
					}
					
					// X Movement
					position.x += velocity.x*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					int leftX = (int) Math.floor(entity.getPosition().x+(1-HITBOX_WIDTH));
					int bottomY = (int) Math.floor(entity.getPosition().y);
					int rightX = (int) Math.ceil(entity.getPosition().x-(1-HITBOX_WIDTH));
					int topY = (int) Math.ceil(entity.getPosition().y);
					
					// Collision check X
					Entity leftEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, topY), null);
					Entity leftEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, bottomY), null);
					Entity rightEntity = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, topY), null);
					Entity rightEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, bottomY), null);
					
					if ( !(entity.getType().equals("player") ) || !scene.getFlag("Player dies").enabled() ) {
					if (velocity.x < 0 && (leftEntity != null || leftEntity2 != null) ) {
						position.x = (float) Math.floor(position.x)+HITBOX_WIDTH;
						if (entity.getType().equals("mushroom") || entity.getType().equals("goomba") || entity.getType().equals("star")) {
							velocity.x = -velocity.x;
						}
						else
							velocity.x = 0;
					}
					if (velocity.x > 0 && (rightEntity != null || rightEntity2 != null) ) {
						position.x = (float) Math.floor(position.x)+(1-HITBOX_WIDTH);
						if (entity.getType().equals("mushroom") || entity.getType().equals("goomba") || entity.getType().equals("star")) {
							velocity.x = -velocity.x;
						}
						else
							velocity.x = 0;
						
						if (entity == player) {
							Entity hitBlock = (rightEntity != null) ? rightEntity : rightEntity2;
							if (hitBlock.getType().equals(";") || hitBlock.getType().equals("<")) {
								scene.getFlag("Player auto walk").setValue(true);
								scene.getFlag("Player flagpole slide").setValue(true);
								position.x = hitBlock.getPosition().x-0.25f;
								position.y = hitBlock.getPosition().y;
								position.z = hitBlock.getPosition().z;
								int points = 100;
								if (position.y > POLE_5000) {
									points = 5000;
								}
								else if (position.y > POLE_4000) {
									points = 4000;
								}else if (position.y > POLE_2000) {
									points = 2000;
								}else if (position.y > POLE_800) {
									points = 800;
								}else if (position.y > POLE_400) {
									points = 400;
								}
								scene.getFlag("Score").increment(points);
								Entity score = new Entity(entity.getId()+" score", Integer.toString(points));
								score.setType("score");
								scene.setFlag(score.getId() + " time", new Flag(-4000));
								score.setPosition(position.x+2, 5.f, position.z);
								score.setRotation(new Quaternionf().rotateX(90));
								score.setScale(SCORE_MODEL_SCALE);
								scene.addEntity(score);
								
							}
						}
					}
					}
					
					if (entity.getType().equals("player") && scene.getFlag("Big mario").enabled()) {
					// Big X Movement
					entity.setPosition(position.x, position.y, position.z);
					leftX = (int) Math.floor(entity.getPosition().x+(1-HITBOX_WIDTH));
					bottomY = (int) Math.floor(entity.getPosition().y+1);
					rightX = (int) Math.ceil(entity.getPosition().x-(1-HITBOX_WIDTH));
					topY = (int) Math.ceil(entity.getPosition().y+1);
					
					// Collision check X
					leftEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, topY), null);
					leftEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, bottomY), null);
					rightEntity = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, topY), null);
					rightEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, bottomY), null);
					
					if ( !entity.getType().equals("player") || !scene.getFlag("Player dies").enabled() ) {
					if (velocity.x < 0 && (leftEntity != null || leftEntity2 != null) ) {
						position.x = (float) Math.floor(position.x)+HITBOX_WIDTH;
						velocity.x = 0;
					}
					if (velocity.x > 0 && (rightEntity != null || rightEntity2 != null) ) {
						position.x = (float) Math.floor(position.x)+(1-HITBOX_WIDTH);
						velocity.x = 0;
					}
					}
					}
					
					// Y Movement
					position.y += velocity.y*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					int centerX = (int) Math.round(entity.getPosition().x);
					bottomY = (int) Math.floor(entity.getPosition().y);
					topY = (int) Math.ceil(entity.getPosition().y);
					leftX = (int) Math.floor(entity.getPosition().x+(1-HITBOX_WIDTH));
					rightX = (int) Math.ceil(entity.getPosition().x-(1-HITBOX_WIDTH));
					
					if (position.x >= centerX-(1-HITBOX_WIDTH)) {
						leftX = centerX;
					}
					if (position.x-(1-HITBOX_WIDTH) <= centerX) {
						rightX = centerX;
					}
					
					
					if (entity.getType().equals("player") && scene.getFlag("Big mario").enabled()) {
						topY = (int) Math.ceil(entity.getPosition().y+1);
					}
					
					// Land
					Entity groundEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, bottomY), null);
					Entity groundEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, bottomY), null);
					Entity ceilingEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, topY), null);
					Entity ceilingEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, topY), null);
					
					boolean onGround = false;

					if ( !(entity.getType().equals("player") && scene.getFlag("Player dies").enabled()) ) {
					if (velocity.y > 0 && (ceilingEntity != null || ceilingEntity2 != null) ) {
						position.y = (float) Math.floor(position.y);
						velocity.y = 0;
						
						if (entity == player) {
							Entity bumpedBlock = (leftX == centerX) ? ceilingEntity : ceilingEntity2;
							if (bumpedBlock == null && bumpedBlock == ceilingEntity)
								bumpedBlock = ceilingEntity2;
							if (bumpedBlock != null && bumpedBlock.getVelocity().y == 0) {
								if (!bumpedBlock.getType().equals("~") && !bumpedBlock.getType().equals(";") && !bumpedBlock.getType().equals("<")) {
									Vector3f blockVel = bumpedBlock.getVelocity();
									blockVel.y = BLOCK_BUMP;
									bumpedBlock.setVelocity(blockVel);
								}
								
								if (bumpedBlock.getType().equals("2")) {
									Entity coin = new Entity("coin " + nextid++ + " " + bumpedBlock.getId(), "coin");
									coin.setPosition(bumpedBlock.getPosition().x+0.5f, bumpedBlock.getPosition().y+1.0f, 0.5f);
									coin.setType("coin");
									coin.setVelocity(new Vector3f(0, COIN_BOUNCE, 0));
									coin.setScale(COIN_MODEL_SCALE);
									scene.addEntity(coin);
									scene.getFlag("Coins").increment(1);
									bumpedBlock.setType("~");
									bumpedBlock.setTextureVariant("resources/models/block/blank.png");
								}
								else if (bumpedBlock.getType().equals("4")) {
									if (scene.getFlag(bumpedBlock.getId() + " coin timer") == null)
										scene.setFlag(bumpedBlock.getId() + " coin timer", new Flag(0));
									Entity coin = new Entity("coin " + nextid++ + " " + bumpedBlock.getId(), "coin");
									coin.setPosition(bumpedBlock.getPosition().x+0.5f, bumpedBlock.getPosition().y+1.0f, 0.5f);
									coin.setType("coin");
									coin.setVelocity(new Vector3f(0, COIN_BOUNCE, 0));
									coin.setScale(COIN_MODEL_SCALE);
									scene.getFlag("Coins").increment(1);
									scene.addEntity(coin);
									if (scene.getFlag(bumpedBlock.getId() + " coin timer").getValue() >= MULTIPLE_COIN_BRICK_TIME) {
										bumpedBlock.setType("~");
										bumpedBlock.setTextureVariant("resources/models/block/blank.png");
										scene.getFlags().remove(bumpedBlock.getId() + " coin timer");
									}
								}
								else if (bumpedBlock.getType().equals("5")) {
									bumpedBlock.setType("~");
									bumpedBlock.setTextureVariant("resources/models/block/blank.png");
									Entity powerup = new Entity("mushroom " + (position.y+(scene.getFlag("Big mario").enabled() ? 1.f : 0.f)), "mushroom");
									powerup.setPosition(bumpedBlock.getPosition().x, bumpedBlock.getPosition().y+POWERUP_OFFSET, 0);
									powerup.setType("powerup");
									powerup.updateModelMatrix();
									powerup.setVelocity(new Vector3f(MUSHROOM_SPEED, 0, 0));
									scene.addEntity(powerup);
								} else if (bumpedBlock.getType().equals("6")) {
									bumpedBlock.setType("~");
									bumpedBlock.setTextureVariant("resources/models/block/blank.png");
									Entity powerup = new Entity("star " + (position.y+(scene.getFlag("Big mario").enabled() ? 1.f : 0.f)), "star");
									powerup.setPosition(bumpedBlock.getPosition().x, bumpedBlock.getPosition().y+POWERUP_OFFSET, 0);
									powerup.setType("powerup");
									powerup.updateModelMatrix();
									powerup.setVelocity(new Vector3f(MUSHROOM_SPEED, 0, 0));
									scene.addEntity(powerup);
								} 
								else if (bumpedBlock.getType().equals("7")) {
									toBeRemoved.add(bumpedBlock);
									Entity blank = new Entity(bumpedBlock.getId(), "mario block");
									blank.setType("~");
									blank.setPosition(bumpedBlock.getPosition().x, bumpedBlock.getPosition().y, bumpedBlock.getPosition().z);
									blank.setTextureVariant("resources/models/block/blank.png");
									blank.updateModelMatrix();
									toBeAdded.add(blank);
									level.replace(bumpedBlock.getId(), bumpedBlock, blank);
									level.put(bumpedBlock.getId(), blank);
									Entity powerup = new Entity("upshroom " + (position.y+(scene.getFlag("Big mario").enabled() ? 1.f : 0.f)), "upshroom");
									powerup.setPosition(bumpedBlock.getPosition().x, bumpedBlock.getPosition().y+POWERUP_OFFSET, 0);
									powerup.setType("powerup");
									powerup.updateModelMatrix();
									powerup.setVelocity(new Vector3f(MUSHROOM_SPEED, 0, 0));
									scene.addEntity(powerup);
								}
								else if (bumpedBlock.getType().equals("1")) {
									if (scene.getFlag("Big mario").enabled()) {
										scene.getFlag("Score").increment(50);
										level.remove(bumpedBlock.getId());
										bumpedBlock.setType("d");
									}
								}
								
							}
						}
					}
					if (velocity.y <= 0 && (groundEntity != null || groundEntity2 != null) ) {
						position.y = (float) Math.floor(position.y)+1.f;
						velocity.y = 0;
						onGround = true;
						if (entity.getType().equals("star")) {
							velocity.y = STAR_BOUNCE;
						}
					}
					}
					
					entity.setPosition(position.x, position.y, position.z);
					entity.setVelocity(velocity);
										
					if (entity == player) {
						
						if (scene.getFlag("Invincibility").getValue() > 0) {
							if (player.hidden())
								player.show();
							else
								player.hide();
						}
						else
							player.show();
						if (scene.getFlag("Big mario").enabled()) {
							if (scene.getFlag("Big mario").getValue() < BIG_ANIMATION_TIME) {
								scene.getFlag("Big mario").time(deltatime);
								if (Math.ceil(scene.getFlag("Big mario").getValue()/80)%2 == 0) 
									player.setScale(1);
								else
									player.setScaleVector(new Vector3f(1, 2, 1));
									
							}
							else 
							
								player.setScaleVector(new Vector3f(1, 2, 1));
						}
						else
							player.setScale(1);
						
						
						scene.getFlag("Player on Ground").setValue(onGround);
						if (onGround) {
							scene.getFlag("Enemies Bounced").setValue(0);
						
							if (scene.getFlag("Player auto walk").enabled())
								scene.getFlag("Time score").setValue(true);
						}
						
						if (entity.getPosition().y < FELL_OFF_A_CLIFF) {
							die(scene, window);
							scene.getFlag("Player dies").setValue(false);
						}
						
						Vector3f lightPos = new Vector3f(0.5f, 0.5f, 0.5f);
						lightPos.add(entity.getPosition());
						PointLight light = new PointLight(
							new Vector3f(1,1,1),
							lightPos,
							0.4f
						);
						SpotLight spotlight = new SpotLight(light, new Vector3f(0, -1, 0), 60.f);
						lights.getSpotLights().add(spotlight);
						// 3rd Person Camera
						if (!scene.getFlag("Free Camera").enabled() && !scene.getFlag("Player auto walk").enabled()) {
							if (cameraPosition.x < entity.getPosition().x)
								cameraPosition.x = entity.getPosition().x;
							scene.getCamera().setPosition(cameraPosition.x+0.5f, cameraPosition.y+0.5f, 0.5f);
							scene.getCamera().moveBackwards(cameraZoom);
						}
					}
					
					if (!entity.getType().equals("player") && !entity.getType().equals("flag")) {
						Vector3f playerpos = player.getPosition();
						if (
								!scene.getFlag("Player dies").enabled() &&
								position.x >= playerpos.x-HITBOX_WIDTH &&
								position.x-HITBOX_WIDTH <= playerpos.x &&
								position.y >= playerpos.y-HITBOX_HEIGHT &&
								position.y-HITBOX_HEIGHT <= playerpos.y
						) {
							// Player Collision
							
							// Powerups
							if (entity.getType().equals("mushroom")) {
								scene.getFlag("Score").increment(1000);
								toBeRemoved.add(entity);
								if (scene.getFlag("Big mario").enabled())
									scene.getFlag("Big mario").setValue(1000000000);
								else
									scene.getFlag("Big mario").setValue(true);
								Entity score = new Entity(entity.getId()+" score", "1000");
								score.setType("score");
								scene.setFlag(score.getId() + " time", new Flag(0));
								score.setPosition(position.x, position.y, position.z);
								score.setRotation(new Quaternionf().rotateX(90));
								score.setScale(SCORE_MODEL_SCALE);
								scene.addEntity(score);
							}
							else if (entity.getType().equals("upshroom")) {
								scene.getFlag("Lives").increment(1);
								toBeRemoved.add(entity);
								Entity score = new Entity(entity.getId()+" score", "1UP");
								
								score.setType("score");
								scene.setFlag(score.getId() + " time", new Flag(0));
								score.setPosition(position.x, position.y, position.z);
								score.setRotation(new Quaternionf().rotateX(90));
								score.setScale(SCORE_MODEL_SCALE);
								scene.addEntity(score);
							}
							else if (entity.getType().equals("star")) {
								scene.getFlag("Lives").increment(1);
								toBeRemoved.add(entity);
								scene.getFlag("Mario star").setValue((int)STAR_TIME);
								Entity score = new Entity(entity.getId()+" score", "1000");
								score.setType("score");
								scene.setFlag(score.getId() + " time", new Flag(0));
								score.setPosition(position.x, position.y, position.z);
								score.setRotation(new Quaternionf().rotateX(90));
								score.setScale(SCORE_MODEL_SCALE);
								scene.addEntity(score);
							}
							else if (entity.getType().equals("goomba")) {
								if (scene.getFlag("Mario star").getValue()>0) {
									int points = 100;
									if (points <= 1000) scene.getFlag("Score").increment(points);
									else scene.getFlag("Lives").increment(1);
									entity.setType("squished");
									scene.setFlag(entity.getId()+" time", new Flag(0));
									Entity score = new Entity(entity.getId()+" score", (points <= 1000) ? Integer.toString(points) : "1UP");
									score.setType("score");
									scene.setFlag(score.getId() + " time", new Flag(0));
									score.setPosition(position.x, position.y, position.z);
									score.setRotation(new Quaternionf().rotateX(90));
									score.setScale(SCORE_MODEL_SCALE);
									scene.addEntity(score);
								}
								else if (player.getVelocity().y >= 0) {
									fall(scene);
								}
								else {
									int points = 100;
									points *= scene.getFlag("Enemies Bounced").getValue()+1;
									scene.getFlag("Enemies Bounced").increment(1);
									if (points <= 1000) scene.getFlag("Score").increment(points);
									else scene.getFlag("Lives").increment(1);
									entity.setType("squished");
									scene.setFlag(entity.getId()+" time", new Flag(0));
									Entity score = new Entity(entity.getId()+" score", (points <= 1000) ? Integer.toString(points) : "1UP");
									score.setType("score");
									scene.setFlag(score.getId() + " time", new Flag(0));
									score.setPosition(position.x, position.y, position.z);
									score.setRotation(new Quaternionf().rotateX(90));
									score.setScale(SCORE_MODEL_SCALE);
									scene.addEntity(score);
									Vector3f playervel = player.getVelocity();
									playervel.y = ENEMY_BOUNCE;
									player.setVelocity(playervel);
								}
							}
						}
					}
					
				}
				else if (entity.getType().equals("d")) {
					Vector3f entityPos = entity.getPosition();
					Vector3f entityVel = entity.getVelocity();
					Quaternionf rotation = entity.getRotation();
					entityVel.z = BLOCK_ZVEL;
					entityPos.z -= entityVel.z*deltatime;
					entityVel.y -= GRAVITY*deltatime;
					entityPos.y += entityVel.y*deltatime;
					rotation.rotateLocalX((float) -(deltatime*0.005));
					entity.setRotation(rotation);
					entity.setPosition(entityPos.x, entityPos.y, entityPos.z);
					entity.setVelocity(entityVel);
					if (entityPos.y < FELL_OFF_A_CLIFF) {
						toBeRemoved.add(entity);
					}
				}
				else {
					Vector2i blockPos = MarioLevelLoader.getBlockPos(entity.getId());
					Vector3f entityPos = entity.getPosition();
					Vector3f entityVel = entity.getVelocity();
					entityVel.y -= BLOCK_GRAVITY*deltatime;
					entity.setVelocity(entityVel);
					entityPos.y += entityVel.y*deltatime;
					if (entityPos.y < blockPos.y || scene.getFlag("Player auto walk").enabled()) {
						entityPos.y = blockPos.y;
						entityVel.y = 0;
					}
					
					if (entity.getType().equals("4") && scene.getFlag(entity.getId() + " coin timer") != null) {
						scene.getFlag(entity.getId() + " coin timer").time(deltatime);
					}
				}
				
				// Update
				entity.updateModelMatrix();
			});
		});
		
		for (Entity entity : toBeRemoved) {
			scene.removeEntity(entity);
		}
		
		for (Entity entity : toBeAdded) {
			scene.addEntity(entity);
		}
		
		if (scene.getFlag("Time").getValue() <= 0 && !scene.getFlag("Player dies").enabled())
			fall(scene);
		
		if (scene.getFlag("Level reset").enabled()) {
			scene.getFlag("Level reset").setValue(false);

	        level.clear();
	        scene.getModelMap().forEach( (String _, Model model) -> {
	        	model.getEntitiesList().clear();
	        } );
	        
	        scene.addEntity(player);
			this.level = MarioLevelLoader.loadLevel(scene, "resources/data/mariolevel.txt", texture_variants, model_variants, enemy_texture_variants);		

	        scene.getFlag("Timer").setValue(0);
	        scene.getFlag("Time").setValue(400);
		}
	}
	
	@Override
	public void cleanup() {
		
	}

}
