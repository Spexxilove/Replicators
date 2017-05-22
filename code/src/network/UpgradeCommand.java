package network;

import engine.Upgrades;

public class UpgradeCommand extends Command{ 
	private Upgrades upgrade;
	public UpgradeCommand(int id,int playerId, Upgrades upgrade){
		super(id,playerId);
		this.id = id;
		this.upgrade = upgrade;
		this.playerId = playerId;
	}
	public Upgrades getUpgrade(){return this.upgrade;}
	public int getId(){return id;}
	public int getPlayerId(){return playerId;}
	public String toString(){
		String out = "round: "+id;
		out+= " playerid: ";
		out+= playerId;
		out+= " target: ";
		return out;
	}
}
