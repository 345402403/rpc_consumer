package com.lm.rpc.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.lm.rpc.utils.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if("ping".equals(msg.toString())) {
			ctx.channel().writeAndFlush("ping\r\n");
			return;
		}
		//ctx.channel().attr(AttributeKey.valueOf("val")).set(msg);
		Response response = JSONObject.parseObject(msg.toString(), Response.class);
		DefaultFuture.receive(response);
		
		//ctx.channel().close();
	}

	
}
