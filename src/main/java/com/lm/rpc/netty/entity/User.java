package com.lm.rpc.netty.entity;

public class User {

	private String username;
	private String userpwd;
	
	public User() {
		super();
	}
	public String getUserpwd() {
		return userpwd;
	}
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
