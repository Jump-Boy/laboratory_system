package team.hmhlyh.dao;

import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Student;

public interface CourDao {
	
	//查询出课程表的课程
	public List<Map<String, Object>> findCourse(Course cour) throws SQLException;
	
	//查询出课程表的课程
	public List<Map<String, Object>> courseIsHaven(Course cour, long teaId) throws SQLException;
	
	//添加信息到课程表
	public int addToCourse(Course cour) throws SQLException;
	
	//添加信息到课程时间表
	public int addToCourseTime(Course cour, long teaId) throws SQLException;
	
	//管理员查看所有课程
	public List<Map<String, Object>> readAllCoursesByMana() throws SQLException;
	
	//教师查看所有课程
	public List<Map<String, Object>> readAllCoursesByTea(long teaId) throws SQLException;
	
	//学生查看所有课程（学生选课）
	public List<Map<String, Object>> readAllCoursesByStu(Student stu) throws SQLException;
	
	//学生选修
	public int selectCourse(Course cour, long stuId) throws SQLException;
	
	//查看已选人数详情
	public List<Map<String, Object>> readSelectedStu(int courseId) throws SQLException;
	
	//删除已选认输详情中的某条
	public int deleteFromSelectedStu(int courseId, long stuId) throws SQLException;
	
	//管理员查看课表
	public List<Map<String, Object>> readClassScheduleByMana() throws SQLException; 
	
	//教师查看课表
	public List<Map<String, Object>> readClassScheduleByTea(long teaId) throws SQLException;
	
	//学生查看课表
	public List<Map<String, Object>> readClassScheduleByStu(long stuId) throws SQLException;
	
	//通过id修改课程表信息
	public int modifyCourse(Course cour) throws SQLException;

	//通过id修改课程时间表信息
	public int modifyCourseTime(Course cour) throws SQLException;
	
	//删除课程表
	public int deleteCourse(long courseId) throws SQLException;
	
	//查看课程学分
	public byte getCredit(long courseNo) throws SQLException;
	
	//（短信发送）根据时间查询出所有课程的所有学生
	public List<Map<String, Object>> getCourAndStu(byte currentWeeks,String week, Time time) throws SQLException;
	
}
