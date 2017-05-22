package objects;

import network.UpgradeCommand;
import meshes.Gui;
import engine.Game;
import engine.Player;
import engine.Textures;
import engine.Upgrades;

/**
 * @author thei
 */
public class UpgradeButton extends GuiElement{
	private Upgrades upgrade;
	private Player player;
	public UpgradeButton(int posX, int posY, int sizeX, int sizeY,
			Textures texture, Upgrades upgrade){
		super(posX, posY, sizeX, sizeY, texture);
		this.player = Game.players.get(Game.playerId);
		this.upgrade = upgrade;
	}
	@Override
	public boolean update(){
		super.update();
		if(player.hasPoints(upgrade)){
			clickId = 1;
		}
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
	protected void onClick(){
		if(player.hasPoints(upgrade)){
			UpgradeCommand upg = new UpgradeCommand(Game.sendId, Game.playerId,
					this.upgrade);
			if(!Game.debug){
				Game.client.sendUpgrade(upg);
			}else{
				Game.commands.add(upg);
			}
		}
	}
	@Override
	protected void onHold(){
		// TODO Auto-generated method stub
	}
	@Override
	public void render(){
		super.render();
		String level = String.valueOf(player.getLevel(upgrade));
		int p = player.getUpgradeCost(upgrade) - player.getPoints();
		p = p >= 0 ? p : 0;
		String pToNextUpdate = String.valueOf(p);
		Game.text.render(level, posX - sizeX + 6, posY - sizeY + 9, 6, 9);
		Game.text.render(pToNextUpdate, posX - sizeX + 5, posY + sizeY - 8, 5,
				8);
	}
}
