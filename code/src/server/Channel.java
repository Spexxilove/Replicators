package server;

import static network.Commands.*;
import network.*;

import java.util.ArrayList;

import network.NetMessage;
import static network.Prefix.*;

/**
 * contains a group of people 
 * distributes messages to all people in the channel
 */
public class Channel {
	 ArrayList<User> users=null;
	protected int size = 0;
	protected String channelName;
	
	public Channel(String channelName){
		this.channelName = channelName; 
		users = new ArrayList<User>();
	}
	
	/**
	 *	returns a String with all usernames in the channel 
	 *	format for netmessage 
	 **/
	synchronized public String getUserNames(){
		String names = ""; 
		for(int i=0; i<size;i++){
			names += (" "+users.get(i).getName());
			
		}
		names = names.trim();
		return names;
	}
	
	/**
	 * add person to channel
	 *@param user
	 */
	synchronized public void addUser(User user){
		if(!users.contains(user)){
			users.add(user);
			size++;
			broadcastMessage(Prefix.CHAT, user.getName(),Commands.JOIN,"");
			//sendList(user);
		}
	}
	
	/**
	 * delete person from channel
	 * @param user
	 */
	synchronized public void delPerson(User user){
		try{
			for(int i=0;i<users.size();++i){
				User c = users.get(i);
				if(c.equals(user)){
					broadcastMessage(CHAT, user.getName(),LEAVE,"");
					users.remove(c);
					size--;
				} 
			}
		}catch(Exception e){
			System.out.println("end...");
		}
	}
	
	/**
	 * send message to all clients in the channel
	 * @param msg
	 */
	synchronized private void broadcast(String msg){
		User c;
		for(int i=0;i<users.size();++i){
			c = users.get(i);
			c.sendMessage(msg);
		}
	}
	
	/**
	 * sends list of clients in the Channel to a client
	 * @param user
	 */
	protected synchronized void sendList(User user){
		String message = "";
		for(int i=0;i<users.size();++i){
			User u = users.get(i);
			message += "|"+u.getName()+"| ";
		}
		NetMessage out = new NetMessage(CHAT,channelName,"SERVER",USERLIST,message);
		user.sendMessage(out.toString());
	}
	
	/**
	 * get name of channel
	 * @return
	 */
	synchronized public String getName(){
		return channelName;
	}
	
	/**
	 * returns number of people in channel
	 * @return
	 */
	synchronized public int size(){
		return size;
	}
	
	/**
	 * builds message from input and sends to all clients in the channel
	 * @param prefix chat or game
	 * @param sender
	 * @param message
	 */
	synchronized public void broadcastMessage(Prefix prefix, String sender,Commands command,String message){
		sender = sender.trim();
		for(User user : users){
			if (user.getName().equals(sender)||sender.equals("SYSTEM")){
				String out = new NetMessage(prefix,channelName,sender,command,message).toString();
				broadcast(out);
				return;
			}
		}
		
	}

	/**
	 * checks if the name is already part of the channel
	 * @param name
	 * @return
	 */
	synchronized public boolean containsName(String name){
		for(User person : users ){
			if (person.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
}