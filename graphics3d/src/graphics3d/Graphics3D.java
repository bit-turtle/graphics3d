package graphics3d;

import static org.lwjgl.glfw.GLFW.*;


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
    private static final float JUMP = 0.021f;
	
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
	};
	String model_variants[];
	Map<String, String> enemy_texture_variants;
		
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
        
        scene.setGui(new GameGui(scene, window));
        
        player = new Entity("player entity", toppoleModel.getId());
        player.setTextureVariant("resources/models/block/question.jpg");
        player.setType("player");
        player.setPosition(0, 5, 0);
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
        
        if (window.isKeyPressed(GLFW_KEY_A)) {
            velocity.x = -MOVEMENT_SPEED;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            velocity.x = MOVEMENT_SPEED;
        }
        else
        	velocity.x = 0;
        if (window.isKeyPressed(GLFW_KEY_SPACE) && scene.getFlag("Player on Ground").enabled()) {
           velocity.y = JUMP;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveDown(move);
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
		scene.getModelMap().forEach((String _, Model model) -> {
			model.getEntitiesList().forEach((Entity entity) -> {
				// Spin
				if (scene.getFlag("Cube Spins").enabled())
					entity.setRotation(entity.getRotation().rotateXYZ(deltatime*0.01f, deltatime*0.01f, deltatime*0.01f));
				else
					entity.setRotation(new Quaternionf());
				
				if (entity.getType().length() > 1) {
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
						velocity.x = 0;
					}
					if (velocity.x > 0 && (rightEntity != null || rightEntity2 != null) ) {
						position.x = (float) Math.floor(position.x);
						velocity.x = 0;
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
						
						if (entity.getType() == "player") {
							Entity bumpedBlock = (leftX == centerX) ? ceilingEntity : ceilingEntity2;
							if (bumpedBlock != null) {
								Vector3f blockVel = bumpedBlock.getVelocity();
								blockVel.y = BLOCK_BUMP;
								bumpedBlock.setVelocity(blockVel);
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
					
					
					if (entity.getType() == "player")
						scene.getFlag("Player on Ground").setValue(onGround);
				}
				else {
					Vector2i blockPos = MarioLevelLoader.getBlockPos(entity.getId());
					Vector3f entityPos = entity.getPosition();
					Vector3f entityVel = entity.getVelocity();
					entityVel.y -= GRAVITY*deltatime;
					entity.setVelocity(entityVel);
					entityPos.y += entityVel.y*deltatime;
					if (entityPos.y < blockPos.y) {
						entityPos.y = blockPos.y;
						entityVel.y = 0;
					}
				}
				
				if (entity.getType() == "player") {
					Vector3f lightPos = new Vector3f(0.5f, 0.5f, 0.5f);
					lightPos.add(entity.getPosition());
					PointLight light = new PointLight(
						new Vector3f(1,1,1),
						lightPos,
						0.4f
					);
					//lights.getPointLights().add(light);
					SpotLight spotlight = new SpotLight(light, new Vector3f(0, -1, 0), 60.f);
					lights.getSpotLights().add(spotlight);
					// 3rd Person Camera
					if (!scene.getFlag("Free Camera").enabled()) {
						cameraPosition.x = entity.getPosition().x;
						scene.getCamera().setPosition(cameraPosition.x+0.5f, cameraPosition.y+0.5f, 0.5f);
						scene.getCamera().moveBackwards(cameraZoom);
					}
				}
				
				// Update
				entity.updateModelMatrix();
			});
		});
	}
	
	@Override
	public void cleanup() {
		
	}

}
