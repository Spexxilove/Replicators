package objects;

import meshes.Gui;
import engine.Engine;
import engine.Programs;
import engine.Textures;

/**
 * @author thei
 */
public class ScreenTexture extends GuiElement{
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public ScreenTexture(){
		super(0, 0, Engine.WIDTH/2, Engine.HEIGHT/2,
				Textures.STENCILTEXTURE);
	}
	@Override
	public void render(){
		updateModelMatrix();
		Programs.PROGRAMGUI.render(this);
	}
	@Override
	public void setGuiMesh(){
		this.gui = Gui.INVERSERECTANGLE;
	}
	@Override
	protected void onClick(){
		// TODO Auto-generated method stub
	}
	@Override
	protected void onHold(){
		// TODO Auto-generated method stub
	}
}
