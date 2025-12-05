package graphics3d;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private Projection projection;
    private TextureCache textureCache;
    private Camera camera;
    private GuiInterface gui;
    private SceneLights lights;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
        lights = new SceneLights();
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null)
            throw new RuntimeException("Could not find model [" + modelId + "]");
        model.getEntitiesList().add(entity);
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
    
    public void setLights(SceneLights lights) {
        this.lights = lights;
    }
    
    public void setGui(GuiInterface gui) {
    	this.gui = gui;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }
}