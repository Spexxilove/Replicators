package objects;

import meshes.Mesh;
import org.lwjgl.util.vector.Vector3f;
import engine.*;

/**
 * @author thei
 */
public class FieldOfView extends GameObject{
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param texture
	 */
	public FieldOfView(float x, float y, float z, Textures texture){
		super();
		this.mesh = Mesh.GROUNDTILE;
		this.texture = texture;
		this.Shininess = 10000;
		this.modelPos = new Vector3f(x, y, z);
		this.modelAngle = new Vector3f(90f, 0.0f, 0f);
		this.modelScale = new Vector3f(Game.TILECOUNT * MapTile.TILESIZE, 1.0f,
				Game.TILECOUNT * MapTile.TILESIZE);
	}
	/**
	 * Calls {@link #updateOwner()}, {@link Turret#update()},
	 * {@link #updateMapTile()}, {@link #updateTexture()}.
	 */
	@Override
	public boolean update(){
		return true;
	}
	/**
	 * Also calls {@link Turret#render()}
	 */
	@Override
	public void render(){
		updateModelMatrix();
		Programs.PROGRAM3DNL.render(this);
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
