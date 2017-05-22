package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public abstract class Unit extends GameObject{
	public volatile static int count = 0;
	protected ViewRadiusRange viewRadius;
	protected ViewRadiusStencil viewRadiusStencil;
	protected ViewRadiusFOV fieldOfView;
	protected Unit hostile;
	protected float viewRange;
	protected float rotDelta;
	public boolean dead = false;
	protected float health;
	protected Mesh icon;
	protected Mesh unit;
	protected Billboard billboard;
	protected Billboard healthbar;
	protected float bsizeX = 0.6f;
	protected float bsizeY = 0.08f;
	/**
	 * Calls {@link #setMesh()}, {@link #setIconMesh()}, {@link #setUnitMesh()},
	 * {@link #adjustAttackspeed()}, {@link #adjustHealth()},
	 * {@link #updateTexture()}
	 * @param position
	 * @param ownerId
	 */
	public Unit(Vector3f position, int ownerId){
		this.ownerId = ownerId;
		this.modelPos = new Vector3f(position);
		setMesh();
		setUnitMesh();
		setIconMesh();
		adjustHealth();
		adjustAttackspeed();
		updateTexture();
		billboard = new Billboard(modelPos, Textures.GUIUPGBGRD, bsizeX, bsizeY);
		healthbar = new Billboard(new Vector3f(this.modelPos.x,
				this.modelPos.y, this.modelPos.z + 1.501f),
				Textures.BUTTONORANGE, bsizeX, 4.0f * bsizeY / 5.0f);
		Game.healthbarbgrd.add(billboard);
		Game.healthbar.add(healthbar);
	}
	protected abstract void updateHealthBar();
	protected abstract void adjustAttackspeed();
	protected abstract void adjustHealth();
	protected abstract void setUnitMesh();
	protected abstract void setIconMesh();
	protected abstract void updateTexture();
	protected abstract void attack();
	protected abstract void processDeath();
	protected abstract void setMesh();
	/**
	 * PROGRAMDNL if reducedgraphics or iconrenderheight program3d else
	 */
	@Override
	public void render(){
		if(!dead){
			if(Game.iconRenderHeight || Config.NOLIGHTING.isOn){
				updateModelMatrix();
				Programs.PROGRAM3DNL.render(this);
			}else{
				updateModelMatrix();
				Programs.PROGRAM3D.render(this);
			}
		}
	}
	/**
	 * Subtracts damage from health and check deadness
	 * @param damage
	 * @return true if dead
	 */
	public boolean takeDamage(float damage,int ownerId){
		health -= damage;
		if(isDead()){
			setDead();
			return true;
		}
		return false;
	}
	protected boolean isDead(){
		if(health <= 0){ return true; }
		return false;
	}
	public void revive(Vector3f modelPos, int ownerId){
		this.ownerId = ownerId;
		this.modelPos.set(modelPos.x, modelPos.y, 0.0f);
		target = new Vector3f(modelPos);
		adjustHealth();
		dead = false;
		billboard.dead = false;
		healthbar.dead = false;
		updateHealthBar();
	}
	public void setDead(){
		dead = true;
		billboard.dead = true;
		healthbar.dead = true;
		processDeath();
	}
	public abstract float getHEALTH();
	public abstract float getATTACKSPEED();
	public abstract float getRANGE();
	public abstract void upgradeHealth(float maxHealth);
}
