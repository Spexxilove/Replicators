package objects;

import java.util.ArrayList;
import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public class MapTile extends GameObject{
	public static int count = 0;
	public static final float TILESIZE = 20f;
	public static final float GAP = 20 - TILESIZE;
	public ArrayList<Unit> unitMap;
	public Turret turret;
	private int SPAWNDELAY = 46; // in rounds
	private float SPAWNRADIUS = (float) (TILESIZE * 0.6);
	private Textures defaultTexture;
	private int spawnRoundId = 4 * 6;
	public boolean deadly = false;
	public boolean visible = false;
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param texture
	 */
	public MapTile(float x, float y, float z, Textures texture){
		super();
		this.id = MapTile.count++;
		this.mesh = Mesh.GROUNDTILE;
		this.texture = texture;
		this.defaultTexture = texture;
		this.Shininess = 10000;
		this.modelPos = new Vector3f(x, y, z);
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(TILESIZE, TILESIZE, TILESIZE);
		this.maxSpeed = 0.5f;
		this.unitMap = new ArrayList<Unit>();
		this.turret = new Turret(this.modelPos, this.ownerId, this.id);
		Game.turrets.add(this.turret);
		spawnTurret();
		updateModelMatrix();
	}
	/**
	 * Calls {@link #updateOwner()}, {@link Turret#update()},
	 * {@link #updateMapTile()}, {@link #updateTexture()}.
	 */
	@Override
	public boolean update(){
		if(deadly){
			this.texture = Textures.TILETEXTUREGREY;
			for(Unit u:unitMap){
				u.setDead();
			}
			return true;
		}
		for(Unit u:unitMap){
			if(u.ownerId == Game.playerId){
				visible = true;
				break;
			}
			visible = false;
		}
		updateOwner();
		turret.update();
		updateMapTile();
		updateTexture();
		return true;
	}
	/**
	 *
	 */
	@Override
	public void render(){
		updateModelMatrix();
		if(Config.NOLIGHTING.isOn){
			Programs.PROGRAM3DNL.render(this);
		}else{
			Programs.PROGRAM3D.render(this);
		}
		return;
	}
	/**
	 * Calls {@link #spawnUnits()}
	 */
	private void updateMapTile(){
		Unit newUnit = spawnUnits(); // spawn units
		if(newUnit != null && (newUnit.ownerId == Game.playerId || Game.debug)){
			int[] unitIDs = { newUnit.id };
			Game.moveUnits(Game.sendId, unitIDs, getSpawnTarget());
		}
	}
	/**
	 * Reuses indices if there are free onces, else creates new Unit
	 * @return null if no unit is spawned otherwise returns spawned unit
	 */
	public Unit spawnUnits(){
		if(ownerId != -1){
			if(Game.players.get(ownerId).unitCount >= Game.UNITCAP)
				return null;
			if(spawnRoundId-- <= 0){
				spawnRoundId = SPAWNDELAY * 6;
				Game.players.get(ownerId).unitCount++;
			}else{
				return null;
			}
			Unit u;
			if(Game.deadIndices.size() != 0){
				u = Game.deadIndices.remove(0);
				u.revive(modelPos, ownerId);
			}else{
				u = new BasicUnit(this.modelPos, ownerId);
				Game.units.add(u);
			}
			return u;
		}
		return null;
	}
	/**
	 * Checks whether turret is still alive or 5 units of the same player are
	 * alone on the tile
	 */
	private void updateOwner(){
		ownerId = turret.ownerId;
		/*
		 * if(!turret.isDead()) // tower present, no capture return; else
		 * ownerId = -1; int calcOwner = calcOwner(); if(calcOwner == -1) //
		 * multiple players' units or less than 5 units return; ownerId =
		 * calcOwner; spawnTurret();
		 */
	}
	/**
	 * Changes textures depending on who owns the tile
	 */
	private void updateTexture(){
		if(ownerId == -1 || !visible)
			texture = defaultTexture;
		else{
			texture = Textures.valueOf("TILETEXTURE"
					+ Game.players.get(ownerId).COLOR.name());
		}
	}
	public void resetUnitMap(){
		unitMap = new ArrayList<Unit>(); // reset list of units on field
	}
	private void spawnTurret(){
		turret.revive(modelPos, this.ownerId);
	}
	/**
	 * @return -1 unconclusive owner, ownerId otherwise
	 */
	/*
	 * private int calcOwner(){ if(unitMap.size() < 5){ // less than 5 units on
	 * map return -1; } int unitOwnerId; unitOwnerId = unitMap.get(0).ownerId;
	 * // take first unit owner for(int i = 0; i < unitMap.size(); ++i){
	 * if(unitOwnerId != unitMap.get(i).ownerId){ // if not the same owner
	 * return -1; } } return unitOwnerId; // will get here if 5 or more units of
	 * the same // player }
	 */
	/**
	 * 
	 */
	public void makeDeadly(){
		this.ownerId = -1;
		turret.setDead();
		deadly = true;
	}
	/**
	 * Changes ownerId, calls {@link #spawnTurret()}
	 * @param ownerId
	 */
	public void setOwner(int ownerId){
		this.ownerId = ownerId;
		spawnTurret();
	}
	public float getSize(){
		return TILESIZE;
	}
	/**
	 * @return a random target withing SPAWNRADIUS
	 */
	public Vector3f getSpawnTarget(){
		float x = (float) (this.getModelPos().x + (Math.random() - 0.5)
				* SPAWNRADIUS);
		float y = (float) (this.getModelPos().y + (Math.random() - 0.5)
				* SPAWNRADIUS);
		return new Vector3f(x, y, 0.0f);
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
