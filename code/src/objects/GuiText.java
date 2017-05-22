package objects;

import org.lwjgl.util.vector.Vector3f;
import meshes.Gui;
import engine.Engine;
import engine.Programs;
import engine.Textures;

/**
 * @author thei
 */
public class GuiText extends GuiElement{
	public GuiText(int posX, int posY, int sizeX, int sizeY){
		super(posX, posY, sizeX, sizeY, Textures.FONT);
	}
	/**
	 * Renders by looping through chars and drawing a box for every character
	 * @param str String to be rendered
	 * @param posX
	 * @param posY
	 * @param sizeX 5 is a good value
	 * @param sizeY 8 is a good value
	 */
	public void render(String str, int posX, int posY, int sizeX, int sizeY){
		this.modelScale = new Vector3f((1.0f / (Engine.WIDTH / 2)) * sizeX,
				(1.0f / (Engine.HEIGHT / 2)) * sizeY, 1.0f);
		this.modelPos = new Vector3f((1.0f / (Engine.WIDTH / 2)) * posX,
				(1.0f / (Engine.HEIGHT / 2)) * posY, 0);
		Vector3f tmp = new Vector3f(this.modelPos);
		for(int i = 0; i < str.length(); ++i){
			int c = (int) str.charAt(i);
			this.offset = c;
			updateModelMatrix();
			Programs.PROGRAMGUI.render(this);
			this.modelPos.x += (1.0f / (Engine.WIDTH / 2)) * 2 * sizeX;
		}
		this.modelPos = new Vector3f(tmp);
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
		this.gui = Gui.FONT;
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
