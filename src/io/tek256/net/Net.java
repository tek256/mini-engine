package io.tek256.net;

import java.io.IOException;
import java.net.InetAddress;

public class Net {
	public static int MAX_TIMEOUT = 1000; //1s
	
	public static boolean ping(String addr){
		try{
			return InetAddress.getByName(addr).isReachable(MAX_TIMEOUT);
		}catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static InetAddress getInet(String addr){
		try{
			return InetAddress.getByName(addr);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
}
