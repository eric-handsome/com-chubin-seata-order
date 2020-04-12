package com.chubin.seata.netty.server.TC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyServerHandler extends ChannelInboundHandlerAdapter{

	private static ChannelGroup channelGroup = 
			         new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static Map<String,List<String>> transactionIdMap = new HashMap<>();
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		 System.out.println("接收数据:"+msg.toString());
		 JSONObject jsonObject = JSON.parseObject((String)msg);
		 String command = jsonObject.getString("command");//create-开启全局事务 register-注册分支事务，commit-提交全局事务
		 String groupId = jsonObject.getString("groupId");//事务组id
		 String transactionType = jsonObject.getString("transactionType");//分支事务类型，commit-待提交 rollback-待回滚
		 String transactionId = jsonObject.getString("transactionId");//分支事务ID
		 if("create".equals(command)){
			 //1开启全局事务
			 transactionIdMap.put(groupId, new ArrayList<String>());
		 }else if("regist".equals(command)){
			 //2注册分支事务
			 transactionIdMap.get(groupId).add(transactionId);
			 //3注册的过程中发现有事务需要回滚
			 if("rollback".equals(transactionType)){
				 System.out.println("接收到了一个回滚状态");
				 sentMsg(groupId,"rollback"); //整个事务组进行回滚
			 }
		 }else if("commit".equals(command)){
			 //4接收到TM发的提交全局事务
			 System.out.println("全局事务提交");
			 sentMsg(groupId,"commit"); 
		 }
		 
	}

	private void sentMsg(String groupId, String command) {
		// TODO Auto-generated method stub
		JSONObject result = new JSONObject();
		result.put("groupId", groupId);
		result.put("command", command);
		sendResult(result);
	}

	private void sendResult(JSONObject result) {
		for (Channel channel : channelGroup) {
		 System.out.println("发送数据："+result.toString());
		 channel.writeAndFlush(result.toJSONString());
		}
	}
	
}
