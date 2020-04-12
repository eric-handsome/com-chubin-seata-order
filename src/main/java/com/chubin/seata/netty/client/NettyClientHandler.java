package com.chubin.seata.netty.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chubin.seata.manager.GlobalTransactionManager;
import com.chubin.seata.manager.TransactionType;
import com.chubin.seata.netty.LbTransaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter{
   private ChannelHandlerContext context;
   
   @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		context = ctx;
	}
   
   //读取数据
   @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	   System.out.println("接受数据:"+msg.toString());
	   JSONObject jsonObject = JSON.parseObject((String)msg);
	   String groupId = jsonObject.getString("groupId"); //事务组id
	   String command = jsonObject.getString("command"); //commit  rollBack
	   System.out.println("接收command:"+command);
	 //获取分支事务
	   LbTransaction lbTransaction = GlobalTransactionManager.getLbTransaction(groupId);
	   //对事物进行操作
	   if("commit".equals(command)){
		   lbTransaction.setTransactionType(TransactionType.commit);
	   }else{
		   lbTransaction.setTransactionType(TransactionType.rollback);
	   }
	   //唤醒
	   lbTransaction.getTask().sinalTask();
	}
   
   //发送请求
   public synchronized Object call(JSONObject data){
	   context.writeAndFlush(data.toJSONString()).channel().newPromise();
	   return null;
   }
}
