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

import team.hmhlyh.dao.ManaDao;
import team.hmhlyh.domain.Manager;
import team.hmhlyh.utils.ManagerThreadLoacl;
/**
 * Dao层中的管理员类交互接口的实现类
 * @author 123
 *
 */
public class ManaDaoImpl implements ManaDao{

	@Override
	public List<Map<String, Object>> readAllManagers() throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String, Object>> managers = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM manager WHERE 1=1", new MapListHandler());
		for (int i = 0; i < managers.size(); i++) {
			managers.get(i).put("userType", "管理员");
		}
		return managers;
	}
	
	@Override
	public int resetPassword(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE manager SET PASSWORD = MD5(?),updatedAt = NOW() WHERE id = ?", id, id);
		return num;
	}

	@Override
	public int addManager(Manager mana) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO manager (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", mana.getId(), mana.getName(), mana.getId());
		return num;
	}

	@Override
	public int[] addManagers(Object[][] params) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int[] num = qr.batch(ManagerThreadLoacl.getConnection(), "INSERT INTO manager (id, name, PASSWORD, createdAt, updatedAt) " +
				"VALUES (?, ?, MD5(?), NOW(), NOW())", params);
		return num;
	}

	@Override
	public int deleteManager(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM manager WHERE id=?", id);
		return num;
	}

//	@Override
//	public int modifyRoleInfo(Manager mana) throws SQLException {
//		QueryRunner qr = new QueryRunner();
//		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE manager SET id=?, name=?, updateTime WHERE id=?", newPassword, id);
//		return 0;
//	}
	
	@Override
	public Manager findManaById(long id) throws SQLException {
		
		Connection conn = ManagerThreadLoacl.getConnection();
		QueryRunner qr = new QueryRunner();
		Manager manager = qr.query(conn, "SELECT * FROM manager WHERE id=?", new BeanHandler<Manager>(Manager.class),id);
		PreparedStatement ps = conn.prepareStatement("SELECT picURL FROM managerPic WHERE manaId=?");
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next() == true) {
			manager.setPicURL(new SerialBlob(rs.getBlob("picURL")));//根据给定Blob对象的序列化形式构造一个SerialBlob对象
		}	
		return manager;
	}

	@Override
	public String findManaNameById(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT NAME FROM manager WHERE id = ?", new ScalarHandler(), id);
		String name = null;
		if (obj != null) {
			name = (String)obj;
		}
		return name;
	}
	
	@Override
	public int modifyPersonInfo(Manager mana) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), 
				"UPDATE manager SET NAME=?, sex=?, province=?, telephone=? WHERE id=?",
				mana.getName(), mana.getSex(), mana.getProvince(), mana.getTelephone(), mana.getId());
		return num;
	}

	@Override
	public int modifyPicURL(Manager mana) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(), "SELECT * FROM managerPic WHERE manaId=?",new ScalarHandler(),mana.getId());
		int num = 0;
		if (obj == null) {
			num = qr.update(ManagerThreadLoacl.getConnection(),"INSERT INTO managerPic VALUES(?,?)", mana.getId(), mana.getPicURL());
		}else {
			num = qr.update(ManagerThreadLoacl.getConnection(),"UPDATE managerPic SET picURL=? WHERE manaId=?",mana.getPicURL(),mana.getId());			
		}
		return num;
	}
	
	@Override
	public int modifyPassword(long id, String newPassword) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE manager SET PASSWORD=MD5(?) WHERE id=?", newPassword, id);
		return num;
	}

}
