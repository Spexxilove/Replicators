package objects;

import meshes.Mesh;
import meshes.Primitives;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public class ViewRadiusRange extends GameObject{
	private float dz = 0.1f;
	/**
	 * @param position
	 * @param size
	 */
	public ViewRadiusRange(Vector3f position, float size, int ownerId){
		this.ownerId = ownerId;
		this.mesh = Mesh.BOX;
		this.primitive = Primitives.CIRCLE;
		this.PrimitiveColor = BufferUtils.createFloatBuffer(4);
		this.PrimitiveColor.put(1.0f).put(0.4f).put(0.0f).put(0.0f);
		this.PrimitiveColor.flip();
		this.texture = Textures.LASER;
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
		this.modelAngle = new Vector3f(0f, 0.0f, 0f);
		this.modelScale = new Vector3f(size, size, 0.1f);
	}
	public void resize(float range){
		this.modelScale = new Vector3f(range, range, 0.1f);
	}
	public void update(Vector3f position){
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
	}
	public void update(Vector3f position, float sizeX, float sizeY){
		this.modelPos = new Vector3f(position.x, position.y, position.z + dz);
		this.modelScale = new Vector3f(sizeX, sizeY, 1f);
	}
	public void update(float sizeX, float sizeY){
		this.modelScale = new Vector3f(sizeX, sizeY, 1f);
	}
	@Override
	public void render(){
		if(selected){
			if(Game.iconRenderHeight || Config.NOVIEWRADII.isOn){}else{
				updateModelMatrix();
				Programs.PRIMITIVE.render(this);
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
		return primitive.vao;
	}
	@Override
	public int getICount(){
		return primitive.iCount;
	}
}
