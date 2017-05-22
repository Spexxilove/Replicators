package engine;

import static org.junit.Assert.*;
import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

public class CameraTest {
	@Test
	public void testInit() {
		Camera.init(40, 0.3f, 1000);
		Camera.update();
		assertEquals("test fov", Camera.fov, 40, 0);
		assertEquals("test nearplane", Camera.near, 0.3f, 0);
		assertEquals("test farplane", Camera.far, 1000, 0);
		assertEquals("test camera origin", Camera.cameraPos, new Vector3f(0, 0,
				150));
		// Projection Matrix
		assertEquals("projection matrix 00", Float.NaN,
				Camera.projectionMatrix.m00, 0.000001f);
		assertEquals("projection matrix 11", 2.747477f,
				Camera.projectionMatrix.m11, 0.000001f);
		assertEquals("projection matrix 22", -1.000600f,
				Camera.projectionMatrix.m22, 0.000001f);
		assertEquals("projection matrix 23", -1, Camera.projectionMatrix.m23,
				0.000001f);
		assertEquals("projection matrix 32", -0.600180f,
				Camera.projectionMatrix.m32, 0.000001f);
		assertEquals("projection matrix 33", 0, Camera.projectionMatrix.m33,
				0.000001f);
		assertEquals("projection matrix 01", 0.0f, Camera.projectionMatrix.m01,
				0.000001f);
		assertEquals("projection matrix 02", 0.0f, Camera.projectionMatrix.m02,
				0.000001f);
		assertEquals("projection matrix 03", 0.0f, Camera.projectionMatrix.m03,
				0.000001f);
		assertEquals("projection matrix 10", 0.0f, Camera.projectionMatrix.m10,
				0.000001f);
		assertEquals("projection matrix 12", 0.0f, Camera.projectionMatrix.m12,
				0.000001f);
		assertEquals("projection matrix 13", 0.0f, Camera.projectionMatrix.m13,
				0.000001f);
		assertEquals("projection matrix 20", 0.0f, Camera.projectionMatrix.m20,
				0.000001f);
		assertEquals("projection matrix 21", 0.0f, Camera.projectionMatrix.m21,
				0.000001f);
		assertEquals("projection matrix 30", 0.0f, Camera.projectionMatrix.m30,
				0.000001f);
		assertEquals("projection matrix 30", 0.0f, Camera.projectionMatrix.m31,
				0.000001f);
		// View Matrix
		assertEquals("view matrix 00", 1.0f, Camera.viewMatrix.m00, 0.000001f);
		assertEquals("view matrix 11", 1.0f, Camera.viewMatrix.m11, 0.000001f);
		assertEquals("view matrix 22", 1.0f, Camera.viewMatrix.m22, 0.000001f);
		assertEquals("view matrix 32", -150.0f, Camera.viewMatrix.m32,
				0.000001f);
		assertEquals("view matrix 33", 1.0f, Camera.viewMatrix.m33, 0.000001f);
		assertEquals("view matrix 01", 0.0f, Camera.viewMatrix.m01, 0.000001f);
		assertEquals("view matrix 02", 0.0f, Camera.viewMatrix.m02, 0.000001f);
		assertEquals("view matrix 03", 0.0f, Camera.viewMatrix.m03, 0.000001f);
		assertEquals("view matrix 10", 0.0f, Camera.viewMatrix.m10, 0.000001f);
		assertEquals("view matrix 12", 0.0f, Camera.viewMatrix.m12, 0.000001f);
		assertEquals("view matrix 13", 0.0f, Camera.viewMatrix.m13, 0.000001f);
		assertEquals("view matrix 20", 0.0f, Camera.viewMatrix.m20, 0.000001f);
		assertEquals("view matrix 21", 0.0f, Camera.viewMatrix.m21, 0.000001f);
		assertEquals("view matrix 23", 0.0f, Camera.viewMatrix.m23, 0.000001f);
		assertEquals("view matrix 30", 0.0f, Camera.viewMatrix.m30, 0.000001f);
		assertEquals("view matrix 31", 0.0f, Camera.viewMatrix.m31, 0.000001f);
		// camera positon
		assertEquals("test camera position", new Vector3f(0.0f, 0.0f, 150.0f),
				Camera.cameraPos);
	}

	@Test
	public void testMoveX() {
		// cant move on max height;
		Camera.init(40, 0.3f, 1000);
		Camera.moveX(0.1f);
		Camera.update();
		assertEquals("test camera position on max z", new Vector3f(0.0f, 0.0f,
				150.0f), Camera.cameraPos);
		// move x positive
		Camera.cameraPos = new Vector3f(0.0f, 0.0f, 50f);
		Camera.moveX(0.1f);
		Camera.update();
		assertEquals("test camera position after move x positive",
				new Vector3f(0.1f, 0.0f, 50.0f), Camera.cameraPos);
		// move x negative
		Camera.moveX(-0.2f);
		Camera.update();
		assertEquals("test camera position after move x negative",
				new Vector3f(-0.1f, 0.0f, 50.0f), Camera.cameraPos);
		// can not move out of border
		Camera.moveX(1000f);
		Camera.update();
		assertEquals("test camera position after move x over positive border",
				new Vector3f(33.333336f, 0.0f, 50.0f), Camera.cameraPos);
		Camera.moveX(-1000f);
		Camera.update();
		assertEquals("test camera position after move x over negative border",
				new Vector3f(-33.333336f, 0.0f, 50.0f), Camera.cameraPos);
	}

	@Test
	public void testMoveY() {
		// cant move on max height;
		Camera.init(40, 0.3f, 1000);
		Camera.moveY(0.1f);
		assertEquals("test camera position on max z", new Vector3f(0.0f, 0.0f,
				150.0f), Camera.cameraPos);
		// move y positive
		Camera.cameraPos = new Vector3f(0.0f, 0.0f, 50f);
		Camera.moveY(0.1f);
		assertEquals("test camera position after move x positive",
				new Vector3f(0.0f, 0.1f, 50.0f), Camera.cameraPos);
		// move y negative
		Camera.moveY(-0.2f);
		assertEquals("test camera position after move x negative",
				new Vector3f(0.0f, -0.1f, 50.0f), Camera.cameraPos);
		// can not move out of border
		Camera.moveY(1000f);
		assertEquals("test camera position after move y over positive border",
				new Vector3f(0.0f, 28.000002f, 50.0f), Camera.cameraPos);
		Camera.moveY(-1000f);
		assertEquals("test camera position after move y over negative border",
				new Vector3f(0.0f, -38.666668f, 50.0f), Camera.cameraPos);
	}

	@Test
	public void testZoom() {
		Camera.init(40, 0.3f, 1000);
		Camera.zoom(false, new Vector3f(0.0f, 0.0f, 0.0f), 0);
		Camera.update();
		assertEquals("cannot zoom over max z", new Vector3f(0.0f, 0.0f, 150f),
				Camera.cameraPos);
		Camera.zoom(true, new Vector3f(0.0f, 0.0f, 0.0f), 0);
		Camera.update();
		assertEquals("zoom in", new Vector3f(0.0f, -0.10651529f, 148.00284f),
				Camera.cameraPos);
		Camera.zoom(false, new Vector3f(0.0f, 0.0f, 0.0f), 0);
		Camera.update();
		assertEquals("zoom out", new Vector3f(0.0f, 0.0f, 150f),
				Camera.cameraPos);
	}
}
