package team.hmhlyh.utils;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 因为需要事务管理，而这里采用了DBUtils（封装了JDBC的操作），在使用事务时，需往QueryRunner.query()方法中传入connnection参数，
 * 同一事务中的多条sql需保证传入同一connection参数，而serviceImpl中写connection不太合适，所以将获取connenction的过程封装到util包下的
 * 该类中，此类时通过ThreadLocal，这种控制线程局部变量的方式以保证同一线程获取到同一变量connection。
 * @author 123
 *
 */
public class ManagerThreadLoacl {

	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	
	//外部得到一个连接
	public static Connection getConnection() {
		Connection conn = tl.get();//从当前线程池中取出一个连接（当前线程的连接，第一次肯定为null）
		if (conn == null) {
			conn = C3P0Util.getConnection();//如果当前线程池中没有连接，则从数据源中取连接，并将其加到线程池中
			tl.set(conn);
		} 
		return conn;
	}
	
	//开启事务
	public static void startTransacation() {
		try {
			getConnection().setAutoCommit(false);//从当前线程池中取出连接，并开启事务
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//提交事务
	public static void commit() {
		try {
			getConnection().commit();//从当前线程池中取出连接，并提交事务
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//回滚事务
	public static void rollback() {
		try {
			getConnection().rollback();//从当前线程池中取出连接，并回滚事务
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//释放资源
	public static void close() {
		try {
			getConnection().close();//将连接放回到数据源中
			tl.remove();//把当前线程池中的conn溢出，减少线程池压力
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
