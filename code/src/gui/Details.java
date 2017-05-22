package gui;

import java.util.regex.Pattern;

/**
 * object for datatransport Integer: ServerPort,TargetPort, String: IP,LoginName
 * ChosenStartup: Choice
 */
public class Details{
	public int ServerPort = 25565;
	public int TargetPort = 25565;
	public String IP = "Localhost";
	public String LoginName = System.getProperty("user.name").trim();
	public ChosenStartup Choice = ChosenStartup.valueOf("Client");
	/**
	 * sets the {@link #setChosenStartup(String)} to the given Choice this
	 * method ignors Cases and returns true if the Choice was valide
	 * @param input
	 * @return
	 */
	public Boolean setChosenStartup(String input){
		Boolean inputwasvalide=true;
		if(input.equalsIgnoreCase("server")){
			Choice = ChosenStartup.Server;
		}else{
			if(input.equalsIgnoreCase("client")){
				Choice = ChosenStartup.Client;
			}else{
				if(input.equalsIgnoreCase("singleplayer")){
					Choice = ChosenStartup.Singleplayer;
				}else{
					inputwasvalide = false;
				}
			}
		}
		return inputwasvalide;
	}
	/**
	 * sets the {@link #setChosenStartup(String)} to the given Choice this F*
	 * method ignors Cases and returns true if the Choice was valide
	 * @param input
	 * @return inputwasvalide
	 */
	public Boolean setIP(String input){
		Boolean inputwasvalide=true;
		if(input.equalsIgnoreCase("Localhost")){
			IP = "Localhost";
		}else{
			if(Pattern.matches("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])", input)){
				IP = input;
			}else{
				inputwasvalide = false;
			}
		}
		return inputwasvalide;
	}
	/**
	 * tries to set the Port which the Server will use 
	 * checks whether the value of the string is between 1024 and 49151
	 * @param input
	 * @return inputwasvalide
	 */
	public Boolean setServerPort(String input){
		Boolean inputwasvalide=false;
		int port = Integer.valueOf(input);
		if(1024<=port&&port<=49151){
			ServerPort = port;
			inputwasvalide=true;
		}
		return inputwasvalide;
	}
	/**
	 * tries to set the Port which the Client will contact
	 * checks whether the value of the string is between 1024 and 49151
	 * @param input
	 * @return inputwasvalide
	 */
	public Boolean setTargetPort(String input){
		Boolean inputwasvalide=false;
		int port = Integer.valueOf(input);
		if(1024<=port&&port<=49151){
			TargetPort = port;
			inputwasvalide=true;
		}
		return inputwasvalide;
	}
	public Boolean setLoginName(String loginName){
		loginName=loginName.replace(" ", "");
		if(loginName.equals("")||loginName.equals("SYSTEM")){
			return false;
		}else{
			LoginName = loginName;
			return true;
		}
	}
}
