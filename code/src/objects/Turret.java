package objects;

import engine.*;
import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author thei
 */
public class Turret extends Unit{
	public volatile static int count = 0;

	private float attackspeed = 20;
	private float range = MapTile.TILESIZE / 2;
	public float reloadTime = 0;
	public int tileId;
	private float maxHealth;
	private int regenTimer = 0;
	private float regenAmount;
	public Turret(Vector3f position, int ownerId, int tileId){
		super(position, ownerId);
		this.id = Turret.count++;
		this.tileId = tileId;
		this.Shininess = 2;
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(.2f, .2f, .2f);
		viewRadius = new ViewRadiusRange(this.modelPos, range, ownerId);
		viewRadiusStencil = new ViewRadiusStencil(this.modelPos, range - 0.02f,
				ownerId);
		fieldOfView = new ViewRadiusFOV(this.modelPos, range + 1.0f,
				ownerId);
		this.rotDelta = 8.0f;
		Game.radii.add(viewRadius);
		Game.stencilradii.add(viewRadiusStencil);
		Game.fieldofview.add(fieldOfView);
	}
	/**
	 * Analogous to unit update, just doesn't move / turn
	 * @see BasicUnit#update()
	 */
	@Override
	public boolean update(){
		if(dead){ return true; }
		fieldOfView.setOwnerId(this.ownerId);
		updateHealthBar();
		updateTexture();
		regenHealth();
		adjustAttackspeed();
		attack();
		return false;
	}
	/**
	 * 
	 */
	protected void regenHealth(){
		if(regenTimer <= 0)
			return;
		health += regenAmount / regenTimer;
		regenAmount -= regenAmount / regenTimer;
		regenTimer--;
	}
	/**
	 * Looks for target and rotates, shoots every 'attackspeed' frames, if
	 * looking in the right direction
	 */
	protected void attack(){
		if(reloadTime-- <= 0){
			reloadTime = 0;
		}
		if(!hostileInRange())
			return;
		if(!rotate()){
			if(reloadTime <= 0){
				shotAt(this.target);
			}
		}
	}
	/**
	 * <strong>'rotDelta' need to be set </strong>
	 * @return false if rotating, true if looking into the right direction
	 */
	protected boolean rotate(){
		if(this.target == null)
			return false;
		if(this.target.equals(this.modelPos))
			return false;
		float tx = modelPos.x - target.x;
		float ty = modelPos.y - target.y;
		float angle = (float) Math.toDegrees(Math.atan(Math.abs(ty / tx)));
		angle = 180 + Math.signum(tx) * 90 + Math.signum(tx) * Math.signum(ty)
				* angle;
		angle = (float) Math.round(angle * 10) / 10;
		if(this.modelAngle.z == angle)
			return false;
		float tmp = this.modelAngle.z - angle;
		if(Math.abs(tmp) < rotDelta){
			this.modelAngle.z = angle;
			return false;
		}else
			this.modelAngle.z += (Math.abs(tmp) > 180 ? 1.0f : -1.0f)
					* Math.signum(tmp) * rotDelta;
		if(this.modelAngle.z > 360.0f)
			this.modelAngle.z %= 360.0f;
		else if(this.modelAngle.z < 0.0f)
			this.modelAngle.z = 360.0f - this.modelAngle.z;
		return true;
	}
	/**
	 * Calls {@link #findTarget()}
	 * @return true if there's a hostile alive and in range, else false
	 */
	private boolean hostileInRange(){
		if(hostile != null && (hostile.dead || hostile.ownerId == this.ownerId)){
			hostile = null;
		}
		if(hostile != null){
			Vector3f tmp = Vector3f.sub(this.modelPos, hostile.modelPos, null);
			if(tmp.length() <= Upgrades.UNITRANGE.defaultAmount - 1.0f)
				return true;
			else
				hostile = null;
		}
		if(findTarget())
			return true;
		return false;
	}
	/**
	 * Asign enemy modelposition to target, skips itself, friendlys and dead
	 * units
	 * @param return true if target found, else false
	 */
	protected boolean findTarget(){
		// shoot units
		float tmpDist = -1;
		Unit tmpUnit = null;
		for(Unit u:Game.map.get(tileId).unitMap){ // loop through units on
													// maptile
			if(this.id == u.id)
				continue;
			if(this.ownerId == u.ownerId)
				continue;
			if(u.dead)
				continue;
			Vector3f tmp = Vector3f.sub(this.modelPos, u.modelPos, null);
			if(tmpDist == -1 || tmp.length() < tmpDist){
				tmpUnit = u;
				tmpDist = tmp.length();
			}else
				continue;
		}
		if(tmpDist != -1 && tmpDist <= range && tmpUnit != null){
			this.hostile = tmpUnit;
			this.target = this.hostile.modelPos;
			return true;
		}
		return false;
	}
	protected void adjustAttackspeed(){
		if(this.ownerId != -1){
			attackspeed = Game.players.get(ownerId).getStats(
					Upgrades.TURRETATTACKSPEED);
		}else{
			attackspeed = 4;
		}
	}
	@Override
	public void revive(Vector3f modelPos,int ownerId){
		this.billboard.dead = false;
		this.healthbar.dead = false;
		this.dead = false;
		this.target = null;
		this.hostile = null;
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		setOwnerId(ownerId);
		adjustHealth();
		adjustAttackspeed();
		billboard.dead = false;
		healthbar.dead = false;
		fieldOfView.dead = false;
		fieldOfView.setOwnerId(this.ownerId);
		viewRadius.setOwnerId(this.ownerId);
		viewRadiusStencil.setOwnerId(this.ownerId);
	}
	public void adjustHealth(){
		if(this.ownerId != -1){
			this.maxHealth = Game.players.get(ownerId).getStats(
					Upgrades.TURRETHEALTH);
		}else{
			this.maxHealth = 30;
		}
		this.health = this.maxHealth;
	}
	protected void shotAt(Vector3f victimPos){
		Sound.playSound(this.modelPos, Sounds.TURRETPROJECTILE);
		reloadTime = attackspeed * 6;
		Projectile shot = new TurretProjectile(this.modelPos, victimPos,
				this.ownerId);
		Game.shots.add(shot);
	}
	@Override
	protected void updateTexture(){
		if(this.ownerId == -1){
			texture = Textures.TURRETORANGE;
		}else{
			texture = Textures.valueOf("TURRET"
					+ Game.players.get(ownerId).COLOR.name());
		}
	}
	@Override
	protected void processDeath(){
		this.dead = true;
		fieldOfView.dead = true;
	}
	@Override
	public float getHEALTH(){
		return health;
	}
	@Override
	public float getATTACKSPEED(){
		return attackspeed;
	}
	@Override
	public float getRANGE(){
		return range;
	}
	@Override
	public int getVAO(){
		return mesh.vao;
	}
	@Override
	public int getICount(){
		return mesh.iCount;
	}
	@Override
	protected void setMesh(){
		this.mesh = Mesh.TURRET;
	}
	@Override
	protected void setUnitMesh(){
		this.unit = Mesh.REPLOCATOR1;
	}
	@Override
	protected void setIconMesh(){
		this.icon = Mesh.BOX;
	}
	@Override
	protected void updateHealthBar(){
		billboard.update(this.modelPos, 1.5f);
		healthbar.update(this.modelPos, 1.501f);
		healthbar.resize(bsizeX / maxHealth * this.health);
	}
	@Override
	public void upgradeHealth(float maxHealth){
		this.regenAmount = maxHealth - this.maxHealth;
		this.maxHealth = maxHealth;
		this.regenTimer = 5 * 6 * 10;
	}
	@Override
	public boolean takeDamage(float damage,int ownerId){
		health -= damage;
		if(isDead()){
			setDead();
			revive(modelPos, ownerId);
			return true;
		}
		return false;
	}
}
