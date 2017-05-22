package objects;

import java.nio.FloatBuffer;
import meshes.Gui;
import meshes.Mesh;
import meshes.Primitives;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.Textures;

/**
 * Top class of everything there is to render and update
 * @author thei
 */
public abstract class GameObject{
	public Mesh mesh = null;
	public Primitives primitive = null;
	public Gui gui = null;
	public int ownerId = -1;
	public int offset = 0;
	public float maxSpeed = 0;
	public Vector3f direction = new Vector3f(0, 0, 0);
	public Vector3f target = null;
	public boolean selected = false;
	public Textures texture = null;
	public Vector3f modelPos = null;
	public Vector3f modelAngle = null;
	public Vector3f modelScale = null;
	public Matrix4f modelMatrix = null;
	public FloatBuffer M = null;
	public FloatBuffer PrimitiveColor;
	public float Shininess = 100;
	public volatile int id;
	public abstract boolean update();
	public abstract void render();
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	public boolean isSelected(){
		return selected;
	}
	public Textures getTexture(){
		return texture;
	}
	/**
	 * Updates floatbuffer by rotating scaling and translating a 4x4 matrix, to
	 * be uploaded to shader
	 */
	public void updateModelMatrix(){
		modelMatrix = new Matrix4f();
		M = BufferUtils.createFloatBuffer(16);
		// Scale, translate and rotate model
		Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1),
				modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0),
				modelMatrix, modelMatrix);
		Matrix4f.rotate(degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0),
				modelMatrix, modelMatrix);
		Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
		modelMatrix.store(M);
		M.flip();
	}
	private float degreesToRadians(float degrees){
		return degrees * (float) (Math.PI / 180d);
	}
	// GETTERS
	public float getShininess(){
		return this.Shininess;
	}
	public FloatBuffer getM(){
		return this.M;
	}
	public FloatBuffer getPrimitiveColor(){
		return this.PrimitiveColor;
	}
	public abstract int getVAO();
	public abstract int getICount();
	public Vector3f getModelPos(){
		return modelPos;
	}
	public Vector3f getmodelAngle(){
		return modelAngle;
	}
	public Vector3f getmodelScale(){
		return modelScale;
	}
	public int getOwnerId(){
		return ownerId;
	}
	// SETTERS
	public void setmodelScale(Vector3f modelScale){
		this.modelScale = modelScale;
	}
	public void setmodelAngles(Vector3f modelAngle){
		this.modelAngle = modelAngle;
	}
	public void setModelPos(Vector3f modelPos){
		this.modelPos = modelPos;
	}
	public void setOwnerId(int ownerId){
		this.ownerId = ownerId;
	}
}
