package objects;

import static engine.Sounds.LASER;
import engine.*;
import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author thei
 */
public class BasicUnit extends Unit{
	public volatile static int count = 0;
	private ViewRadiusRange viewRadius;
	private ViewRadiusStencil viewRadiusStencil;
	private ViewRadiusFOV fieldOfView;
	private int animationId = 0;
	private float attackspeed = 0;
	private float range = Game.players.get(ownerId)
			.getStats(Upgrades.UNITRANGE);
	public float reloadTime = 0;
	private float maxHealth;
	private float regenAmount = 0.1f / 6.0f;
	/**
	 * @param position
	 * @param ownerId
	 */
	public BasicUnit(Vector3f position, int ownerId){
		super(position, ownerId);
		this.id = BasicUnit.count++;
		this.Shininess = 2;
		this.target = this.modelPos;
		this.modelAngle = new Vector3f(90f, 0.0f, 90f);
		this.modelScale = new Vector3f(.3f, .3f, .3f);
		this.maxSpeed = Game.players.get(ownerId).getStats(
				Upgrades.UNITMOVEMENTSPEED);
		this.rotDelta = 5.0f;
		viewRadius = new ViewRadiusRange(this.modelPos, Game.players.get(
				ownerId).getStats(Upgrades.UNITRANGE), ownerId);
		viewRadiusStencil = new ViewRadiusStencil(this.modelPos, Game.players
				.get(ownerId).getStats(Upgrades.UNITRANGE) - 0.02f, ownerId);
		fieldOfView = new ViewRadiusFOV(this.modelPos, Game.players
				.get(ownerId).getStats(Upgrades.UNITRANGE) + 1.0f, ownerId);
		Game.radii.add(viewRadius);
		Game.stencilradii.add(viewRadiusStencil);
		Game.fieldofview.add(fieldOfView);
	}
	/**
	 * Checks if dead, calls {@link #attack()}, {@link #move()},
	 * {@link #updateTexture()},{@link #updateMesh()}
	 * @returns false if dead
	 */
	@Override
	public boolean update(){
		if(dead){ return false; }
		updateHealthBar();
		viewRadius.resize(Game.players.get(ownerId)
				.getStats(Upgrades.UNITRANGE));
		viewRadiusStencil.resize(Game.players.get(ownerId).getStats(
				Upgrades.UNITRANGE) - 0.02f);
		fieldOfView.resize(Game.players.get(ownerId).getStats(
				Upgrades.UNITRANGE) + 1.0f);
		this.maxSpeed = Game.players.get(ownerId).getStats(
				Upgrades.UNITMOVEMENTSPEED);
		Game.addToTileList(this);
		attack();
		rotate();
		move();
		updateMesh();
		updateTexture();
		return true;
	}
	@Override
	public void revive(Vector3f modelPos, int ownerId){
		super.revive(modelPos, ownerId);
		fieldOfView.dead = false;
		fieldOfView.setOwnerId(this.ownerId);
		viewRadius.setOwnerId(this.ownerId);
		viewRadiusStencil.setOwnerId(this.ownerId);
	}
	/**
	 * 
	 */
	public void regenHealth(){
		if(health + regenAmount >= maxHealth){
			health = maxHealth;
			return;
		}else{
			health += regenAmount;
		}
	}
	/**
	 * <strong>'rotDelta' need to be set </strong>
	 * @return false if rotating, true if looking into the right direction
	 */
	protected boolean rotate(){
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
	 * Loops though animation meshes
	 */
	protected void updateMesh(){
		this.mesh = Mesh.valueOf("REPLOCATOR" + (animationId + 1));
		if(!(this.target.equals(this.modelPos))){
			animationId++;
			animationId %= 19;
		}
	}
	/**
	 * Changes texture if unit selected
	 */
	@Override
	protected void updateTexture(){
		if(selected){
			texture = Textures.valueOf("UNIT"
					+ Game.players.get(ownerId).COLOR.name() + "SELECTED");
		}else{
			texture = Textures.valueOf("UNIT"
					+ Game.players.get(ownerId).COLOR.name());
		}
	}
	/**
	 * Checks whether an enemy unit is in range. If so, calls
	 * {@link #shotAt(GameObject, GameObject)}
	 */
	protected void attack(){
		adjustAttackspeed();
		adjustRange();
		if(reloadTime-- <= 0){
			reloadTime = 0;
		}
		if(!hostileInRange())
			return;
		if(reloadTime <= 0){
			shotAt(this.hostile.modelPos);
		}
	}
	/**
	 * Calls {@link #findTarget()} if hostile out of range
	 * @return true if there's a hostile alive and in range, else false
	 */
	private boolean hostileInRange(){
		if(hostile != null && (hostile.dead || hostile.ownerId == this.ownerId)){
			hostile = null;
		}
		if(hostile != null){
			Vector3f tmp = Vector3f.sub(this.modelPos, hostile.modelPos, null);
			if(tmp.length() <= range)
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
		float tmpDist = -1;
		Unit tmpUnit = null;
		for(Unit u:Game.units){
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
		for(MapTile tile:Game.map){
			Turret t = tile.turret;
			if(this.id == t.id)
				continue;
			if(this.ownerId == t.ownerId)
				continue;
			if(t.dead)
				continue;
			Vector3f tmp = Vector3f.sub(this.modelPos, t.modelPos, null);
			if(tmpDist == -1 || tmp.length() < tmpDist){
				tmpUnit = t;
				tmpDist = tmp.length();
			}else
				continue;
		}
		if(tmpDist != -1 && tmpDist <= range && tmpUnit != null){
			this.hostile = tmpUnit;
			return true;
		}
		return false;
	}
	/**
	 * Plays a bullet sound creates a new bullet at unit position and adds shot
	 * to list
	 * @param attacker
	 * @param victim
	 */
	protected void shotAt(Vector3f victimPos){
		Sound.playSound(this.modelPos, LASER);
		reloadTime = attackspeed * 6;
		Projectile shot = new Laser(this.modelPos, victimPos, this.ownerId);
		Game.shots.add(shot);
	}
	/**
	 * 
	 */
	private void move(){
		direction = new Vector3f((float) Math.cos(Math
				.toRadians(this.modelAngle.z - 90)), (float) Math.sin(Math
				.toRadians(this.modelAngle.z - 90)), 0.0f);
		// Vector3f.sub(target, modelPos, direction);
		if(direction.length() != 0){ // if 0 normalize produces null
			direction.normalise(direction);
		}
		direction.scale(maxSpeed);
		Vector3f temp = new Vector3f(0, 0, 0);
		Vector3f.sub(target, modelPos, temp);
		if(temp.length() < direction.length()){
			Vector3f.add(modelPos, temp, modelPos);
		}else{
			Vector3f.add(modelPos, direction, modelPos);
		}
		viewRadius.update(this.modelPos);
		viewRadiusStencil.update(this.modelPos);
	}
	/**
	 * Adds unit reference to deadindices arraylist for reuse
	 */
	public void processDeath(){
		if(!Game.deadIndices.contains(this)){ // if there, already done
			Game.players.get(ownerId).unitCount--;
			ownerId = -1;
			setSelected(false);
			fieldOfView.dead = true;
			Game.unitsSelected.remove(this);
			Game.deadIndices.add(this);
			return;
		}
	}
	/**
	 * Does what name suggests but also calls
	 * {@link ViewRadiusRange#setSelected(boolean)}
	 */
	@Override
	public void setSelected(boolean selected){
		super.setSelected(selected);
		viewRadius.setSelected(selected);
		viewRadiusStencil.setSelected(selected);
	}
	@Override
	public float getHEALTH(){
		return Game.players.get(ownerId).getStats(Upgrades.UNITHEALTH);
	}
	@Override
	public float getATTACKSPEED(){
		return Game.players.get(ownerId).getStats(Upgrades.UNITATTACKSPEED);
	}
	@Override
	public float getRANGE(){
		return Game.players.get(ownerId).getStats(Upgrades.UNITRANGE);
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
	public void adjustHealth(){
		this.maxHealth = Game.players.get(ownerId)
				.getStats(Upgrades.UNITHEALTH);
		this.health = this.maxHealth;
	}
	@Override
	protected void setMesh(){
		this.mesh = Mesh.REPLOCATOR1;
	}
	@Override
	protected void setUnitMesh(){
		this.unit = Mesh.REPLOCATOR1;
	}
	@Override
	protected void setIconMesh(){
		this.icon = Mesh.BOX;
	}
	protected void adjustRange(){
		range = Game.players.get(ownerId).getStats(Upgrades.UNITRANGE);
	}
	protected void adjustAttackspeed(){
		attackspeed = Game.players.get(ownerId).getStats(
				Upgrades.UNITATTACKSPEED);
	}
	@Override
	protected void updateHealthBar(){
		billboard.update(this.modelPos, 1.5f);
		healthbar.update(this.modelPos, 1.501f);
		float maxhealth = 0;
		for(Player p:Game.players){
			if(maxhealth < p.getStats(Upgrades.UNITHEALTH))
				maxhealth = p.getStats(Upgrades.UNITHEALTH);
		}
		healthbar.resize(bsizeX / maxhealth * this.health);
	}
	@Override
	public void upgradeHealth(float maxHealth){
		this.maxHealth = maxHealth;
	}
}
