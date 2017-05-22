package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import gui.ServerInterface;

public class NetworkServer extends Thread{
	private HashMap<String,Channel> channels=null;
	public ArrayList<GameChannel> gameChannels;
	public PingChecker serverPing;
	public ServerInterface ServerInterface;
	
	public NetworkServer(int port){		
		//common object for chat group,accessed only by synchronized methods
		channels = new HashMap<String,Channel>();
		channels.put("MAIN", new Channel("MAIN"));
		ServerInterface = new ServerInterface();
		
		try{
			printf("%s","Initializing...: ");
			ServerSocket serverSocket = new ServerSocket(port);
			printf("%s\n","Port assigned, Ready.");
			Socket clientSocket;
			//channels.put("MAIN", new Channel("MAIN")); /*-----WHY?-----*/
			gameChannels = new ArrayList<GameChannel>();
			boolean done = false;
			serverPing = new PingChecker();
			serverPing.start();
			while(!done){
				//waiting for clients
				clientSocket = serverSocket.accept();
				
				//starts a new Thread for each Client connection
				new ServerListener(clientSocket,channels,serverPing,gameChannels).start();	
			}
			
			serverSocket.close();
			println("Socket closed, exiting");
			System.out.println("Socket closed, exiting");  /*----DEBUG---->>>  why does the programme never get here??*/
			
		}catch(Exception e){
			printf("%s\n",e.getMessage());
		}
	}
	
	/**
	 * print message to server gui
	 * @param Message
	 */
	private void println(String Message){
		gui.ServerInterface.print(Message);
		//System.out.println("this should appear" + Message);
	}
	private void printf(String Format, Object Message){
		
		//System.out.printf(Format,Message);
		gui.ServerInterface.print(Message.toString());
		//System.out.println("this should appear" + Message.toString());
	}
}
