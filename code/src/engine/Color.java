package engine;

/**
 * @author thei
 */
public enum Color{
	BLUE(0), GREEN(1), PINK(2), YELLOW(3), ;
	public final int id;
	public static Color getColor(int id){
		return Color.values()[id];
	}
	Color(int id){
		this.id = id;
	}
}
