package engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL.*;
import static engine.Sounds.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;

/**
 * Static openal sound methods
 * @author thei
 */
public class Sound{
	private static int size;
	private static int atmId;
	private static int num_buffers;
	private static int num_sources;
	private static OggDecoder oggDecoder;
	private static IntBuffer buffer;
	private static IntBuffer source;
	private static FloatBuffer sourcePos;
	private static FloatBuffer sourceVel;
	private static FloatBuffer listenerPos;
	private static FloatBuffer listenerVel;
	private static FloatBuffer listenerOri;
	/**
	 * Creates as many sound sources as declared in sounds enum
	 * @return AL_FALSE or AL_TRUE
	 */
	private static int loadALData(){
		alGenBuffers(buffer);
		if(alGetError() != AL_NO_ERROR)
			return AL_FALSE;
		try{
			alGenSources(source);
			oggDecoder = new OggDecoder();
			for(Sounds sound:Sounds.values()){
				for(int c = 0; c < sound.numSources; ++c){
					loadSound(sound, c);
				}
			}
			if(alGetError() != AL_NO_ERROR)
				return AL_FALSE;
			// Do another error check and return.
			if(alGetError() == AL_NO_ERROR)
				return AL_TRUE;
		}
		catch(Exception e){
			System.out.println("loadALData() failed");
		}
		return AL_FALSE;
	}
	/**
	 * Loads sounds from ogg files and uploads them
	 * @param TYPE
	 * @param c
	 * @throws IOException
	 */
	private static void loadSound(Sounds TYPE, int c) throws IOException{
		String filename = "assets/sounds/" + TYPE.filename;
		InputStream is = Sound.class.getClassLoader().getResourceAsStream(
				filename);
		OggData oggData = oggDecoder.getData(is);
		int id = TYPE.id + c;
		alBufferData(buffer.get(id), oggData.channels > 1 ? AL_FORMAT_STEREO16
				: AL_FORMAT_MONO16, oggData.data, oggData.rate);
		alSourcei(source.get(id), AL_BUFFER, buffer.get(id));
		alSourcef(source.get(id), AL_PITCH, 1.0f);
		alSourcef(source.get(id), AL_GAIN, TYPE.volume);
		alSource(source.get(id), AL_POSITION,
				(FloatBuffer) sourcePos.position(id * 3));
		alSource(source.get(id), AL_VELOCITY,
				(FloatBuffer) sourceVel.position(id * 3));
		alSourcei(source.get(id), AL_LOOPING, TYPE.loop);
	}
	/**
	 * Calls {@link #updateListener()}
	 */
	public static void update(){
		updateListener();
	}
	/**
	 * Moves cameraposition for directional sound
	 */
	public static void updateListener(){
		listenerPos.put(0, Camera.cameraPos.x);
		listenerPos.put(1, Camera.cameraPos.y);
		listenerPos.put(2, Camera.cameraPos.z);
		listenerPos.rewind();
		alListener(AL_POSITION, listenerPos);
		alListener(AL_VELOCITY, listenerVel);
		alListener(AL_ORIENTATION, listenerOri);
	}
	/**
	 * Deletes sources and buffers
	 */
	private static void killALData(){
		alDeleteSources(source);
		alDeleteBuffers(buffer);
	}
	/**
	 * Creates int and floatbuffers for sound attributes and creates openal
	 * context
	 */
	public static void init(){
		atmId = 0;
		listenerPos = (FloatBuffer) BufferUtils
				.createFloatBuffer(3)
				.put(new float[] { Camera.cameraPos.x, Camera.cameraPos.y,
						Camera.cameraPos.z }).rewind();
		listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3)
				.put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
		listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6)
				.put(new float[] { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f })
				.rewind();
		size = 0;
		for(Sounds s:Sounds.values()){
			size += s.numSources;
		}
		num_buffers = size;
		num_sources = size;
		buffer = BufferUtils.createIntBuffer(num_buffers);
		source = BufferUtils.createIntBuffer(num_sources);
		sourcePos = BufferUtils.createFloatBuffer(3 * num_buffers);
		sourceVel = BufferUtils.createFloatBuffer(3 * num_buffers);
		try{
			create();
		}
		catch(Exception e){
			System.out.println("Error setting up sound");
			System.exit(0);
		}
		if(loadALData() == AL_FALSE){
			System.out.println("Error loading data.");
			System.exit(0);
		}
		alGetError();
		updateListener();
		Sound.playSound(Camera.cameraPos, PREPARE); // play start sound
		Sound.playSound(Camera.cameraPos, SUPCOM);
	}
	/**
	 * Plays sound at position
	 * @param sourceP Position of the sound source
	 * @param TYPE
	 */
	public static void playSound(Vector3f sourceP, Sounds TYPE){
		int id = TYPE.id;
		if(TYPE == Sounds.LASER)
			id += atmId++;
		sourcePos.put(id * 3, sourceP.x);
		sourcePos.put(id * 3 + 1, sourceP.y);
		sourcePos.put(id * 3 + 2, sourceP.z);
		sourcePos.rewind();
		alSource(source.get(id), AL_POSITION,
				(FloatBuffer) sourcePos.position(id * 3));
		alSourcePlay(source.get(id));
		if(atmId >= Sounds.LASER.numSources){
			atmId = 0;
		}
	}
	public static void playSound(Sounds TYPE){
		alSourcePlay(source.get(TYPE.id));
		if(atmId >= Sounds.LASER.numSources){
			atmId = 0;
		}
	}
	public static void stopSound(Sounds TYPE){
		alSourceStop(source.get(TYPE.id));
	}
	public static void pauseSound(Sounds TYPE){
		alSourcePause(source.get(TYPE.id));
	}
	public static void toggleSound(Sounds TYPE){
		int s = source.get(TYPE.id);
		if(alGetSourcei(s, AL_SOURCE_STATE) == AL_PLAYING)
			pauseSound(TYPE);
		else
			playSound(TYPE);
	}
	/**
	 * Calls {@link #killALData()} and destroys openal context
	 */
	public static void cleanUp(){
		killALData();
		destroy();
	}
}
