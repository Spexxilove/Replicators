package objects;

import meshes.Mesh;
import meshes.Primitives;
import org.lwjgl.util.vector.Vector3f;
import engine.Textures;

/**
 * @author thei
 */
public class SelectionRing extends GameObject{
	private boolean active = false;
	private int frameId;
	private final float scale = .1f;
	private final float SIZE = 0.02f;
	private Primitive ring;
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public SelectionRing(float x, float y, float z){
		super();
		this.mesh = Mesh.BOX;
		this.modelPos = new Vector3f(x, y, z);
		this.texture = Textures.VIEWRADIUSEMPTY;
		this.target = this.modelPos;
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(SIZE, SIZE, 1f);
		this.ring = new Primitive(this.modelPos, SIZE, Primitives.CIRCLE, 0.0f,
				1.0f, 0.0f);
		updateModelMatrix();
	}
	/**
	 * Grows for incFrames number of round and then reverses
	 */
	@Override
	public boolean update(){
		int incFrames = 6;
		if(active){
			frameId++;
			if(frameId <= incFrames){
				this.modelScale.x += scale;
				this.modelScale.y += scale;
			}else if(frameId <= incFrames * 2){
				this.modelScale.x -= scale;
				this.modelScale.y -= scale;
			}else{
				active = false;
				this.modelScale = new Vector3f(SIZE, SIZE, 1f);
			}
			ring.update(this.modelScale.x, this.modelScale.y);
		}
		return true;
	}
	@Override
	public void render(){
		if(active){
			ring.render();
		}
	}
	/**
	 * Sets modelposition to click position and resets scale, calls
	 * {@link Primitive#update()}
	 * @param modelPos
	 */
	public void click(Vector3f modelPos){
		this.active = true;
		frameId = 0;
		this.modelPos.set(modelPos.x, modelPos.y, 0f);
		this.modelScale = new Vector3f(SIZE, SIZE, 1f);
		ring.update(this.modelPos, this.modelScale.x, this.modelScale.y);
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
