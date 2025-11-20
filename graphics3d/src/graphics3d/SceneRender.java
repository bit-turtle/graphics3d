package graphics3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {
	
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
	}
	
	public void cleanup() {
		shader.cleanup();
	}
	
	public void render(Scene scene) {
        shader.bind();

        uniforms.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        uniforms.setUniform("txtSampler", 0);

        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                Texture texture = textureCache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                for (Mesh mesh : material.getMeshList()) {
                    glBindVertexArray(mesh.getVaoId());
                    for (Entity entity : entities) {
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
