package meshes;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;
import engine.Engine;
import engine.OBJReader;

public enum Mesh{
	GROUNDTILE,
	PROJECTILE,
	BOX,
	FONT,
	REPLOCATOR1,
	REPLOCATOR2,
	REPLOCATOR3,
	REPLOCATOR4,
	REPLOCATOR5,
	REPLOCATOR6,
	REPLOCATOR7,
	REPLOCATOR8,
	REPLOCATOR9,
	REPLOCATOR10,
	REPLOCATOR11,
	REPLOCATOR12,
	REPLOCATOR13,
	REPLOCATOR14,
	REPLOCATOR15,
	REPLOCATOR16,
	REPLOCATOR17,
	REPLOCATOR18,
	REPLOCATOR19,
	TURRET,
	VIEWRADIUS, ;
	public int vao;
	public int vbo;
	public int vboiId;
	public int iCount;
	private String filename;
	private Float attributes[]; // vertices normals textures interleaved
	private Integer indices[]; // indices
	private FloatBuffer attributesBuffer;
	private IntBuffer indexBuffer;
	Mesh(){
		this.filename = "assets/meshes/" + this.name().toLowerCase() + ".obj";
	}
	public void init(){
		initVA();
		initBuffers();
		initVAO();
	}
	private void initVA(){
		OBJReader obj = new OBJReader(filename);
		List<Integer> ind = obj.getIndices();
		List<Float> att = obj.getAttributes();
		indices = new Integer[ind.size()];
		attributes = new Float[10 * att.size() / 8];
		for(int i = 0; i < ind.size(); ++i){
			indices[i] = ind.get(i);
		}
		for(int i = 0; i < att.size() / 8; i++){
			attributes[i * 10 + 0] = att.get(i * 8 + 0); // x
			attributes[i * 10 + 1] = att.get(i * 8 + 1); // y
			attributes[i * 10 + 2] = att.get(i * 8 + 2); // z
			attributes[i * 10 + 3] = 1.0f;
			attributes[i * 10 + 4] = att.get(i * 8 + 3); // u
			attributes[i * 10 + 5] = att.get(i * 8 + 4); // v
			attributes[i * 10 + 6] = att.get(i * 8 + 5); // nx
			attributes[i * 10 + 7] = att.get(i * 8 + 6); // ny
			attributes[i * 10 + 8] = att.get(i * 8 + 7); // nz
			attributes[i * 10 + 9] = 0.0f;
		}
	}
	private void initBuffers(){
		// vertices texcoord normals interleaved
		attributesBuffer = BufferUtils.createFloatBuffer(4 * attributes.length); // stride
																					// =
																					// 4B
																					// per
																					// float
		for(int i = 0; i < attributes.length; i++){
			attributesBuffer.put(attributes[i]);
		}
		attributesBuffer.flip();
		// indices
		iCount = indices.length; // used for glDrawElements
		indexBuffer = BufferUtils.createIntBuffer(iCount * 4);
		for(int i = 0; i < iCount; ++i){
			indexBuffer.put(indices[i]);
		}
		indexBuffer.flip();
	}
	private void initVAO(){
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, attributesBuffer, GL_STREAM_DRAW);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		// in_Position
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * 10, 0);
		// in_TextureCoord
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 10, 4 * 4);
		// in_Normal
		glVertexAttribPointer(2, 4, GL_FLOAT, false, 4 * 10, 6 * 4);
		// index buffers
		vboiId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		// Deselect (bind to 0) the VAO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		// till here vao remembers
		glBindVertexArray(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		Engine.exitOnGLError("Mesh.initVAO");
	}
}
