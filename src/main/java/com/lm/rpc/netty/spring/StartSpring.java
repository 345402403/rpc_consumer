package com.lm.rpc.netty.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.lm.rpc.netty")
public class StartSpring {

	public static void main(String[] args)  throws InterruptedException{
		 ApplicationContext context = new AnnotationConfigApplicationContext(StartSpring.class);
	}

}
