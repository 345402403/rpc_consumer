package lm.study.netty_rpc_consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lm.rpc.netty.annotation.RemoteInvoke;
import com.lm.rpc.netty.entity.User;
import com.lm.rpc.netty.remote.UserRemote;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokeTest.class)
@ComponentScan("com.lm")
public class RemoteInvokeTest {
	
	@RemoteInvoke
	private UserRemote userRemote;
	
	@Test
	public void testRpc() {
		User user = new User();
		user.setUsername("u1");
		user.setUserpwd("p1");
		userRemote.saveUser(user,27);
	}
}
