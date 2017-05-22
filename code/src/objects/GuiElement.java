package objects;

import org.lwjgl.util.vector.Vector3f;
import engine.Config;
import engine.Engine;
import engine.Programs;
import engine.Textures;

/**
 * @author thei
 */
public abstract class GuiElement extends GameObject{
	protected static float xRatio = 1.0f / (Engine.WIDTH / 2);
	protected static float yRatio = 1.0f / (Engine.HEIGHT / 2);
	protected int posX;
	protected int posY;
	protected int sizeX;
	protected int sizeY;
	protected int clickId = -1;
	protected Textures defaultTexture;
	public boolean isClicked = false;
	/**
	 * @param posX
	 * @param posY
	 * @param sizeX is actually half the size, i.e. size from posS in both
	 * directions
	 * @param sizeY is actually half the size, i.e. size from posY in both
	 * directions
	 * @param texture
	 */
	public GuiElement(int posX, int posY, int sizeX, int sizeY, Textures texture){
		setGuiMesh();
		this.texture = texture;
		this.defaultTexture = texture;
		this.modelPos = new Vector3f(xRatio * posX, yRatio * posY, 0);
		this.modelAngle = new Vector3f(0.0f, 0.0f, 0.0f);
		this.modelScale = new Vector3f(xRatio * sizeX, yRatio * sizeY, 1.0f);
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	public abstract void setGuiMesh();
	/**
	 * Changes button color on click for clickId number of frames
	 */
	@Override
	public boolean update(){
		if(clickId-- >= 0){
			this.texture = Textures.valueOf(defaultTexture.name() + "SELECTED");
		}else{
			this.texture = this.defaultTexture;
		}
		return false;
	}
	public boolean hit(int x, int y){
		if(!Config.SHOWMENU.isOn)
			return false;
		if(Math.abs(x - posX) <= sizeX && Math.abs(y - posY) <= sizeY)
			return true;
		return false;
	}
	public void click(){
		if(isClicked){
			this.clickId = 1;
			onHold();
		}else{
			this.clickId = 6;
			isClicked = true;
			onClick();
		}
	}
	/**
	 *
	 */
	protected abstract void onClick();
	/**
	 * 
	 */
	protected abstract void onHold();
	/**
	 * 
	 */
	public void release(){
		this.isClicked = false;
	}
	@Override
	public void render(){
		updateModelMatrix();
		Programs.PROGRAMGUI.render(this);
	}
	@Override
	public int getVAO(){
		return gui.vao;
	}
	@Override
	public int getICount(){
		return gui.iCount;
	}
}
