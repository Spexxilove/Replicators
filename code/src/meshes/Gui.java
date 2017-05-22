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
import org.lwjgl.BufferUtils;
import engine.Engine;

public enum Gui {
	FONT(0),
	RECTANGLE(1),
	INVERSERECTANGLE(2),
	;
	public int vao;
	public int vbo;
	public int vboiId ;
	public int iCount;
	public int id;
	private Float attributes[]; 
	private Integer indices[];
	private FloatBuffer attributesBuffer;
	private IntBuffer indexBuffer;
	Gui(int id){
		this.id = id;
	}
	public void init(){
		initBuffers();
		initVAO();
	}
	private void initBuffers(){
		switch(this.id){
			case 0:
				initFont();
				break;
			case 1:
				initRect();
				break;
			case 2:
				initInverseRect();
				break;
			default:
				break;
		}
	}
	private void initFont(){
		int numChars = 256;
		float gridSize = 16.0f;
		float cellSize = 1.0f/gridSize;
		float uvX,uvY;
		attributesBuffer = BufferUtils.createFloatBuffer(numChars*6*4);	
		for(int c=0;c<numChars;++c){
			uvX=((int) (c%gridSize))*cellSize;
			uvY=((int) (c/gridSize))*cellSize;
			attributes = new Float[]{
					-1.0f,	1.0f,	0.0f,	1.0f,   uvX,			uvY,
					-1.0f,	-1.0f,	0.0f,	1.0f,   uvX,			uvY+cellSize,
					1.0f,	-1.0f,	0.0f,	1.0f,	uvX+cellSize,	uvY+cellSize,
					1.0f,	1.0f,	0.0f,	1.0f,  	uvX+cellSize,	uvY,	
	
			};	
			for (int i = 0; i < attributes.length; i++) {
				attributesBuffer.put(attributes[i]);
			}
		}
		attributesBuffer.flip();
		indexBuffer = BufferUtils.createIntBuffer(numChars*6);
		for(int i=0;i<numChars*4;i+=4){
			indexBuffer.put(i+0);
			indexBuffer.put(i+1);
			indexBuffer.put(i+2);
			indexBuffer.put(i+2);
			indexBuffer.put(i+3);
			indexBuffer.put(i+0);
		}
		indexBuffer.flip();
		iCount = 6;
	}
	private void initInverseRect(){
		attributes = new Float[]{
				-1.0f,	1.0f,	0.0f,	1.0f,   0.0f,	1.0f,
				-1.0f,	-1.0f,	0.0f,	1.0f,   0.0f,	0.0f,	
				1.0f,	-1.0f,	0.0f,	1.0f,	1.0f,	0.0f,	
				1.0f,	1.0f,	0.0f,	1.0f,   1.0f,	1.0f,	
		
		};	
		attributesBuffer = BufferUtils.createFloatBuffer(4*attributes.length);
		for (int i = 0; i < attributes.length; i++) {
			attributesBuffer.put(attributes[i]);
		}
		attributesBuffer.flip();
		indices = new Integer[]{
				0,1,2,
				2,3,0,
		};
		iCount = indices.length;
		indexBuffer = BufferUtils.createIntBuffer(iCount*4);
		for(int i=0;i<iCount;++i){
			indexBuffer.put(indices[i]);
		}
		indexBuffer.flip();
	}
	private void initRect(){
		attributes = new Float[]{
				-1.0f,	1.0f,	0.0f,	1.0f,   0.0f,	0.0f,
				-1.0f,	-1.0f,	0.0f,	1.0f,   0.0f,	1.0f,	
				1.0f,	-1.0f,	0.0f,	1.0f,	1.0f,	1.0f,	
				1.0f,	1.0f,	0.0f,	1.0f,   1.0f,	0.0f,	
		
		};	
		attributesBuffer = BufferUtils.createFloatBuffer(4*attributes.length);
		for (int i = 0; i < attributes.length; i++) {
			attributesBuffer.put(attributes[i]);
		}
		attributesBuffer.flip();
		indices = new Integer[]{
				0,1,2,
				2,3,0,
		};
		iCount = indices.length;
		indexBuffer = BufferUtils.createIntBuffer(iCount*4);
		for(int i=0;i<iCount;++i){
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
		// in_Position
		glVertexAttribPointer(0, 4, GL_FLOAT,false, 4*6, 0);
		// in_TextureCoord
		glVertexAttribPointer(1, 2, GL_FLOAT,false, 4*6, 4*4);
		//index buffers
		vboiId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboiId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		//till here vao remembers
		glBindVertexArray(0);	
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		Engine.exitOnGLError("GuiElement.initVAO");
	}
}
