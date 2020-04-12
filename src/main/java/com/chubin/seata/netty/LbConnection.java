package com.chubin.seata.netty;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.chubin.seata.manager.TransactionType;

public class LbConnection implements Connection {
	private Connection connection;
	private LbTransaction lbTransaction;

	public LbConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Statement createStatement() throws SQLException {
		// TODO Auto-generated method stub
		return connection.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return connection.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return connection.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return connection.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void commit() throws SQLException {
		//

		// wait

		// 因为 这里是阻塞 后面的方法不能执行 那TM拿不到分支事务的消息 不能提交或 回滚 所以应该用线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				lbTransaction.getTask().waitTask();
				
				if (lbTransaction.getTransactionType().equals(TransactionType.commit)) {
					try {
						connection.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						connection.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
//		lbTransaction.getTask().waitTask();
//
//		connection.commit();
//		connection.rollback();
	}

	@Override
	public void rollback() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return connection.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
