package engine;

public enum Upgrades{
	UNITHEALTH(12.0f, 1.0f, 1.0f,"U-H"),
	UNITDAMAGE(0.5f, 0.05f, 1.0f,"U-D"),
	UNITRANGE(5.0f, 0.25f, 1.0f,"U-R"),
	UNITATTACKSPEED(5.0f, -0.1f, 1.0f,"U-A"),
	UNITMOVEMENTSPEED(0.025f, 0.003f, 1.0f,"U-M"),
	TURRETHEALTH(100.0f, 50.0f, 1.5f,"T-H"),
	TURRETDAMAGE(5.0f, 6.0f, 1.5f,"T-D"),
	TURRETATTACKSPEED(6.0f, -0.4f, 1.0f,"T-A"), ;
	public final float defaultAmount;
	public final float increaseAmount;
	public final float multiplier;
	public final Textures texture;
	public final String dispName;
	Upgrades(float defaultAmount, float increaseAmount, float multiplier,String dispName){
		this.defaultAmount = defaultAmount;
		this.increaseAmount = increaseAmount;
		this.multiplier = multiplier;
		this.dispName = dispName;
		texture = Textures.valueOf("BUTTON" + this.name());
	}
}
