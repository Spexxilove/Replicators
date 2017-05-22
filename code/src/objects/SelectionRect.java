package objects;

import meshes.Primitives;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author thei
 */
public class SelectionRect extends GameObject{
	private boolean active = false;
	private Primitive rect;
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public SelectionRect(float x, float y, float z){
		super();
		this.modelPos = new Vector3f(x, y, z);
		this.target = this.modelPos;
		this.modelAngle = new Vector3f(90f, 0.0f, 0.0f);
		this.modelScale = new Vector3f(.0001f, .0001f, 1f);
		this.rect = new Primitive(this.modelPos, 0, Primitives.RECTANGLE, 0f,
				0f, 0f);
		updateModelMatrix();
	}
	/**
	 * Resizes rectangle based on map position
	 * @param mapP
	 */
	public void update(Vector3f mapP){
		Vector3f rectPos = Vector3f.sub(mapP, modelPos, null);
		rectPos.scale(0.5f);
		Vector3f.add(modelPos, rectPos, rectPos);
		float sizeX = Math.abs(mapP.x - modelPos.x) / 2, sizeY = Math
				.abs(mapP.y - modelPos.y) / 2;
		this.rect.update(rectPos, sizeX, sizeY);
	}
	@Override
	public void render(){
		if(active){
			this.rect.render();
		}
	}
	public void click(Vector3f mapP){
		this.active = true;
		this.modelPos.set(mapP.x, mapP.y, 0f);
		this.modelScale = new Vector3f(.001f, .001f, 1f);
	}
	public void release(){
		this.active = false;
	}
	@Override
	public boolean update(){
		return true;
	}
	@Override
	public int getVAO(){
		return -1;
	}
	@Override
	public int getICount(){
		return -1;
	}
}
