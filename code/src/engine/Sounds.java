package engine;

import static org.lwjgl.openal.AL10.*;

/**
 * @author thei
 */
public enum Sounds{
	// quake sounds in order
	FIRSTBLOOD(0, 1, 0.2f, AL_FALSE),
	DOMINATING(1, 1, 0.2f, AL_FALSE),
	RAMPAGE(2, 1, 0.2f, AL_FALSE),
	ULTRAKILL(3, 1, 0.2f, AL_FALSE),
	KILLINGSPREE(4, 1, 0.2f, AL_FALSE),
	MULTIKILL(5, 1, 0.2f, AL_FALSE),
	MONSTERKILL(6, 1, 0.2f, AL_FALSE),
	UNSTOPPABLE(7, 1, 0.2f, AL_FALSE),
	GODLIKE(8, 1, 0.2f, AL_FALSE),
	LUDICROUSKILL(9, 1, 0.2f, AL_FALSE),
	HOLYSHIT(10, 1, 0.2f, AL_FALSE),
	WICKEDSICK(11, 1, 0.2f, AL_FALSE),
	PREPARE(12, 1, 0.1f, AL_FALSE),
	SUPCOM(13, 1, 0.03f, AL_TRUE),
	TURRETPROJECTILE(14, 10, 2.0f, AL_FALSE),
	HUMILIATION(24, 1, 0.2f, AL_FALSE),
	VICTORY(25, 1, 0.2f, AL_FALSE),
	ALEXLAUGH(26, 1, 0.5f, AL_FALSE),
	NUKE(27, 1, 0.8f, AL_FALSE),
	SIREN(28, 1, 0.8f, AL_FALSE),
	LASER(29, 200, 2.0f, AL_FALSE),;
	public final int id;
	public final float volume;
	public final String filename;
	public final int loop;
	public final int numSources;
	/**
	 * @param id from last id + numSources-1
	 * @param numSources number of sound sources (are looped automatically)
	 * @param volume
	 * @param filename
	 * @param loop AL_TRUE or AL_FALSE
	 */
	Sounds(int id, int numSources, float volume, int loop){
		this.id = id;
		this.volume = volume;
		this.filename = this.name().toLowerCase() + ".ogg";
		this.loop = loop;
		this.numSources = numSources;
	}
}
