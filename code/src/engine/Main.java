package engine;

import javax.swing.UIManager;
import server.NetworkServer;
import gui.Launcher;
import gui.Details;
import client.NetworkClient;

public class Main{
	public static Details Details;
	public static void main(String args[]){
		try{
			UIManager
					.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Details = new Details();
		Boolean x = checkandinsert(args);
		if(x){
			start();
		}else{
			try{
				Launcher Launcher = new Launcher(Details);
				Launcher.setVisible(true);
				while(Launcher.isdone == false){
					try{
						Thread.sleep(10);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				Launcher.dispose();
				start();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * tries to get ChosenStartup, as well as the Details needed, from input and
	 * returns its success
	 * @param input
	 * @return inputisvalide
	 */
	private static boolean checkandinsert(String[] input){
		Boolean inputwassuccesful = false;
		if(input.length > 0){
			if(Details.setChosenStartup(input[0])){
				switch(Details.Choice){
					case Client:
						switch(input.length){
							case 2:
								String adress = input[1];
								String[] parts = adress.split(":");
								if(parts.length==2){
									if(Details.setIP(parts[0])&&Details.setTargetPort(parts[1])){
										inputwassuccesful=true;
									}
								}
								break;
							case 3:
								if(!Details.setLoginName(input[2])){
									inputwassuccesful=true;
								};
								break;
						}
						break;
					case Server:
						if(Details.setServerPort(input[1])){
							inputwassuccesful=true;
						}
						break;
					case Singleplayer:
						inputwassuccesful=true;
						break;
				}
			}
		}
		return inputwassuccesful;
	}
	/**
	 * starts Client/Server/Singleplayer defined by {@link Details}
	 */
	public static void start(){
		switch(Details.Choice){
			case Client:
				clientstart();
				break;
			case Server:
				serverstart();
				break;
			case Singleplayer:
				singleplayerstart();
				break;
		}
	}
	/**
	 * creates client with {@link Details} and runs it
	 */
	static void clientstart(){
		String IP = Details.IP;
		int Port = Integer.valueOf(Details.TargetPort);
		String Loginname = Details.LoginName;
		NetworkClient client = new NetworkClient(IP, Port);
		try{
			Thread.sleep(1500);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		client.run(Loginname);
	}
	/**
	 * creates server with {@link Details} and runs it
	 */
	static void serverstart(){
		int Port = Details.ServerPort;
		NetworkServer server = new NetworkServer(Port);
		server.start();
	}
	/**
	 * creates localengine and start it (=starts Game) great for Debuging
	 */
	static void singleplayerstart(){
		Engine localengine = new Engine();
		localengine.run();
	}
}
