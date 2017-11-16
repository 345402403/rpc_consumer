package com.lm.rpc.netty.remote;

import java.util.List;

import com.lm.rpc.netty.entity.User;
import com.lm.rpc.utils.Response;

public interface UserRemote {
	public Response saveUser(User user,int i);

	public Response saveUsers(List<User> users);
}
