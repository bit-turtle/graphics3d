package graphics3d;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;
    private GuiInterface gui;
    private SceneLights lights;
    private SkyBox sky;
    private boolean paused = false;
    private Map<String, Flag> flags;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
        lights = new SceneLights();
        flags = new HashMap<>();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null)
            throw new RuntimeException("Could not find model [" + modelId + "]");
        model.getEntitiesList().add(entity);
    }
    
    public void removeEntity(Entity entity) {
    	String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        model.getEntitiesList().remove(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }
    
    public TextureCache getTextureCache() {
    	return textureCache;
    }
    
    public Camera getCamera() {
    	return camera;
    }
    
    public GuiInterface getGui() {
    	return gui;
    }
    
    public SceneLights getLights() {
        return lights;
    }
    
    public SkyBox getSky() {
    	return sky;
    }
    
    public void setSky(SkyBox sky) {
    	this.sky = sky;
    }
    
    public void setLights(SceneLights lights) {
        this.lights = lights;
    }
    
    public void setGui(GuiInterface gui) {
    	this.gui = gui;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }
    
    public void pause() {
    	paused = true;
    }
    
    public void resume() {
    	paused = false;
    }
    
    public boolean paused() {
    	return this.paused;
    }
    
    public Flag getFlag(String key) {
    	return flags.getOrDefault(key, null);
    }
    
    public void setFlag(String key, Flag flag) {
    	flags.put(key, flag);
    }
    
    public Map<String, Flag> getFlags() {
    	return flags;
    }
    
}