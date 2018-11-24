package team.hmhlyh.dao.impl;

import java.sql.Date;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import team.hmhlyh.dao.OpenDateDao;
import team.hmhlyh.utils.ManagerThreadLoacl;

public class OpenDateDaoImpl implements OpenDateDao {

	@Override
	public int setOpenDate(Date date) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO openDate VALUES(?,NOW(),1)", date);
		return num;
	}

	@Override
	public int updateOpenDate(Date date) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "UPDATE opendate SET openDate=? WHERE id=1 AND TIMESTAMPDIFF(HOUR,createdAt,NOW())<24", date);
		return num;
	}
	
	@Override
	public Date readOpenDate() throws SQLException {
		QueryRunner qr = new QueryRunner();
		Object obj = qr.query(ManagerThreadLoacl.getConnection(),"SELECT * FROM openDate WHERE id=1", new ScalarHandler());
		Date date = null;
		if (obj != null) {
			date = (Date)obj;
		}
		return date;
	}

	@Override
	public int getResidueTime() throws SQLException {
		QueryRunner qr = new QueryRunner();
		long residueTime = (Long)qr.query(ManagerThreadLoacl.getConnection(), "SELECT TIMESTAMPDIFF(HOUR,createdAt,NOW()) FROM opendate WHERE id=1", new ScalarHandler());
		return (int)residueTime;
	}

}
