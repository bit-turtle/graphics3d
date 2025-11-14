package graphics3d;

import org.lwjgl.opengl.GL30;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.system.MemoryUtil;

public class Mesh {

    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;

    public Mesh(float[] positions, float[] colors, int[] indices) {
        this.numVertices = indices.length;
        vboIdList = new ArrayList<>();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Positions VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(0, positions);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        // Color VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer colorsBuffer = MemoryUtil.memCallocFloat(colors.length);
        colorsBuffer.put(0, colors);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        // Index VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(positionsBuffer);
    }

    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public final int getVaoId() {
        return vaoId;
    }
    
    // Mesh generation
    static Mesh generateStar(int points, float top, float bot, float z) {
		int length = (points*2)*3;
		double angle = (2*Math.PI)/points;
		float[] positions = new float[length+3];
		float[] colors = new float[length+3];
		int[] indices = new int[points*3*2];
		// Center point
		positions[length] = 0.f;
		positions[length+1] = 0.f;
		positions[length+2] = z;
		colors[length] = 0.5f;
		colors[length+1] = 0.5f;
		colors[length+2] = 0.5f;
		for (int i = 0; i < points; i++) {
			// Generate points
			double pointx = Math.sin(angle*i)*top;
			double pointy = Math.cos(angle*i)*top;
			double rightx = Math.sin(angle*i+angle/2)*bot;
			double righty = Math.cos(angle*i+angle/2)*bot;
			positions[i*6+0] = (float) pointx;
			positions[i*6+1] = (float) pointy;
			positions[i*6+2] = z;
			positions[i*6+3] = (float) rightx;
			positions[i*6+4] = (float) righty;
			positions[i*6+5] = z;
			
			// Generate colors
			Color pointcolor = Color.getHSBColor((float) ((angle*i)/(2*Math.PI)), 1.f, 1.f);
			Color rightcolor = Color.getHSBColor((float) ((angle*i+angle/2)/(2*Math.PI)), 1.f, 1.f);
			colors[i*6+0] = pointcolor.getRed()/255.f;
			colors[i*6+1] = pointcolor.getGreen()/255.f;
			colors[i*6+2] = pointcolor.getBlue()/255.f;
			colors[i*6+3] = rightcolor.getRed()/255.f;
			colors[i*6+4] = rightcolor.getGreen()/255.f;
			colors[i*6+5] = rightcolor.getBlue()/255.f;
			
			// Generate Indices
			indices[i*3+0] = i*2;
			indices[i*3+1] = i*2+1;
			indices[i*3+2] = (i == 0) ? points*2-1 : i*2-1;
			
			// Inwards facing triangles
			indices[i*3+0+points*3] = points*2;
			indices[i*3+1+points*3] = i*2+1;
			indices[i*3+2+points*3] = (i == 0) ? points*2-1 : i*2-1;
		}
		return new Mesh(positions, colors, indices);
    }
}
