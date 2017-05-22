package engine;

public enum Config{
	ISTOUCHSCREEN(false),
	NOVIEWRADII(false),
	FULLSCREEN(false),
	NOMUSIC(false),
	NOLIGHTING(false),
	NOHEALTHBARS(false),
	HALF_FPS(false),
	SHOWMENU(false),;
	public boolean isOn;
	Config(boolean isOn){
		this.isOn = isOn;
	}
	public void toggleSetting(){
		this.isOn = !this.isOn;
	}
}
