package graphics3d;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.*;

public class ShaderProgram {

	private final int id;
	
	public ShaderProgram(List<ShaderModuleData> moduledata) {
		id = glCreateProgram();
		if (id == 0)
			throw new RuntimeException("Could not create Shader");
		
		List<Integer> modules = new ArrayList<>();
		moduledata.forEach( s -> modules.add(createShader(Util.readfile(s.shaderFile), s.shaderType)) );
		
		link(modules);
	}
	
	protected int createShader(String code, int type) {
        int shaderId = glCreateShader(type);
        if (shaderId == 0)
            throw new RuntimeException("Error creating shader. Type: " + type);

        glShaderSource(shaderId, code);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));

        glAttachShader(id, shaderId);

        return shaderId;
    }
	
	public void cleanup() {
        unbind();
        if (id != 0)
            glDeleteProgram(id);
    }
	
	private void link(List<Integer> modules) {
		glLinkProgram(id);
		if (glGetProgrami(id, GL_LINK_STATUS) == 0)
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(id, 1024));
		
		modules.forEach(s -> glDetachShader(id, s));
        modules.forEach(GL30::glDeleteShader);
	}
	
	public void validate() {
        glValidateProgram(id);
        if (glGetProgrami(id, GL_VALIDATE_STATUS) == 0)
            throw new RuntimeException("Error validating Shader code: " + glGetProgramInfoLog(id, 1024));
    }
	
	public int getId() {
		return id;
	}
	
	public record ShaderModuleData(String shaderFile, int shaderType) {}

	public void unbind() {
        glUseProgram(0);
    }
	
	public void bind() {
        glUseProgram(id);
    }
	
}
