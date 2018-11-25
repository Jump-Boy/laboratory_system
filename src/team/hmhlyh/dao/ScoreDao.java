package team.hmhlyh.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ScoreDao {

	//查询是否签到
	public String[] readSignUpState(int[] usualGradeIds) throws SQLException;
	
	//（随堂）查询是否有课程
	public List<Map<String, Object>> findHavenCourse(long teaId, String week, byte currentWeeks) throws SQLException;  
	
	//初始化stuusualscore
	public int[] iniUsualScore(Object[][] params) throws SQLException;
	
	//验证stuusualscore中是否已有某条记录
	public Map<String, Object> findHavenUsualScore(long stuId, int courseId, String currentWeeks) throws SQLException;
	
	//点击随堂打分显示内容
	public List<Map<String, Object>> readForMarkEvery(long teaId, String week, byte currentWeeks) throws SQLException; 
	
//	//（批处理）实现随堂打分
//	public int[] markEvery(String[] vacates, String[] truancys, byte[] usualGrades, int[] usualGradeIds) throws SQLException; 
	//实现随堂打分
	public int markEvery(String vacate, String truancy, byte usualScore, int usualScoreId) throws SQLException;
	
	//管理员查看某教师所有课程的所有学生
	public List<Map<String, Object>> readAllCourStuByMana() throws SQLException;
	
	//教师查看某教师所有课程的所有学生
	public List<Map<String, Object>> readAllCourStuByTea(long teaId) throws SQLException;
	
	//求出某个学生某门课程的请假次数
	public int getVacateNum(int courseNo, long stuId) throws SQLException;
	
	//求出某个学生某门课程的旷课次数
	public int getTruancy(int courseNo, long stuId) throws SQLException;
	
	//求出某个学生某门课程的平时成绩的平均分做为参考成绩
	public int getReferScore(int courseNo, long stuId) throws SQLException;
	
	//教师点击成绩管理显示的内容
	public List<Map<String, Object>> readForMarkTotalByTea(long teaId) throws SQLException;
	
	//教师点击成绩管理显示的内容（按年级分组查询）
	public List<Map<String, Object>> readGroupForMarkTotalByTea(long teaId, String groupGrade) throws SQLException;
	
	//管理员点击成绩管理显示的内容
	public List<Map<String, Object>> readForMarkTotalByMana() throws SQLException;
	
	//教师点击成绩管理显示的内容（按年级分组查询）
	public List<Map<String, Object>> readGroupForMarkTotalByMana(String groupGrade) throws SQLException;
	//最终成绩打分
	public int markTotal(byte totalScore, int totalScoreId) throws SQLException;
	
	//初始化成绩表（确保成绩表中已经有学生信息）
	public int iniTotalScore(int courseNo, long stuId) throws SQLException;
	
	//自动根据输入的总成绩自动设置是否及格，是否获得学分，以及绩点等
	public int setOtherUseTotal(boolean pass, float gpa, byte getCredit,
			boolean rebuild,int totalScoreId)throws SQLException;
	
	//得到总成绩表中的courseNo
	public int getCourseNo(int totalScoreId) throws SQLException;
	
	//确定是否重修
	public int isRebuild(int totalScoreId) throws SQLException;
	
	//查看成绩（学生）
	public List<Map<String, Object>> lookScoreTableByStu(long stuId) throws SQLException;
	
	//查看成绩（管理员，教师）
	public List<Map<String, Object>> lookScoreTable() throws SQLException;
	
	//查看成绩明细
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId) throws SQLException;
	
	//按学期筛选成绩（学生）
	public List<Map<String, Object>> lookGroupScoreTableByStu(long stuId, String studySemester) throws SQLException;
	
	//按学期筛选成绩（教师，管理员）
	public List<Map<String, Object>> lookGroupScoreTable(String studySemester) throws SQLException;
	
	//列出某条记录的课程信息
	public Map<String, Object> readCourseByTotalId(int totalScoreId) throws SQLException;
	
	//列出学号
	public long readStuIdByTotalId(int totalScoreId) throws SQLException;
	
	//根据当前记录的课程id，学号，创建时间来筛选出符合条件的课程名字（不是当前记录的课程，且时间在这之前）
	public String[] readCourseNameByTotal(int courseNo, long stuId, Timestamp createdAt) throws SQLException;
	
}
