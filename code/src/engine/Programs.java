package engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import objects.GameObject;
import objects.ViewRadiusRange;

/**
 * Opengl shader programs
 * @author thei
 */
public enum Programs{
	/**
	 * Renders GL_TRIANLGES in 3d with lighting
	 */
	PROGRAM3D(GL_TRIANGLES, new Uniforms[] { Uniforms.CameraPosition,
			Uniforms.Shininess, Uniforms.P, Uniforms.V, Uniforms.M, }, true, 3, "vertex.glsl", "fragment.glsl"),
	/**
	 * Renders GL_TRIANLGES onto nearplane
	 */
	PROGRAMGUI(GL_TRIANGLES, new Uniforms[] { Uniforms.M, }, true, 2, "vertex2d.glsl", "fragment2d.glsl"),
	/**
	 * Renders GL_LINE_LOOP in 3d without lighting
	 */
	PRIMITIVE(GL_LINE_LOOP, new Uniforms[] { Uniforms.M, Uniforms.P,
			Uniforms.V, Uniforms.PrimitiveColor, }, false, 1, "vertexP.glsl", "fragmentP.glsl"),
	/**
	 * Renders GL_TRIANLGES in 3d without lighting
	 */
	PROGRAM3DNL(GL_TRIANGLES, new Uniforms[] { Uniforms.P, Uniforms.V,
			Uniforms.M, }, true, 3, "vertexNL.glsl", "fragmentNL.glsl"), ;
	public int pId;
	private int glMode;
	private Uniforms[] uniforms;
	private boolean isTextured;
	private String vShader;
	private String fShader;
	private static int frameBufferid;
	public static int screenTextureid;
	/**
	 * Constructor
	 * @param GL_MODE
	 * @param uniforms Array of uniform enums
	 * @param isTextured
	 * @param numAttribs number of attribpointer to cleanup
	 * @param vShader vertex shader filename
	 * @param fShader fragment shader filename
	 */
	Programs(int GL_MODE, Uniforms[] uniforms, boolean isTextured,
			int numAttribs, String vShader, String fShader){
		glMode = GL_MODE;
		this.uniforms = uniforms;
		this.isTextured = isTextured;
		this.vShader = vShader;
		this.fShader = fShader;
	}
	/**
	 * Calls {@link #initShaders(String, String)},
	 * {@link #initUniformLocations()}
	 */
	public void init(){
		initShaders(vShader, fShader);
		initUniformLocations();
	}
	/**
	 * Binds program, textures, vertexarray, calls
	 * {@link #uploadUniforms(GameObject)}, draws and unbinds everything again
	 * @param object
	 */
	public void render(GameObject object){
		if(isTextured){
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, object.getTexture().id);
		}
		glBindVertexArray(object.getVAO());
		uploadUniforms(object);
		if(glMode == GL_TRIANGLES){
			glDrawElements(glMode, object.getICount(), GL_UNSIGNED_INT,
					(long) (object.offset * 6 * 4));
		}else{
			glDrawArrays(glMode, 0, object.getICount());
		}
		Engine.exitOnGLError("Program.render");
	}
	/**
	 * Creates names for uniforms
	 */
	protected void initUniformLocations(){
		for(Uniforms u:uniforms){
			u.setLocId(pId, glGetUniformLocation(pId, u.name()));
		}
		Engine.exitOnGLError("Programs.initUniformLocation()");
	}
	/**
	 * Creates names for program, vertex and fragment shader, compiles and links
	 * the program
	 * @param vert
	 * @param frag
	 */
	protected void initShaders(String vert, String frag){
		int vsId = loadShader("assets/shaders/" + vert, GL_VERTEX_SHADER);
		int fsId = loadShader("assets/shaders/" + frag, GL_FRAGMENT_SHADER);
		pId = glCreateProgram();
		glAttachShader(pId, vsId);
		glAttachShader(pId, fsId);
		glLinkProgram(pId);
		glValidateProgram(pId);
		Engine.exitOnGLError("Programs.initShaders()");
	}
	/**
	 * Uploads all uniforms decladed in program enum for object, if
	 * uniform.object is a string then the uniform is to be fetched from the
	 * object by the method named in the string
	 * @param object
	 */
	protected void uploadUniforms(GameObject object){
		for(Uniforms u:uniforms){
			Object uo = u.object;
			if(u.object instanceof String){
				Method method = null;
				try{
					method = object.getClass().getMethod((String) u.object);
					switch(u.type){
						case 0:
							glUniform1f(u.getLocId(pId),
									(Float) method.invoke(object));
							break;
						case 1:
							glUniform4(u.getLocId(pId),
									(FloatBuffer) method.invoke(object));
							break;
						case 2:
							glUniformMatrix4(u.getLocId(pId), false,
									(FloatBuffer) method.invoke(object));
							break;
						default:
							break;
					}
				}
				catch(Exception e){
					System.out.println("wrong uniform methodname: "
							+ method.toString());
					e.printStackTrace();
				}
			}else{
				switch(u.type){
					case 0:
						glUniform1f(u.getLocId(pId), (Float) uo);
						break;
					case 1:
						glUniform4(u.getLocId(pId), (FloatBuffer) uo);
						break;
					case 2:
						glUniformMatrix4(u.getLocId(pId), false,
								(FloatBuffer) uo);
						break;
					default:
						break;
				}
			}
		}
		Engine.exitOnGLError("Programs.uploadUniforms()");
	}
	/**
	 * Reads .glsl or just txt files and compiles shader from it.
	 * @param filename
	 * @param type
	 * @return
	 */
	private int loadShader(String filename, int type){
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Programs.class.getClassLoader().getResourceAsStream(
							filename)));
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}
		catch(IOException e){
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		Engine.exitOnGLError("Programs.loadShader");
		return shaderID;
	}
	public static void createFrameBuffer(){
		frameBufferid = glGenFramebuffers();
		screenTextureid = glGenTextures();
		int dbid = glGenRenderbuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferid);
		glBindTexture(GL_TEXTURE_2D, screenTextureid);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Engine.WIDTH, Engine.HEIGHT, 0,
				GL_RGB, GL_INT, (java.nio.ByteBuffer)null);
		glBindRenderbuffer(GL_RENDERBUFFER, dbid);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT,
				Engine.WIDTH, Engine.HEIGHT);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
				GL_RENDERBUFFER, dbid);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
				GL_TEXTURE_2D, screenTextureid, 0); // could be wrong
		//glBindTexture(GL_TEXTURE_2D, 0); // remove if fail
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		Engine.exitOnGLError("Programs.createFrameBuffer");
	}
	/**
	 * Calls glUseProgram, no need to before
	 */
	public static void renderViewradiiToTexture(){
		glBindTexture(GL_TEXTURE_2D,0);
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferid);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glBindTexture(GL_TEXTURE_2D, screenTextureid);
	/*	glEnable(GL_STENCIL_TEST);
		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);
		glUseProgram(Programs.PROGRAM3DNL.pId);
		for(ViewRadiusStencil sv:Game.stencilradii)
			sv.render();
		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);
		glStencilFunc(GL_EQUAL, 0, 0xff);*/
		glUseProgram(Programs.PROGRAM3DNL.pId);
		for(ViewRadiusRange v:Game.radii)
			v.render();
		//glDisable(GL_STENCIL_TEST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glBindTexture(GL_TEXTURE_2D,0);
	}
}
