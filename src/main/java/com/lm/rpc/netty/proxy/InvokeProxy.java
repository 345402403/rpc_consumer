package com.lm.rpc.netty.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.lm.rpc.netty.annotation.RemoteInvoke;
import com.lm.rpc.netty.client.ClientRequest;
import com.lm.rpc.netty.client.TcpClient;
import com.lm.rpc.utils.Response;

@Component
public class InvokeProxy implements BeanPostProcessor {

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		
		Field[] fields = bean.getClass().getDeclaredFields();// 获取每个初始化bean的字段
		for(Field f : fields) {// 遍历这些字段
			if(f.isAnnotationPresent(RemoteInvoke.class)) {// 标注了@RemoteInvoke 的字段需要远程调用
				f.setAccessible(true);// 使私有变量可反射访问
				final Map<Method, Class> methodMap = new HashMap<Method,Class>();//存储方法所属的类名，在反射回调方法时，能获取到类
				putMethodMap(methodMap,f);//存储
				
				Enhancer enhance = new Enhancer();// 增强器
				enhance.setInterfaces(new Class[] {f.getType()});// 代理类实现的接口
				enhance.setCallback(new MethodInterceptor(){// 调用方法拦截器

					public Object intercept(Object instance, Method method, Object[] args, MethodProxy methodProxy)
							throws Throwable {
						ClientRequest req = new ClientRequest();
						req.setCommand(methodMap.get(method).getName()+"."+method.getName());//方法的全路径
						req.setContent(args);// 调用参数
						Response response = TcpClient.send(req);// 发送到服务端
						System.out.println(response);
						return response;
					}
	
				});
				try {
					f.set(bean, enhance.create());// 生成代理类
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		return bean;
	}

	private void putMethodMap(Map<Method, Class> map, Field f) {
		Method[] methods = f.getType().getDeclaredMethods();
		for(Method m : methods) {
			map.put(m, f.getType());
		}
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
