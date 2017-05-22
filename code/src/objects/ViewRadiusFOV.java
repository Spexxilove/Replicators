package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public class ViewRadiusFOV extends GameObject{
	public boolean dead = false;
	/**
	 * @param position
	 * @param size
	 */
	public ViewRadiusFOV(Vector3f position, float size, int ownerId){
		this.ownerId = ownerId;
		this.mesh = Mesh.VIEWRADIUS;
		this.texture = Textures.TURRETPROJECTILE;
		this.modelPos = position;
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(2 * size, 1.0f, 2 * size);
	}
	public void resize(float range){
		this.modelScale = new Vector3f(2 * range, 1.0f, 2 * range);
	}
	@Override
	public void render(){
		if(!dead){
			updateModelMatrix();
			Programs.PROGRAM3DNL.render(this);
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
