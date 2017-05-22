package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PingChecker extends Thread {
	HashMap<ServerListener, Boolean> pingMap;
	ArrayList<ServerListener> clientList;
	public boolean done = false;

	public PingChecker() {
		pingMap = new HashMap<ServerListener, Boolean>();
		clientList = new ArrayList<ServerListener>();
	}

	/**
	 * test every 30 seconds if a client has lost connection
	 */
	public void run() {
		long time = System.currentTimeMillis() + 30000;
		while (!done) {
			if (System.currentTimeMillis() > time) {
				time = System.currentTimeMillis()+30000;
				testPing();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(":( System didn't wanna let me sleep....");
			}
		}
	}

	synchronized private void testPing() {
		for (ServerListener client : clientList) {
			if (client != null) {
				if (!pingMap.get(client)) {
					try {
						client.getSocket().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					removeClient(client);
				} else {
					pingMap.put(client, false);
				}
			}
		}
	}

	public void addClient(ServerListener client) {
		pingMap.put(client, true);
		clientList.add(client);
	}

	public void removeClient(ServerListener client) {
		pingMap.remove(client);
		clientList.remove(client);
		
	}

	public void setPing(ServerListener client) {
		pingMap.put(client, true);
	}
}
