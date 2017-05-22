package engine;

import objects.MapTile;
import objects.Unit;

public class Upgrade{
	private static final int DEFAULTUPGRADECOST = 10;
	protected final int costMultiplicator = 2;
	private int ownerId;
	private Upgrades upgrade;
	public float amount;
	public int cost;
	public float multiplier;
	public int level;
	public Upgrade(Upgrades upgrade, int ownerId){
		this.ownerId = ownerId;
		this.upgrade = upgrade;
		this.amount = this.upgrade.defaultAmount;
		this.cost = DEFAULTUPGRADECOST;
		this.multiplier = this.upgrade.multiplier;
		this.level = 0;
	}
	public void upgrade(){
		amount += upgrade.increaseAmount * multiplier;
		multiplier = (float) Math.pow(multiplier, 1.3);
		cost *= costMultiplicator;
		level++;
		if(upgrade == Upgrades.TURRETHEALTH){
			for(MapTile m:Game.map){
				if(m.ownerId != this.ownerId)
					continue;
				m.turret.upgradeHealth(amount);
			}
		}
		if(upgrade == Upgrades.UNITHEALTH){
			for(Unit u:Game.units){
				if(u.ownerId != this.ownerId)
					continue;
				u.upgradeHealth(amount);
			}
		}
	}
}
