package team.hmhlyh.dao;

import java.sql.Date;
import java.sql.SQLException;

/**
 * 该接口为数据库Dao层中的开学时间表交互接口
 * @author 123
 *
 */
public interface OpenDateDao {

	//设置开学时间
	public int setOpenDate(Date date) throws SQLException;
	
	//更新开学时间
	public int updateOpenDate(Date date) throws SQLException;
	
	//获取开学时间
	public Date readOpenDate() throws SQLException;
	
	//获取剩余可修改时长
	public int getResidueTime() throws SQLException;
	
}
