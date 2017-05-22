package engine;

import java.nio.FloatBuffer;
import objects.MapTile;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Handles camera movement by storing and providing function to manipulate view-
 * and projectionmatrix
 * @author thei
 */
public class Camera{
	public static float fov;
	public static float aspect;
	public static float near;
	public static float far;
	private static float nPDRatio; // nearplane pixel to distance ratio
	public static final float MAXZ = 150;
	private static final float MINZ = 8;
	private static final float BACKOFF = 8;
	private static final float ZOOMDELTA = 3.5f;
	private static final float WIDTHRATIO = (Game.TILECOUNT * MapTile.TILESIZE / 2)
			/ MAXZ;
	private static final float BACKOFFRATIO = 2 * BACKOFF / MAXZ;
	private static final float MAXROTATION = 47;
	public static float rotationX = 0;
	public static float rotationY = 0;
	public static float rotationZ = 0;
	private static final Vector3f CAMERAORIGIN = new Vector3f(0, 0, MAXZ);
	private static final Vector3f BACKOFFVECTOR = new Vector3f(0, -BACKOFF, 0);
	// ZOOM INTERP
	private static Vector3f zoomV = new Vector3f(CAMERAORIGIN);
	private static volatile Vector3f mapP = new Vector3f(0, 0, 0);
	private static int yPixel = 0;
	private static Vector3f targetPos = new Vector3f(CAMERAORIGIN);
	public static int zFrameId = 0;
	/*---UNIFORM BUFFER VALUES---*/
	public static volatile FloatBuffer CameraPosition;
	public static volatile FloatBuffer P = null;
	public static volatile FloatBuffer V = null;
	public static volatile Matrix4f projectionMatrix = null;
	public static volatile Matrix4f viewMatrix = null;
	public static volatile Vector3f cameraPos = null;
	private static volatile Vector3f cameraInverse = null;
	/**
	 * Creates viewmatrix and projection matrix and initializes some variables,
	 * calls {@link #setBounds()} {@link #updateProjectionMatrix()}
	 * {@link #updateMatrices()} {@link #initFrustrumLength()}
	 * @param fov Field of view in degrees
	 * @param near Distance to near plane
	 * @param far Distance to far plane
	 */
	public static void init(float fov, float near, float far){
		Camera.fov = fov;
		Camera.aspect = ((float) Display.getWidth() / (float) Display
				.getHeight());
		Camera.near = near;
		Camera.far = far;
		P = BufferUtils.createFloatBuffer(16);
		V = BufferUtils.createFloatBuffer(16);
		setBounds();
		updateProjectionMatrix();
		updateMatrices();
		initFrustrumLength();
	}
	/**
	 * Sets global nearplane pixel to distance(in float) ratio for angle
	 * calculations
	 */
	private static void initFrustrumLength(){
		float nearHalfSize = (float) Math.tan(Math.toRadians(fov / 2)) * near;
		nPDRatio = nearHalfSize / (Engine.HEIGHT / 2);
	}
	/**
	 * Sets cameraInverse for first use in shader
	 */
	private static void setBounds(){
		cameraPos = new Vector3f(CAMERAORIGIN);
		cameraInverse = new Vector3f(cameraPos);
		cameraInverse.scale(-1);
	}
	/**
	 * Calls {@link #updateProjectionMatrix()} {@link #updateMatrices()}
	 */
	public static void useView(){
		updateProjectionMatrix();
		updateMatrices();
	}
	/**
	 * Sets 4x4 projectionmatrix values from fov, aspect, near, far, calculates
	 * frustrum length
	 */
	private static void updateProjectionMatrix(){
		projectionMatrix = new Matrix4f();
		float y_scale = coTangent((float) Math.toRadians(Camera.fov / 2f));
		float x_scale = y_scale / Camera.aspect;
		float frustum_length = Camera.near - Camera.far;
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = (Camera.far + Camera.near) / frustum_length;
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = (2 * Camera.near * Camera.far) / frustum_length;
		projectionMatrix.m33 = 0;
	}
	/**
	 * Sets 4x4 viewmatrix and stores view and projection matrix in floatbuffers
	 */
	private static void updateMatrices(){
		viewMatrix = new Matrix4f();
		cameraInverse.set(cameraPos);
		cameraInverse.scale(-1);
		Matrix4f.rotate((float) Math.toRadians(-rotationX), new Vector3f(1, 0,
				0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(-rotationY), new Vector3f(0, 1,
				0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(-rotationZ), new Vector3f(0, 0,
				1), viewMatrix, viewMatrix);
		Matrix4f.translate(cameraInverse, viewMatrix, viewMatrix);
		CameraPosition = BufferUtils.createFloatBuffer(4);
		CameraPosition.put(cameraPos.x).put(cameraPos.y).put(cameraPos.z)
				.put(1.0f).flip();
		projectionMatrix.store(P);
		P.flip();
		viewMatrix.store(V);
		V.flip();
	}
	/**
	 * @param angle in radians
	 * @return
	 */
	private static float coTangent(float angle){
		return (float) (1f / Math.tan(angle));
	}
	/**
	 * Moves cameraposition in x direction and checks boundaries
	 * @param amt
	 */
	public static void moveX(float amt){
		cameraPos.x += amt;
		enforceBounds();
	}
	/**
	 * Moves cameraposition in y direction and checks boundaries
	 * @param amt
	 */
	public static void moveY(float amt){
		cameraPos.y += amt;
		enforceBounds();
	}
	private static void enforceBounds(){
		float borderX = (MAXZ - cameraPos.z) * WIDTHRATIO;
		if(cameraPos.x >= borderX)
			cameraPos.x = borderX;
		else if(cameraPos.x <= -borderX)
			cameraPos.x = -borderX;
		float borderY = (MAXZ - cameraPos.z) * WIDTHRATIO;
		float backoff = (MAXZ - cameraPos.z) * BACKOFFRATIO;
		if(cameraPos.y + backoff >= borderY)
			cameraPos.y = borderY - backoff;
		else if(cameraPos.y + backoff <= -borderY)
			cameraPos.y = -borderY - backoff;
		if(cameraPos.z >= MAXZ)
			cameraPos.z = MAXZ;
		else if(cameraPos.z <= MINZ)
			cameraPos.z = MINZ;
	}
	/**
	 * Sets camera zoom target
	 * @param zoomIn
	 * @param mapP Point on map
	 * @param yPixel Pixel from screen center in y direction
	 */
	public static void zoom(boolean zoomIn, Vector3f mapP, int yPixel){
		zFrameId += 6;
		Camera.mapP = new Vector3f(mapP);
		Camera.yPixel = new Integer(yPixel);
		if(zoomIn) // ZOOM IN
			targetPos = Vector3f.add(Camera.mapP, BACKOFFVECTOR, null);
		else{
			// ZOOM OUT
			Vector3f tp = Vector3f.sub(Camera.mapP, Camera.cameraPos, null);
			tp.normalise(tp);
			tp.scale(50);
			if(cameraPos.z <= 2 * BACKOFF)
				targetPos = Vector3f.sub(Camera.cameraPos, tp, null);
			else
				targetPos = new Vector3f(CAMERAORIGIN);
		}
	}
	/**
	 * Interpolates camera zoom to camera target position
	 */
	public static void update(){
		if(zFrameId-- <= 0){
			zFrameId = 0;
			return;
		}
		Vector3f.sub(targetPos, cameraPos, zoomV);
		zoomV.normalise(zoomV);
		if(cameraPos.z > 3 * BACKOFF)
			zoomV.scale(ZOOMDELTA);
		else
			zoomV.scale(ZOOMDELTA / 2);
		if(zoomV.z < 0){ // ZOOM IN
			if(cameraPos.z <= MINZ){
				zFrameId = 0;
				return;
			}
			if(cameraPos.z + zoomV.z <= targetPos.z){
				cameraPos = new Vector3f(targetPos);
				zFrameId = 0;
			}else
				Vector3f.add(cameraPos, zoomV, cameraPos);
		}else{ // ZOOM OUT
			if(cameraPos.z >= MAXZ){
				rotationX = 0;
				return;
			}
			if(cameraPos.z + zoomV.z >= targetPos.z){
				cameraPos = new Vector3f(targetPos);
				zFrameId = 0;
				return;
			}else
				Vector3f.add(cameraPos, zoomV, cameraPos);
		}
		enforceBounds();
		adjustRotation();
	}
	/**
	 * Sets cameraangle, calls: {@link #calcAngle()}
	 */
	private static void adjustRotation(){
		if(zoomV.z < 0)
			rotationX = calcAngle();
		else{
			if(cameraPos.z < MAXZ / 2){
				rotationX = calcAngle();
			}
			float stepsRemaining = (MAXZ - cameraPos.z) / zoomV.z;
			rotationX -= rotationX / stepsRemaining;
		}
		if(rotationX <= 0)
			rotationX = 0;
	}
	/**
	 * Calculates cameraangle in a way that doesn't move the mousepointer while
	 * zooming in
	 * @return
	 */
	private static float calcAngle(){
		float angle = (float) Math.toDegrees(Math.atan(Math.abs(mapP.y
				- cameraPos.y)
				/ cameraPos.z));
		// x-angle from center of screen to mouse location
		float mAngle = (float) Math.toDegrees(Math.atan((nPDRatio * Math
				.abs(yPixel)) / near));
		if(cameraPos.y > mapP.y){ // point behind
			if(yPixel < 0)
				angle = -angle + mAngle;
			else
				angle = -angle - mAngle;
		}else{
			if(yPixel < 0)
				angle += mAngle;
			else
				angle -= mAngle;
		}
		if(angle >= MAXROTATION)
			return MAXROTATION;
		else if(angle <= 0)
			return 0;
		else
			return angle;
	}
	public static FloatBuffer getCameraPosition(){
		return Camera.CameraPosition;
	}
	public static FloatBuffer getProjectionMatrix(){
		return Camera.P;
	}
	public static FloatBuffer getViewMatrix(){
		return Camera.V;
	}
}
