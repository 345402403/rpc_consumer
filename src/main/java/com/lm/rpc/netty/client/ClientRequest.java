package com.lm.rpc.netty.client;

import java.util.concurrent.atomic.AtomicLong;

public class ClientRequest {

	private final long id;
	
	private Object content;
	
	private final AtomicLong aid = new AtomicLong(1);
	
	private String command;// rpc 调用接口的路径

	
	
	public ClientRequest() {
		id = aid.incrementAndGet();
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public long getId() {
		return id;
	}
	
	
}
