package client;

import static network.Prefix.*;
import static network.Commands.*;

/**
 * sends ping every second
 * @author Alex
 *
 */
public class Heartbeat extends Thread{
	private boolean done=false;
	private NetworkClient client;
	@Override
	public void run() {
		long nowTime;
		while(!done){
			nowTime = System.currentTimeMillis();
			client.sendMessage(PING,"MAIN",POKE,String.valueOf(nowTime));
			NetworkClient.pingNumber++; 
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(":( System didn't wanna let me sleep....");
			}
		}
	}
	public Heartbeat(NetworkClient client){
		this.client = client;
	}
}
