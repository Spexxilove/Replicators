package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public class ViewRadiusStencil extends GameObject{
	private float dz = 0.1f;
	/**
	 * @param position
	 * @param size
	 */
	public ViewRadiusStencil(Vector3f position, float size, int ownerId){
		this.ownerId = ownerId;
		this.mesh = Mesh.VIEWRADIUS;
		this.texture = Textures.TURRETPROJECTILE;
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(2 * size, 0.1f, 2 * size);
	}
	public void resize(float range){
		this.modelScale = new Vector3f(2 * range, 1.0f, 2 * range);
	}
	public void update(Vector3f position){
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
	}
	public void update(Vector3f position, float sizeX, float sizeY){
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
		this.modelScale = new Vector3f(2 * sizeX, 1.0f, 2 * sizeY);
	}
	public void update(float sizeX, float sizeY){
		this.modelScale = new Vector3f(2 * sizeX, 1.0f, 2 * sizeY);
	}
	@Override
	public void render(){
		if(selected){
			if(Game.iconRenderHeight || Config.NOVIEWRADII.isOn){}else{
				updateModelMatrix();
				Programs.PROGRAM3DNL.render(this);
			}
		}
	}
	@Override
	public boolean update(){
		// TODO Auto-generated method stub
		return false;
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
