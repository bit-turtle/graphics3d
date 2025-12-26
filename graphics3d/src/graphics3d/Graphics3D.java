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
    private static final float MOVEMENT_SPEED = 0.005f;
    
    private static final float GRAVITY = 0.00005f;
    private static final float BLOCK_BUMP = 0.0075f;
    private static final float COIN_BOUNCE = 0.015f;
    private static final float JUMP = 0.021f;
    private static final float HOP = 0.01f;
    private static final float SCORE_TEXT_SPEED = 0.005f;
    private static final float POWERUP_RISE_SPEED = 0.005f;
    
    private static final long SCORE_TEXT_TIME = 500;
    private static final long MULTIPLE_COIN_BRICK_TIME = 3800;
    
    private static final float SCORE_MODEL_SCALE = 0.8f;
    private static final float COIN_MODEL_SCALE = 0.5f;
    
    private static final float POWERUP_HEIGHT = 2.0f;
    private static final float MUSHROOM_SPEED = 0.0025f;
    
    private static final float POWERUP_OFFSET = 1.50f;
    
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
		Model blockModel = ModelLoader.loadModel("mario block", "resources/models/block/block.obj", scene.getTextureCache());
		scene.addModel(blockModel);
		Model airModel = ModelLoader.loadModel("air block", "resources/models/block/air.obj", scene.getTextureCache());
		scene.addModel(airModel);
		Model groundModel = ModelLoader.loadModel("ground block", "resources/models/block/ground.obj", scene.getTextureCache());
		scene.addModel(groundModel);
		Model pipeModel = ModelLoader.loadModel("pipe block", "resources/models/pipe/pipe.obj", scene.getTextureCache());
		scene.addModel(pipeModel);
		Model toppipeModel = ModelLoader.loadModel("pipetop block", "resources/models/pipe/pipetop.obj", scene.getTextureCache());
		scene.addModel(toppipeModel);
		Model poleModel = ModelLoader.loadModel("pole block", "resources/models/flag/pole.obj", scene.getTextureCache());
		scene.addModel(poleModel);
		Model toppoleModel = ModelLoader.loadModel("poletop block", "resources/models/flag/poletop.obj", scene.getTextureCache());
		scene.addModel(toppoleModel);
		Model coinModel = ModelLoader.loadModel("coin", "resources/models/coin/coin.obj", scene.getTextureCache());
		scene.addModel(coinModel);
		Model flagModel = ModelLoader.loadModel("flag", "resources/models/flag/flag.obj", scene.getTextureCache());
		scene.addModel(flagModel);
		Model mushroomModel = ModelLoader.loadModel("mushroom", "resources/models/mushroom/mushroom.obj", scene.getTextureCache());
		scene.addModel(mushroomModel);
		// Load Score Models
		for (String modelId : score_models) {
			Model scoreModel = ModelLoader.loadModel(modelId, "resources/models/score/" + modelId + ".obj", scene.getTextureCache());
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
		model_variants[7] = airModel.getId();
		model_variants[3] = pipeModel.getId();
		model_variants[8] = toppipeModel.getId();
		model_variants[10] = airModel.getId();
		model_variants[11] = poleModel.getId();
		model_variants[12] = toppoleModel.getId();
		// Load enemy texture variants
		enemy_texture_variants = new HashMap<String, String>();
		
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
        scene.setFlag("Show Flags", new Flag(false));
        scene.setFlag("Coins", new Flag(0));
        scene.setFlag("Score", new Flag(0));
        scene.setFlag("Lives", new Flag(3));
        scene.setGui(new GameGui(scene, window));
        
        player = new Entity("player entity", toppoleModel.getId());
        player.setTextureVariant("resources/models/block/question.jpg");
        player.setType("player");
        player.setPosition(20, 5, 0);
        scene.addEntity(player);
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
        
        if (!scene.getFlag("Player auto walk").enabled()) {
	        if (window.isKeyPressed(GLFW_KEY_A)) {
	            velocity.x = -MOVEMENT_SPEED;
	        } else if (window.isKeyPressed(GLFW_KEY_D)) {
	            velocity.x = MOVEMENT_SPEED;
	        }
	        else
	        	velocity.x = 0;
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
		SceneLights lights = scene.getLights();
		lights.getPointLights().clear();
		lights.getSpotLights().clear();
		ArrayList<Entity> toBeRemoved = new ArrayList<Entity>();
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
						entity.setScale(0.5f*(1-velocity.y/(-COIN_BOUNCE/2)));
					if (velocity.y <= -COIN_BOUNCE/2) {
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
						position.y = targetHeight+POWERUP_HEIGHT;
						entity.setPosition(position.x, position.y, position.z);
						entity.setType(id[0]);
					}
				}
				else if (entity.getType().length() > 1) {
					Vector3f position = entity.getPosition();
					Vector3f velocity = entity.getVelocity();
					// Physics
					velocity.y -= GRAVITY*deltatime;
					
					entity.setVelocity(velocity);
					
					// X Movement
					position.x += velocity.x*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					int leftX = (int) Math.floor(entity.getPosition().x);
					int bottomY = (int) Math.floor(entity.getPosition().y);
					int rightX = (int) Math.ceil(entity.getPosition().x);
					int topY = (int) Math.ceil(entity.getPosition().y);
					
					// Collision check X
					Entity leftEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, topY), null);
					Entity leftEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, bottomY), null);
					Entity rightEntity = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, topY), null);
					Entity rightEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, bottomY), null);
					if (velocity.x < 0 && (leftEntity != null || leftEntity2 != null) ) {
						position.x = (float) Math.floor(position.x)+1.f;
						velocity.x = (entity.getType().equals("mushroom")) ? -velocity.x : 0;
					}
					if (velocity.x > 0 && (rightEntity != null || rightEntity2 != null) ) {
						position.x = (float) Math.floor(position.x);
						velocity.x = (entity.getType().equals("mushroom")) ? -velocity.x : 0;
						
						if (entity == player) {
							Entity hitBlock = (rightEntity != null) ? rightEntity : rightEntity2;
							if (hitBlock.getType().equals(";") || hitBlock.getType().equals("<")) {
								scene.getFlag("Player auto walk").setValue(true);
								scene.getFlag("Player flagpole slide").setValue(true);
								position.x = hitBlock.getPosition().x-0.25f;
								position.y = hitBlock.getPosition().y;
								position.z = hitBlock.getPosition().z;
								
							}
						}
					}
					
					// Y Movement
					position.y += velocity.y*deltatime;
					entity.setPosition(position.x, position.y, position.z);
					int centerX = (int) Math.round(entity.getPosition().x);
					leftX = (int) Math.floor(entity.getPosition().x);
					bottomY = (int) Math.floor(entity.getPosition().y);
					rightX = (int) Math.ceil(entity.getPosition().x);
					topY = (int) Math.ceil(entity.getPosition().y);
					
					// Land
					Entity groundEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, bottomY), null);
					Entity groundEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, bottomY), null);
					Entity ceilingEntity = level.getOrDefault(MarioLevelLoader.getBlockId(leftX, topY), null);
					Entity ceilingEntity2 = level.getOrDefault(MarioLevelLoader.getBlockId(rightX, topY), null);

					if (velocity.y > 0 && (ceilingEntity != null || ceilingEntity2 != null) ) {
						position.y = (float) Math.floor(position.y);
						velocity.y = 0;
						
						if (entity == player) {
							Entity bumpedBlock = (leftX == centerX) ? ceilingEntity : ceilingEntity2;
							if (bumpedBlock != null) {
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
									Entity powerup = new Entity("mushroom " + position.y, "mushroom");
									powerup.setPosition(position.x, position.y+POWERUP_OFFSET, position.z);
									powerup.setType("powerup");
									powerup.updateModelMatrix();
									powerup.setVelocity(new Vector3f(MUSHROOM_SPEED, 0, 0));
									scene.addEntity(powerup);
								}
								else if (bumpedBlock.getType().equals("7")) {
									Entity powerup = new Entity("mushroom " + position.y + " " + entity.getId(), "mushroom");
									powerup.setPosition(position.x, position.y+POWERUP_OFFSET, position.z);
									powerup.setType("powerup");
									powerup.updateModelMatrix();
									powerup.setVelocity(new Vector3f(MUSHROOM_SPEED, 0, 0));
									scene.addEntity(powerup);
								}
							}
						}
					}
					
					
					boolean onGround = false;
					if (velocity.y <= 0 && (groundEntity != null || groundEntity2 != null) ) {
						position.y = (float) Math.floor(position.y)+1.f;
						velocity.y = 0;
						onGround = true;
					}
					
					entity.setPosition(position.x, position.y, position.z);
					entity.setVelocity(velocity);
					
					
					if (entity == player)
						scene.getFlag("Player on Ground").setValue(onGround);
					
					if (entity == player) {
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
						if (!scene.getFlag("Free Camera").enabled()) {
							cameraPosition.x = entity.getPosition().x;
							scene.getCamera().setPosition(cameraPosition.x+0.5f, cameraPosition.y+0.5f, 0.5f);
							scene.getCamera().moveBackwards(cameraZoom);
						}
					}
					
				}
				else {
					Vector2i blockPos = MarioLevelLoader.getBlockPos(entity.getId());
					Vector3f entityPos = entity.getPosition();
					Vector3f entityVel = entity.getVelocity();
					entityVel.y -= GRAVITY*deltatime;
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
	}
	
	@Override
	public void cleanup() {
		
	}

}
