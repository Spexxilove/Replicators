package client;

import static network.Prefix.*;
import network.*;
import gui.Formatation;
import gui.Lobby;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.EventQueue;
import static network.Commands.*;

/**
 * @author Alex
 * @version 0.1 Base Class for Client Communication updated on 0.2 by Severin
 */
public class NetworkClient extends Thread{
	private final int port; // server details
	private String host;
	private String s;
	public ClientListener listener = null;
	public Socket sock;
	private BufferedReader in; // i/o for the client
	private BufferedReader stdin;
	private PrintWriter out;
	private String name;
	private String mainChannelName = "MAIN";
	public volatile static int pingNumber = 0;
	private int playersInCurrentGame = 0;
	private int myPlayerNumber = 0;
	private String[] playernames;
	private Lobby lobby;
	public NetworkClient me = this;
	// public PipedWriter Reciver;//TODO better name
	// public PipedReader Sender;
	public volatile HashMap<Integer, ArrayList<Command>> playerCommands;
	public String gameName = "";
	private int gameRound = 0;
	

	
	public NetworkClient(String host, int port){
		this.host = host;
		this.port = port;
		playerCommands = new HashMap<Integer,ArrayList<Command>>();
		// --- DEBUG ---System.out.println(System.currentTimeMillis());
		EventQueue.invokeLater(
				new Runnable() {
					public void run() {
						try {
							lobby = new Lobby(me);
							lobby.setVisible(true);
							// --- DEBUG ---System.out.println(System.currentTimeMillis());
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
			}
		});
		/*
		 * try { Thread.sleep(00);// TODO prop fix with invokeandwait } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}
	public void run(String LoginName){
		// TODO invokeLater necessary
		// print("das hier sollte im chatfenster stehen");
		// TODO Kommentar wieder integriere, isch für Gui - buildig usegno,
		// damit chatfenster simuliert werde cha...
		try{
			stdin = new BufferedReader(new InputStreamReader(System.in));
			initConnection(); // open Streams
			new Heartbeat(this).start();
			handShake(LoginName); // HandShake i.e. send Name
			try{
				// Thread.sleep(300);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			// join main channel
			joinChannel(mainChannelName);
			// System.out.println("now there may be printed----------------------");
			// TODO readline muss mit methode umgebaut werde processuserimput
			// has to get a string
			// TODO system.out.println ersetze
			while((s = stdin.readLine()) != null){
				if(!listener.isAlive()){
					print("---LOOKING FOR SERVER---", "Main");
					initConnection();
					handShake(LoginName);
					joinChannel(mainChannelName);
				}
				processUserInput(s);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{}
	}
	private void handShake(String LoginName){
		if(LoginName == ""){
			String nameIn = "";
			while(name == null){
				try{
					System.out.println("enter name");// TODO sev you know
					nameIn = stdin.readLine();
					changeName(nameIn);
					Thread.sleep(2000);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			changeName(LoginName);
		}
	}
	private void initConnection(){
		makeContact(); // open socket, in-, out streams
		listener = new ClientListener(this, in, lobby); // watches input from
														// server
		listener.start();
	}
	/**
	 * processes user input to send right commands
	 * @param input
	 */
	public void processUserInput(String input){
		String channel = "MAIN";
		String message = "";
		if(input.startsWith("/")){// input is command
			String prefix = input.trim().split(" ")[0].trim();
			switch(prefix){
				case "/help":
					printhelp();
					return;
				case "/join":
					joinChannel(input.replaceFirst("/join", "").trim());
					return;
				case "/leave":
					leaveChannel(input.replaceFirst("/leave", "").trim());
					return;
				case "/createGame":
					createGame(input.replaceFirst("/createGame", "").trim());
					return;
				case "/joinGame":
					joinGame(input.replaceFirst("/joinGame", "").trim());
					return;
				case "/leaveGame":
					leaveGame(input.replaceFirst("/leaveGame", "").trim());
					return;
				case "/toggleReady":
					toggleReady(input.replaceFirst("/toggleReady", "").trim());
					return;
				case "/getGames":
					getGameList();
					return;
				case "/getChannels":
					getChannelList();
					return;
				case "/exit":
					endProgram();
				default:
					break;
			}
		}else if(input.startsWith("@")){
			channel = input.trim().split(" ")[0].trim();
			channel = channel.replaceFirst("@", "");
			int pos = input.indexOf(" ");
			message = input.substring(pos+1);
			message = message.trim();
		}else{
			message = input;
		}
		if(!message.equals("")){
			sendChatMessage(channel, message);
		}
	}
	/**
	 * print command options to chat
	 */
	private void printhelp(){
		String Help = "/join CHANNELNAME \n" + "/leave CHANNELNAME \n"
				+ "@CHANNELNAME message \n" + "/createGame GAMENAME \n"
				+ "/leaveGame GAMENAME \n" + "/toggleReady \n" + "/getGames \n"
				+ "/exit \n" + "/exit \n";
		try{
			print(Help, "Main");
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(Help);
		}
	}
	private void endProgram(){
		Runtime.getRuntime().exit(0);
	}
	/**
	 * sends message to toggle ready state if user has joined a game
	 * @param trim
	 */
	private void toggleReady(String trim){
		if(!gameName.equals("")){
			NetMessage msg = new NetMessage(GAME, gameName, name, PLAYERREADY,
					"");
			sendMessage(msg);
		}
	}
	/**
	 * sends message to leave a channel
	 * @param channelName
	 */
	private void leaveChannel(String channelName){
		sendMessage(CHAT, channelName, LEAVE, "");
	}
	/**
	 * assigns socket, opens streams
	 */
	public void makeContact(){
		try{
			// create socket + set input and output streams
			sock = new Socket(host, port);
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * sends message to server
	 * @param prefix chat or game
	 * @param channel
	 * @param message
	 */
	public void sendMessage(Prefix prefix, String channel, Commands command,
			String message){
		NetMessage outMsg = new NetMessage(prefix, channel,
				this.name == null ? "" : this.name.trim(), command, message);
		out.println(outMsg.toString());
	}
	public void sendMessage(NetMessage message){
		out.println(message.toString());
	}
	/**
	 * sends message to join a channel
	 * @param channelName
	 */
	public void joinChannel(String channelName){
		// System.out.println("joinChannel for Channel: "+channelName +
		// "     at time: "+System.currentTimeMillis());
		// print(channelName, "Main");
		sendMessage(CHAT, mainChannelName, JOIN, channelName);
	}
	/**
	 * prints message to output
	 * @param message
	 */
	public void print(NetMessage Message){
		print(Message.toString(), Message.CHANNEL);
	}
	/**
	 * print message to channels tab
	 * @param message
	 * @param channel
	 */
	private void print(String message, String channel){
		// System.out.println("NetworkClient: " + message + " -- " + channel);
		lobby.print(message, channel,Formatation.normal);
	}
	/**
	 * sends a chat message in channel
	 * @param channel default is MAIN
	 * @param message
	 */
	public void sendChatMessage(String channel, String message){
		sendMessage(CHAT, channel, SEND, message);
		System.out.println("sendMessageaaaaaaaaa: "+message.toString());
	}
	public void sendChatMessage(String message){
		sendChatMessage(mainChannelName, message);
	}
	/**
	 * requests name change
	 * @param name
	 */
	public void changeName(String name){
		NetMessage outMsg = new NetMessage(CHAT, "MAIN", name, CHANGENAME, "");
		out.println(outMsg.toString());
	}
	/**
	 * closes connection
	 */
	public void closeSocket(){
		try{
			sendMessage(CHAT, mainChannelName, LEAVE, "");
			sock.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * returns user name
	 * @return
	 */
	public String getClientName(){
		if(name != null){
			return name;
		}else{
			return "";
		}
	}
	/**
	 * sets new Name
	 * @param name
	 */
	public void setClientName(String name){
		this.name = name;
		// TODO this must be done diffently
		// bad place to do that processUserInput("/getGames");
	}
	/**
	 * get list of games
	 */
	private void getGameList(){
		// System.out.println("someone called get gamelist");
		NetMessage getGameMsg = new NetMessage(GAME, "MAIN", name, GAMELIST, "");
		sendMessage(getGameMsg);
	}
	/**
	 * create new game and join it
	 * @param gameName
	 */
	public void createGame(String gameName){
		if(!this.gameName.equals("")){
			leaveGame(this.gameName);
		}
		this.gameName = gameName;
		sendMessage(GAME, "MAIN", CREATEGAME, gameName);
	}
	/**
	 * try to join a game
	 * @param gameName
	 */
	public void joinGame(String gameName){
		if(!this.gameName.equals("")){
			leaveGame(this.gameName);
		}
		this.gameName = gameName;
		sendMessage(GAME, gameName, JOINGAME, "");
	}
	/**
	 * sets name of joined game
	 * @param gameName
	 */
	public void setGameName(String gameName){
		this.gameName = gameName;
	}
	/**
	 * leave a game if user is in this game
	 * @param gameName
	 */
	private void leaveGame(String gameName){
		if(this.gameName.equals(gameName)){
			this.gameName = "";
			playersInCurrentGame = 0;
			myPlayerNumber = 0;
			sendMessage(GAME, gameName, LEAVEGAME, "");
		}
	}
	/**
	 * send to server message to move units in array to coordinates x,y
	 */
	public void moveUnits(MoveCommand command){
		String message = "";
		message += command.getId();
		message += NetMessage.BATTRIBUTESPLITB;
		for(int id:command.getUnitIds()){
			message += " " + String.valueOf(id);
		}
		message += NetMessage.BATTRIBUTESPLITB;
		message += String.valueOf(command.getMoveTarget().getX()) + " "
				+ String.valueOf(command.getMoveTarget().getY()) + " "
				+ String.valueOf(command.getMoveTarget().getZ());
		message += NetMessage.BATTRIBUTESPLITB;
		message += myPlayerNumber;
		NetMessage moveMessage = new NetMessage(GAME, gameName, name, MOVEUNIT,
				message);
		sendMessage(moveMessage);
	}
	/**
	 * send message to server that round with number roundNumber
	 * @param roundNumber
	 */
	public void roundDone(int roundNumber){
		NetMessage roundDoneMessage = new NetMessage(GAME, gameName, name,
				ROUNDDONE, String.valueOf(roundNumber));
		sendMessage(roundDoneMessage);
	}
	/**
	 * returns commands of all players in a round
	 * @param roundNumber
	 * @return list if round done else return null
	 */
	public synchronized ArrayList<Command> getRound(int roundNumber){
		if(roundNumber <= gameRound){
			if(playerCommands.containsKey(roundNumber)){
				return playerCommands.remove(roundNumber);
			}else{
				return new ArrayList<Command>();
			}
			
		}else{
			return null;
		}
	}
	/**
	 * sends playerready for currently joined game
	 */
	public void sendPlayerReady(){
		NetMessage msg = new NetMessage(GAME, gameName, name,
				Commands.PLAYERREADY, "");
		sendMessage(msg);
	}
	/**
	 * set new rounddone from server
	 * @param roundnumber
	 */
	public synchronized void setRound(int roundnumber){
		if(this.gameRound < roundnumber){
			this.gameRound = roundnumber;
		}
	}
	/**
	 * sends request on channel and user list
	 */
	public void getChannelList(){
		NetMessage msg = new NetMessage(CHAT, mainChannelName, name,
				CHANNELLIST, "");
		sendMessage(msg);
	}
	/**
	 * get the number of players in the current game
	 * @return
	 */
	public int getNumberOfPlayers(){
		return playersInCurrentGame;
	}
	/**
	 * set your own player number is updated on game start
	 * @param playerNumber
	 */
	public void setMyPlayerNumber(int playerNumber){
		myPlayerNumber = playerNumber;
	}
	/**
	 * get your own player number for current game
	 * @return
	 */
	public int getMyPlayerNumber(){
		return myPlayerNumber;
	}
	/**
	 * set the number of players in the current game
	 * @param numberOfPlayers
	 */
	public void setNumberOfPLayers(int numberOfPlayers){
		playersInCurrentGame = numberOfPlayers;
	}
	public Lobby getLobby(){
		return lobby;
	}
	/**
	 * send to server that the player has won / lost / left the game
	 */
	public void sendEndGame(){
		NetMessage msg = new NetMessage(GAME, gameName, name, Commands.ENDGAME,
				"");
		sendMessage(msg);
	}
	/**
	 * sen upgrade message to current game
	 * @param cmd
	 */
	public void sendUpgrade(UpgradeCommand cmd){
		String message = "";
		message += cmd.getId();
		message += NetMessage.BATTRIBUTESPLITB;
		message += cmd.getUpgrade().toString();
		message += NetMessage.BATTRIBUTESPLITB;
		message += myPlayerNumber;
		NetMessage msg = new NetMessage(GAME, gameName, name, UPGRADE, message);
		sendMessage(msg);
	}

	public void setPlayerNames(String[] playernames){
		this.playernames= playernames;
	}
	/**
	 * resets game varibles to start
	 */
	public void gameCleanup(){
		gameRound =-1;
		playerCommands = new HashMap<Integer, ArrayList<Command>>();
	}

	public String[] getPlayerNames(){
		return playernames;
	}
}
