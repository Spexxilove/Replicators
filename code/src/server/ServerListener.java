package server;

import static network.Prefix.*;
import static network.Commands.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
import java.util.HashMap;
import network.*;

public class ServerListener extends Thread {

	private Socket clientSocket = null;
	private String cliAddr;
	private User user;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Date date = new Date();
	private HashMap<String, Channel> channels = null;
	private Channel activeChannel;
	private int port;
	private PingChecker serverPing;
	private ArrayList<GameChannel> gameChannels;

	public ServerListener(Socket socket, HashMap<String, Channel> channels,
			PingChecker serverPing, ArrayList<GameChannel> gameChannels) {
		this.clientSocket = socket;
		this.cliAddr = clientSocket.getInetAddress().getCanonicalHostName();
		this.channels = channels;
		this.port = clientSocket.getLocalPort();
		this.serverPing = serverPing;
		this.gameChannels = gameChannels;
		serverPing.addClient(this);
		println(date.toString() + " Client connection from " + cliAddr);
	}

	/**
	 * thread entry point
	 */
	@Override
	public void run() {
		try {
			// Open IO streams from socket
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			processClient(); // interact with client

			cleanUp(); // client has finished when execution reaches here
			// close clientSocket
			clientSocket.close();
			serverPing.removeClient(this);
			println(date.toString() + " Client(" + cliAddr
					+ ") connection closed");
			sendChannelList(new NetMessage(null,null,getClientName(),null,null));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * processes incoming messages from the client
	 */
	private void processClient() {
		String line;
		try {
			// wait for name request. thou shall not pass
			while ((line = in.readLine()) != null) {
				NetMessage in = new NetMessage(line);
				if (in.COMMAND == POKE) {
					sendPong(in);
				} else if (in.COMMAND == CHANGENAME) {
					if (addName(in)) {
						break;
					}
					println("Name not accepted" + line); /*---SYSOUT---*/
				}
			}
			while ((line = in.readLine()) != null) {
				NetMessage in = new NetMessage(line);
				switch (in.PREFIX) { // might be an issue (!.equals())
				case CHAT:
					//println(line);
					processChatMessage(in); /*---DEBUG---*/
					break;
				case GAME:
					//println(line);
					processGameMessage(in); /*---DEBUG---*/
					break;
				case PING:
					sendPong(in);
					break;
				default:
					println("message :" + line + " has  invalid prefix");
					break;
				}
			}
		} catch (IOException e) { // if client kills process //TODO timeout
									// serverside on ping
			println("---LOST CONNECTION TO---: " + user == null ? "" : user
					.getName()); /*---DEBUG---*/
		}
	} // end of processClient

	/**
	 * processes incoming message with prefix chat
	 * 
	 * @param messageIn
	 */
	private void processChatMessage(NetMessage in) {
		switch (in.COMMAND) {
		case JOIN:
			sendChannelList(in);
			joinChannel(in);
			break;
		case SEND:
			sendChatMessage(in);
			break;
		case LEAVE:
			userLeaveChannel(in);
			sendChannelList(in);
			break;
		case CHANNELLIST:
			sendChannelList(in);
			break;
		default:
			println("---NO KNOWN COMMAND---");
			break;
		}
	}

	/**
	 * processes incoming message with prefix game
	 * 
	 * @param line
	 */
	private void processGameMessage(NetMessage in) {
		switch (in.COMMAND) {
		case JOINGAME:
			joinGame(in);
			sendChannelList(in);
			break;
		case LEAVEGAME:
			userLeaveGame(in);
			sendChannelList(in);
			break;
		case CREATEGAME:
			createGame(in);
			sendChannelList(in);
			break;
		case MOVEUNIT:
			moveUnit(in);
			break;
		case ROUNDDONE:
			increaseRound(in);
			break;
		case PLAYERREADY:
			setPlayerReady(in);
			break;
		case GAMELIST:
			sendGameList(in);
			sendChannelList(in);
			break;
		case ENDGAME:
			endGame(in);
			break;
		case UPGRADE:
			upgrade(in);
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
	private void setPlayerReady(NetMessage in) {
		Channel ch = channels.get(in.CHANNEL);
		if (ch != null && ch.getClass() == GameChannel.class) {
			((GameChannel) ch).toggleReady(in.SENDER);
		}

	}

	/**
	 * process round done message
	 * 
	 * @param in
	 */
	private void increaseRound(NetMessage in) {
		Channel ch = channels.get(in.CHANNEL);
		if (ch != null && ch.getClass() == GameChannel.class) {
			((GameChannel) ch).increaseRound(in.SENDER,
					Integer.parseInt(in.MESSAGE));
		}
	}

	/**
	 * process move command
	 * 
	 * @param in
	 */
	private void moveUnit(NetMessage in) {
		Channel ch = channels.get(in.CHANNEL);
		if (ch != null && ch.getClass() == GameChannel.class) {
			((GameChannel) ch).broadcastMessage(in.PREFIX, in.SENDER,
					in.COMMAND, in.MESSAGE);
		}
	}

	/**
	 * returns client socket
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return clientSocket;
	}

	/**
	 * returns client address
	 * 
	 * @return
	 */
	public String getAddr() {
		return cliAddr;
	}

	/**
	 * send ping answer
	 * @param in
	 */
	private void sendPong(NetMessage in) {
		long tmpTime = System.currentTimeMillis();
		String nowTime = String.valueOf(tmpTime);
		// long clientTime = Long.parseLong(in[1].trim()); //TODO display ping
		// in overlay
		// System.out.println("Ping: " + user.getName() + " "
		// + String.valueOf(tmpTime - clientTime));
		sendMessage(PONG, "MAIN", "server", POKE, nowTime);
		serverPing.setPing(this);
	}

	/**
	 * user leaves channel
	 * 
	 * @param channel
	 * @param sender
	 */
	private void userLeaveChannel(NetMessage in) {
		if (!in.CHANNEL.equals("MAIN")) {
			if (channels.containsKey(in.CHANNEL)) {
				channels.get(in.CHANNEL).delPerson(user);
			}
		}

	}

	/**
	 * adds sender to channel if channel exists otherwise creates new channel
	 * 
	 * @param channelName
	 * @param sender
	 */
	private void joinChannel(NetMessage in) {
		if (!in.MESSAGE.trim().contains(" ")) {
			if (channels.containsKey(in.MESSAGE)) { // does channel exist?
				activeChannel = channels.get(in.MESSAGE);
				activeChannel.addUser(user); // add //
				return;

			} else {
				Channel newChannel = new Channel(in.MESSAGE);
				channels.put(in.MESSAGE, newChannel);
				activeChannel = newChannel;
				activeChannel.addUser(user);
				channels.get("MAIN").broadcastMessage(CHAT, in.SENDER,
						NEWCHANNEL, in.MESSAGE);
			}
		}
	}

	public String getClientName(){
		return user.getName();
	}
	
	/**
	 * send channel list with userlist to user requesting it
	 * 
	 * @param in
	 */
	private void sendChannelList(NetMessage in) {
		String message = "";
		Iterator<Map.Entry<String, Channel>> it = channels.entrySet()
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, Channel> entry = it.next();

			message += entry.getValue().getName();
			message += ":";
			message += entry.getValue().getUserNames();
			if (it.hasNext()) {
				message += NetMessage.BATTRIBUTESPLITB;
			}
		}
		NetMessage msg = new NetMessage(CHAT, "MAIN", "SYSTEM", CHANNELLIST,
				message);
		channels.get("MAIN").broadcastMessage(msg.PREFIX, msg.SENDER, msg.COMMAND, msg.MESSAGE);
	}

	/**
	 * processes message to create the user with the sent name
	 * 
	 * @param message
	 * @return true if the change was successful false if name is already in use
	 */
	private boolean addName(NetMessage in) {
		Channel mainChannel = channels.get("MAIN");
		String name = in.SENDER;
		if (mainChannel.containsName(name)) {
			int i=1;
			while (mainChannel.containsName(name+i)){
				i++;
			}
			name = name+i;
		}
			this.user = new User(name, cliAddr, port, out);
			sendMessage(CHAT, "MAIN", "SERVER", SETNAME, name);
			return true;
		
	}

	/**
	 * Broadcasts a chat message to all clients in the channel
	 * 
	 * @param channelName
	 * @param sender
	 * @param message
	 */
	private void sendChatMessage(NetMessage in) {
		if (channels.containsKey(in.CHANNEL)) {
			activeChannel = channels.get(in.CHANNEL);
			activeChannel.broadcastMessage(CHAT, in.SENDER, SEND, in.MESSAGE);
		}
	}

	/**
	 * sends message to client
	 * 
	 * @param prefix
	 *            chat or game
	 * @param channel
	 * @param message
	 */
	public void sendMessage(Prefix prefix, String channel, String sender,
			Commands command, String message) {
		NetMessage outMsg = new NetMessage(prefix, channel, sender, command,
				message);
		// System.out.println("sendMessage: "+sendMessage); /*---DEBUG---*/
		out.println(outMsg.toString());
	}

	/**
	 * creates a game
	 * 
	 * @param in
	 */
	private void createGame(NetMessage in) {
		if (channels.containsKey(in.MESSAGE)) {
			// name already in use
		} else {
			Channel newChannel = new GameChannel(in.MESSAGE);
			channels.put(in.MESSAGE, newChannel);
			activeChannel = newChannel;//TODO Spexx is this here necessary?
			
			channels.get("MAIN").broadcastMessage(GAME, in.SENDER, NEWGAME,
					in.MESSAGE);
			newChannel.addUser(user);
			gameChannels.add((GameChannel) newChannel);
		}
	}

	/**
	 * user leaves a game
	 * 
	 * @param in
	 */
	private void userLeaveGame(NetMessage in) {
		if (channels.containsKey(in.CHANNEL)
				&& channels.get(in.CHANNEL).getClass()
						.equals(GameChannel.class)) {
			channels.get(in.CHANNEL).delPerson(user);
		}
	}

	/**
	 * user joins a game
	 * 
	 * @param in
	 */
	private void joinGame(NetMessage in) {
		if (!in.CHANNEL.trim().contains(" ")) {
			if (channels.containsKey(in.CHANNEL)
					&& channels.get(in.CHANNEL).getClass()
							.equals(GameChannel.class)) {
				channels.get(in.CHANNEL).addUser(user);
			}
		}
	}

	/**
	 * leaves all channels
	 */
	synchronized private void cleanUp() {
		Iterator<Map.Entry<String, Channel>> it = channels.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Channel> entry = it.next();
			entry.getValue().delPerson(user);
		}
	}

	/**
	 * send list of gamenames
	 * @param in
	 */
	private void sendGameList(NetMessage in) {
		String message = "";
		for (GameChannel ch : gameChannels) {
			if (message.equals("")) {
				message += ch.getName();
			} else {
				message += NetMessage.BATTRIBUTESPLITB + ch.getName();
			}
		}
		sendMessage(GAME, "MAIN", user.getName(), GAMELIST, message);
	}

	private void println(String Message) {
		gui.ServerInterface.print(Message);
	}

	/**
	 * send upgrade command
	 * @param in
	 */
	private void upgrade(NetMessage in) {
		Channel ch = channels.get(in.CHANNEL);
		if (ch != null && ch.getClass() == GameChannel.class) {
			((GameChannel) ch).broadcastMessage(in.PREFIX, in.SENDER,
					in.COMMAND, in.MESSAGE);
		}
	}

	/**
	 * send End of Game command
	 * @param in
	 */
	private void endGame(NetMessage in) {
		Channel ch = channels.get(in.CHANNEL);
		if (ch != null && ch.getClass() == GameChannel.class) {
			((GameChannel) ch).playerGameEnd(in.SENDER);
		}	
	}
}// End of ServerListener

