package team.hmhlyh.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import team.hmhlyh.dao.TeaDao;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.utils.ManagerThreadLoacl;
/**
 * Dao层中的教师类交互接口的实现类
 * @author 123
 *
 */
public class TeaDaoImpl implements TeaDao {

	@Override
	public List<Map<String, Object>> readAllTeachers() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> teachers = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM teacher WHERE 1=1", new MapListHandler());
		for (int i = 0; i < teachers.size(); i++) {
			teachers.get(i).put("userType", "教师");
		}
		return teachers;
	}
	
	@Override
	public int resetPassword(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE teacher SET PASSWORD = MD5(?),updatedAt = NOW() WHERE id = ?", id, id);
		return num;
	}
	
	@Override
	public int addTeacher(Teacher tea) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO teacher (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", tea.getId(), tea.getName(), tea.getId());
		return num;
	}

	@Override
	public int[] addTeachers(Object[][] params) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int[] num = qr.batch(ManagerThreadLoacl.getConnection(), "INSERT INTO teacher (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", params);
		return num;
	}
	
	@Override
	public int deleteTeacher(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM teacher WHERE id=?", id);
		return num;
	}

	@Override
	public String findTeaNameById(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		String teaName = null;
		Object obj = (String)qr.query(ManagerThreadLoacl.getConnection(), "SELECT name FROM teacher WHERE id=?", new ScalarHandler(), id);
		if (obj != null) {
			teaName = (String)obj;
		}
		return teaName;
	}

	@Override
	public Teacher findTeaById(long id) throws SQLException {
		Connection conn = ManagerThreadLoacl.getConnection();
		QueryRunner qr = new QueryRunner();
		Teacher teacher = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM teacher WHERE id=?", new BeanHandler<Teacher>(Teacher.class), id);
		PreparedStatement ps = conn.prepareStatement("SELECT picURL FROM teacherPic WHERE teaId=?");
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next() == true) {
			teacher.setPicURL(new SerialBlob(rs.getBlob("picURL")));//根据给定Blob对象的序列化形式构造一个SerialBlob对象
		}	
		return teacher;
	}
	
	@Override
	public int modifyPersonInfo(Teacher tea) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), 
				"UPDATE teacher SET NAME=?, sex=?, province=?, telephone=? WHERE id=?",
				tea.getName(), tea.getSex(), tea.getProvince(), tea.getTelephone(), tea.getId());
		return num;
	}
	
	@Override
	public int modifyPicURL(Teacher tea) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM teacherPic WHERE teaId=?",new ScalarHandler(),tea.getId());
		int num = 0;
		if (obj == null) {
			num = qr.update(ManagerThreadLoacl.getConnection(),"INSERT INTO teacherPic VALUES(?,?)", tea.getPicURL(), tea.getId());
		}else {
			num = qr.update(ManagerThreadLoacl.getConnection(),"UPDATE teacherPic SET picURL=? WHERE teaId=?",tea.getPicURL(),tea.getId());			
		}
		return num;
	}

	@Override
	public int modifyPassword(long id, String newPassword) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE teacher SET PASSWORD=MD5(?) WHERE id=?", newPassword, id);
		return num;
	}

}
