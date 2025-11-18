package graphics3d;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

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
    
    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Integer location = uniforms.get(uniformName);
            if (location == null) {
                throw new RuntimeException("Could not find uniform [" + uniformName + "]");
            }
            glUniformMatrix4fv(location.intValue(), false, value.get(stack.mallocFloat(16)));
        }
    }
}