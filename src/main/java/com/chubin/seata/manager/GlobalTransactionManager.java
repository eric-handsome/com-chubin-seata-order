package com.chubin.seata.manager;

import java.awt.geom.CubicCurve2D;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.chubin.seata.netty.LbTransaction;
import com.chubin.seata.netty.client.NettyClient;
import com.chubin.seata.netty.client.NettyClientHandler;

@Component
public class GlobalTransactionManager {
 private static NettyClient nettyClient;
 
 private static ThreadLocal<LbTransaction> current = new ThreadLocal<>();
 private static ThreadLocal<String> currentGroupId = new ThreadLocal<>();
 
 @Autowired
 public void setNettyClient(NettyClient nettyClient){
	 GlobalTransactionManager.nettyClient = nettyClient;
 }
 
 public static Map<String,LbTransaction> LB_TRANSACTION_MAP = new HashMap<>();
 
 
 /**
  * 创建事务组  并且返回 groupId
  */
 public static String createGroup(){
//	 if(currentGroupId.get() != null){
//		 return currentGroupId.get();
//	 }else{
		 String groupId = UUID.randomUUID().toString();
		 JSONObject jsonObject = new JSONObject();
		 jsonObject.put("groupId", groupId);
		 jsonObject.put("command", "create");
		 nettyClient.send(jsonObject);
		 currentGroupId.set(groupId);
		 System.out.println("创建事务组");
		 return groupId;
//	 }
 }
 
 /**
  * 创建分支事务
  */
 public static LbTransaction createLbTransaction(String groupId){
	 String transactionId = UUID.randomUUID().toString();
	 LbTransaction lbTransaction = new LbTransaction(transactionId, groupId);
	 LB_TRANSACTION_MAP.put(groupId, lbTransaction);
//	 current.set(lbTransaction);
	 System.out.println("创建事务");
	 return lbTransaction; 
 }
 
 /**
  * 注册分支事务
  */
 public static LbTransaction addLbTransaction(LbTransaction lbTransaction){
	 JSONObject jsonObject = new JSONObject();
	 jsonObject.put("command", "register");
	 jsonObject.put("groupId", lbTransaction.getGroupId());
	 jsonObject.put("transactionId", lbTransaction.getTransactionId());
	 jsonObject.put("transactionType", lbTransaction.getTransactionType());
	 nettyClient.send(jsonObject);
	 System.out.println("添加事务");
	 return lbTransaction;
 }
 
 
 //提交全局分布式事务
    public static void commitGlobalTransaction(String groupId){
             JSONObject jsonObject = new JSONObject();
             jsonObject.put("groupId", groupId);
             jsonObject.put("command", "commit");
             nettyClient.send(jsonObject);
             System.out.println("提交全局事务");
    }
 
    public static LbTransaction getLbTransaction(String groupId){
    	return LB_TRANSACTION_MAP.get(groupId);
    }
    
    public static LbTransaction getCurrent(){
    	return current.get();
    }
    public static String getCurrentGroupId(){
    	return currentGroupId.get();
    }
   public static void setCurrentGroupId(String groupId){
	   currentGroupId.set(groupId);
   }
 }
