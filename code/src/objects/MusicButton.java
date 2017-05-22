package objects;

import engine.Config;
import engine.Sound;
import engine.Sounds;
import engine.Textures;

/**
 * @author thei
 */
public class MusicButton extends MenuButton{
	public MusicButton(int posX, int posY, int sizeX, int sizeY, Config setting){
		super(posX, posY, sizeX, sizeY, setting);
	}
	/**
	 * {@link GuiElement#GuiElement(int, int, int, int, Textures)}
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
		Sound.toggleSound(Sounds.SUPCOM);
	}
}
