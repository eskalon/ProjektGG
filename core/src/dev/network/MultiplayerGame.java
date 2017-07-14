package dev.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.network.impl.Client;
import com.esotericsoftware.kryonet.network.impl.Server;

public class MultiplayerGame /* extends Game */ {

	private Server server;
	private Client client;
	
	public void setUpServer(int port) throws IOException{
		server = new Server();
		server.start();
		server.bind(port);
		
		setUpClient("localhost", port);
	}
	
	public void setUpClient(String ip, int port) throws IOException{
		client = new Client();
		client.start();
		client.connect(15, ip, port, port);
	}

	public boolean isHost() {
		return server != null;
	}

	public void dispose() {
		if (server != null)
			server.close();
		if (client != null)
			client.close();
	}

}
