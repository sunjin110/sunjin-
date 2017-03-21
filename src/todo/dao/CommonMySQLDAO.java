package todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

public class CommonMySQLDAO {

	private Connection connection = null;

	public CommonMySQLDAO() {
	}

	/**
	 * データベースとの接続を取得する。 もし取得していた場合には既存の接続を利用し、取得していない場合は新たにコンテナから取得する。
	 *
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {

		// NamingException, SQLExceptionがスローされる
		try {
			if (connection == null || connection.isClosed()) {
				// JNDI参照コンテキストを取得
				InitialContext initCtx = new InitialContext();

				// Tomcatで管理されたデータベース接続をJNDI経由で取得。java:comp/env/を必ずつける。
				DataSource ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/localDB");

				// データベース接続を取得する。JNDI経由。
				connection = ds.getConnection();

				// 自動コミットをfalseに設定
				connection.setAutoCommit(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			connection = null;
			throw e;
		}

		return connection;
	}

	/**
	 * 接続を閉じる。確実に接続を解放するためfinallyでconnection=nullを行う。
	 */
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection = null;
		}
	}

	/**
	 * PreparedStatementを返す。
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement getPreparedStatement(String sql) throws Exception {
		return getConnection().prepareStatement(sql);
	}

	/**
	 * トランザクションのコミットを行う。
	 *
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * トランザクションのロールバックを行う。
	 *
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		connection.rollback();
	}
}
