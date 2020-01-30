package io.tek256.net;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	protected int PORT = 4444;
	private ServerSocket socket;
	
	public Server(){
		init();
	}
	
	public Server(int port){
		this.PORT = port;
		init();
	}
	
	private void init(){
		try{
			socket = new ServerSocket(PORT);
			socket.accept();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
