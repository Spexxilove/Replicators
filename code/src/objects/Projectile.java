package objects;

import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public abstract class Projectile extends GameObject{
	public volatile static int count = 0;
	protected int startRound;
	protected int lifetime;
	protected float hitradius = 0.35f;
	protected float damage;
	/**
	 * {@link #setMesh()}, {@link #setTexture()}, {@link #setSpeed()},
	 * {@link #setLifetime()}, {@link #setDamage()}, {@link #setScale()}
	 * @param position
	 * @param target
	 * @param ownerId
	 */
	public Projectile(Vector3f position, Vector3f target, int ownerId){
		this.id = Projectile.count++;
		this.ownerId = ownerId;
		setMesh();
		setTexture();
		setSpeed();
		setLifetime();
		setDamage();
		setScale();
		Vector3f.sub(target, position, direction);
		this.target = new Vector3f(target);
		double rot = Math.toDegrees(Math.atan(direction.y / direction.x));
		this.modelAngle = new Vector3f(90f, 0.0f, (float) (90 + rot));
		this.startRound = Game.getId;
		direction.normalise(direction);
		Vector3f offset = new Vector3f(direction);
		offset.scale(0.2f);
		this.modelPos = new Vector3f(Vector3f.add(position, offset, null));
		direction.scale(maxSpeed);
	}
	protected abstract void setScale();
	protected abstract void setMesh();
	protected abstract void setTexture();
	protected abstract void setSpeed();
	protected abstract void setDamage();
	protected abstract void setLifetime();
	/**
	 * Calls {@link #checkHit()}, {@link #checkLife()}, {@link #move()}
	 * @return true if hit or travelled far enough, destroyed
	 */
	@Override
	public boolean update(){
		if(checkLife() || checkHit()){ return true; }
		move();
		return false;
	}
	/**
	 * Stop for noone xD
	 */
	private void move(){
		Vector3f.add(modelPos, direction, modelPos);
	}
	/**
	 * @return false if withing range
	 */
	private boolean checkLife(){
		if(Game.getId - startRound > lifetime){ return true; }
		return false;
	}
	/**
	 * Simple radius check
	 * @return true if shot is to be removed
	 */
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
				damage(t, this.damage);
				return true;
			}
		}
		for(Unit u:Game.units){
			if(u.ownerId == this.ownerId)// if you want friendly fire, change
				continue;
			if(u.dead)
				continue;
			Vector3f.sub(this.modelPos, u.modelPos, dist);
			if(dist.length() <= hitradius){
				damage(u, this.damage);
				return true;
			}
		}
		return false;
	}
	/**
	 * Deal damage
	 * @param t victim
	 * @param damage
	 */
	protected void damage(Unit t, float damage){
		int toid = t.ownerId;
		if(t.takeDamage(damage,this.ownerId)){
			if(ownerId != -1){ // if not neutral
				if(t instanceof Turret)
					if(toid == -1)
						Game.players.get(ownerId).score++;
					else{
						int score = Game.players.get(toid).score/10;
						Game.players.get(ownerId).score += (score>20?score:20);
					}
				else
					Game.players.get(ownerId).score++;
			}
		}
	}
	@Override
	public void render(){
		updateModelMatrix();
		Programs.PROGRAM3DNL.render(this);
	}
	@Override
	public int getVAO(){
		return mesh.vao;
	}
	@Override
	public int getICount(){
		return mesh.iCount;
	}
}
