package engine;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

/**
 * @author thei
 */
public enum Textures{
	TILETEXTUREFLOOREVEN,
	TILETEXTUREFLOORODD,
	TILETEXTUREBLUE,
	TILETEXTUREGREEN,
	TILETEXTUREPINK,
	TILETEXTUREYELLOW,
	TILETEXTUREGREY,
	UNITBLUE,
	UNITGREEN,
	UNITPINK,
	UNITYELLOW,
	UNITORANGE,
	UNITBLUESELECTED,
	UNITGREENSELECTED,
	UNITPINKSELECTED,
	UNITYELLOWSELECTED,
	SELECTIONRINGTEXTURE,
	SELECTIONRECTTEXTURE,
	TURRETPROJECTILE,
	LASER,
	VIEWRADIUSFILLED,
	VIEWRADIUSEMPTY,
	BUTTONARROWDOWN,
	BUTTONARROWDOWNSELECTED,
	BUTTONARROWLEFT,
	BUTTONARROWLEFTSELECTED,
	BUTTONARROWRIGHT,
	BUTTONARROWRIGHTSELECTED,
	BUTTONARROWUP,
	BUTTONARROWUPSELECTED,
	BUTTONCLOSE,
	BUTTONCLOSESELECTED,
	BUTTONORANGE,
	BUTTONUNITATTACKSPEED,
	BUTTONUNITDAMAGE,
	BUTTONUNITHEALTH,
	BUTTONUNITMOVEMENTSPEED,
	BUTTONUNITRANGE,
	BUTTONTURRETHEALTH,
	BUTTONTURRETDAMAGE,
	BUTTONTURRETATTACKSPEED,
	BUTTONTURRETRANGE,
	BUTTONORANGESELECTED,
	BUTTONUNITATTACKSPEEDSELECTED,
	BUTTONUNITDAMAGESELECTED,
	BUTTONUNITHEALTHSELECTED,
	BUTTONUNITMOVEMENTSPEEDSELECTED,
	BUTTONUNITRANGESELECTED,
	BUTTONTURRETHEALTHSELECTED,
	BUTTONTURRETDAMAGESELECTED,
	BUTTONTURRETATTACKSPEEDSELECTED,
	BUTTONTURRETRANGESELECTED,
	GUIBACKGROUND,
	FONT,
	UNITBLUEICON,
	UNITGREENICON,
	UNITPINKICON,
	UNITYELLOWICON,
	UNITORANGEICON,
	UNITBLUEICONSELECTED,
	UNITGREENICONSELECTED,
	UNITPINKICONSELECTED,
	UNITYELLOWICONSELECTED,
	GUIUPGBGRD,
	TURRETORANGE,
	TURRETBLUE,
	TURRETGREEN,
	TURRETYELLOW,
	TURRETPINK,
	FIELDOFVIEW,
	STENCILTEXTURE,;
	public int id;
	public final String filename;
	Textures(){
		this.filename = "assets/images/" + this.name().toLowerCase() + ".png";
	}
	public void init(){
		this.id = loadPNGTexture(this.filename, GL_TEXTURE0);
		Engine.exitOnGLError("Textures");
	}
	/**
	 * Creates opengl name for textures
	 * @param filename
	 * @param textureUnit
	 * @return
	 */
	private static int loadPNGTexture(String filename, int textureUnit){
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		try{
			// Open the PNG file as an InputStream
			InputStream in = Textures.class.getClassLoader()
					.getResourceAsStream(filename);
			if(in == null){
				System.out.println(filename + " null");
			}
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(4 * decoder.getWidth()
					* decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		// Create a new texture object in memory and bind it
		int texId = glGenTextures();
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, texId);
		// All RGB bytes are aligned to each other and each component is 1 byte
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		// Upload the texture data and generate mip maps (for scaling)
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tWidth, tHeight, 0, GL_RGBA,
				GL_UNSIGNED_BYTE, buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		// Setup the ST coordinate system
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		// Setup what to do when the texture has to be scaled
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		Engine.exitOnGLError("Textures.loadPNGTexture");
		return texId;
	}
	public void changeTextureId(int id){
		this.id = id;
	}
}
