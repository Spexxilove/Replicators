package objects;

import meshes.Gui;
import engine.Config;
import engine.Game;
import engine.Textures;

/**
 * @author thei
 */
public class MenuButton extends GuiElement{
	public Config setting;
	/**
	 * {@link GuiElement#GuiElement(int, int, int, int, Textures)}
	 */
	public MenuButton(int posX, int posY, int sizeX, int sizeY, Config setting){
		super(posX, posY, sizeX, sizeY, Textures.BUTTONORANGE);
		this.setting = setting;
	}
	@Override
	public boolean update(){
		return false;
	}
	@Override
	public boolean hit(int x, int y){
		return super.hit(x, y);
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
		Game.text.render(this.setting.name(), this.posX
				- this.setting.name().length() * 5, this.posY, 5, 8);
	}
	/**
	 * toggles setting and button color
	 */
	@Override
	protected void onClick(){
		if(!Config.SHOWMENU.isOn)
			return;
		this.setting.toggleSetting();
		if(this.setting.isOn)
			this.texture = Textures.BUTTONORANGESELECTED;
		else
			this.texture = Textures.BUTTONORANGE;
	}
	@Override
	protected void onHold(){
	}
}
