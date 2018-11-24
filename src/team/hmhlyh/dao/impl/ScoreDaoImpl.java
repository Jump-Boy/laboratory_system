package team.hmhlyh.dao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import team.hmhlyh.dao.ScoreDao;
import team.hmhlyh.utils.C3P0Util;
import team.hmhlyh.utils.ManagerThreadLoacl;

public class ScoreDaoImpl implements ScoreDao {

	@Override
	public String[] readSignUpState(int[] usualGradeIds)
			throws SQLException {
		String[] signUps = new String[usualGradeIds.length];
		QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
		
		for (int i = 0; i < usualGradeIds.length; i++) {
			signUps[i] = (String)qr.query("SELECT vacate FROM stuusualgrade WHERE usualGradeId=?", new ScalarHandler(), usualGradeIds[i]);
		}
		return signUps;
	}

	@Override
	public List<Map<String, Object>> findHavenCourse(long teaId, String week,
			byte currentWeeks) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT stuId,courseId FROM (SELECT * FROM course NATURAL JOIN courseTime WHERE (? BETWEEN " +
				"startWeeks AND endWeeks) AND WEEK=? AND (CURTIME() BETWEEN startTime AND endTime) AND teaId=?)AS course,stuselectcourse WHERE id=courseId", new MapListHandler(), currentWeeks,week,teaId);
		return list;
	}

	@Override
	public Map<String, Object> findHavenUsualScore(long stuId,
			int courseId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Map<String, Object> map = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM stuusualscore WHERE stuId=? AND courseId=? AND currentWeeks=?", new MapHandler(), stuId, courseId);
		return map;
	}

	@Override
	public int[] iniUsualScore(Object[][] params) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int[] num =  qr.batch(ManagerThreadLoacl.getConnection(), "INSERT INTO stuusualscore(stuId,courseId,currentWeeks) VALUES(?,?,?)", params);
		return num;
	}
	
//	@Override
//	public List<Map<String, Object>> readForMarkEvery(long teaId, String week,
//			 byte currentWeeks) throws SQLException {
//		QueryRunner qr = new QueryRunner();
//		List<Map<String, Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT usualGradeId,stuId,NAME,courseName,CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ," +
//				"'~',TIME_FORMAT(endTime,'%H:%i')) AS schoolTime,location,signUp FROM student,(SELECT * FROM stuusualgrade NATURAL JOIN(SELECT " +
//				"* FROM (SELECT * FROM course NATURAL JOIN courseTime WHERE (? BETWEEN startWeeks AND endWeeks) AND WEEK=? AND (CURTIME() " +
//				"BETWEEN startTime AND endTime) AND teaId=?)AS course,stuselectcourse WHERE id=courseId)AS curCourse )AS markCourse WHERE " +
//				"student.id=markCourse.stuId;", new MapListHandler(), currentWeeks, week, teaId);
//		return list;
//	}

	@Override
	public List<Map<String, Object>> readForMarkEvery(long teaId, String week,
			 byte currentWeeks) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT usualScoreId,stuId,NAME AS stuName,courseName,CONCAT(WEEK,' ',TIME_FORMAT(" +
				"startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime,location,currentWeeks,usualScore FROM student,(SELECT * FROM stuusualscore NATURAL " +
				"JOIN(SELECT * FROM (SELECT * FROM course NATURAL JOIN courseTime WHERE (? BETWEEN startWeeks AND endWeeks) AND WEEK=? AND (CURTIME() BETWEEN startTime AND" +
				" endTime) AND teaId=?)AS course,stuselectcourse WHERE id=courseId)AS curCourse )AS markCourse WHERE student.id=markCourse.stuId", new MapListHandler(), currentWeeks, week, teaId);
		return list;
	}
