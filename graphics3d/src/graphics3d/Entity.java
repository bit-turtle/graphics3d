package graphics3d;

import org.joml.*;

public class Entity {

    private final String id;
    private final String modelId;
    private Matrix4f modelMatrix;
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;
    private String textureVariant;
    private boolean hidden;
    private float cullingradius = 2.f;
    private String type;
    private Vector3f velocity;

    public Entity(String id, String modelId) {
        this.id = id;
        this.modelId = modelId;
        modelMatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1,1,1);
        hidden = false;
        velocity = new Vector3f();
    }

    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getVelocity() {
    	return velocity;
    }
    
    public void setVelocity(Vector3f velocity) {
    	this.velocity = velocity;
    }
    

    public Quaternionf getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale.z;
    }
    
    public Vector3f getScaleVector() {
    	return scale;
    }
    
    public void setScaleVector(Vector3f scale) {
    	this.scale = scale;
    }

    public final void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    
    public void offset(float x, float y, float z) {
    	position.x += x;
        position.y += y;
        position.z += z;
    }

    public void setRotation(float x, float y, float z, float angle) {
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }
    
    public void setRotation(Quaternionf rotation) {
    	this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = new Vector3f(scale,scale,scale);
    }

    public void updateModelMatrix() {
        modelMatrix.translationRotateScale(position, rotation, scale);
    }
    

    
    public void setTextureVariant(String texturePath) {
    	textureVariant = texturePath;
    }
    
    public void clearTextureVariant() {
    	textureVariant = null;
    }
    
    public String getTextureVariant() {
    	return textureVariant;
    }
    
    public void show() {
    	this.hidden = false;
    }
    
    public void hide() {
    	this.hidden = true;
    }
    
    public boolean hidden () {
    	return this.hidden;
    }
    
    public void setCullingRadius(float radius) {
    	this.cullingradius = radius;
    } 
    
    public float getCullingRadius() {
    	return this.cullingradius;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getType() {
    	return this.type;
    }
}
