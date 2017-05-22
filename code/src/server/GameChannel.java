package server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import network.Commands;
import network.NetMessage;
import network.Prefix;

public class GameChannel extends Channel {

	private int maxPlayers = 4;
	private volatile int[] roundnumbers;
	private volatile ArrayList<Boolean> playerready;
	private Boolean inProgress;
	private PrintWriter writer;

	public GameChannel(String channelName) {
		super(channelName);
		roundnumbers = new int[maxPlayers];
		for(int i=0; i<maxPlayers;i++){
			roundnumbers[i]=-1;
		}
		playerready = new ArrayList<Boolean>();
		inProgress = false;
		try {
			writer = new PrintWriter(channelName + ".log", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * adds user only if Game is not already full
	 * 
	 */
	public void addUser(User user) {
		if (!inProgress && this.size() < maxPlayers) {
			if (!users.contains(user)) {	
				users.add(user);
				broadcastMessage(Prefix.GAME, user.getName(), Commands.JOINGAME,
						"");
				sendList(user);
				size++;
				playerready.add(false);
			}
		}
	}

	/**
	 * increase roundnumber of player and brodcasts round done if all players
	 * have completed this round.
	 * 
	 * @param playerName
	 * @param roundnumber
	 */
	public synchronized void increaseRound(String playerName, int roundnumber) {
		if (inProgress) {
			for (int i = 0; i < this.size(); i++) {
				if (this.users.get(i).getName().equals(playerName)) {
					if (playerready.get(i)) {
						roundnumbers[i] = roundnumber;
						for (int j = 0; j < this.size(); j++) {
							if (playerready.get(j)
									&& roundnumbers[j] < roundnumber) {
								return;
							}
						}
						broadcastMessage(Prefix.GAME, playerName,
								Commands.ROUNDDONE, String.valueOf(roundnumber));
						return;
					}
				}
			}
		}
	}

	/**
	 * stets player ready and starts the game if all players are ready
	 * 
	 * @param playerName
	 */
	public synchronized void toggleReady(String playerName) {
		if(!inProgress){
		for (int i = 0; i < this.size(); i++) {
			if (this.users.get(i).getName().equals(playerName)) {
				playerready.set(i, !playerready.get(i)); // toggle
				broadcastMessage(Prefix.GAME, playerName, Commands.PLAYERREADY,
						playerready.get(i) ? "TRUE" : "FALSE");
				for (int j = 0; j < this.size(); j++) { // is everyone ready?
					if (!playerready.get(j)) {
						return;// return if anyone is not ready yet
					}
				}
				NetMessage msg = new NetMessage(Prefix.GAME, channelName,
						playerName, Commands.STARTGAME, "");
				String players = "";
				User c;
				for (int u = 0; u < users.size(); ++u) {
					c = users.get(u);
					players += c.getName();
					players += " ";
				}
				players = players.trim();
				
				inProgress = true;
				for (int u = 0; u < users.size(); ++u) {
					c = users.get(u);
					c.sendMessage(msg.toString() + u
							+ NetMessage.BATTRIBUTESPLITB + users.size() +NetMessage.BATTRIBUTESPLITB+players);
				}
				return;
			}
		}
		}
	}

	synchronized public void delPerson(User user) {
		try {
			for (int i = 0; i < users.size(); ++i) {
				User c = users.get(i);
				if (c.equals(user)) {
					if (inProgress && playerready.get(i)) {
						playerGameEnd(user.getName());
					}
					playerready.remove(i);
					broadcastMessage(Prefix.GAME, user.getName(),
							Commands.LEAVEGAME, "");
					users.remove(c);
					size--;
				}
			}
		} catch (Exception e) {
			System.out.println("end...");
		}
	}

	/**
	 * builds message from input and sends to all clients in the channel
	 * 
	 * @param prefix
	 *            chat or game
	 * @param sender
	 * @param message
	 **/
	synchronized public void broadcastMessage(Prefix prefix, String sender,
			Commands command, String message) {
		sender = sender.trim();
		for (User user : users) {
			if (user.getName().equals(sender)) {
				if (command == Commands.MOVEUNIT || command == Commands.ROUNDDONE) {
					NetMessage msg = new NetMessage(prefix, channelName,
							sender, command, message);
					writer.println(msg.toString());
					writer.flush();
				}
				super.broadcastMessage(prefix, sender, command, message);
				return;
			}
		}

	}

	/**
	 * the player has won / lost / left the game
	 * does not stop messages to player
	 * @param playerName
	 */
	synchronized public void playerGameEnd(String playerName) {
		for (int i = 0; i < this.size; i++) {
			if (this.users.get(i).getName().equals(playerName)) {
				playerready.set(i, false);
				break;
			}
		}
		for (int i = 0; i < this.size; i++) {
			if (playerready.get(i)) {
				increaseRound(users.get(i).getName(), roundnumbers[i]);
				return;
			}
		}
		cleanUp();
	}

	/**
	 * resets the game
	 */
	synchronized private void cleanUp() {
		inProgress = false;
		for (int i = 0; i < size; i++) {
			playerready.set(i, false);
			roundnumbers[i]=-1;
		}
	}
}
