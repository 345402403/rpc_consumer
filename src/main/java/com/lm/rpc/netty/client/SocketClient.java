package com.lm.rpc.netty.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Socket socket = new Socket("localhost",8888);
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		output.writeBytes("hello netty");
		output.flush();
		socket.close();
	}

}
