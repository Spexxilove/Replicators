package meshes;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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
import org.lwjgl.BufferUtils;
import engine.Engine;

public enum Primitives{
	RECTANGLE(0),
	CIRCLE(1),
	;
	public int id;
	public int vao;
	public int vbo;
	public int vboiId;
	public int iCount;
	private Float attributes[]; // vertices normals textures interleaved
	private FloatBuffer attributesBuffer;
	Primitives(int id){
		this.id = id;
	}
	public void init(){
		initBuffers();
		initVAO();
	}
	protected void initBuffers(){
		switch(this.id){
			case 0:
				initVARectangle();
				break;
			case 1:
				initVACircle();
				break;
		}
	}
	protected void initVACircle(){
		iCount = 40;
		attributesBuffer = BufferUtils.createFloatBuffer(4*8*iCount); 
		for(int i=0;i<iCount;++i){
			attributesBuffer.put((float)Math.cos(i*Math.PI*2/iCount)); //x
			attributesBuffer.put((float)Math.sin(i*Math.PI*2/iCount)); //y
			attributesBuffer.put(0f); //z
			attributesBuffer.put(1f); //w
		}
		attributesBuffer.flip();
	}
	protected void initVARectangle(){
		iCount = 4;
		attributes = new Float[]{
				//vertices						
				1.0f,	1.0f,	1.0f,	1.0f,
				-1.0f,	1.0f,	1.0f,	1.0f, 
				-1.0f,	-1.0f,	1.0f,	1.0f,  
				1.0f,	-1.0f,	1.0f,	1.0f,		
		};
		//vertices texcoord normals interleaved		
		attributesBuffer = BufferUtils.createFloatBuffer(4*attributes.length); //stride = 4B per float	
		for (int i = 0; i < attributes.length; i++) {
			attributesBuffer.put(attributes[i]);
		}
		attributesBuffer.flip();
	}
	protected void initVAO(){
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, attributesBuffer, GL_STREAM_DRAW);
		glEnableVertexAttribArray(0);
		// in_Position
		glVertexAttribPointer(0, 4, GL_FLOAT,false, 4*4, 0);
		// Deselect (bind to 0) the VAO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//till here vao remembers
		glBindVertexArray(0);	
		glDisableVertexAttribArray(0);
		Engine.exitOnGLError("Engine.setupQuad");
	}
}
