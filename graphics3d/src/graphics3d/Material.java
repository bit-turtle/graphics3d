package graphics3d;

import java.util.*;

import org.joml.Vector4f;

public class Material {
	
	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    private Vector4f diffuseColor;
    private Vector4f ambientColor;
    private Vector4f specularColor;
    private float reflectance;
    
    private List<Mesh> meshList;
    private String texturePath;

    public Material() {
        meshList = new ArrayList<Mesh>();
        diffuseColor = DEFAULT_COLOR;
    }
    
    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }
    
    public Vector4f getAmbientColor() {
        return ambientColor;
    }
    
    public Vector4f getSpecularColor() {
        return specularColor;
    }
    
    public float getReflectance() {
    	return reflectance;
    }
    
    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
    
    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }
    
    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }
    
    public void setReflectance(float reflectance) {
    	this.reflectance = reflectance;
    }

    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
}
