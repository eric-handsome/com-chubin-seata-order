package com.chubin.seata.aspect;

import java.sql.Connection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.chubin.seata.netty.LbConnection;

@Aspect
@Component
public class LbDataSourceAspect {
  /**
   * 切的是一个接口 所以所有的实现类都会被切到
   * spring肯定会调用这个方法来生成一个本地事务
   * 所以point.proceed()返回的也是一个connection
   * 
   */
	@Around("execution(* javax.sql.DataSource.getConnection(..))")
	public Connection around(ProceedingJoinPoint point)throws Throwable{
		
		Connection connection = (Connection)point.proceed();  // spring本身实现类
		
		return new LbConnection(connection);
	}
}
