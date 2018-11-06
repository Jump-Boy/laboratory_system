package team.hmhlyh.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * 数据源工具类（数据库连接池）使用数据库连接池，目的是解决建立数据库连接耗费资源和时间很多的问题，通过连接池提高性能，从初始化后的池中取出连接，
 * 而不是一味的建立连接。C3P0为常用的数据源，它提供了很好的连接池封装。需要添加Jar包和相应的配置文件（C3P0-config.xml）
 * 
 * @author 123
 *
 */
public class C3P0Util {
	
	//创建并初始化一个数据源
	private static DataSource dataSource = new ComboPooledDataSource();

	//外部获取数据源的方法
	public static DataSource getDataSource() {
		return dataSource;
	}
	
	//从数据源中获得一个连接对象
	public static Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("服务器错误");
		}
	}
	
	public static void release(Connection conn, Statement stmt, ResultSet rs) {
		//关闭资源
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();//并非真正关闭，而是放回数据源（数据库连接池）中
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = null;
		}
	}
	
}
