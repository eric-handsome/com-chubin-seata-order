package com.chubin.seata.netty.client;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.chubin.seata.netty.server.TC.NettyServerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

@Component
public class NettyClient implements InitializingBean{
	
	public NettyClientHandler client = null;
	
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	@Override
	public void afterPropertiesSet() throws Exception {
		start("localhost",8000);
	}

	public  void start(String hostName, int port) {
		client = new NettyClientHandler();
		Bootstrap b = new Bootstrap();
		NioEventLoopGroup group = new NioEventLoopGroup();
		b.group(group).channel(NioSocketChannel.class)
		              .option(ChannelOption.TCP_NODELAY, true)
		              .handler(new ChannelInitializer<SocketChannel>(){
							@Override
							protected void initChannel(SocketChannel socketChannel) throws Exception {
								 ChannelPipeline pipeline = socketChannel.pipeline();
					        	 pipeline.addLast("decoder",new StringDecoder());
					        	 pipeline.addLast("encoder",new StringEncoder());
					        	 pipeline.addLast("handler",client);
							}
				         });
		try{
			b.connect(hostName,port).sync();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void send(JSONObject iSObject){
		try{
			client.call(iSObject);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

}
