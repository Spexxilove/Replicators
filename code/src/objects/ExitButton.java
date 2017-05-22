package objects;

import meshes.Gui;
import engine.Config;
import engine.Game;
import engine.Textures;

/**
 * @author thei
 */
public class ExitButton extends GuiElement{
	public ExitButton(int posX, int posY, int sizeX, int sizeY, Textures texture){
		super(posX, posY, sizeX, sizeY, texture);
		updateModelMatrix();
	}
	@Override
	public boolean update(){
		super.update();
		return false;
	}
	@Override
	public void setGuiMesh(){
		this.gui = Gui.RECTANGLE;
	}
	@Override
	public void render(){
		if(!Config.SHOWMENU.isOn)
			return;
		super.render();
	}
	@Override
	protected void onClick(){
		if(!Config.SHOWMENU.isOn)
			return;
		Game.closeRequested = true;
	}
	@Override
	protected void onHold(){
		// TODO Auto-generated method stub
	}
}
