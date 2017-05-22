package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.Game;
import engine.Textures;
import engine.Upgrades;

/**
 * @author thei
 */
public class TurretProjectile extends Projectile{
	public TurretProjectile(Vector3f position, Vector3f target, int ownerId){
		super(position, target, ownerId);
	}
	public void updateDamage(int damage){
		this.damage = damage;
	}
	@Override
	protected void setMesh(){
		this.mesh = Mesh.PROJECTILE;
	}
	@Override
	protected void setTexture(){
		this.texture = Textures.TURRETPROJECTILE;
	}
	@Override
	protected void setSpeed(){
		if(ownerId == -1){
			this.maxSpeed = 0.2f;
		}else{
			this.maxSpeed = 0.2f+0.075f*Game.players.get(ownerId).getLevel(Upgrades.TURRETATTACKSPEED);
		}
	}
	@Override
	protected void setDamage(){
		if(ownerId == -1){
			this.damage = Upgrades.TURRETDAMAGE.defaultAmount;
		}else{
			this.damage = Game.players.get(ownerId).getStats(
					Upgrades.TURRETDAMAGE);
		}
	}
	@Override
	protected void setLifetime(){
		this.lifetime = 8;
	}
	@Override
	protected void setScale(){
		this.modelScale = new Vector3f(0.7f, 0.3f, 0.7f);
	}
	/**
	 * Simple radius check
	 * @return true if shot is to be removed
	 */
	@Override
	protected boolean checkHit(){
		Vector3f dist = new Vector3f(0, 0, 0);
		for(MapTile tile:Game.map){
			Turret t = tile.turret;
			if(t.ownerId == this.ownerId)// if you want friendly fire, change
				continue;
			if(t.dead)
				continue;
			Vector3f.sub(this.modelPos, t.modelPos, dist);
			if(dist.length() <= hitradius){
				float h = t.health;
				damage(t, this.damage);
				weakenShotByDamageDone(h);
			}
		}
		for(Unit u:Game.units){
			if(u.ownerId == this.ownerId)// if you want friendly fire, change
				continue;
			if(u.dead)
				continue;
			Vector3f.sub(this.modelPos, u.modelPos, dist);
			if(dist.length() <= hitradius){
				float h = u.health;
				damage(u, this.damage);
				weakenShotByDamageDone(h);
			}
		}
		return false;
	}
	/**
	 * @param health
	 * @return true if shot is to be removed, false if still damage left, so it
	 * may continue its journey
	 */
	protected void weakenShotByDamageDone(float health){
		this.damage -= health;
		if(this.damage <= 0)
			this.damage = 1.0f;
	}
}
