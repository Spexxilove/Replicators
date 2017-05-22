package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.Game;
import engine.Textures;
import engine.Upgrades;

/**
 * @author thei
 */
public class Laser extends Projectile{
	public Laser(Vector3f position, Vector3f target, int ownerId){
		super(position, target, ownerId);
	}
	@Override
	protected void setMesh(){
		this.mesh = Mesh.PROJECTILE;
	}
	@Override
	protected void setTexture(){
		this.texture = Textures.LASER;
	}
	@Override
	protected void setSpeed(){
		this.maxSpeed = 0.5f;
	}
	@Override
	protected void setDamage(){
		this.damage = Game.players.get(ownerId).getStats(Upgrades.UNITDAMAGE);
	}
	@Override
	protected void setLifetime(){
		this.lifetime = 6;
	}
	@Override
	protected void setScale(){
		this.modelScale = new Vector3f(0.5f, 0.3f, 0.5f);
	}
}
