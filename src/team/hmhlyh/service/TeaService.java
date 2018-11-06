package team.hmhlyh.service;

import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.TeaException;
/**
 * servlet接口，规定教师业务逻辑处理方法的规范。该接口中的方法只声明抛出TeaException。
 * @author 123
 *
 */
public interface TeaService {

	//教师登录
	public Teacher login(Teacher tea) throws TeaException;
	
	//添加公告
	public void addAnnouncement(String title, String abstractContent, String attachmentName,String attachmentURL , long publisherId) throws TeaException;
	
	//主页读取公告
	public List<Map<String, Object>> readAnnouncements(long id) throws TeaException;
	
	//公告详情
	public Map<String, Object> readAnnouncementsDetails(int announcementId) throws TeaException;
	
	//附件下载
	public Map<String, Object> attachmentDownload(int announcementId) throws TeaException;
	
	//查看所有课程
	public List<Map<String, Object>> readAllCourses(Teacher tea) throws TeaException;
	
	//添加课程
	public void addCourse(Course cour, Teacher tea) throws TeaException;
	
	//教师修改课程
	public void modifyCourse(Course cour) throws TeaException;
	
	//教师删除课程
	public void deleteCourse(Course cour) throws TeaException;
	
	//教师查看已选详情
	public List<Map<String, Object>> lookSelectedStu(int courseId) throws TeaException;
	
	//教师删除已选人数详情中的某条
	public void deleteFromSelectedStu(int courseId, long stuId) throws TeaException;
	
	//教师查看课表
	public List<Map<String, Object>> readClassSchedule(Teacher tea) throws TeaException;
	
	//点击随堂打分显示信息
	public List<Map<String, Object>> readUseForEveryMark(Teacher tea, String week, byte currentWeeks) throws TeaException; 
	
//	随堂打分
//	public void markEveryClass(List<Map<String, Object>> paramList) throws TeaException;
    //随堂打分
	public void markEveryClass(byte usualScore, int usualScoreId) throws TeaException;
	
	//教师查看成绩
	public List<Map<String, Object>> lookScoreTable() throws TeaException;
	
	//分类按学期筛选成绩单
	public List<Map<String, Object>> lookGroupScoreTable(String studySemester) throws TeaException;
	
	//教师查看成绩明细
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId) throws TeaException;
	
	//成绩管理按钮
	public List<Map<String, Object>> readUseForTotalMark(Teacher tea) throws TeaException;
	
	//成绩管理按钮（按年级分组）
	public List<Map<String, Object>> readGroupUseForTotalMark(Teacher tea,String groupGrade) throws TeaException; 
	
	//总成绩打分
	public void markTotal(byte totalScore, int totalScoreId) throws TeaException;
	
	//读取个人信息
	public Teacher readPersonInfo(Teacher tea) throws TeaException;
	
	//修改个人信息
	public void  modifyPersonInfo(Teacher tea) throws TeaException;
	
	//教师修改密码
	public void modifyPassword(Teacher tea, String newPassword) throws TeaException;
	
}
