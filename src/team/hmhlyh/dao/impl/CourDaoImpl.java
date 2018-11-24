package team.hmhlyh.dao.impl;

import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import team.hmhlyh.dao.CourDao;
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Student;
import team.hmhlyh.utils.ManagerThreadLoacl;

public class CourDaoImpl implements CourDao {

	@Override
	public List<Map<String, Object>> findCourse(Course cour) throws SQLException {
		Object[] params = new Object[] { cour.getStartWeeks(), cour.getStartWeeks(),
				cour.getEndWeeks(), cour.getEndWeeks(),cour.getWeek(), cour.getLocation(),
				cour.getStartTime(), cour.getStartTime(), cour.getEndTime(),
				cour.getEndTime() };
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT * FROM (SELECT * FROM (SELECT * FROM courseTime NATURAL JOIN (SELECT * FROM course WHERE (startWeeks<=? AND endWeeks>=?) OR ( startWeeks<=? AND endWeeks>=?))AS a)" +
						"AS b WHERE b.week=? AND location=?)AS c WHERE (c.startTime<=? AND c.endTime>=?) OR (c.startTime<=? AND c.endTime>=?)",
						new MapListHandler(), params);
		return list;
	}

	@Override
	public List<Map<String, Object>> courseIsHaven(Course cour, long teaId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM course WHERE courseName=? AND studySemester=? AND teaId=? AND MONTH(NOW())-MONTH(createdAt)< 6", 
				new MapListHandler(), cour.getCourseName(),cour.getStudySemester(), teaId);
		return list;
	}
	
	@Override
	public List<Map<String, Object>> readAllCoursesByMana() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> courses = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT courseName,attribute,majorScope,credit,studySemester,teaId,teaName,limitNum,CONCAT(startWeeks,'~',endWeeks,'周')AS classWeeks,location, id AS courseId,CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~'," +
						"TIME_FORMAT(endTime,'%H:%i')) AS classTime,selectedNum FROM (SELECT * FROM (SELECT * FROM course NATURAL JOIN courseTime) AS course NATURAL JOIN (SELECT id AS teaId, NAME AS teaName FROM teacher WHERE 1=1) AS teacher)AS " +
						"finalCourse LEFT OUTER JOIN (SELECT COUNT(*) AS selectedNum, courseId FROM stuselectcourse GROUP BY courseId) AS haveSelectNum ON finalCourse.id = haveSelectNum.courseId",
						new MapListHandler());
		if (courses.size() != 0) {
			for (Map<String, Object> course : courses) {

				for (Map.Entry<String, Object> entry : course.entrySet()) {
					if (entry.getKey().equals("selectedNum")
							&& entry.getValue() == null) {
						entry.setValue(0);
					}
				}
			}
		}
		return courses;
	}

	@Override
	public List<Map<String, Object>> readAllCoursesByTea(long teaId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> courses = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT * FROM(SELECT * FROM (SELECT * FROM (SELECT courseName,attribute,majorScope,credit,studySemester,teaId,limitNum,CONCAT(startWeeks,'~',endWeeks,'周')AS "
								+ "classWeeks,location, id AS courseId,CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime FROM course NATURAL JOIN courseTime) "
								+ "AS course NATURAL JOIN (SELECT id AS teaId, NAME AS teaName FROM teacher WHERE 1=1) AS teacher)AS finalCourse LEFT OUTER JOIN (SELECT COUNT(*) AS selectedNum, courseId AS id FROM stuselectcourse "
								+ "GROUP BY courseId) AS haveSelectNum ON finalCourse.courseId = haveSelectNum.id) AS teaCourse WHERE teaId=?",
						new MapListHandler(), teaId);
		if (courses.size() != 0) {
			for (Map<String, Object> course : courses) {
				for (Map.Entry<String, Object> entry : course.entrySet()) {
					//因为通过外连接查询，所以count(*)显示存在null，但是在页面显示应该为0，所以这里做处理。
					if (entry.getKey().equals("selectedNum")
							&& entry.getValue() == null) {
						entry.setValue(0);
					}
				}
			}
		}
		return courses;
	}

	@Override
	public List<Map<String, Object>> readAllCoursesByStu(Student stu)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> courses = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT * FROM(SELECT * FROM (SELECT * FROM (SELECT courseName,attribute,majorScope,credit,studySemester,teaId,limitNum,CONCAT(startWeeks,'~',endWeeks,'周')AS classWeeks,location,"
								+ " id AS courseId,CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime FROM course NATURAL JOIN courseTime) AS course NATURAL JOIN (SELECT"
								+ " id AS teaId, NAME AS teaName FROM teacher WHERE 1=1) AS teacher)AS finalCourse LEFT OUTER JOIN (SELECT COUNT(*) AS selectedNum, courseId AS id FROM stuselectcourse GROUP BY courseId) AS haveSelectNum ON "
								+ "finalCourse.courseId = haveSelectNum.id) AS teaCourse WHERE studySemester=? AND LOCATE(?, majorScope)!=0",
						new MapListHandler(), stu.getGrade(), stu.getMajor());
		List<Object> courseIds = qr.query(ManagerThreadLoacl.getConnection(),
				"SELECT courseId FROM stuselectcourse WHERE stuId=?",
				new ColumnListHandler(), stu.getId());
		//向每条记录中添加标志，该标志为学生是否已经选修标志，通过该标志的判断，以达到“选修”和“已选”按钮的选择呈现
		if (courses.size() != 0 && courseIds.size() != 0) {
			for (Map<String, Object> courseMap : courses) {
				//这种方式会报并发修改异常
//				for (Map.Entry<String, Object> course : courseMap.entrySet()) {
//					if (course.getKey().equals("courseId")) {
//						for (int i = 0; i < courseIds.size(); i++) {
//							if (courseIds.get(i) == course.getValue()) {
//								courseMap.put("selectFlag", "true");
//							} else {
//								courseMap.put("selectFlag", "false");
//							}
//						}
//					}
//				}
				//采用将object转换成String在比较，而不是转换成Integer比较，会出错
				String id = "" + courseMap.get("courseId");
				//默认false，如果courseId相同，则true（注意false默认，在循环里去判断courseId，再置为true）
				courseMap.put("selectFlag", false);
				for (Object object : courseIds) {
					String courseId = "" + object;
					if (id.equals(courseId)) {
						courseMap.put("selectFlag", true);
					}
//					} else {
//						courseMap.put("selectFlag", false);
//					}
				}
			}
		}
		if (courses != null) {
			for (Map<String, Object> course : courses) {

				for (Map.Entry<String, Object> entry : course.entrySet()) {
					if (entry.getKey().equals("selectedNum")
							&& entry.getValue() == null) {
						entry.setValue(0);
					}
				}
			}
		}
		return courses;
	}

	@Override
	public int addToCourse(Course cour) throws SQLException {
		QueryRunner qr = new QueryRunner();
		// 创建参数
		Object[] params = new Object[] { cour.getCourseName(),
				cour.getAttribute(), cour.getMajorScope(), cour.getCredit(),
				cour.getStudySemester(), cour.getTeaId(),
				cour.getStartWeeks(), cour.getEndWeeks(), cour.getLocation()};
		int num = qr
				.update(ManagerThreadLoacl.getConnection(),
						"INSERT INTO course(courseName, attribute, majorScope, credit, studySemester, teaId, startWeeks, "
								+ "endWeeks, location, createdAt) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ? , NOW())",
						params);
		return num;
	}

	@Override
	public int addToCourseTime(Course cour, long teaId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Integer courseNo = (Integer) qr.query(
				ManagerThreadLoacl.getConnection(),
				"SELECT courseNo FROM course WHERE courseName=? AND studySemester=? AND teaId=?",
				new ScalarHandler(), cour.getCourseName(), cour.getStudySemester(),teaId);
		cour.setCourseNo(courseNo);
		Object[] params = new Object[] { cour.getCourseNo(), cour.getWeek(),
				cour.getStartTime(), cour.getEndTime(), cour.getLimitNum() };
		int num = qr
				.update(ManagerThreadLoacl.getConnection(),
						"INSERT INTO courseTime(courseNo, WEEK, startTime, endTime, limitNum) VALUES(?, ?, ?, ?, ?)",
						params);
		return num;
	}

	@Override
	public int modifyCourse(Course cour) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Integer courseNo = (Integer) qr.query(
				ManagerThreadLoacl.getConnection(),
				"SELECT courseNo FROM courseTime WHERE id=?",
				new ScalarHandler(), cour.getId());
		cour.setCourseNo(courseNo);
		Object[] params = new Object[] { cour.getCourseName(),
				cour.getAttribute(), cour.getMajorScope(), cour.getCredit(),
				cour.getStudySemester(), cour.getTeaId(),
				cour.getStartWeeks(), cour.getEndWeeks(), cour.getLocation(),
			    cour.getCourseNo() };
		int num = qr
				.update(ManagerThreadLoacl.getConnection(),
						"UPDATE course SET courseName=?, attribute=?, majorScope=?, credit=?, studySemester=?, teaId=?, " +
						" startWeeks=?, endWeeks=?, location=? WHERE courseNo=?",
						params);
		System.out.println(num);
		return num;
	}

	@Override
	public int modifyCourseTime(Course cour) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object[] params = new Object[] { cour.getWeek(), cour.getStartTime(),
				cour.getEndTime(), cour.getLimitNum(), cour.getId()};
		int num = qr
				.update(ManagerThreadLoacl.getConnection(),
						"UPDATE courseTime SET WEEK=?, startTime=?, endTime=?, limitNum=? WHERE id=?",
						params);
		return num;
	}

	@Override
	public int deleteCourse(long courseId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(),
				"DELETE FROM courseTime WHERE id=?", courseId);
		return num;
	}

	@Override
	public int selectCourse(Course cour, long stuId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT COUNT(*) FROM stuselectcourse GROUP BY courseId HAVING courseId=?",
				new ScalarHandler(), cour.getId());
		long selectedNum = 0;
		if (obj != null) {
			selectedNum = (Long)obj;
		}
		obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT limitNum FROM courseTime WHERE id=?",
				new ScalarHandler(), cour.getId());
		long limitNum = 0;
		if (obj != null) {
			limitNum = (Integer)obj;
		}
		int num = 0;
		if (selectedNum < limitNum) {
			num = qr.update(ManagerThreadLoacl.getConnection(),
					"INSERT INTO stuselectcourse VALUES(?, ?)", stuId, cour.getId());
		}
		return num;
	}

	@Override
	public List<Map<String, Object>> readSelectedStu(int courseId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> selectedStuTable = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT student.name AS stuName,stuId,courseName,classTime,location,teaName FROM student,(SELECT stuId,courseName," +
						"CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime,location,teacher.name " +
						"AS teaName FROM teacher ,(SELECT * FROM course NATURAL JOIN (SELECT * FROM courseTime,stuselectcourse WHERE " +
						"courseTime.id=stuselectcourse.courseId AND courseId=?)AS a)AS b WHERE teacher.id=teaId)AS c WHERE student.id=stuId ",
						new MapListHandler(),courseId);
		return selectedStuTable;
	}

	@Override
	public int deleteFromSelectedStu(int courseId, long stuId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM stuselectcourse WHERE courseId=? AND stuId=?", courseId, stuId);
		return num;
	}

	@Override
	public List<Map<String, Object>> readClassScheduleByMana()
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> classSchedule = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT courseName,attribute,credit,CONCAT(startWeeks,'~',endWeeks,'周')AS classWeeks,CONCAT(WEEK,' '," +
						"TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime,location,NAME AS teaName FROM " +
						"teacher,(SELECT * FROM course NATURAL JOIN courseTime)AS a WHERE teacher.id=a.teaId",
						new MapListHandler());
		return classSchedule;
	}
	
	@Override
	public List<Map<String, Object>> readClassScheduleByStu(long stuId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> classSchedule = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT courseName,attribute,credit,classWeeks,classTime,location,NAME AS teaName FROM (SELECT courseName," +
						"attribute,credit,CONCAT(startWeeks,'~',endWeeks,'周')AS classWeeks,classTime,location,teaId FROM " +
						"course,(SELECT courseNo, WEEK, CONCAT(WEEK,' ',TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT" +
						"(endTime,'%H:%i')) AS classTime FROM courseTime,(SELECT courseId FROM stuselectcourse WHERE stuId=?) " +
						"AS stuselectcourse WHERE stuselectcourse.courseId=courseTime.id)AS courseTime WHERE courseTime.courseNo=" +
						"course.courseNo) AS selectedCourse,teacher WHERE teaId=id",
						new MapListHandler(), stuId);
		return classSchedule;
	}

	@Override
	public List<Map<String, Object>> readClassScheduleByTea(long teaId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> classSchedule = qr
				.query(ManagerThreadLoacl.getConnection(),
						"SELECT courseName,attribute,credit,CONCAT(startWeeks,'~',endWeeks,'周')AS classWeeks,CONCAT(WEEK,' '," +
						"TIME_FORMAT(startTime,'%H:%i') ,'~',TIME_FORMAT(endTime,'%H:%i')) AS classTime,location,NAME AS teaName " +
						"FROM teacher,(SELECT * FROM course NATURAL JOIN courseTime WHERE teaId=?)AS a WHERE teacher.id=a.teaId",
						new MapListHandler(), teaId);
		return classSchedule;
	}
	
	@Override
	public byte getCredit(long courseNo) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int creditInt = (Integer)qr.query(ManagerThreadLoacl.getConnection(), "SELECT credit FROM course WHERE courseNo=?", new ScalarHandler(), courseNo);
		byte credit = (byte)creditInt;
		return credit;
	}

	@Override
	public List<Map<String, Object>> getCourAndStu(byte currentWeeks, String week, Time time)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT c.startTime,c.courseName,NAME,telephone,c.location FROM (SELECT * FROM (SELECT " +
				"startTime,id,courseName,location FROM (SELECT * FROM course WHERE ?>=startWeeks AND ?<=endWeeks) AS a NATURAL JOIN courseTime " +
				"WHERE WEEK=? AND (TIME_TO_SEC(?)-TIME_TO_SEC(startTime))/60 >= 0 AND (TIME_TO_SEC(?)-TIME_TO_SEC(startTime))/60 <10 " +
				"AND ?<endTime)AS b,stuselectcourse WHERE id = courseId)AS c,student WHERE c.stuId=student.id;", new MapListHandler(), 
				currentWeeks,currentWeeks,week,time,time,time);
		return list;
	}

}
