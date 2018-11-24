package team.hmhlyh.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import team.hmhlyh.dao.StuDao;
import team.hmhlyh.domain.Student;
import team.hmhlyh.utils.DBUtils;
import team.hmhlyh.utils.ManagerThreadLoacl;
/**
 * Dao层中的学生类交互接口的实现类
 * @author 123
 *
 */
public class StuDaoImpl implements StuDao {

	@Override
	public List<Map<String, Object>> readAllStudents() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> students = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM student WHERE 1=1", new MapListHandler());
		for (int i = 0; i < students.size(); i++) {
			students.get(i).put("userType", "学生");
		}
		return students;
	}
	
	@Override
	public int resetPassword(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE student SET PASSWORD = MD5(?),updatedAt = NOW() WHERE id = ?", id, id);
		return num;
	}
	
	//注册添加学生
	@Override
	public int addStudent(Student stu) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO student (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", stu.getId(), stu.getName(), stu.getId());
		return num;
	}

	@Override
	public int[] addStudents(Object[][] params) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int[] num = qr.batch(ManagerThreadLoacl.getConnection(), "INSERT INTO student (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", params);
		return num;
	}
	
	@Override
	public int deleteStudent(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM student WHERE id=?", id);
		return num;
	}
	//登陆校验，通过id查找
	@Override
    //旧版方法
//	public Student findStuById(long id) throws Exception {
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		Student student = null;//student为数据库查询返回的结果，若没有查询到则返回null，若查询到，则student将结果返回给servlet
//		try {
//			conn = DBUtils.getConnection();
//			ps = conn.prepareStatement("SELECT * FROM student WHERE id=?");
//			ps.setLong(1, id);
//			rs = ps.executeQuery();
//			//executeQuery()永远不会返回null，如果没有查询到，则返回以rs.next()为false的结果集
//			if (rs.next() == true) {
//				student.setName(rs.getString("NAME"));
//				student.setId(rs.getLong("id"));
//				student.setPassword(rs.getString("PASSWORD"));
//				student.setSex(rs.getString("sex"));
//				student.setGrade(rs.getDate("grade"));
//				student.setInstitute(rs.getString("institute"));
//				student.setMajor(rs.getString("major"));
//				student.setClassName(rs.getString("className"));
//				student.setEducationLevel(rs.getString("educationLevel"));
//				student.setProvince(rs.getString("province"));
//				student.setTelphone(rs.getString("telphone"));			
//			} 
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new RuntimeException("查询失败");
//		} finally {
//			DBUtils.closeAll(rs, ps, conn);
//		}
//		return student;
//	}
	
	/* 利用DBUtils中的QueryRunner进行sql操作，利用ResultHandler封装结果集
	 * 注意需要分两部查询，先查除过picURL属性的字段，因为使用DBUtils封装的方法无法封装对象的picURL字段（BLOB强转SerialBlob会出错）
	 * 先从学生表中查出所有字段信息，再从studentPic中查出picURL值
	 * @see team.hmhlyh.dao.StuDao#findStuById(long)
	 */
	public Student findStuById(long id) throws SQLException {
		
		Connection conn = ManagerThreadLoacl.getConnection();
		QueryRunner qr = new QueryRunner();
		Student student = qr.query(conn, "SELECT * FROM student WHERE id=?", new BeanHandler<Student>(Student.class),id);
		PreparedStatement ps = conn.prepareStatement("SELECT picURL FROM studentPic WHERE stuId=?");
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next() == true) {
			student.setPicURL(new SerialBlob(rs.getBlob("picURL")));//根据给定Blob对象的序列化形式构造一个SerialBlob对象
		}	
		return student;
	}

