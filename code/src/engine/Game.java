package engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import network.*;
import objects.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import client.NetworkClient;
import gui.*;

/**
 * Handles input and game logic
 * @author thei
 */
public class Game{
	private static final float ICONRENDERHEIGHT = 58;
	public final static int TILECOUNT = 5;
	public final static int FPS = 60;
	private float EDGEMOVEDELTA = 5;
	private final float POSDELTA = 0.6f;
	public static final int UNITCAP = 250;
	public static boolean debug;
	public static NetworkClient client;
	public static ArrayList<MapTile> map;
	public static MapTile[][] mapA;
	public static ArrayList<Unit> units;
	public static ArrayList<Unit> deadIndices;
	public static ArrayList<Unit> unitsSelected;
	public static ArrayList<Projectile> shots;
	public static ArrayList<ViewRadiusRange> radii;
	public static ArrayList<ViewRadiusStencil> stencilradii;
	public static ArrayList<ViewRadiusFOV> fieldofview;
	public static ArrayList<GuiElement> gui;
	public static ArrayList<Billboard> healthbar;
	public static ArrayList<Billboard> healthbarbgrd;
	public static ArrayList<Turret> turrets;
	public static GuiText text;
	public static int quake;
	public static ArrayList<Player> players;
	private SelectionRing selectionRing;
	private SelectionRect selectionRect;
	public static int playerId;
	private boolean button0Down;
	private boolean button1Down;
	private boolean keyTDown = false;
	private PrintWriter writer;
	private Lobby lobby;
	/*---ROUND COUNTERS---*/
	public static int frameId;
	public static int framesPerRound = FPS / 10;
	public static int sendId;
	public static int getId;
	/*---UNIT MOVEMENT---*/
	private int unitIds[];
	public static volatile ArrayList<Command> commands;
	/*---INPUT DELTA---*/
	private int x, y;
	private volatile Vector3f ray;
	private volatile Vector3f mapP;
	private boolean isSelecting;
	private Unit selectedUnit;
	private Vector3f rectStart;
	private boolean gameOver;
	private boolean victorious;
	private boolean playedEndSound;
	public static boolean iconRenderHeight;
	private static ArrayList<UpgradeString> upgradeMessages;
	public static boolean closeRequested;
	private int halfPX;
	private int halfPY;
	private GuiBackground guiUpgBgrd;
	private GuiBackground scoreBgrd;
	private boolean showScores;
	private int suddenDeathId;
	private static long gameStartTime;
	private static long gameNowTime;
	private int swapDir;
	private static int discMsgTimer;
	private static String discMsg;
	private String resizeMsg;
	private int resizeTimer;
	private FieldOfView fieldOfView;
	private boolean oddFrame;
	/**
	 * @param client
	 */
	public Game(NetworkClient client, Lobby lobby){
		if(client == null){
			debug = true;
		}else{
			Game.client = client;
			this.lobby = lobby;
		}
		initVars();
		initPlayers();
		initGui();
		initMap();
		initPlayerSpawn();
		selectionRing = new SelectionRing(0, 0, 1);
		selectionRect = new SelectionRect(0, 0, 1);
		try{
			writer = new PrintWriter("movelog.log", "UTF-8");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	private void initVars(){
		playerId = 0;
		button0Down = false;
		button1Down = false;
		keyTDown = false;
		frameId = 0;
		sendId = 0;
		getId = -2;
		unitIds = null;
		commands = new ArrayList<Command>();
		map = new ArrayList<MapTile>();
		units = new ArrayList<Unit>();
		unitsSelected = new ArrayList<Unit>();
		deadIndices = new ArrayList<Unit>();
		shots = new ArrayList<Projectile>();
		players = new ArrayList<Player>();
		radii = new ArrayList<ViewRadiusRange>();
		stencilradii = new ArrayList<ViewRadiusStencil>();
		fieldofview = new ArrayList<ViewRadiusFOV>();
		gui = new ArrayList<GuiElement>();
		healthbar = new ArrayList<Billboard>();
		healthbarbgrd = new ArrayList<Billboard>();
		turrets = new ArrayList<Turret>();
		isSelecting = false;
		selectedUnit = null;
		gameOver = false;
		victorious = false;
		playedEndSound = false;
		iconRenderHeight = false;
		upgradeMessages = new ArrayList<UpgradeString>();
		closeRequested = false;
		BasicUnit.count = 0;
		MapTile.count = 0;
		Turret.count = 0;
		Projectile.count = 0;
		TurretProjectile.count = 0;
		Laser.count = 0;
		Unit.count = 0;
		halfPX = Engine.WIDTH / 2;
		halfPY = Engine.HEIGHT / 2;
		showScores = false;
		suddenDeathId = 15 * 6 * 10 - 1;
		gameStartTime = 0;
		gameNowTime = 0;
		swapDir = 1;
		discMsgTimer = 0;
		discMsg = "";
		resizeMsg = "";
		resizeTimer = 0;
		oddFrame = false;
	}
	/**
	 * Adds buttons, background to gui list, and creates a text object
	 */
	private void initGui(){
		int bsize = 25;
		int bHalfSize = bsize + 2;
		int i = 0;
		int guiXOffset = halfPX / 5;
		for(Upgrades upg:Upgrades.values()){
			gui.add(new UpgradeButton(guiXOffset + (bsize + 2) * i,
					-(halfPY - bHalfSize), bsize, bsize, upg.texture, upg));
			i += 2;
		}
		gui.add(new ExitButton((halfPX - bHalfSize), (halfPY - bHalfSize),
				bsize, bsize, Textures.BUTTONCLOSE));
		if(Config.ISTOUCHSCREEN.isOn){
			EDGEMOVEDELTA = 0;
			bsize *= 2;
			bHalfSize *= 2;
			gui.add(new NavigateButton((halfPX - bHalfSize), 0, bsize, bsize,
					Textures.BUTTONARROWRIGHT, "moveX", POSDELTA));
			gui.add(new NavigateButton(-(halfPX - bHalfSize), 0, bsize, bsize,
					Textures.BUTTONARROWLEFT, "moveX", -POSDELTA));
			gui.add(new NavigateButton(0, (halfPY - bHalfSize), bsize, bsize,
					Textures.BUTTONARROWUP, "moveY", POSDELTA));
			gui.add(new NavigateButton(0, -(halfPY - bHalfSize), bsize, bsize,
					Textures.BUTTONARROWDOWN, "moveY", -POSDELTA));
		}
		guiUpgBgrd = new GuiBackground(halfPX, halfPY, 0, 0,
				Textures.GUIUPGBGRD);
		scoreBgrd = new GuiBackground(halfPX, halfPY, 0, 0, Textures.GUIUPGBGRD);
		int tsize = 12;
		text = new GuiText(-halfPX, halfPY - (tsize + 5), tsize - 5, tsize);
		int s = 1;
		for(Config setting:Config.values()){
			switch(setting){
				case FULLSCREEN:
				case ISTOUCHSCREEN:
					continue;
				case SHOWMENU:
					gui.add(new SettingsButton(120 + 2 * bsize, (halfPY - 21),
							80, 20, setting));
					continue;
				case NOMUSIC:
					gui.add(new MusicButton(0, (halfPY / 2 - 41 * s), 120, 20,
							setting));
					s++;
					continue;
				default:
					gui.add(new MenuButton(0, (halfPY / 2 - 41 * s), 120, 20,
							setting));
					s++;
					continue;
			}
		}
	}
	/**
	 * Calls {@link NetworkClient#getNumberOfPlayers()} and adds that many
	 * player enums to the list
	 */
	private void initPlayers(){
		int size;
		if(!debug){
			playerId = client.getMyPlayerNumber();
			size = client.getNumberOfPlayers();
			String names[] = client.getPlayerNames();
			for(int i = 0; i < size; ++i){
				HashMap<Upgrades, Upgrade> upgrades = new HashMap<Upgrades, Upgrade>();
				for(Upgrades upg:Upgrades.values()){
					Upgrade u = new Upgrade(upg, i);
					upgrades.put(upg, u);
				}
				players.add(new Player(i, names[i], Color.getColor(i), upgrades));
			}
		}else{
			playerId = 0;
			size = 4;
			for(int i = 0; i < size; ++i){
				if(playerId == i){
					HashMap<Upgrades, Upgrade> upgrades = new HashMap<Upgrades, Upgrade>();
					for(Upgrades upg:Upgrades.values()){
						Upgrade u = new Upgrade(upg, i);
						upgrades.put(upg, u);
					}
					players.add(new Player(i, "YOU", Color.getColor(i),
							upgrades));
				}else{
					HashMap<Upgrades, Upgrade> upgrades = new HashMap<Upgrades, Upgrade>();
					for(Upgrades upg:Upgrades.values()){
						Upgrade u = new Upgrade(upg, i);
						upgrades.put(upg, u);
					}
					players.add(new Player(i, "BOT", Color.getColor(i),
							upgrades));
				}
			}
		}
		quake = 0;
	}
	/**
	 * Assignes ownership to one maptile per player
	 */
	private void initPlayerSpawn(){
		int size = players.size();
		if(size == 2){
			spawnPlayerOnField(0, 0);
			spawnPlayerOnField(1, 24);
			return;
		}
		if(size == 3){
			spawnPlayerOnField(0, 0);
			spawnPlayerOnField(1, 9);
			spawnPlayerOnField(2, 21);
			return;
		}
		if(size == 4){
			spawnPlayerOnField(0, 0);
			spawnPlayerOnField(1, 4);
			spawnPlayerOnField(2, 24);
			spawnPlayerOnField(3, 20);
			return;
		}
	}
	private void spawnPlayerOnField(int playerId, int fieldNumber){
		map.get(fieldNumber).setOwner(playerId);
		int sign = fieldNumber % 5 - 2 >= 0 ? 1 : -1;
		if(playerId == Game.playerId){
			Camera.zoom(
					true,
					new Vector3f(map.get(fieldNumber).modelPos.x, map
							.get(fieldNumber).modelPos.y
							+ sign
							* MapTile.TILESIZE / 2,
							map.get(fieldNumber).modelPos.z), sign * halfPY * 9
							/ 10);
			Camera.zFrameId = 6 * 6;
		}
	}
	/**
	 * Creates playingfield, adds tiles to map list
	 */
	private void initMap(){
		mapA = new MapTile[TILECOUNT][TILECOUNT];
		int size = TILECOUNT - 1;
		float dx = MapTile.GAP;
		for(int x = 0; x <= size; ++x){
			for(int y = 0; y <= size; ++y){
				MapTile m;
				if(x % 2 == 1 && y % 2 == 1 || x % 2 == 0 && y % 2 == 0){
					m = new MapTile((MapTile.TILESIZE + dx) * (x - 2),
							(MapTile.TILESIZE + dx) * (y - 2), 0,
							Textures.TILETEXTUREFLOORODD);
				}else{
					m = new MapTile((MapTile.TILESIZE + dx) * (x - 2),
							(MapTile.TILESIZE + dx) * (y - 2), 0,
							Textures.TILETEXTUREFLOOREVEN);
				}
				map.add(m);
				mapA[x][y] = m;
			}
		}
		fieldOfView = new FieldOfView(0f, 0f, 0.1f, Textures.FIELDOFVIEW);
	}
	/**
	 * Fetches mouse coordinates, stores {@link #getPickingRay(float, float)}
	 * and {@link #intersectsMap(Vector3f)} in global variable , handles mouse
	 * wheel zoom by calling {@link engine.Camera#zoom(boolean)} functions and
	 * screen edge movement, also unitselection by calling
	 * {@link #clickOnUnits(Vector3f)}, also performs gui click check
	 * {@link #clickGui()}
	 */
	public synchronized void getInput(){
		x = Mouse.getX();
		y = Mouse.getY();
		ray = getPickingRay(x, y);
		mapP = intersectsMap(ray);
		checkZoom();
		checkKeyboard();
		checkLeftclick();
		if(!Config.ISTOUCHSCREEN.isOn)
			checkRightclick();
		checkEdgeMove();
	}
	/**
	 * 
	 */
	private void checkZoom(){
		float dwheel = Mouse.getDWheel();
		if(dwheel != 0){
			if(getTileIndex(mapP.x, mapP.x) == -1){ return; }
			if(dwheel > 0){
				Camera.zoom(true, mapP, (y - halfPY));
			}else if(dwheel < 0){
				Camera.zoom(false,
						intersectsMap(getPickingRay(halfPX, halfPY)), 0);
			}
		}
	}
	/**
	 * 
	 */
	private void checkKeyboard(){
		if(Camera.zFrameId == 0){
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
				Camera.moveY(POSDELTA);
			if(Keyboard.isKeyDown(Keyboard.KEY_A))
				Camera.moveX(-POSDELTA);
			if(Keyboard.isKeyDown(Keyboard.KEY_S))
				Camera.moveY(-POSDELTA);
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				Camera.moveX(POSDELTA);
		}
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
					Config.SHOWMENU.toggleSetting();
				if(Keyboard.getEventKey() == Keyboard.KEY_TAB)
					showScores = !showScores;
			}
		}
	}
	/**
	 * 
	 */
	private void checkLeftclick(){
		if(button0Down){
			if(Mouse.isButtonDown(0)){ // on holding
				if(!clickGui(true)){
					select();
				}
			}
			if(!Mouse.isButtonDown(0)){ // on release
				releaseGui();
				selectionRect.release();
				selectedUnit = null;
				isSelecting = false;
				button0Down = false;
				keyTDown = false;
			}
		}
		if(!button0Down){
			if(Mouse.isButtonDown(0)){ // on press
				if(!clickGui(false)){
					rectStart = new Vector3f(mapP);
					selectionRect.click(mapP);
					select();
					isSelecting = true;
				}
				button0Down = true;
			}
		}
	}
	/**
	 * 
	 */
	private void checkRightclick(){
		if(button1Down){
			if(!Mouse.isButtonDown(1)){ // on release
				button1Down = false;
			}
		}
		if(!button1Down){
			if(Mouse.isButtonDown(1)){ // on press
				selectionRing.click(mapP);
				moveUnits(mapP);
				button1Down = true;
			}
		}
	}
	/**
	 * 
	 */
	private void checkEdgeMove(){
		if(Camera.zFrameId == 0){
			if(x < EDGEMOVEDELTA)
				Camera.moveX(-POSDELTA);
			if(x > Engine.WIDTH - EDGEMOVEDELTA)
				Camera.moveX(POSDELTA);
			if(y < EDGEMOVEDELTA)
				Camera.moveY(-POSDELTA);
			if(y > Engine.HEIGHT - EDGEMOVEDELTA)
				Camera.moveY(POSDELTA);
		}
	}
	/**
	 * @return true if clicked onto something in the hud, false otherwise
	 */
	private boolean clickGui(boolean holding){
		for(GuiElement ge:gui){
			if(ge.hit(x - halfPX, y - halfPY)){
				if(holding)
					if(!ge.isClicked)
						return false;
				ge.click();
				return true;
			}
		}
		return false;
	}
	private void releaseGui(){
		for(GuiElement ge:gui){
			ge.release();
		}
	}
	/**
	 * Handles game logic and movementcommand fetching. <br> Calls:
	 * {@link Sound#update()}, {@link Camera#update()}, {@link #checkKills()},
	 * {@link #getCommands()}, {@link #checkWin()}, {@link #updateProjectiles()}
	 * , {@link MapTile#resetUnitMap()}, {@link Unit#update()},
	 * {@link MapTile#update()}, {@link GuiElement#update()},
	 * {@link GuiText#update()}, {@link SelectionRing#update()},
	 * {@link SelectionRect#update()} Programs.PROGRAM3D.render(this);
	 */
	public synchronized void update(){
		Sound.update();
		Camera.update();
		Camera.useView();
		checkQuakeSounds();
		if(Camera.cameraPos.z >= ICONRENDERHEIGHT)
			iconRenderHeight = true;
		else
			iconRenderHeight = false;
		if(!getCommands())
			return;
		frameId++;
		if(!gameOver && getId % 20 == 0 && frameId == 1){
			checkWin();
		}
		updateProjectiles();
		for(MapTile maptile:map){ // reset here, because needed for
									// selection
			maptile.resetUnitMap();
		}
		for(Unit u:units){
			if(!u.update()){ // returns false if dead
				continue;
			}
		}
		for(MapTile maptile:map){
			maptile.update();
		}
		for(GuiElement ge:gui){
			ge.update();
		}
		text.update();
		selectionRing.update();
		selectionRect.update(mapP);
		if(sendId == 10 * 60 * 10){
			resizeTimer = 240;
			resizeMsg = "1 MIN: SHRINKING FIELD";
			Sound.playSound(Camera.cameraPos, Sounds.SIREN);
		}
		if(sendId == 11 * 60 * 10)
			suddenResize();
		if(sendId >= 12 * 60 * 10) // rounds i.e. 15min
			suddenSwap();
		if(sendId == 13 * 60 * 10){
			resizeTimer = 240;
			resizeMsg = "1 MIN: TURRETS WILL DIE";
			Sound.playSound(Camera.cameraPos, Sounds.SIREN);
		}
		if(sendId == 14 * 60 * 10)
			finalResize();
		gameNowTime += (100.0 / 6.0);
	}
	/**
	 * 
	 */
	private void suddenResize(){
		Sound.playSound(Camera.cameraPos, Sounds.NUKE);
		map.get(0).makeDeadly();
		map.get(1).makeDeadly();
		map.get(2).makeDeadly();
		map.get(3).makeDeadly();
		map.get(4).makeDeadly();
		map.get(5).makeDeadly();
		map.get(9).makeDeadly();
		map.get(10).makeDeadly();
		map.get(12).makeDeadly();
		map.get(14).makeDeadly();
		map.get(15).makeDeadly();
		map.get(19).makeDeadly();
		map.get(20).makeDeadly();
		map.get(21).makeDeadly();
		map.get(22).makeDeadly();
		map.get(23).makeDeadly();
		map.get(24).makeDeadly();
	}
	/**
	 * 
	 */
	private void finalResize(){
		Sound.playSound(Camera.cameraPos, Sounds.NUKE);
		for(MapTile m:map){
			m.turret.setDead();
		}
	}
	/**
	 * 
	 */
	private void suddenSwap(){
		suddenDeathId++;
		if(suddenDeathId < 15 * 6 * 10) // frames i.e. 30s
			return;
		suddenDeathId = 0;
		MapTile m = null;
		ArrayList<MapTile> swapList = new ArrayList<MapTile>();
		ArrayList<Integer> pList = new ArrayList<Integer>();
		for(Player p:players){
			m = swapTile(p.id);
			if(m == null)
				continue;
			swapList.add(m);
			pList.add(m.ownerId);
		}
		int size = swapList.size();
		if(size <= 1)
			return;
		swapDir *= -1;
		for(int i = 0; i < size; ++i){
			m = swapList.get(i);
			m.setOwner(pList.get(((i + swapDir) % size + size) % size));
		}
	}
	/**
	 * @param pid
	 * @return maptile with most owned tiles surrounding
	 */
	private MapTile swapTile(int pid){
		int min = 250, t;
		MapTile ret = null;
		for(MapTile m:map){
			if(m.ownerId != pid)
				continue;
			t = m.unitMap.size();
			if(min >= t){
				ret = m;
				min = t;
			}
		}
		return ret;
	}
	/**
	 * @param m
	 * @return number of surrounding tiles owned
	 */
	/*
	 * private int numberAdjOwned(MapTile m, int pid){ int count = 0, t = m.id,
	 * index; for(int i = -1; i <= 1; ++i){ for(int j = -1; j <= 1; ++j){ if(j
	 * == 0 && i == 0) continue; index = t + 5 * i + j; if(index < 0 || index >=
	 * TILECOUNT * TILECOUNT) continue; if(map.get(index).ownerId == pid){
	 * count++; } } } return count; }
	 */
	/**
	 * Plays quakesounds when a certain number of frags are reached
	 */
	private void checkQuakeSounds(){
		for(Player p:players){
			switch(p.score){
				case 1:
					if(quake >= 1)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.FIRSTBLOOD);
					quake = 1;
					break;
				case 5:
					if(quake >= 5)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.DOMINATING);
					quake = 5;
					break;
				case 10:
					if(quake >= 10)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.RAMPAGE);
					quake = 10;
					break;
				case 20:
					if(quake >= 20)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.ULTRAKILL);
					quake = 20;
					break;
				case 30:
					if(quake >= 30)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.KILLINGSPREE);
					quake = 30;
					break;
				case 50:
					if(quake >= 50)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.MULTIKILL);
					quake = 50;
					break;
				case 75:
					if(quake >= 75)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.MONSTERKILL);
					quake = 75;
					break;
				case 100:
					if(quake >= 100)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.UNSTOPPABLE);
					quake = 100;
					break;
				case 250:
					if(quake >= 250)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.GODLIKE);
					quake = 250;
					break;
				case 500:
					if(quake >= 500)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.LUDICROUSKILL);
					quake = 500;
					break;
				case 750:
					if(quake >= 750)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.HOLYSHIT);
					quake = 750;
					break;
				case 1000:
					if(quake >= 1000)
						break;
					Sound.playSound(Camera.cameraPos, Sounds.WICKEDSICK);
					quake = 1000;
					break;
			}
		}
	}
	/**
	 * Updates projectiles, removes dead shots from render list
	 */
	private void updateProjectiles(){
		int j = 0;
		for(int i = 0; i < shots.size(); ++i){
			if(shots.get(j).update()){
				shots.remove(j);
			}else{
				j++;
			}
		}
	}
	/**
	 * Fetches movement commands with {@link #updateCommands()} and
	 * {@link #processCommands()}, increases round number only if commands were
	 * fetched, will guarantee deterministic excecution on all clients
	 * @return false if commands couln't be fetched, true otherwise
	 */
	private boolean getCommands(){
		if(debug){
			if(frameId == framesPerRound){
				frameId = 0;
				processCommands();
				sendId++;
				getId++;
				return true;
			}else{
				return true;
			}
		}
		if(frameId == framesPerRound){
			if(!updateCommands()){ return false; }
			frameId = 0;
			client.roundDone(sendId);
			processCommands();
			sendId++;
			getId++;
		}
		return true;
	}
	/**
	 * Calls {@link NetworkClient#getRound(int)}
	 * @return false if commands couldn't be fetched
	 */
	private boolean updateCommands(){
		if(getId < 0){
			commands = new ArrayList<Command>();
			return true;
		}
		if(!debug)
			commands = client.getRound(getId);
		if(commands == null){ return false; }
		return true;
	}
	/**
	 * Updates list of units standing on each field
	 * @param unit
	 */
	public static void addToTileList(BasicUnit unit){
		if(!unit.dead){
			int i = getTileIndex(unit.modelPos.x, unit.modelPos.y);
			if(i == -1){ // no map hit
				return;
			}
			map.get(i).unitMap.add(unit);
		}
	}
	/**
	 * @param px mouse position x pixel
	 * @param py mouse position y pixel
	 * @return index of maptile in map list, -1 if clicked outside map
	 */
	public static int getTileIndex(float px, float py){
		float x = px + ((float) TILECOUNT / 2) * MapTile.TILESIZE;
		float y = py + ((float) TILECOUNT / 2) * MapTile.TILESIZE;
		if(x < 0 || y < 0) // left side out
			return -1;
		int i = (int) (x / MapTile.TILESIZE); // round down tile in x dir
		int j = (int) (y / MapTile.TILESIZE); // round down tile in y dir
		if(i >= TILECOUNT || j >= TILECOUNT) // didn't hit map?
			return -1;
		int index = i * 5 + j;
		return index;
	}
	/**
	 * Loops though all fetched commands and sets unit targets accordingly, also
	 * writes log file
	 */
	private void processCommands(){
		while(commands.size() != 0){
			Command com = commands.get(0);
			if(com instanceof MoveCommand)
				setTargets((MoveCommand) com);
			if(com instanceof UpgradeCommand)
				upgrade((UpgradeCommand) com);
			writer.println(getId + " " + frameId + " " + com.toString());
			writer.flush();
			commands.remove(0);
		}
	}
	/**
	 * Does upgrades
	 */
	private void upgrade(UpgradeCommand up){
		Upgrades upg = up.getUpgrade();
		players.get(up.getPlayerId()).upgrade(upg);
		int bs = 7;
		int cs = 20;
		Player p = players.get(up.getPlayerId());
		String c = String.format("%" + bs + "s", p.COLOR);
		String u = String.format("%" + cs + "s", upg);
		String a = String.format("%7."
				+ getDecimalPlaces(p.upgrades.get(upg).amount) + "f",
				p.upgrades.get(upg).amount);
		upgradeMessages.add(new UpgradeString(c + " " + u + " " + a));
	}
	/**
	 * Calls render function off all gameobjects present, renders objects by
	 * rendertype to minimize shader switching {@link Display#update()} and
	 * {@link Display#sync(int)} to fix fps to 60
	 */
	public void render(){
		oddFrame = !oddFrame;
		if(oddFrame && Config.HALF_FPS.isOn){
			Display.update();
			Display.sync(FPS);
			return;
		}
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glUseProgram(Programs.PROGRAM3D.pId);
		if(Config.NOLIGHTING.isOn)
			glUseProgram(Programs.PROGRAM3DNL.pId);
		else
			glUseProgram(Programs.PROGRAM3D.pId);
		for(MapTile tile:map)
			tile.render();
		glEnable(GL_STENCIL_TEST);
		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);
		glUseProgram(Programs.PROGRAM3DNL.pId);
		for(ViewRadiusFOV sv:fieldofview)
			if(sv.ownerId == playerId || gameOver)
				sv.render();
		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);
		glStencilFunc(GL_EQUAL, 0, 0xff);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glUseProgram(Programs.PROGRAM3DNL.pId);
		fieldOfView.render();
		glDisable(GL_BLEND);
		glStencilFunc(GL_EQUAL, 1, 0xff);
		glEnable(GL_BLEND);
		glUseProgram(Programs.PROGRAM3DNL.pId);
		glBlendFunc(GL_ONE, GL_ONE);
		for(Projectile shot:shots)
			shot.render();
		glDisable(GL_BLEND);
		if(Game.iconRenderHeight || Config.NOLIGHTING.isOn)
			glUseProgram(Programs.PROGRAM3DNL.pId);
		else
			glUseProgram(Programs.PROGRAM3D.pId);
		for(Unit u:units)
			u.render();
		for(Turret t:turrets)
			t.render();
		if(!Game.iconRenderHeight && !Config.NOHEALTHBARS.isOn){
			glUseProgram(Programs.PROGRAM3DNL.pId);
			for(Billboard bb:healthbarbgrd)
				bb.render();
			for(Billboard b:healthbar)
				b.render();
		}
		glDisable(GL_STENCIL_TEST);
		renderViewRadii();
		glUseProgram(Programs.PRIMITIVE.pId);
		selectionRing.render();
		selectionRect.render();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glUseProgram(Programs.PROGRAMGUI.pId);
		renderGui();
		glDisable(GL_BLEND);
		glBindVertexArray(0);
		glUseProgram(0);
		Engine.exitOnGLError("Game.render");
		Display.update();
		Display.sync(FPS);
	}
	private void renderGui(){
		guiUpgBgrd.render();
		if(showScores)
			scoreBgrd.render();
		for(GuiElement ge:gui){
			ge.render();
		}
		glBlendFunc(GL_ONE, GL_ONE);
		if(showScores)
			renderScore();
		renderUpgradeTexts();
		if(gameOver)
			renderWinMessage();
		renderGameTimer();
		if(suddenDeathId <= 60 && suddenDeathId != -1)
			text.render("SUDDENDEATH", (int) (-15 * 5.5f), 0, 15, 20);
		if(discMsgTimer > 0)
			renderDiscMsg();
		if(resizeTimer > 0)
			renderResizeWarning();
	}
	private void renderResizeWarning(){
		resizeTimer--;
		text.render(resizeMsg, -20 * resizeMsg.length(), 0, 20, 25);
	}
	/**
	 * 
	 */
	private void renderDiscMsg(){
		discMsgTimer--;
		text.render(discMsg + " HAS DISCONNECTED", -20
				* (discMsg.length() + 17), 0, 20, 25);
	}
	/**
	 * on disconnect handles cleanup
	 */
	public static void kickPlayer(int id){
		discMsgTimer = 1200;
		discMsg = players.get(id).NAME;
	}
	/**
	 * 
	 */
	private void renderGameTimer(){
		long t = (gameNowTime - gameStartTime) / 1000;
		long m = t / 60;
		long s = t % 60;
		text.render(String.format("%02d:%02d", m, s), halfPX - 6 * 14 - 27,
				halfPY - 2 * 9, 6, 9);
	}
	/**
	 * Calls glUseProgram, no need to before
	 */
	private void renderViewRadii(){
		glEnable(GL_STENCIL_TEST);
		glColorMask(false, false, false, false);
		glDepthMask(false);
		glStencilFunc(GL_NEVER, 1, 0xFF);
		glStencilOp(GL_REPLACE, GL_KEEP, GL_KEEP);
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);
		glUseProgram(Programs.PROGRAM3DNL.pId);
		for(ViewRadiusStencil sv:stencilradii)
			if(sv.ownerId == playerId || gameOver)
				sv.render();
		glColorMask(true, true, true, true);
		glDepthMask(true);
		glStencilMask(0x00);
		glStencilFunc(GL_EQUAL, 0, 0xff);
		glUseProgram(Programs.PRIMITIVE.pId);
		for(ViewRadiusRange v:radii)
			if(v.ownerId == playerId || gameOver)
				v.render();
		glDisable(GL_STENCIL_TEST);
	}
	/**
	 * {@link GuiText#render()}
	 */
	private void renderWinMessage(){
		int ssize = 25;
		int spacing = 6;
		if(victorious){
			String str = String.format("%" + spacing + "s", "VICTORY");
			text.render(str, -140, -3 * halfPY / 4, ssize, ssize);
		}else{
			String str = String.format("%" + spacing + "s", "DEFEAT");
			text.render(str, -140, -3 * halfPY / 4, ssize, ssize);
		}
		if(!playedEndSound){
			Sound.stopSound(Sounds.SUPCOM);
			Sound.stopSound(Sounds.SIREN);
			Sound.stopSound(Sounds.NUKE);
			if(victorious)
				Sound.playSound(Camera.cameraPos, Sounds.VICTORY);
			else
				Sound.playSound(Camera.cameraPos, Sounds.ALEXLAUGH);
			playedEndSound = true;
		}
	}
	/**
	 * {@link GuiText#render()}
	 */
	private void renderUpgradeTexts(){
		int tsize = 11;
		int dx = 5;
		int i = 0;
		for(UpgradeString us:upgradeMessages){
			text.render(us.string, +halfPX / 3,
					halfPY - (i + 1) * (tsize + dx), tsize - dx, tsize);
			i++;
			if(us.timeLeft-- <= 0){
				upgradeMessages.remove(us);
				return;
			}
		}
		int halfStringSize = 38 * (tsize - dx);
		guiUpgBgrd.moveNresize(halfPX / 3 + halfStringSize, halfPY - (i + 1)
				* (tsize + dx) / 2, halfStringSize, (tsize + dx) / 2 * i
				+ (i > 0 ? 2 : 0));
	}
	/**
	 * {@link GuiText#render()}
	 */
	private void renderScore(){
		int i = 0, txsize = 5, tysize = txsize + 3, spacing = 8, col = spacing
				* 2 * txsize, row = (tysize + 3), c = 1;
		int column = col * i++;
		String str;
		str = String.format("%" + spacing + "s", "NAME:");
		text.render(str, -(halfPX - column), halfPY - row * c++, txsize, tysize);
		str = String.format("%" + spacing + "s", "COLOR:");
		text.render(str, -(halfPX - column), halfPY - row * c++, txsize, tysize);
		str = String.format("%" + spacing + "s", "FRAGS:");
		text.render(str, -(halfPX - column), halfPY - row * c++, txsize, tysize);
		c++;
		for(Upgrades u:Upgrades.values()){
			str = String.format("%" + spacing + "s", u.dispName + ":");
			text.render(str, -(halfPX - column), halfPY - row * c++, txsize,
					tysize);
		}
		for(Player p:players){
			column = col * i++;
			c = 1;
			str = String.format("%" + spacing + "s",
					p.NAME.length() > 8 ? p.NAME.substring(0, 7) : p.NAME);
			text.render(str, -(halfPX - column), halfPY - row * c++, txsize,
					tysize);
			str = String.format("%" + spacing + "s", p.COLOR);
			text.render(str, -(halfPX - column), halfPY - row * c++, txsize,
					tysize);
			str = String.format("%" + spacing + "s", p.score);
			text.render(str, -(halfPX - column), halfPY - row * c++, txsize,
					tysize);
			c++;
			for(Upgrades u:Upgrades.values()){
				str = String.format(
						"%" + spacing + "." + getDecimalPlaces(p.getStats(u))
								+ "f", p.getStats(u));
				text.render(str, -(halfPX - column), halfPY - row * c++,
						txsize, tysize);
			}
		}
		int xoff = col * i / 2;
		int yoff = c * row / 2;
		scoreBgrd.moveNresize(-(halfPX - xoff), halfPY - yoff, xoff - 2,
				yoff - 2);
	}
	/**
	 * @param value
	 * @return number of decimal places
	 */
	public static int getDecimalPlaces(float value){
		if((float) Math.round(value) == value)
			return 0;
		final String s = Float.toString(value);
		final int index = s.indexOf('.');
		if(index < 0){ return 0; }
		return((s.length() - 1 - index) >= 3 ? 3 : (s.length() - 1 - index));
	}
	/**
	 * Unit click selection
	 */
	private void select(){
		if(Config.ISTOUCHSCREEN.isOn)
			touchSelect();
		else
			normalSelect();
	}
	/**
	 * Checks whether one clicked on a unit or, if button is still down, units
	 * withing the selection field if shift is pressed, won't unselect units
	 * @param ray picking ray
	 */
	private void normalSelect(){
		if(keyTDown)
			return;
		if(!isSelecting){
			if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
				unselectAll();
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_T)){
				int i = getTileIndex(mapP.x, mapP.y);
				if(i != -1){ // if hit map
					MapTile tile = map.get(i);
					for(Unit u:tile.unitMap){
						selectUnit(u);
					}
					keyTDown = true;
					return;
				}
			}
			for(Unit unit:units){ // first click hit unit?
				if(unit.ownerId != playerId){
					continue;
				}
				if(intersects(ray, unit.modelPos, 0.15)){
					if(unit.selected == true){
						unselectUnit(unit);
					}else if(unit.selected == false){
						selectUnit(unit);
						selectedUnit = unit;
					}
					return;
				}
			}
		}
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			unselectAll();
		}
		for(Unit unit:units){ // if within rectangle
			if(rectStart == null)
				rectStart = new Vector3f(mapP);
			float dx = rectStart.x - mapP.x;
			float dy = rectStart.y - mapP.y;
			float rx = mapP.x - unit.modelPos.x;
			float ry = mapP.y - unit.modelPos.y;
			float lx = unit.modelPos.x - rectStart.x;
			float ly = unit.modelPos.y - rectStart.y;
			if(dx < 0 && dy > 0){ // right down
				if(rx > 0 && ry < 0 && lx > 0 && ly < 0){
					selectUnit(unit);
				}
			}else if(dx < 0 && dy < 0){ // right up
				if(rx > 0 && ry > 0 && lx > 0 && ly > 0){
					selectUnit(unit);
				}
			}else if(dx > 0 && dy < 0){ // left up
				if(rx < 0 && ry > 0 && lx < 0 && ly > 0){
					selectUnit(unit);
				}
			}else if(dx > 0 && dy > 0){ // left down
				if(rx < 0 && ry < 0 && lx < 0 && ly < 0){
					selectUnit(unit);
				}
			}
		}
	}
	private void touchSelect(){
		if(!isSelecting){
			moveUnits(mapP);
		}
		unselectAll();
		for(Unit unit:units){ // if within rectangle
			if(rectStart == null)
				rectStart = new Vector3f(mapP);
			float dx = rectStart.x - mapP.x;
			float dy = rectStart.y - mapP.y;
			float rx = mapP.x - unit.modelPos.x;
			float ry = mapP.y - unit.modelPos.y;
			float lx = unit.modelPos.x - rectStart.x;
			float ly = unit.modelPos.y - rectStart.y;
			if(dx < 0 && dy > 0){ // right down
				if(rx > 0 && ry < 0 && lx > 0 && ly < 0){
					selectUnit(unit);
				}
			}else if(dx < 0 && dy < 0){ // right up
				if(rx > 0 && ry > 0 && lx > 0 && ly > 0){
					selectUnit(unit);
				}
			}else if(dx > 0 && dy < 0){ // left up
				if(rx < 0 && ry > 0 && lx < 0 && ly > 0){
					selectUnit(unit);
				}
			}else if(dx > 0 && dy > 0){ // left down
				if(rx < 0 && ry < 0 && lx < 0 && ly < 0){
					selectUnit(unit);
				}
			}
		}
	}
	/**
	 * Adds selected unit to list of selected units
	 * @param unit
	 */
	private static void selectUnit(Unit unit){
		if(unit.ownerId == playerId && unit.selected == false && !unit.dead){
			unit.setSelected(true);
			unitsSelected.add(unit);
		}
	}
	/**
	 * Removes selected unit from list of selected units
	 * @param unit
	 */
	private void unselectUnit(Unit unit){
		if(unit.selected == true){
			unit.setSelected(false);
			unitsSelected.remove(unit);
		}
	}
	/**
	 * Empties list of selected units
	 */
	private void unselectAll(){
		for(int i = 0; i < unitsSelected.size();){
			Unit u = unitsSelected.get(i);
			if(selectedUnit == null || u != selectedUnit){ // skip singleclick
															// selected unit
				u.setSelected(false);
				unitsSelected.remove(u);
				continue;
			}
			i++;
		}
	}
	/**
	 * Selects every unit owned by player
	 */
	public static void selectAll(){
		for(Unit u:Game.units){
			selectUnit(u);
		}
	}
	/**
	 * Moves unit to location map where player clicked
	 * @param ray picking ray
	 */
	private void moveUnits(Vector3f p){
		int i = getTileIndex(p.x, p.y);
		if(i != -1){ // hit map
			if(unitsSelected.size() != 0){
				int size = unitsSelected.size();
				unitIds = new int[size];
				for(int j = 0; j < size; ++j){
					unitIds[j] = unitsSelected.get(j).id;
				}
				moveUnits(sendId, unitIds, p);
			}
		}
	}
	/**
	 * Sends the movecommand per {@link NetworkClient#moveUnits(MoveCommand)} to
	 * the server for processing
	 * @param sendId round send number
	 * @param unitIds
	 * @param target
	 */
	public static void moveUnits(int sendId, int[] unitIds, Vector3f target){
		MoveCommand moveCmd = new MoveCommand(sendId, playerId, unitIds, target);
		if(!debug){
			client.moveUnits(moveCmd);
		}else{
			commands.add(moveCmd);
		}
	}
	/**
	 * Line sphere intersection check
	 * @param ray picking ray
	 * @param object objects position
	 * @param r sphere radius
	 * @return true if sphere hit, false otherwise
	 */
	private boolean intersects(Vector3f ray, Vector3f object, double r){
		double cx = object.x, cy = object.y, cz = object.z + 0.3f;
		double px = Camera.cameraPos.x, py = Camera.cameraPos.y, pz = Camera.cameraPos.z;
		double ux = ray.x, uy = ray.y, uz = ray.z;
		double a = -ux * (px - cx) - uy * (py - cy) - uz * (pz - cz);
		double qx = (px + a * ux), qy = py + a * uy, qz = pz + a * uz;
		double dx = qx - cx, dy = qy - cy, dz = qz - cz;
		double quada = dx * dx + dy * dy + dz * dz;
		double Abstand = Math.sqrt(quada);
		if(r >= Abstand){ return true; }
		return false;
	}
	/**
	 * Line plane intersection check
	 * @param ray
	 * @return
	 */
	private synchronized Vector3f intersectsMap(Vector3f ray){
		float scale = Camera.cameraPos.z / ray.z;
		Vector3f tmp = new Vector3f(ray);
		tmp.scale(-scale);
		Vector3f.add(Camera.cameraPos, tmp, tmp);
		return tmp;
	}
	/**
	 * Calculates picking ray by transforming point on near plane into world
	 * coordinates by inverse multiplication with perspective- and viewmatrix
	 * and then subtracting from camera position
	 * @param cursorX lwjgl mouse x pixel coordinates
	 * @param cursorY lwjgl mouse y pixel coordinates
	 * @return picking ray
	 */
	private synchronized Vector3f getPickingRay(float cursorX, float cursorY){
		// set 0,0 to middle of screen
		float x = 2.0f * (cursorX) / Engine.WIDTH - 1;
		float y = -2.0f * (Engine.HEIGHT - cursorY) / Engine.HEIGHT + 1;
		Matrix4f projectionInverse = Matrix4f.invert(Camera.projectionMatrix,
				null);
		Matrix4f viewnInverse = Matrix4f.invert(Camera.viewMatrix, null);
		// point on near plane
		Vector4f point = new Vector4f(x, y, 0, 1);
		// transform into world coordinates
		Matrix4f.transform(projectionInverse, point, point);
		Matrix4f.transform(viewnInverse, point, point);
		Vector3f pointNear = new Vector3f(point.x, point.y, point.z);
		pointNear.scale(1 / point.w);
		Vector3f ret = new Vector3f();
		Vector3f.sub(pointNear, Camera.cameraPos, ret);
		ret.normalise(ret);
		return ret;
	}
	/**
	 * Checks units left and field in possession to determine loss or victory
	 */
	private void checkWin(){
		Boolean win = true;
		Boolean lost = true;
		won:
		{
			for(MapTile tile:map){
				if(tile.ownerId != playerId && tile.ownerId != -1){
					win = false;
					break won;
				}
			}
			for(Unit unit:units){
				if(unit.ownerId != playerId && !unit.dead){
					win = false;
					break won;
				}
			}
		}
		loose:
		{
			if(!win){
				for(MapTile tile:map){
					if(tile.ownerId == playerId){
						lost = false;
						break loose;
					}
				}
				for(Unit unit:units){
					if(unit.ownerId == playerId){
						lost = false;
						break loose;
					}
				}
			}
		}
		if(win){
			if(!debug){
				client.sendEndGame();
			}
			showScore();
			gameOver = true;
			victorious = true;
		}else if(lost){
			if(!debug){
				client.sendEndGame();
			}
			showScore();
			gameOver = true;
		}
	}
	/**
	 * Displays score in chat by calling {@link gui.TabbeChats#print(String)}
	 */
	private void showScore(){
		StringBuilder Scoreoutput = new StringBuilder("---SCORE---");
		for(Player p:players){
			Scoreoutput.append("\n " + p.NAME + ": " + p.score);
		}
		Scoreoutput.append("\n -----------");
		if(!debug){
			try{
				lobby.print(Scoreoutput.toString(), client.gameName,
						Formatation.game);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println(Scoreoutput.toString());
		}
	}
	/**
	 * Calculates unit formation by changing unit targets when multiple units
	 * selected
	 * @param mc
	 */
	private void setTargets(MoveCommand mc){
		float mapborder = TILECOUNT * MapTile.TILESIZE / 2;
		int uSelected[];
		Vector3f commandTarget;
		Vector3f unitSpace;
		uSelected = mc.getUnitIds();
		int unitColumn = 1;
		int maxUnitsInColumn;
		float columnspacing = 0.6f;
		float rowspacing = 0.6f;
		if(uSelected.length < 10){
			maxUnitsInColumn = uSelected.length;
		}else{
			maxUnitsInColumn = uSelected.length <= 100 ? 10 : (int) Math
					.ceil(Math.sqrt(uSelected.length));
		}
		commandTarget = mc.getMoveTarget();
		Vector3f unitDirection = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f averageDirection = new Vector3f(0.0f, 0.0f, 0.0f);
		for(int u:uSelected){ // update unit target
			if(units.size() <= u){
				writer.println("---DESYNC---NONEXISTENT UNIT---" + u);
				writer.flush();
				System.err.println("---DESYNC---NONEXISTENT UNIT---" + u);
				continue;
			}
			Vector3f.sub(commandTarget, units.get(u).modelPos, unitDirection);
			Vector3f.add(averageDirection, unitDirection, averageDirection);
		}
		averageDirection = new Vector3f(averageDirection.x / uSelected.length,
				averageDirection.y / uSelected.length, 0.0f);
		if(averageDirection.length() != 0){
			averageDirection.normalise();
		}else{
			averageDirection = new Vector3f(1f, 0f, 0f);
		}
		averageDirection.scale(rowspacing);
		unitSpace = new Vector3f(-averageDirection.y, averageDirection.x, 0.0f);
		Vector3f unitposition = new Vector3f(commandTarget);
		unitSpace.scale((float) (maxUnitsInColumn - 1) / 2f);
		Vector3f.add(unitposition, unitSpace, unitposition);
		if(unitSpace.length() > 0){
			unitSpace.normalise();
		}
		unitSpace.scale(-columnspacing);
		averageDirection.scale(-1f);
		Boolean first = true;
		Boolean outOfBounds = false;
		for(int u:uSelected){ // update unit target
			if(units.size() <= u){
				continue;
			}
			Unit unit = units.get(u);
			if((unit.dead || unit.ownerId != mc.getPlayerId()) && !debug){
				writer.println("---UNIT INDEX REUSED---NOT YOUR UNIT ANYMORE---"
						+ u);
				writer.flush();
				continue;
			}
			if(!first){
				if(unitColumn < maxUnitsInColumn){
					Vector3f.add(unitposition, unitSpace, unitposition);
				}
				unitColumn++;
				if(unitColumn > maxUnitsInColumn){
					unitColumn = 1;
					unitSpace.scale(-1f);
					Vector3f.add(unitposition, averageDirection, unitposition);
				}
			}else{
				first = false;
			}
			int counter = 0;
			while(unitposition.x > mapborder || unitposition.x < -mapborder
					|| unitposition.y < -mapborder
					|| unitposition.y > mapborder){
				if(unitColumn < maxUnitsInColumn){
					Vector3f.add(unitposition, unitSpace, unitposition);
				}
				unitColumn++;
				if(unitColumn > maxUnitsInColumn){
					unitColumn = 1;
					unitSpace.scale(-1f);
					Vector3f.add(unitposition, averageDirection, unitposition);
				}
				counter++;
				if(counter > maxUnitsInColumn){
					Vector3f tempPos = new Vector3f(unitposition);
					if(unitposition.x > mapborder){
						tempPos.x = mapborder;
					}else if(unitposition.x < -mapborder){
						tempPos.x = -mapborder;
					}
					if(unitposition.y < -mapborder){
						tempPos.y = -mapborder;
					}else if(unitposition.y > mapborder){
						tempPos.y = mapborder;
					}
					unit.target = new Vector3f(tempPos);
					outOfBounds = true;
					break;
				}
			}
			if(!outOfBounds){
				unit.target = new Vector3f(unitposition);
			}
		}
	}
}
