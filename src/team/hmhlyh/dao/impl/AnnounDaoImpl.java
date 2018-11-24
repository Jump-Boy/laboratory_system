package team.hmhlyh.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import team.hmhlyh.dao.AnnounDao;
import team.hmhlyh.utils.ManagerThreadLoacl;

public class AnnounDaoImpl implements AnnounDao {

	@Override
	public int addAnnouncement(String title, String abstractContent,
			String attachmentName, String attachmentURL, long publisherId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO announcement(title,abstract,attachmentName,attachmentURL,createdAt,publisherId) VALUES(?,?,?,?,CURRENT_DATE,?)", title,abstractContent,attachmentName,attachmentURL,publisherId);
		return num;
	}

	@Override
	public int deleteAnnouncement(long id) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM announcement WHERE announcementId = ?", id);
		return num;
	}

	@Override
	public int deleteAnnouncementByTea(long id, long teaId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		int num = qr.update(ManagerThreadLoacl.getConnection(), "DELETE FROM announcement WHERE announcementId = ? AND publisherId = ?", new Object[]{id, teaId});
		return num;
	}

	@Override
	public List<Map<String, Object>> readAnnouncementsFromMana()
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String,Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT announcementId,title,attachmentName,NAME,announcement.createdAt FROM announcement,manager WHERE publisherId=id", new MapListHandler());
		return list;
	}

	@Override
	public List<Map<String, Object>> readAnnouncementsFromTeaByMana()
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String,Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT announcementId,title,attachmentName,NAME,announcement.createdAt FROM announcement,teacher WHERE publisherId=id", new MapListHandler());
		return list;
	}
	
	@Override
	public List<Map<String, Object>> readAnnouncementsFromTeaByTea(long id)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String,Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT announcementId,title,attachmentName,NAME,announcement.createdAt FROM announcement,teacher WHERE publisherId=id AND publisherId=?", new MapListHandler(), id);
		return list;
	}

	@Override
	public List<Map<String, Object>> readAnnouncementsByStu(long id)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		List<Map<String,Object>> list = qr.query(ManagerThreadLoacl.getConnection(), "SELECT announcementId,title,attachmentName,NAME,announcement.createdAt FROM announcement,teacher WHERE publisherId=id AND publisherId IN (SELECT DISTINCT teaId FROM course WHERE courseNo IN (SELECT courseNo FROM coursetime WHERE id IN (SELECT courseId FROM stuselectcourse WHERE stuId=?)))", new MapListHandler(), id);
		return list;
	}

	@Override
	public Map<String, Object> readAnnouncementsDetails(int announcementId)
			throws SQLException {
		QueryRunner qr = new QueryRunner();
		Map<String, Object> map = qr.query(ManagerThreadLoacl.getConnection(), "SELECT announcementId,title,abstract,attachmentName,announcement.createdAt,publisherId FROM announcement WHERE announcementId=?", new MapHandler(), announcementId);
		return map;
	}

	@Override
	public Map<String, Object> attachmentDownload(int announcementId) throws SQLException {
		QueryRunner qr = new QueryRunner();
		Map<String, Object> attachment = qr.query(ManagerThreadLoacl.getConnection(), "SELECT attachmentName,attachmentURL FROM announcement WHERE announcementId=?", new MapHandler(), announcementId);
		
		return attachment;
	}

}
