package engine;

import java.util.HashMap;

public class Player{
	public final String NAME;
	public final Color COLOR;
	public final int id;
	public int score = 0;
	public HashMap<Upgrades, Upgrade> upgrades;
	protected int lastUpgrade = 0;
	public int unitCount = 0;
	public Player(int id, String name, Color color,
			HashMap<Upgrades, Upgrade> upgrades){
		this.id = id;
		this.COLOR = color;
		this.NAME = name;
		this.upgrades = upgrades;
	}
	public void upgrade(Upgrades upg){
		Upgrade u = upgrades.get(upg);
		if(hasPoints(upg)){
			lastUpgrade += u.cost;
			u.upgrade();
		}
	}
	public boolean hasPoints(Upgrades upg){
		Upgrade u = upgrades.get(upg);
		if(getPoints() >= u.cost){ return true; }
		return false;
	}
	public int getUpgradeCost(Upgrades upg){
		return upgrades.get(upg).cost;
	}
	public float getStats(Upgrades upg){
		return this.upgrades.get(upg).amount;
	}
	public int getLevel(Upgrades upg){
		return this.upgrades.get(upg).level;
	}
	public int getPoints(){
		return this.score - lastUpgrade;
	}
}
