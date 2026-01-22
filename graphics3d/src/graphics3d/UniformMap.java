package graphics3d;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;

public class UniformMap {

    private int id;
    private Map<String, Integer> uniforms;

    public UniformMap(int id) {
        this.id = id;
        uniforms = new HashMap<>();
    }

    public void createUniform(String name) {
        int uniformLocation = glGetUniformLocation(id, name);
        if (uniformLocation < 0)
            throw new RuntimeException("Could not find uniform [" + name + "] in shader program [" +
                    id + "]");
        uniforms.put(name, uniformLocation);
    }
    
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName).intValue(), value);
    }
    
    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(uniforms.get(uniformName), value.x, value.y);
    }
    
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }
    
    public void setUniform(String uniformName, float value) {
    	glUniform1f(uniforms.get(uniformName), value);
    }
    
    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Integer location = uniforms.get(uniformName);
            if (location == null)
                throw new RuntimeException("Could not find uniform [" + uniformName + "]");
            glUniformMatrix4fv(location.intValue(), false, value.get(stack.mallocFloat(16)));
        }
    }
    
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }
    
    public void setUniform(String uniformName, boolean[] value) {
    	glUniform2i(uniforms.get(uniformName), value[0] ? 1 : 0, value[1] ? 1 : 0);
    }
    public void setUniform(String uniformName, Matrix4f[] matrices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }
}