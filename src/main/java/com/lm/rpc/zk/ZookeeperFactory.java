package com.lm.rpc.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {
	static CuratorFramework client;

	public static CuratorFramework getCurator() {
		if (client == null) {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
			client.start();
		}
		return client;
	}
	
	public static void main(String[] args) throws Exception {
		CuratorFramework client1 = getCurator();
		client1.create().forPath("/netty");
	}
	
}
