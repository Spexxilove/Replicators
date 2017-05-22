package client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import network.*;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import javax.swing.text.SimpleAttributeSet;
import org.lwjgl.util.vector.Vector3f;
import engine.Engine;
import engine.Game;
import engine.Upgrades;
import gui.Formatation;
import gui.Lobby;
import client.NetworkClient;

// listens to incoming messages and executes commands
public class ClientListener extends Thread {
	private int TIMEOUT = 10000;
	private BufferedReader in = null;
	private NetworkClient client = null;
	private Engine engine;
	private boolean running = true;
	private PrintWriter logWriter;
	private Lobby lobby;

	public ClientListener(NetworkClient client, BufferedReader in, Lobby lobby) {
		this.client = client;
		this.in = in;
		this.lobby = lobby;
		try {
			logWriter = new PrintWriter("client.log", "UTF-8");
		} catch (Exception e) {
			System.out.println("Couldn't open printwriter");
		}
	}

	@Override
	public void run() {
		try {
			String line;
			client.sock.setSoTimeout(TIMEOUT); // TODO this forces server to
												// handle reconnect
			while (running && (line = in.readLine()) != null) {
				// System.out.println("line is: "+line+"-----------------");
				/*if(!line.contains("PONG")){
					System.out.println(line);
				}*/
				NetMessage in = new NetMessage(line);
				switch (in.PREFIX) {
				case CHAT:
					// System.out.println(line); /*---DEBUG---*/
					processChatMessage(in);
					break;
				case GAME:
					// System.out.println(line); /*---DEBUG---*/
					processGameMessage(in);
					break;
				case PONG:
					processPong(in);
					break;
				default:
					System.out.println("message :" + line
							+ " has  invalid prefix");
				}
			}
		} catch (SocketTimeoutException e) {
			print("---CONNECTION LOST---", "Main", Formatation.system);
			
			try {
				client.sock.close();
				playerdisconnected(client.getClientName());
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			print("Lost Connection to Server", "Main",Formatation.system);
			playerdisconnected(client.getClientName());
		}
	}

	/**
	 * processes incoming message with prefix chat
	 * 
	 * @param messageIn
	 */
	private void processChatMessage(NetMessage in) {
		switch (in.COMMAND) {
		case JOIN:
			joinChannel(in.CHANNEL, in.SENDER);
			break;
		case SEND:
			showChatMessage(in.CHANNEL, in.SENDER, in.MESSAGE);
			break;
		case REQUESTNAME:
			requestNewName();
			break;
		case SETNAME:
			setNewName(in.MESSAGE);
			break;
		case USERLIST:
			processUserList(in.CHANNEL, in.MESSAGE);
			break;
		case NEWCHANNEL:
			processNewChannelOpened(in.SENDER, in.MESSAGE);
			break;
		case LEAVE:
			processUserLeaveChannel(in.CHANNEL, in.SENDER);
			break;
		case CHANNELLIST:
			processChannelList(in);
			break;
		default:
			break;
		}
	}

	/**
	 * processes incoming message with prefix game
	 * 
	 * @param line
	 */
	private void processGameMessage(NetMessage in) {
		logWriter.println(in.toString()); // write message to file for debugging
		logWriter.flush();
		switch (in.COMMAND) {
		case JOINGAME:
			processJoinGame(in.CHANNEL, in.SENDER);
			break;
		case LEAVEGAME:
			userLeaveGame(in.CHANNEL, in.SENDER);
			break;
		case NEWGAME:
			// System.out.println("recived newgame:   "+in.MESSAGE);
			createGame(in.MESSAGE, in.SENDER);
			break;
		case ROUNDDONE:
			roundDone(in.MESSAGE);
			break;
		case MOVEUNIT:
			moveUnit(in);
			break;
		case UPGRADE:
			upgrade(in);
			break;
		case STARTGAME:
			startGame(in);
			break;
		case PLAYERREADY:
			processPlayerReady(in);
			if (in.SENDER.equals(client.getClientName())) {
				if (in.MESSAGE.contains("TRUE")) {
					lobby.setReadyState(true);
				} else {
					lobby.setReadyState(false);
				}
			}
			break;
		case GAMELIST:
			processGameList(in);
			break;
		default:
			break;
		}
	}

	/**
	 * process player ready command
	 * 
	 * @param in
	 */
	private void processPlayerReady(NetMessage in) {
		print("player " + in.SENDER + " has set readystate to: " + in.MESSAGE,
				in.CHANNEL, Formatation.game);
	}

	/**
	 * process start game message
	 * 
	 * @param in
	 */
	private void startGame(NetMessage in) {
		String[] playernumbers = in.MESSAGE.split(NetMessage.BATTRIBUTESPLITB);
		try {
			client.setMyPlayerNumber(Integer.parseInt(playernumbers[0]));
			client.setNumberOfPLayers(Integer.parseInt(playernumbers[1]));
			String[] players = playernumbers[2].split(" ");
			client.setPlayerNames(players);
		} catch (Exception e) {
			print("wrong playernumbers", in.CHANNEL, Formatation.system);
		}
		if (in.CHANNEL.equals(client.gameName)) {
			client.gameCleanup();
			engine = new Engine(client, lobby);
			engine.start();
		}
	}

	/**
	 * process movecommand
	 * 
	 * @param in
	 */
	private synchronized void moveUnit(NetMessage in) {
		String[] msg = in.MESSAGE.split(NetMessage.BATTRIBUTESPLITB);
		try {
			int id = Integer.parseInt(msg[0]);
			int playerId = Integer.parseInt(msg[3]);
			String[] uIDsString = msg[1].split(" ");
			int[] unitIds = new int[uIDsString.length - 1];
			for (int i = 0; i < unitIds.length; i++) {
				unitIds[i] = Integer.parseInt(uIDsString[i + 1]);
			}
			String[] targetCoordsString = msg[2].split(" ");
			Vector3f moveTarget = new Vector3f(
					Float.parseFloat(targetCoordsString[0]),
					Float.parseFloat(targetCoordsString[1]),
					Float.parseFloat(targetCoordsString[2]));
			addCommand(new MoveCommand(id, playerId, unitIds, moveTarget));
		} catch (Exception e) {
			print("invalid movecommand " + in.toString(), "Main", Formatation.key);// TODO
																					// send
			// into Game
		}
	}

	/**
	 * process upgrade command
	 * 
	 * @param in
	 */
	private void upgrade(NetMessage in) {
		String[] msg = in.MESSAGE.split(NetMessage.BATTRIBUTESPLITB);
		try {
			int id = Integer.parseInt(msg[0]);
			Upgrades upgrade = Upgrades.valueOf(msg[1]);
			int playerId = Integer.parseInt(msg[2]);
			addCommand(new UpgradeCommand(id, playerId, upgrade));
		} catch (Exception e) {
			print("invalid movecommand " + in.toString(), "Main", Formatation.key);// TODO
																					// send
			// into Game
		}
	}

	/**
	 * adds command to execution list
	 * 
	 * @param command
	 */
	private synchronized void addCommand(Command command) {
		ArrayList<Command> commands = client.playerCommands
				.get(command.getId());
		if (commands == null) {
			commands = new ArrayList<Command>();
			client.playerCommands.put(command.getId(), commands);
		}
		commands.add(command);
	}

	/**
	 * process rounddone message
	 * 
	 * @param roundnumber
	 */
	private synchronized void roundDone(String roundnumber) {
		client.setRound(Integer.parseInt(roundnumber));
	}

	/**
	 * processes information that a user left the channel
	 * 
	 * @param channel
	 * @param sender
	 */
	private void processUserLeaveChannel(String channel, String sender) {
		lobby.processleaveChannel(channel, sender);
	}

	/**
	 * informs user that a new channel has been opened
	 * 
	 * @param sender
	 * @param channelName
	 */
	private void processNewChannelOpened(String sender, String channelName) {
		print(sender + " has opened the channel " + channelName, "Main",
				Formatation.system);
		try {
			// System.out.println("232 is accessing addChannel");
			lobby.addChannel(channelName, sender);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * proccess networkmessage to channellist
	 * 
	 * @param in
	 */
	private void processChannelList(NetMessage in) {
		// TODO remove here what is not needed
		String message = new String();
		message = in.MESSAGE.replace("MAIN", "Global");// TODO don't do that,
														// remove caused ifs and
														// make replacement
														// deeper within
		lobby.processChannelList(message, in.SENDER);
		String[] channels = in.MESSAGE.split(NetMessage.BATTRIBUTESPLITB);
		ArrayList<ArrayList<String>> nameLists = new ArrayList<ArrayList<String>>();
		ArrayList<String> channelNames = new ArrayList<String>();
		for (String str : channels) {
			ArrayList<String> tempList = new ArrayList<String>();
			String[] tempString = str.split(" ");
			channelNames.add(tempString[0]);
			for (int i = 1; i < tempString.length; i++) {
				tempList.add(tempString[i]);
			}
			nameLists.add(tempList);
		}
		// System.out.println("ChannelNames: "+channelNames.toString());
		// TO SEVERIN DO WITH THIS WHATEVER U WANT AND THEN DELETE THIS COMMENT
		// WITH EVERYTHING BELOW---
		for (int i = 0; i < channelNames.size(); i++) {
			String outp = "";
			outp += channelNames.get(i);
			outp += ": ";
			for (int j = 0; j < nameLists.get(i).size(); j++) {
				outp += nameLists.get(i).get(j);
				outp += ", ";
			}
			// ----------------------------------------------------------------------------------------------
			// System.out.println("Members in Channel "+channelNames.get(i)+" are: "+outp);
			// lobby.addChannel(channelNames.get(i), "admin");
		}
	}

	/**
	 * processes list of users in a channel
	 * 
	 * @param channel
	 * @param message
	 */
	private void processUserList(String channel, String message) {
		// lobby.processChannelList(message); delets everything
		String[] users = message.split("\\| \\|");
		users[0] = users[0].replaceFirst("\\|", "");
		String lastEntry = users[users.length - 1].trim();
		users[users.length - 1] = lastEntry
				.substring(0, lastEntry.length() - 1);
		if (channel.equals(client.gameName)) {
			String outputMsg = "the players in the game " + channel + " are: ";
			for (String username : users) {
				outputMsg += username + " , ";
			}
			print(outputMsg, channel, Formatation.game);
		} else {
			String outputMsg = "the users in the channel " + channel + " are: ";
			for (String username : users) {
				outputMsg += username + " , ";
			}
			lobby.addChannel(channel, "Admin");
			try {
				Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
			}
			print(outputMsg, channel, Formatation.system);
		}
	}

	/**
	 * sets user name if it is not already set
	 * 
	 * @param name
	 */
	private void setNewName(String name) {
		if (client.getClientName() == "") {
			client.setClientName(name);
			// to be done in Lobby somehow... print("welcome " + name,"Main");
		}
	}

	/**
	 * requests new name if no name is set
	 */
	private void requestNewName() {
		print("the name is already in use.", "Main", Formatation.system);
	}

	/**
	 * informs user that a new user has entered the channel
	 * 
	 * @param channel
	 * @param sender
	 */
	private void joinChannel(String channel, String sender) {
		/*
		 * if(sender.equals(client.getClientName())){ lobby.add(comp) }
		 * print(sender + " has entered channel " + channel,channel);
		 */
		lobby.processjoinChannel(channel, sender);
		client.processUserInput("/getGames");
		client.processUserInput("/getChannels");
	}

	/**
	 * prints chat message to the window
	 * 
	 * @param channel
	 * @param sender
	 * @param message
	 */
	private void showChatMessage(String channel, String sender, String message) {
		// print("@" + channel + " " + sender + ": " + message);
		print(sender + ":"," "+ message, channel, Formatation.normal);
		//TODO this shall use the special for it designet output
	}

	/**
	 * processes information about new created game
	 * 
	 * @param gameName
	 * @param sender
	 */
	private void createGame(String gameName, String sender) {
		print(sender + " has created a new game: " + gameName, "Main",
				Formatation.system);
		// lobby.addGame(gameName);
		lobby.processnewGame(gameName, sender);
	}

	/**
	 * processes information about a player leaving a game
	 * 
	 * @param channel
	 * @param sender
	 */
	private void userLeaveGame(String channel, String sender) {
		lobby.processleaveGame(channel, sender);
		playerdisconnected(sender);
	}

	/**
	 * processes information about a player joining your game
	 * 
	 * @param channel
	 * @param sender
	 */
	private void processJoinGame(String channel, String sender) {
		lobby.processjoinGame(channel, sender);
		try {
			Thread.sleep(30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * process Pong message
	 * 
	 * @param in
	 */
	private void processPong(NetMessage in) {
	}

	/**
	 * ^M proccess networkmessage to gamelist^M
	 * 
	 * @param in
	 *            ^M
	 */
	private void processGameList(NetMessage in) {
		String[] games = in.MESSAGE.split(NetMessage.BATTRIBUTESPLITB);
		for (int i = 0; i < games.length; i++) {
			lobby.addGame(games[i]);
		}
		;
	}

	/**
	 * end client listener
	 */
	public void end() {
		running = false;
	}

	/**
	 * calls print(message,channel) from {@link Lobby#print(String, String)}
	 * first input is the message to be send to the channel (second input)
	 * 
	 * @param message
	 * @param channel
	 */
	private void print(String message, String channel, SimpleAttributeSet stylemode) {
		try {
			lobby.print(message, channel, stylemode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void print(String sender,String message, String channel, SimpleAttributeSet stylemode) {
		try {
			lobby.print(sender,message, channel, stylemode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void playerdisconnected(String playername) {
		if (engine != null && engine.isAlive()) {
			String[] names = client.getPlayerNames();
			for (int i = 0; i < names.length; i++) {
				if (playername.equals(names[i])) {
					Game.kickPlayer(i);
				}
			}
		}
	}
}
