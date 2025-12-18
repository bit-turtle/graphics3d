package graphics3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {
	
	private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
	
	private ShaderProgram shader;
	
	private UniformMap uniforms;
	
	public SceneRender() {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.frag", GL_FRAGMENT_SHADER));
        shader = new ShaderProgram(shaderModuleDataList);
        
        // Uniforms
        uniforms = new UniformMap(shader.getId());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("modelMatrix");
        uniforms.createUniform("txtSampler");
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("material.diffuse");
        uniforms.createUniform("material.ambient");
        uniforms.createUniform("material.diffuse");
        uniforms.createUniform("material.specular");
        uniforms.createUniform("material.reflectance");
        uniforms.createUniform("ambientLight.factor");
        uniforms.createUniform("ambientLight.color");

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            String name = "pointLights[" + i + "]";
            uniforms.createUniform(name + ".position");
            uniforms.createUniform(name + ".color");
            uniforms.createUniform(name + ".intensity");
            uniforms.createUniform(name + ".att.constant");
            uniforms.createUniform(name + ".att.linear");
            uniforms.createUniform(name + ".att.exponent");
        }
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            String name = "spotLights[" + i + "]";
            uniforms.createUniform(name + ".pl.position");
            uniforms.createUniform(name + ".pl.color");
            uniforms.createUniform(name + ".pl.intensity");
            uniforms.createUniform(name + ".pl.att.constant");
            uniforms.createUniform(name + ".pl.att.linear");
            uniforms.createUniform(name + ".pl.att.exponent");
            uniforms.createUniform(name + ".conedir");
            uniforms.createUniform(name + ".cutoff");
        }

        uniforms.createUniform("directionalLight.color");
        uniforms.createUniform("directionalLight.direction");
        uniforms.createUniform("directionalLight.intensity");
	}
	
	public void cleanup() {
		shader.cleanup();
	}
	
	private void updateLights(Scene scene) {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();

        SceneLights lights = scene.getLights();
        AmbientLight ambientLight = lights.getAmbientLight();
        uniforms.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniforms.setUniform("ambientLight.color", ambientLight.getColor());

        DirectionalLight directionalLight = lights.getDirectionalLight();
        Vector4f auxDir = new Vector4f(directionalLight.getDirection(), 0);
        auxDir.mul(viewMatrix);
        Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
        uniforms.setUniform("directionalLight.color", directionalLight.getColor());
        uniforms.setUniform("directionalLight.direction", dir);
        uniforms.setUniform("directionalLight.intensity", directionalLight.getIntensity());

        List<PointLight> pointLights = lights.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }


        List<SpotLight> spotLights = lights.getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            if (i < numSpotLights) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            String name = "spotLights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix);
        }
    }
	
	private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        uniforms.setUniform(prefix + ".position", lightPosition);
        uniforms.setUniform(prefix + ".color", color);
        uniforms.setUniform(prefix + ".intensity", intensity);
        uniforms.setUniform(prefix + ".att.constant", constant);
        uniforms.setUniform(prefix + ".att.linear", linear);
        uniforms.setUniform(prefix + ".att.exponent", exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }

        uniforms.setUniform(prefix + ".conedir", coneDirection);
        uniforms.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix);
    }

	
	public void render(Scene scene) {
        shader.bind();

        uniforms.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        uniforms.setUniform("txtSampler", 0);
        
        updateLights(scene);

        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                Texture texture = textureCache.getTexture(material.getTexturePath());

                for (Mesh mesh : material.getMeshList()) {
                	
                    for (Entity entity : entities) {
                    	
                        glActiveTexture(GL_TEXTURE0);
                    	if (entity.getTextureVariant() == null)
                            texture.bind();
                    	else
                    		textureCache.getTexture(entity.getTextureVariant()).bind();
                        glBindVertexArray(mesh.getVaoId());
                    	
                    	uniforms.setUniform("material.ambient", material.getAmbientColor());
                        uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                        uniforms.setUniform("material.specular", material.getSpecularColor());
                        uniforms.setUniform("material.reflectance", material.getReflectance());
                        uniforms.setUniform("modelMatrix", entity.getModelMatrix());
                        uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                        glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                        
                    }
                }
            }
        }

        glBindVertexArray(0);

        shader.unbind();
    }
	
}
