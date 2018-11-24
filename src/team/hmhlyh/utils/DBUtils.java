package team.hmhlyh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
/**
 * 此类为数据库工具类，方便管理JDBC，将JDBC所需要的信息配置到配置文件中，可以达到很好的代码重用性，方便日常维护
 * @author 123
 *
 */
public class DBUtils {

	private static String driverClass;
	private static String url;
	private static String username;
	private static String password;
	
	//在静态块中初始化变量
	static {
		//此对象rb是用来加载dbinfo.properties文件数据的
		ResourceBundle rb = ResourceBundle.getBundle("dbinfo");
		driverClass = rb.getString("driverClass");
		url = rb.getString("url");
		username = rb.getString("username");
		password = rb.getString("password");
		//加载驱动
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//得到连接的方法
	public  static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	} 
	
	//关闭资源的方法
	public static void closeAll(ResultSet rs, Statement stmt, Connection conn) {
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
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn = null;
		}
	}
	
}
