package objects;

import meshes.Gui;
import engine.Camera;
import engine.Config;
import engine.Textures;
import java.lang.reflect.*;

/**
 * @author thei
 */
public class NavigateButton extends GuiElement{
	private String methodName;
	private float p;
	public NavigateButton(int posX, int posY, int sizeX, int sizeY,
			Textures texture, String methodName, float p){
		super(posX, posY, sizeX, sizeY, texture);
		updateModelMatrix();
		this.methodName = methodName;
		this.p = p;
	}
	@Override
	public boolean update(){
		super.update();
		return false;
	}
	@Override
	public boolean hit(int x, int y){
		if(!Config.ISTOUCHSCREEN.isOn)
			return false;
		if(Math.abs(x - posX) <= sizeX && Math.abs(y - posY) <= sizeY)
			return true;
		return false;
	}
	@Override
	public void setGuiMesh(){
		this.gui = Gui.RECTANGLE;
	}
	@Override
	protected void onClick(){
		onHold();
	}
	@Override
	protected void onHold(){
		try{
			Method m = Camera.class.getDeclaredMethod(methodName, Float.TYPE);
			m.invoke(null, p);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
