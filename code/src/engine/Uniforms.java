package engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.HashMap;
import org.lwjgl.BufferUtils;

/**
 * Uniforms to be used in opengl shaders, used by programs enum
 * @author thei
 */
public enum Uniforms{
	CameraPosition(1, "engine.Camera.getCameraPosition"),
	M(2, "getM"),
	P(2, "engine.Camera.getProjectionMatrix"),
	PrimitiveColor(1, "getPrimitiveColor"),
	Shininess(0, "getShininess"),
	V(2, "engine.Camera.getViewMatrix"), ;
	private HashMap<Integer, Integer> locId;
	public int type;
	public Object temp;
	public Object object;
	/**
	 * @param type 0:float, 1:floatarray,String to method returning floatarray,
	 * 2:floatarray(matrix)
	 * @param object corresponding to type specified
	 */
	Uniforms(int type, Object object){
		this.type = type;
		this.temp = object;
		this.object = object;
	}
	/**
	 * Stores 2nd enum parameter in an object, floatarrays will be converted to
	 * floatbuffer, floats left alone, Strings either fetch floatbuffers from
	 * other classes or are left alone to fetch the object's floatbuffer by
	 * method name in the render method
	 * {@link Programs#render(objects.GameObject)}
	 */
	public void init(){
		locId = new HashMap<Integer, Integer>();
		if(this.temp instanceof float[]){
			float[] fa = (float[]) this.temp;
			this.object = BufferUtils.createFloatBuffer(fa.length);
			((FloatBuffer) this.object).put(fa);
			((FloatBuffer) this.object).flip();
			return;
		}
		if(this.temp instanceof String){
			String[] sa = ((String) this.temp).split("\\.");
			if(sa.length == 1)
				return;
			String name = sa[0];
			for(int i = 1; i < sa.length - 1; ++i){
				name += "." + sa[i];
			}
			String method = sa[sa.length - 1];
			try{
				Class<?> c = Class.forName(name);
				Method m = c.getDeclaredMethod(method);
				this.object = m.invoke(c);
			}
			catch(ClassNotFoundException e1){
				e1.printStackTrace();
			}
			catch(IllegalAccessException e){
				e.printStackTrace();
			}
			catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			catch(InvocationTargetException e){
				e.printStackTrace();
			}
			catch(NoSuchMethodException e){
				e.printStackTrace();
			}
			catch(SecurityException e){
				e.printStackTrace();
			}
		}
		if(this.temp instanceof FloatBuffer || this.temp instanceof Float){ return; }
	}
	public int getLocId(int pId){
		return this.locId.get(pId);
	}
	public void setLocId(int pId, int locId){
		this.locId.put(pId, locId);
	}
}