//	@Override
//	public int[] markEvery(String[] vacates, String[] truancys, byte[] usualGrades, int[] usualGradeIds) throws SQLException {
//		Object[][] params = new Object[vacates.length][];
//		for (int i = 0; i < params.length; i++) {
//			params[i] = new Object[]{vacates[i], truancys[i], usualGrades[i], usualGradeIds[i]};
//		}
//		QueryRunner qr = new QueryRunner();
//		int[] num =  qr.batch(ManagerThreadLoacl.getConnection(), "UPDATE stuusualgrade SET vacate=?,truancy=?,usualGrade=? WHERE usualGradeId=?", params);
//		return num;
//	}

	@Override
	public int markEvery(String vacate, String truancy, byte usualScore,
			int usualScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num =  qr.update(ManagerThreadLoacl.getConnection(), "UPDATE stuusualscore SET vacate=?,truancy=?,usualScore=? WHERE usualScoreId=?", vacate, truancy, usualScore,usualScoreId);
		return num;
	}

	@Override
	public int getVacateNum(int courseNo, long stuId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		long vacateNum = (Long) qr.query(ManagerThreadLoacl.getConnection(), "SELECT COUNT(vacate) FROM stuusualScore WHERE vacate='true' AND courseId IN (SELECT id FROM courseTime WHERE " +
				"courseNo=?) AND stuId=?", new ScalarHandler(), courseNo, stuId);
		return (int)vacateNum;
	}

	@Override
	public int getTruancy(int courseNo, long stuId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		long truancyNum = (Long) qr.query(ManagerThreadLoacl.getConnection(), "SELECT COUNT(truancy) FROM stuusualScore WHERE truancy='true' AND courseId IN (SELECT id FROM courseTime WHERE " +
				"courseNo=?) AND stuId=?", new ScalarHandler(), courseNo, stuId);
		return (int)truancyNum;
	}

	@Override
	public int getReferScore(int courseNo, long stuId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int referScore = 0;
		Object obj =  qr.query(ManagerThreadLoacl.getConnection(), "SELECT AVG(usualScore)AS referScore FROM stuusualScore WHERE courseId IN (SELECT id FROM courseTime WHERE courseNo=?) " +
				"AND stuId=?", new ScalarHandler(), courseNo, stuId);
		//注意avg返回的结果类型为BigDecimal，所以需要先转成BigDecimal，然后调用intValue()转int
		if (obj != null) {
			BigDecimal bd = (BigDecimal)obj;
			referScore = bd.intValue();
		}
		return referScore;
	}

	@Override
	public List<Map<String, Object>> readForMarkTotalByTea(long teaId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreList = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId, LEFT(grade,2) AS grade, NAME AS  stuName, stuId, className, scoreTable.courseNo," +
						"course.courseName, totalScore FROM (SELECT * FROM student,stuTotalScore WHERE stuId=id)AS scoreTable,course WHERE course.courseNo=scoreTable.courseNo AND MONTH(NOW())-MONTH" +
						"(course.createdAt)<6 AND teaId=? ORDER BY grade,stuId",
				new MapListHandler(), teaId);
		return scoreList;
	}

	@Override
	public List<Map<String, Object>> readGroupForMarkTotalByTea(long teaId, String groupGrade)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreList = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,  LEFT(grade,2) AS grade, NAME AS  stuName, stuId, className, scoreTable.courseNo," +
						"course.courseName, totalScore FROM (SELECT * FROM student,stuTotalScore WHERE stuId=id)AS scoreTable,course WHERE course.courseNo=scoreTable.courseNo AND MONTH(NOW())-MONTH" +
						"(course.createdAt)<6 AND teaId=? AND LEFT(grade,2)=? ORDER BY grade,stuId",
				new MapListHandler(), teaId,groupGrade);
		return scoreList;
	}

	@Override
	public int markTotal(byte totalScore, int totalScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE stuTotalScore SET totalScore=? WHERE totalScoreId=?", totalScore, totalScoreId);
		return num;
	}

	@Override
	public int iniTotalScore(int courseNo, long stuId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM stutotalscore WHERE stuId=? AND courseNo=?", new MapHandler(), stuId,courseNo);
		int num = 0;
		if (obj == null) {
			num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO stutotalscore(stuId, courseNo) VALUES(?,?)", stuId,courseNo);
		}
		return num;
	}

	@Override
	public int setOtherUseTotal(boolean pass, float gpa, byte getCredit,
			boolean rebuild,int totalScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE stutotalscore SET pass=?,gpa=?,getCredit=?,isRebuild=? WHERE totalScoreId=?",
				pass,gpa,getCredit,rebuild,totalScoreId);
		return num;
	}

	@Override
	public int getCourseNo(int totalScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int courseNo = (Integer)qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseNo FROM stutotalscore WHERE totalScoreId=?", new ScalarHandler(), totalScoreId);
		return courseNo;
	}

	@Override
	public int isRebuild(int totalScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int courseNo = 0;
				Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseNo FROM course WHERE (SELECT courseName FROM course WHERE courseNo=(SELECT courseNo " +
						"FROM stutotalscore WHERE totalScoreId=?)) IN (SELECT courseName FROM course WHERE courseNo != (SELECT courseNo FROM stutotalscore WHERE totalScoreId=?))",
						new ScalarHandler(), totalScoreId,totalScoreId);
				if (obj != null) {
					courseNo = (Integer)obj;
				}
		return courseNo;
	}

	@Override
	public List<Map<String, Object>> lookScoreTableByStu(long stuId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreTable = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,courseName,attribute,totalScore,pass,getCredit,GPA,isRebuild FROM course NATURAL JOIN(SELECT * FROM stutotalscore WHERE stuId=?)AS totalScoreTable", new MapListHandler(), stuId);
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookGroupScoreTableByStu(long stuId,
			String studySemester) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreTable = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,courseName,attribute,totalScore,pass,getCredit,GPA,isRebuild FROM course NATURAL JOIN(SELECT * FROM stutotalscore WHERE stuId=?)AS totalScoreTable WHERE LOCATE(?,studySemester)",
				new MapListHandler(), stuId, studySemester);
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookScoreTable() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreTable = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,courseName,attribute,totalScore,pass,getCredit,GPA,isRebuild FROM course NATURAL JOIN(SELECT * FROM stutotalscore)AS totalScoreTable", new MapListHandler());
		return scoreTable;
	}
	
	@Override
	public List<Map<String, Object>> lookGroupScoreTable(String studySemester)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreTable = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,courseName,attribute,totalScore,pass,getCredit,GPA,isRebuild FROM course NATURAL JOIN(SELECT * FROM stutotalscore)AS totalScoreTable WHERE LOCATE(?,studySemester)",
				new MapListHandler(), studySemester);
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreDetail = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseName,classTime,location,currentWeeks,teacher.name AS teaName,usualScore FROM teacher,(SELECT * FROM course NATURAL JOIN(SELECT * FROM stuusualscore NATURAL JOIN" +
				"(SELECT * FROM stuselectcourse NATURAL JOIN(SELECT courseTime.courseNo,stuId,id,CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i'))AS classTime FROM stutotalscore,courseTime WHERE totalScoreId=? AND courseTime.courseNo=" +
				"stutotalscore.courseNo)AS a WHERE a.id=stuselectcourse.courseId)AS b)AS c)AS d WHERE d.teaId=teacher.id", new MapListHandler(), totalScoreId);
		return scoreDetail;
	}

	@Override
	public List<Map<String, Object>> readForMarkTotalByMana()
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreList = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,  SUBSTRING_INDEX(grade,'\\u7B2C',1) AS grade, NAME AS  stuName, stuId, className, scoreTable.courseNo,course.courseName, totalScore,teaId FROM (SELECT * FROM " +
						"student,stuTotalScore WHERE stuId=id)AS scoreTable,course WHERE course.courseNo=scoreTable.courseNo AND MONTH(NOW())-MONTH(course.createdAt)<6 ORDER BY grade,stuId",
				new MapListHandler());
		return scoreList;
	}

	@Override
	public List<Map<String, Object>> readGroupForMarkTotalByMana(
			String groupGrade) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> scoreList = qr.query(ManagerThreadLoacl.getConnection(), "SELECT totalScoreId,  LEFT(grade,2) AS grade, NAME AS  stuName, stuId, className, scoreTable.courseNo,course.courseName, totalScore,teaId FROM (SELECT * FROM student,stuTotalScore" +
						" WHERE stuId=id)AS scoreTable,course WHERE course.courseNo=scoreTable.courseNo AND MONTH(NOW())-MONTH(course.createdAt)<6 AND LEFT(grade,2)=? ORDER BY grade,stuId",
				new MapListHandler(),groupGrade);
		return scoreList;
	}

	@Override
	public List<Map<String, Object>> readAllCourStuByTea(long teaId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> courStus = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseId,courseNo,stuId FROM (SELECT id,courseNo FROM course NATURAL JOIN courseTime WHERE teaId=?)AS a,stuselectcourse WHERE a.id=stuselectcourse.courseId",
				new MapListHandler(), teaId);
		return courStus;
	}

	@Override
	public List<Map<String, Object>> readAllCourStuByMana() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> courStus = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseId,courseNo,stuId FROM (SELECT id,courseNo FROM course NATURAL JOIN courseTime)AS a,stuselectcourse WHERE a.id=stuselectcourse.courseId",
				new MapListHandler());
		return courStus;
	}

	@Override
	public Map<String,Object> readCourseByTotalId(int totalScoreId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		Map<String,Object> map = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseNo,courseName,createdAt FROM course WHERE courseNo=(SELECT courseNo FROM stutotalscore WHERE totalScoreId=?)", new MapHandler(), totalScoreId);
		return map;
	}

	@Override
	public long readStuIdByTotalId(int totalScoreId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		long stuId = (Long)qr.query(ManagerThreadLoacl.getConnection(), "SELECT stuId FROM stutotalscore WHERE totalScoreId=?", new ScalarHandler(), totalScoreId);
		return stuId;
	}

	@Override
	public String[] readCourseNameByTotal(int courseNo, long stuId,
			Timestamp createdAt) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Object> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT courseName FROM course WHERE courseNo IN (SELECT courseNo FROM stuselectcourse NATURAL JOIN (SELECT courseNo,id AS courseId FROM courseTime WHERE courseNo" +
				" IN (SELECT courseNo FROM course WHERE courseNo!=? AND createdAt<?))AS a WHERE stuId=?)", new ColumnListHandler(), courseNo,createdAt, stuId);
		String[] courseNames = new String[list.size()];
		if (list.size() != 0) {
			for (int i =0; i < list.size(); i++) {
				courseNames[i] = (String)list.get(i);
			}
		}
		return courseNames;
	}

}
