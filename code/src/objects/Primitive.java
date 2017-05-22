package objects;

import meshes.Primitives;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import engine.Programs;

/**
 * @author thei
 */
public class Primitive extends GameObject{
	/**
	 * @param position
	 * @param size
	 * @param primitive
	 * @param r
	 * @param g
	 * @param b
	 */
	public Primitive(Vector3f position, float size, Primitives primitive,
			float r, float g, float b){
		super();
		this.PrimitiveColor = BufferUtils.createFloatBuffer(4);
		this.PrimitiveColor.put(r).put(g).put(b).put(1.0f).flip();
		this.primitive = primitive;
		this.modelPos = new Vector3f(position.x, position.y, position.z + .2f);
		this.modelAngle = new Vector3f(0f, 0.0f, 0f);
		this.modelScale = new Vector3f(size, size, 0f);
		updateModelMatrix();
	}
	public void update(Vector3f position){
		this.modelPos = new Vector3f(position.x, position.y, position.z + .2f);
	}
	public void update(Vector3f position, float sizeX, float sizeY){
		this.modelPos = new Vector3f(position.x, position.y, position.z + .2f);
		this.modelScale = new Vector3f(sizeX, sizeY, 0f);
	}
	public void update(float sizeX, float sizeY){
		this.modelScale = new Vector3f(sizeX, sizeY, 0f);
	}
	@Override
	public void render(){
		updateModelMatrix();
		Programs.PRIMITIVE.render(this);
	}
	@Override
	public boolean update(){
		// do nothing
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
