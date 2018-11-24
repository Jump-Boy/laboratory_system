package team.hmhlyh.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Student;
/**
 * 该接口为数据库Dao层中的学生类交互接口
 * @author 123
 *
 */
public interface StuDao {
	/*
	 * 将异常抛出，serlvet再进行处理
	 */
	
	//读取所有学生
	public List<Map<String, Object>> readAllStudents() throws SQLException;
	
	//重置密码
	public int resetPassword(long id) throws SQLException;
		
	//向数据库中添加学生
	public int addStudent(Student stu) throws SQLException;
	
	//向数据库中添加多个学生
	public int[] addStudents(Object[][] params) throws SQLException;
	
	//通过id删除学生
	public int deleteStudent(long id) throws SQLException;
	
//	//从数据库中查询学生
//	public Student findStu(Student stu) throws SQLException;

	//通过id从数据库中查询学生
	public Student findStuById(long id) throws SQLException;
	
	//通过id修改个人信息
	public int modifyPersonInfo(Student stu) throws SQLException;
	
	//系统自动设置学生当前学期
	public int autoSetGrade(long id) throws SQLException;
	
	//修改图片表信息
	public int modifyPicURL(Student stu) throws SQLException;
	
	//通过id修改密码
	public int modifyPassword(long id, String newPassword) throws SQLException;

}
