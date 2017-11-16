package com.lm.rpc.netty.client;

import com.lm.rpc.utils.ServerConfigReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

public class NettyClient {

	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		try {
			client.group(group); // (2)
			client.channel(NioSocketChannel.class); // (3)
			client.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			client.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
							.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new SimpleClientHandler());
					ch.pipeline().addLast(new StringEncoder());
				}
			});
			
			System.out.println("client started");
			int port = Integer.parseInt(ServerConfigReader.findProp("netty.port"));
			ChannelFuture future = client.connect("localhost",port).sync();
			String msg = "hello netty server";
			future.channel().writeAndFlush(msg);
			future.channel().writeAndFlush("\r\n");
			future.channel().closeFuture().sync();
			System.out.println("client finished.");
			
			
			Object obj = future.channel().attr(AttributeKey.valueOf("val")).get();
			System.out.println("main thread received message:" + obj.toString());
			
		} finally {
			group.shutdownGracefully();
		}
		/*
		 * 1. 返回数据到主线程 
		 * 2. 心跳检测
		 * 3. 重连
		 * 4. 连接池
		 */
		
	}

}
