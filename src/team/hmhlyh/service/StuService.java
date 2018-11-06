package team.hmhlyh.service;


import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Student;
import team.hmhlyh.exception.StuException;
/**
 * servlet接口，规定学生业务逻辑处理方法的规范。该接口中的方法只声明抛出StuException
 * @author 123
 *
 */
public interface StuService {
	
	//学生登录
	public Student login(Student stu) throws StuException;
	
	//主页读取公告
	public List<Map<String, Object>> readAnnouncements(long id) throws StuException;
	
	//公告详情
	public Map<String, Object> readAnnouncementsDetails(int announcementId) throws StuException;
	
	//附件下载
	public Map<String, Object> attachmentDownload(int announcementId) throws StuException;
	
	//（学生选课）读取可选课程（在每次开始选课前，先校正年级）
	public List<Map<String, Object>> readAllCourses(Student stu) throws StuException;
	
	//学生选修课程
	public void selectCourse(Course cour, Student stu) throws StuException;
	
	//学生查看已选详情
	public List<Map<String, Object>> lookSelectedStu(int courseId) throws StuException;
	
	//学生查看课表
	public List<Map<String, Object>> readClassSchedule(Student stu) throws StuException;
	
	//学生查看成绩
	public List<Map<String, Object>> lookScoreTable(Student stu) throws StuException;
	
	//分类按学期筛选成绩单
	public List<Map<String, Object>> lookGroupScoreTable(Student stu, String studySemester) throws StuException;
	
	//学生查看成绩明细
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId) throws StuException;
	
	//读取个人信息
	public Student readPersonInfo(Student stu) throws StuException;
	
	//学生修改个人信息
	public void modifyPersonInfo(Student stu) throws StuException;
	
	//学生修改密码
	public void modifyPassword(Student stu, String newPassword) throws StuException;
	
}
