package team.hmhlyh.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Teacher;
/**
 * 该接口为数据库Dao层中的教师类交互接口
 * @author 123
 *
 */
public interface TeaDao {
	
	//读取所有教师
	public List<Map<String, Object>> readAllTeachers() throws SQLException;
	
	//重置密码
	public int resetPassword(long id) throws SQLException;
	
	//向数据库中添加教师
	public int addTeacher(Teacher tea) throws SQLException;
	
	//向数据库中添加多个教师
	public int[] addTeachers(Object[][] obj) throws SQLException;
	
	//通过id删除教师
	public int deleteTeacher(long id) throws SQLException;
		
	//通过id从数据库中查询教师姓名
	public String findTeaNameById(long id) throws SQLException;
	
	//通过id从数据库中查询教师
	public Teacher findTeaById(long id) throws SQLException;
	
	//通过id修改个人信息
	public int modifyPersonInfo(Teacher tea) throws SQLException;
	
	//修改图片表信息
	public int modifyPicURL(Teacher tea) throws SQLException;
	
	//通过id修改密码
	public int modifyPassword(long id, String newPassword) throws SQLException;
	
}
