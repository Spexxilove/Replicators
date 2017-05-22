package objects;

import org.lwjgl.util.vector.Vector3f;
import meshes.Gui;
import engine.Textures;

/**
 * @author thei
 */
public class GuiBackground extends GuiElement{
	public GuiBackground(int posX, int posY, int sizeX, int sizeY,
			Textures texture){
		super(posX, posY, sizeX, sizeY, texture);
		updateModelMatrix();
	}
	@Override
	public boolean update(){
		return false;
	}
	@Override
	public boolean hit(int x, int y){
		return false;
	}
	@Override
	public void click(){
		// do nothing
	}
	@Override
	public void setGuiMesh(){
		this.gui = Gui.RECTANGLE;
	}
	@Override
	protected void onClick(){
		// TODO Auto-generated method stub
	}
	@Override
	protected void onHold(){
		// TODO Auto-generated method stub
	}
	public void moveNresize(int posX,int posY, int sizeX,int sizeY){
		this.modelPos = new Vector3f(xRatio * posX, yRatio * posY, 0);
		this.modelScale = new Vector3f(xRatio * sizeX, yRatio * sizeY, 1.0f);
	}
}
