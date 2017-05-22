package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.Camera;
import engine.Programs;
import engine.Textures;

/**
 * @author thei
 */
public class Billboard extends GameObject{
	private float sizeX;
	private float sizeY;
	private float dx = 0;
	public boolean dead;
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Billboard(Vector3f position,Textures texture,float sizeX,float sizeY){
		this.modelPos = new Vector3f(position);
		this.texture = texture;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		dead = false;
		setModelAngle();
		setModelScale();
		setMeshorGui();
	}
	/**
	 * Resizes rectangle based on map position
	 * @param mapP
	 */
	public void update(Vector3f mapP,float dz){
		this.modelPos = new Vector3f(mapP.x+dx,mapP.y,mapP.z + dz);
		setModelAngle();
		setModelScale();
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
		return true;
	}
	public void resize(float sizeX){
		dx = sizeX - this.sizeX;
	}
	@Override
	public int getVAO(){
		return this.mesh.vao;
	}
	@Override
	public int getICount(){
		return this.mesh.iCount;
	}
	protected void setModelAngle(){
		this.modelAngle = new Vector3f(Camera.rotationX,0f,0f);
	}
	protected void setModelScale(){
		this.modelScale = new Vector3f(sizeX+dx, sizeY, 0.1f);
	}
	protected void setMeshorGui(){
		this.mesh = Mesh.BOX;
	}
}
