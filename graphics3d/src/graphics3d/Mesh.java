package graphics3d;

import org.lwjgl.opengl.GL30;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.system.MemoryUtil;

public class Mesh {

    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;

    public Mesh(float[] positions, float[] textCoords, int[] indices) {
        numVertices = indices.length;
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

        // Texture coordinates VBO
        vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length);
        textCoordsBuffer.put(0, textCoords);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

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
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(indicesBuffer);
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
    static Mesh generateBox(float width, float height, float length) {
    	float x = width/2, y = height/2, z = length/2;
    	float[] positions = new float[]{
            // VO
            -x, y, z,
            // V1
            -x, -y, z,
            // V2
            x, -y, z,
            // V3
            x, y, z,
            // V4
            -x, y, -z,
            // V5
            x, y, -z,
            // V6
            -x, -y, -z,
            // V7
            x, -y, -z,
        };

        float[] colors = new float[]{
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
        };
        int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
        };
    	
    	return new Mesh(positions, colors, indices);
    }
    
    static Mesh generateStar(float width, float height, float length, float spacing) {
    	float x = width/2, y = height/2, z = length/2;
    	float u = x*spacing, v = y*spacing, w = z*spacing;
    	float[] positions = new float[] {
    		// Points
    		x, 0, 0,	// Right
    		0, y, 0,	// Top
    		-x, 0, 0,	// Left
    		0, -y, 0,	// Bottom
    		0, 0, z,	// Front
    		0, 0, -z,	// Back
    		// 6: Front
    		u, v, w,	// Top-Right
    		-u, v, w,	// Top-Left
    		-u, -v, w,	// Bottom-Left
    		u, -v, w,	// Bottom-Right,
    		// 10: Back
    		u, v, -w,	// Top-Right
    		-u, v, -w,	// Top-Left
    		-u, -v, -w,	// Bottom-Left
    		u, -v, -w,	// Bottom-Right,
    	};
    	float[] colors = new float[] {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
    	};
    	int[] indices = new int[] {
    		// Right
    		0, 9, 13,
    		1, 6, 10,
    		2, 7, 11,
    		3, 8, 12,
    		4, 6, 9,
    		5, 10, 13,
    		// Left
    		0, 6, 10,
    		1, 7, 11,
    		2, 8, 12,
    		3, 9, 13,
    		4, 7, 8,
    		5, 11, 12,
    		// Front
    		0, 6, 9,
    		1, 6, 7,
    		2, 7, 8,
    		3, 8, 9,
    		4, 6, 7,
    		5, 10, 11,
    		// Back
    		0, 10, 13,
    		1, 10, 11,
    		2, 11, 12,
    		3, 12, 13,
    		4, 8, 9,
    		5, 12, 13,
    		
    	};
    	
    	return new Mesh(positions, colors, indices);
    }
}