//	@Override
//	public Student findStu(Student stu) throws SQLException {
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		Student student = null;
//		
//		try {
//			conn = DBUtils.getConnection();
//			ps = conn.prepareStatement("SELECT * FROM student WHERE id=? AND PASSWORD=?");
//			rs = ps.executeQuery();
//			student = new Student();
//			if (rs.next() == true) {
//				student.setName(rs.getString("NAME"));
//				student.setId(rs.getLong("id"));
//				student.setPassword(rs.getString("PASSWORD"));
//				student.setSex(rs.getString("sex"));
//				student.setMajor(rs.getString("major"));
//				student.setClassName(rs.getString("className"));
//				student.setEducationLevel(rs.getString("educationLevel"));
//				student.setProvince(rs.getString("province"));
//				student.setTelephone(rs.getString("telphone"));
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return student;
//	}
	/*
	 * 修改个人信息需要注意，通过从studentPic表中查询得到的结果，考虑有两种情况，一种原本学生没有照片，此时应该insert语句，如果原本表中有图片信息，则应该update语句，最终方法返回int值
	 * @see team.hmhlyh.dao.StuDao#modifyPersonInfo(team.hmhlyh.domain.Student)
	 */
	public int modifyPersonInfo(Student stu) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), 
				"UPDATE student SET NAME=?, sex=?, className=?, major=?, educationLevel=?, province=?, telephone=? WHERE id=?",
				stu.getName(), stu.getSex(), stu.getClassName(), stu.getMajor(), stu.getEducationLevel(), stu.getProvince(), stu.getTelephone(), stu.getId());
//		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM studentPic WHERE stuId=?",new ScalarHandler(),stu.getId());
//		if (obj == null) {
//			num += qr.update(ManagerThreadLoacl.getConnection(),"INSERT INTO studentPic VALUES(?,?)", stu.getPicURL(), stu.getId());
//		}else {
//			num += qr.update(ManagerThreadLoacl.getConnection(),"UPDATE studentPic SET picURL=? WHERE stuId=?",stu.getPicURL(),stu.getId());			
//		}
		return num;
	}

	@Override
	public int modifyPicURL(Student stu) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM studentPic WHERE stuId=?",new ScalarHandler(),stu.getId());
		int num = 0;
		if (obj == null) {
			num = qr.update(ManagerThreadLoacl.getConnection(),"INSERT INTO studentPic VALUES(?,?)", stu.getPicURL(), stu.getId());
		}else {
			num = qr.update(ManagerThreadLoacl.getConnection(),"UPDATE studentPic SET picURL=? WHERE stuId=?",stu.getPicURL(),stu.getId());			
		}
		return num;
	}
	
	@Override
	public int modifyPassword(long id, String newPassword) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE student SET PASSWORD=MD5(?) WHERE id=?", newPassword, id);
		return num;
	}

	@Override
	public int autoSetGrade(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		String className = (String)qr.query(ManagerThreadLoacl.getConnection(), "SELECT className FROM student WHERE id=?", new ScalarHandler(), id);
		String s = className.split("-")[0];
		String regex = "[^0-9]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		String yearAfterTwo = m.replaceAll("").trim();
		int year = Integer.parseInt(("20" + yearAfterTwo));
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);
		String grade = "";
		if (currentYear - year == 0) {
			grade += "大一上学期";
		} else if (currentYear - year == 1) {
			if (currentMonth < 3) {
				grade += "大一上学期";
			} else if (currentMonth >= 3 && currentMonth < 9) {
				grade += "大一下学期";
			} else if (currentMonth >= 9) {
				grade += "大二上学期";
			}
		} else if (currentYear -year == 2) {
			if (currentMonth < 3) {
				grade += "大二上学期";
			} else if (currentMonth >= 3 && currentMonth < 9) {
				grade += "大二下学期";
			} else if (currentMonth >= 9) {
				grade += "大三上学期";
			}
		} else if (currentYear - year == 3) {
			if (currentMonth < 3) {
				grade += "大三上学期";
			} else if (currentMonth >= 3 && currentMonth < 9) {
				grade += "大三下学期";
			} else if (currentMonth >= 9) {
				grade += "大四上学期";
			}
		} else {
			if (currentMonth < 3) {
				grade += "大四上学期";
			} else if (currentMonth >= 3 && currentMonth < 9) {
				grade += "大四下学期";
			}
		}
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE student SET grade=? WHERE id=?", grade, id);
		return num;
	}

}
