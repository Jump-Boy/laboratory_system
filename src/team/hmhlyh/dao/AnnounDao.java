package team.hmhlyh.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AnnounDao {

	//新增公告
	public int addAnnouncement(String title, String abstractContent, String attachmentName, String attachmentURL, long publisherId) throws SQLException;

	//删除公告
	int deleteAnnouncement(long var1) throws SQLException;

	int deleteAnnouncementByTea(long var1, long var3) throws SQLException;

	//管理员读取所有公告
	public List<Map<String, Object>> readAnnouncementsFromMana() throws SQLException;
	
	//管理员读取所有公告
	public List<Map<String, Object>> readAnnouncementsFromTeaByMana() throws SQLException;
	
	//教师读取公告
	public List<Map<String, Object>> readAnnouncementsFromTeaByTea(long id) throws SQLException;
	
	//学生读取所有公告
	public List<Map<String, Object>> readAnnouncementsByStu(long id) throws SQLException;
	
	//公告详情
	public Map<String, Object> readAnnouncementsDetails(int announcementId) throws SQLException;
	
	//附件下载
	public Map<String, Object> attachmentDownload(int announcementId) throws SQLException;
	
}
