package com.lm.rpc.netty.client;

import com.alibaba.fastjson.JSONObject;
import com.lm.rpc.consts.Constants;
import com.lm.rpc.utils.Response;
import com.lm.rpc.utils.ServerConfigReader;

import com.lm.rpc.zk.ZookeeperFactory;
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
import org.apache.curator.framework.CuratorFramework;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TcpClient {

	public final Bootstrap client = new Bootstrap();
	public static ChannelFuture future = null;
	static {
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
			String host = null;
			try {
				List<String> addrs = ZookeeperFactory.getCurator().getChildren().forPath(Constants.SERVER_PATH);
				Set<String> set = new HashSet<>();
				for (String str : addrs) {
					set.add(str.split("#")[0]);
				}
				host = set.toArray()[0].toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			future = client.connect(host, port).sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//group.shutdownGracefully();
		}
	}
	
	
	public static Response send(ClientRequest request){
		future.channel().writeAndFlush(JSONObject.toJSONString(request));
		future.channel().writeAndFlush("\r\n");
		DefaultFuture df = new DefaultFuture(request);
		return df.get();
		
	}

}
