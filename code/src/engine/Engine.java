package engine;

import meshes.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import client.*;
import gui.Formatation;
import gui.Lobby;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Engine: Handles initialization, gameloop and cleanup
 * @author thei
 * @version 1.0
 */
public class Engine extends Thread{
	public static int WIDTH;
	public static int HEIGHT;
	private static NetworkClient client;
	private Game game;
	private Lobby lobby;
	/**
	 * Constructor
	 * @param client NetworkClient ref, to access send methods
	 */
	public Engine(NetworkClient client, Lobby lobby){
		Engine.client = client;
		this.lobby = lobby;
	}
	public Engine(){
	}
	/**
	 * Runs: {@link engine.Engine#init()} {@link engine.Engine#gameLoop()}
	 * {@link engine.Engine#cleanUp()}
	 */
	public void run(){
		init();
		gameLoop();
		cleanUp();
	}
	/**
	 * Loops: input: {@link engine.Game#getInput()} update:
	 * {@link engine.Game#update()} render: {@link engine.Game#render()}
	 */
	private void gameLoop(){
		while(!Display.isCloseRequested() && !Game.closeRequested){
			game.getInput();
			game.update();
			game.render();
		}
	}
	/**
	 * Calls every init functions: display {@link #initDisplay()} opengl
	 * {@link #initOpenGL()} input {@link #initInput()} game {@link Game#Game()}
	 * , textures {@link #initTextures()} uniforms {@link #initUniforms()}
	 * meshes {@link #initMeshes()} shader programs
	 * {@link #initShaderPrograms()} sound {@link Sound#init()}
	 */
	private void init(){
		initDisplay();
		initOpenGL();
		initInput();
		Camera.init(40, 0.3f, 1000);
		game = new Game(client, lobby);
		Sound.init();
		initTextures();
		initUniforms();
		initShaderPrograms();
		initMeshes();
		initGui();
		initPrimitives();
	}
	/**
	 * Creates a borderless window titled "REPLICATORS" and resizes it to fit
	 * desktop resolution {@link engine.Engine#setDisplayMode()}
	 */
	private void initDisplay(){
		try{
			System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			setDisplayMode();
			Display.setTitle("REPLICATORS");
			Display.create(new PixelFormat(4, 4, 4, 4));
		}
		catch(LWJGLException e){
			System.out.println("Error setting up display");
			System.exit(0);
		}
		Engine.exitOnGLError("Engine.initDisplay");
	}
	/**
	 * Sets line smooth, depth tests, initialized matrices to the identity
	 * matrix lots of probably unneded stuff
	 */
	private void initOpenGL(){
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glLineWidth(3.0f);
		glMatrixMode(GL_MODELVIEW);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glLoadIdentity();
		glShadeModel(GL_SMOOTH);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		Programs.createFrameBuffer();
	}
	/**
	 * Init static keyboard and mouse {@link Mouse#create()}
	 * {@link Keyboard#create()}
	 */
	private void initInput(){
		try{
			Keyboard.create();
			Mouse.create();
		}
		catch(LWJGLException e){
			e.printStackTrace();
		}
	}
	/**
	 * Loops though Texture enum {@link Textures#init()}
	 */
	private void initTextures(){
		for(Textures t:Textures.values()){
			t.init();
		}
	}
	/**
	 * Loops though Uniforms enum {@link Uniformss#init()}
	 */
	private void initUniforms(){
		for(Uniforms u:Uniforms.values()){
			u.init();
		}
	}
	/**
	 * Loops though Programs enum {@link Programs#init()}
	 */
	private void initShaderPrograms(){
		for(Programs p:Programs.values()){
			p.init();
		}
	}
	/**
	 * Loops though Mesh enum {@link Mesh#init()}
	 */
	private void initMeshes(){
		for(Mesh m:Mesh.values()){
			m.init();
		}
	}
	/**
	 * Loops though Gui enum {@link Gui#init()}
	 */
	private void initGui(){
		for(Gui g:Gui.values()){
			g.init();
		}
	}
	/**
	 * Loops though Primitives enum {@link Primitives#init()}
	 */
	private void initPrimitives(){
		for(Primitives p:Primitives.values()){
			p.init();
		}
	}
	/**
	 * Cleans references mostly opengl ints, closes display sound keyboard
	 * mouse, frees graphics vram
	 */
	private void cleanUp(){
		if(!(client == null))
			client.sendEndGame();
		for(Textures t:Textures.values()){
			glDeleteTextures(t.id);
		}
		glUseProgram(0);
		for(Programs p:Programs.values()){
			glDeleteProgram(p.pId);
		}
		for(Mesh m:Mesh.values()){
			glBindVertexArray(m.vao);
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glDisableVertexAttribArray(2);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glDeleteBuffers(m.vbo);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glDeleteBuffers(m.vboiId);
			glBindVertexArray(0);
			glDeleteVertexArrays(m.vao);
		}
		Display.destroy();
		Sound.cleanUp();
		Keyboard.destroy();
		Mouse.destroy();
		if(lobby!=null){
		lobby.print("Spiel wurde erfolgreich beendet", "Main",Formatation.game);//("cleanup successful")
		lobby.cleanup();
		}
	}
	/**
	 * Sets resolution to desktop resolution and vsync to true
	 */
	public static void setDisplayMode(){
		DisplayMode desktopMode = Display.getDesktopDisplayMode();
		Engine.HEIGHT = desktopMode.getHeight();
		Engine.WIDTH = desktopMode.getWidth();
		try{
			Display.setDisplayMode(desktopMode);
			if(Config.FULLSCREEN.isOn)
				Display.setFullscreen(true);
			Display.setVSyncEnabled(true);
		}
		catch(LWJGLException e){
			System.out.println("Unable to set display mode");
		}
	}
	/**
	 * Static opengl error check function to call when doing opengl calls
	 * @param errorMessage String to be display alongside opengl error
	 */
	public static void exitOnGLError(String errorMessage){
		int errorValue = glGetError();
		if(errorValue != GL_NO_ERROR){
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);
			if(Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}
}// END OF ENGINE CLASS
