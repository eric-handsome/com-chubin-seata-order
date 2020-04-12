package com.chubin.seata.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.chubin.seata.annotation.GlobalTransaction;
import com.chubin.seata.manager.GlobalTransactionManager;
import com.chubin.seata.manager.TransactionType;
import com.chubin.seata.netty.LbTransaction;

@Aspect
@Component
public class GlobalTransactionAspect implements Ordered {

	@Around("@annotation(com.chubin.seata.annotation.GlobalTransaction)")
	public void invoke(ProceedingJoinPoint point) {
		
		//before
		MethodSignature signature = (MethodSignature)point.getSignature();
		Method method = signature.getMethod();
		GlobalTransaction globalTransaction = method.getAnnotation(GlobalTransaction.class);
		String groupId = null;
		if(globalTransaction.isStart()){
			 groupId = GlobalTransactionManager.createGroup(); //seata里面叫XID
		}
		//分支事务
		LbTransaction lbTransaction = GlobalTransactionManager.createLbTransaction(groupId);
		
		try {
			Object target = point.getTarget();
			Object this1 = point.getThis();
			System.out.println(target.toString()+this1.toString());
			 point.proceed(); //spring切面   这里会执行 LbDataSourceAspect的around方法
			 lbTransaction.setTransactionType(TransactionType.commit);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			 lbTransaction.setTransactionType(TransactionType.rollback);
		}  
		
		//注册
		GlobalTransactionManager.addLbTransaction(lbTransaction);
		
	}
	
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 10000;
	}

}
