package server;
import java.io.PrintWriter;

public class User{
	private PrintWriter out=null;
	private String name;
	private String cliAddr;
	private int port;

	public User(String name,String cliAddr,int port,PrintWriter out){
		this.out = out;
		this.name = name;
		this.cliAddr = cliAddr;
		this.port = port;
	}
	
	/**
	 * send message to this user
	 * @param msg
	 */
	public void sendMessage(String msg){
		out.println(msg);
	}
	public String toString(){
		return name==null? "" : name+"/"+cliAddr+"/"+port;
	}
	public String getName(){
		return name;
	}
	
	public PrintWriter getSendStream(){
		return out;
	}
}