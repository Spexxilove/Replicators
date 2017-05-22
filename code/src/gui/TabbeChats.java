package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.swing.JTabbedPane;
import javax.swing.text.SimpleAttributeSet;

/**
 * Creates and manages Channels in tabs
 * 
 * @author Severin
 *
 */
public class TabbeChats extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private static Channel Main = new Channel("MAIN");
	private static HashMap <String,Integer> Channels = new HashMap <String,Integer>();
	private String activeGame = "";
	private int tabs=0;

	/**
	 * Constructor,
	 * creates first tab with name GlobalChat
	 */
	public TabbeChats() {
		insertTab("Global",null,Main,"GlobalChat",tabs);
		tabs++;
		Channels.put("MAIN", 0);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * returns the HashMap of all Channels added and the indexes of their tabs
	 * @return {@link #Channels}
	 */
	public HashMap <String,Integer> getopenedChannels(){
		return Channels;
	}
	
	/**
	 * overwrites {@link #activeGame} and calls {@link #addChannel(String)}, both with gameName 
	 * @param gameName
	 */
	public void addGame(String gameName) {
		if (activeGame.equals("")) {
			addChannel(gameName);
			activeGame = gameName;
		} else {
			//TODO maybe remove the old tap
			addChannel(gameName);
			activeGame = gameName;
		}
	}

	/**
	 * inserts new tab with given name
	 * @param channelName
	 */
	public void addChannel(String channelName) {
		if (channelName.equals("Main")||channelName.equals("Global")) {
			channelName = "MAIN";
		}
		if (Channels.containsKey(channelName)) {
			Channel temp =(Channel)getComponentAt(Channels.get(channelName));
			temp.setactiv(true);
		} else {	
			try {
				insertTab(channelName, null, new Channel(channelName), null, tabs);
				//TODO move activision into own method
				Channel temp =(Channel)getComponentAt(tabs);
				setSelectedComponent(temp);
				temp.setactiv(true);
				Channels.put(channelName, tabs);
				tabs++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * sets {@link #activeGame} to Default (empty)
	 * @param gameName
	 */
	public void leaveGame(String gameName){
		if(gameName.equals(activeGame)){
			activeGame="";
		}else{
			System.out.println("you tried to leave a game that is not the active Game!!!!");
		}
	}
	
	/**
	 * {@link Channel#setactiv(Boolean)} to false
	 * @param channel
	 */
	public void leaveChannel(String channel){
		Channel leftChannel = (Channel)getComponentAt(Channels.get(channel));
		leftChannel.setactiv(false);
	}
	
	/**
	 * returns name of the focused tab
	 * @return nameselectedtab
	 */
	public String getSelectedName() {
		return getSelectedComponent().getName();
	}

	/**
	 * returns Boolean stating weather a Game is active.
	 * @return isthereactiveGame
	 */
	public Boolean hasactivGame(){
		if (!activeGame.equals("")){
			return true;
		}
		return false;
	}
	
	/**
	 * returns String {@link #activeGame}
	 * @return {@link #activeGame}
	 */
	public String getactiveGame(){
		return activeGame;
	}
	
	/**
	 * calls {@link Channel#print(String)} of channel, passing input
	 * @param input
	 * @param channel
	 */
	public void print(String input, String channel,SimpleAttributeSet stylemode) {
		if (channel.equals("Main")) {
			channel = "MAIN";
		}
		int channelindex = Channels.get(channel);
		try {
			getChannelAt(channelindex).println(input,stylemode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print(String sender,String input, String channel,SimpleAttributeSet stylemode) {
		if (channel.equals("Main")) {
			channel = "MAIN";
		}
		int channelindex = Channels.get(channel);
		try {
			getChannelAt(channelindex).println(sender,input,stylemode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * gets ComponentAt(index) and casts it to Channel
	 * @param index
	 * @return tabedChannelAt(index)
	 */
	private Channel getChannelAt(int index) {
		Channel requestedChannel = (Channel) getComponentAt(index);
		return requestedChannel;
	}
	
	/**
	 * returns an ArrayList<String> containing all Channels that exist as a Tab but are currently inactive
	 * @param listToCheck
	 * @return inactivchannels
	 */
	public List<String> findinactivChannels(List<String> listToCheck){
		ArrayList<String> inactivchannels = new ArrayList <String>();
		String temp = new String();
		for(int i = 0;i<listToCheck.size();i++){
			temp = listToCheck.get(i);
			if(!(temp.equals("MAIN")||temp.trim().equals(""))){
				if(Channels.containsKey(temp)){
					Channel existenter = (Channel)getComponentAt(Channels.get(temp));
					if(existenter.getactive()){
					}else{
						inactivchannels.add(listToCheck.get(i));
					}
				}else{
					inactivchannels.add(listToCheck.get(i));
				}
			}
		}
		return inactivchannels;
	}
	
	/**
	 * returns an ArrayList<String> containing all Channels that exist as a Tab and are active, except for Main (Globalchannel)!
	 * @return leaveablechannels
	 */
	public ArrayList<Channel> getleaveableChannels(List<String> games){
		ArrayList<Channel> leaveablechannels = new ArrayList <Channel>();
		for(int i = 0; Channels.containsValue(i);i++){
			try{
			Channel temp =(Channel) getComponentAt(i); 
			if(!(temp.getName()=="MAIN")&&temp.getactive()&&!(games.contains(temp.getName()))){
				leaveablechannels.add(temp); 
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return leaveablechannels; 
	}

	public void jumptoChannel(String channel){
		int channelindex = Channels.get(channel);
		setSelectedIndex(channelindex);
	}
}
