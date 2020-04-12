package com.chubin.seata.netty.server.TC;

public class Main {

	public static void main(String[] args) {
		NettyServer nettyServer = new NettyServer();
		nettyServer.start("localhost", 8000);
		System.out.println("netty 启动成功");
	}
}
