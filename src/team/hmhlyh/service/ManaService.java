package team.hmhlyh.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Manager;
import team.hmhlyh.exception.ManaException;
/**
 * servlet接口，规定管理员业务逻辑处理方法的规范。该接口中的方法只声明抛出ManaException。
 * @author 123
 *
 */
public interface ManaService {

	//管理员登录
	public Manager login(Manager mana) throws ManaException;

	//管理员设置开学时间
	public void setOpenDate(Date date) throws ManaException;
	
	//管理员获取开学时间相关字段值
	public Map<String, Object> readOpenDate();
	
	//添加公告
	public void addAnnouncement(String title, String abstractContent, String attachmentName, String attachmentURL, long publisherId) throws ManaException;

	//删除公告
	void deleteAnnouncement(long var1) throws ManaException;

	//主页读取公告
	public List<Map<String, Object>> readAnnouncements() throws ManaException;
	
	//公告详情
	public Map<String, Object> readAnnouncementsDetails(int announcementId) throws ManaException;
	
	//附件下载
	public Map<String, Object> attachmentDownload(int announcementId) throws ManaException;
	
	//读取所有角色
	public List<Map<String, Object>> readAllRoles() throws ManaException;
	
	//添加角色
	public void addRole(Object obj) throws ManaException;
	
	//添加多个管理员
	public void addManagers(Object[][] params) throws ManaException;
	
	//添加多个教师
	public void addTeachers(Object[][] params) throws ManaException;
	
	//添加多个学生
	public void addStudents(Object[][] params) throws ManaException;
	
	//重置密码
	public void resetPassword(Object obj) throws ManaException;
	
	//删除角色
	public void deleteRole(Object obj) throws ManaException;
	
	//管理员查看所有课程
	public List<Map<String, Object>> readAllCourses() throws ManaException;
	
//	//管理员添加课程
//	public void addCourse(Course cour, long id) throws ManaException;
	
	//管理员修改课程
	public void modifyCourse(Course cour) throws ManaException;
	
	//管理员删除课程
	public void deleteCourse(Course cour) throws ManaException;
	
	//管理员查看已选详情
	public List<Map<String, Object>> lookSelectedStu(int courseId) throws ManaException; 
	
	//管理员删除已选人数详情中的某条
	public void deleteFromSelectedStu(int courseId, long stuId) throws ManaException;
	
	//管理员查看课表
	public List<Map<String, Object>> readClassSchedule() throws ManaException;
	
	//管理员查看成绩
	public List<Map<String, Object>> lookScoreTable() throws ManaException;
	
	//分类按学期筛选成绩单
	public List<Map<String, Object>> lookGroupScoreTable(String studySemester) throws ManaException;
	
	//管理员查看成绩明细
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId) throws ManaException;
	
	//管理员成绩管理按钮
	public List<Map<String, Object>> readUseForTotalMark() throws ManaException;
		
	//管理员成绩管理按钮（按年级分组）
	public List<Map<String, Object>> readGroupUseForTotalMark(String groupGrade) throws ManaException;
	
	//读取个人信息
	public Manager readPersonInfo(Manager mana) throws ManaException;
	
	//修改个人信息
	public void modifyPersonInfo(Manager mana) throws ManaException;
	
	//管理员修改密码
	public void modifyPassword(Manager mana, String newPassword) throws ManaException;
	
}
