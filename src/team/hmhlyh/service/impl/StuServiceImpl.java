package team.hmhlyh.service.impl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import team.hmhlyh.dao.AnnounDao;
import team.hmhlyh.dao.CourDao;
import team.hmhlyh.dao.ManaDao;
import team.hmhlyh.dao.ScoreDao;
import team.hmhlyh.dao.StuDao;
import team.hmhlyh.dao.TeaDao;
import team.hmhlyh.dao.impl.AnnounDaoImpl;
import team.hmhlyh.dao.impl.CourDaoImpl;
import team.hmhlyh.dao.impl.ManaDaoImpl;
import team.hmhlyh.dao.impl.ScoreDaoImpl;
import team.hmhlyh.dao.impl.StuDaoImpl;
import team.hmhlyh.dao.impl.TeaDaoImpl;
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Student;
import team.hmhlyh.exception.StuException;
import team.hmhlyh.service.StuService;
import team.hmhlyh.utils.ManagerThreadLoacl;
/**
 * StuServicesImpl实现了StuServices接口中的方法，实现后的方法中使用try，catch块进行日志记录，所以不将异常向上抛出
 * @author 123
 *
 */
public class StuServiceImpl implements StuService {

	ManaDao manaDao = new ManaDaoImpl();
	TeaDao teaDao = new TeaDaoImpl();
	StuDao stuDao = new StuDaoImpl();
	CourDao courDao = new CourDaoImpl();
	ScoreDao scoreDao = new ScoreDaoImpl();
	AnnounDao announDao = new AnnounDaoImpl();
	
	@Override
	public Student login(Student stu) throws StuException{
		Student student = null;
		try {
			//findStuById()返回的为一个Student对象
			student = stuDao.findStuById(stu.getId());
			if (student == null) {//如果未查到，则抛出StuException异常，错误信息为notFound
				throw new StuException("notFound");
			} else if (!stu.getPassword().equals(student.getPassword())) {//如果密码不正确，则抛出StuException异常，错误信息为error
				throw new StuException("error");
			}
		} catch (SQLException e) {//注意这里只catch SQLException
			e.printStackTrace();
		}
		return student;
	}
	
	@Override
	public List<Map<String, Object>> readAnnouncements(long id) throws StuException {
		//最终list
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		try {
			List<Map<String, Object>> listFromMana = announDao.readAnnouncementsFromMana();
			List<Map<String, Object>> listFromTea = announDao.readAnnouncementsByStu(id);
			//通过迭代器遍历集合将每一条记录放入总list中，遍历过程中，判断附件，初始化map的isHaveAttachment判断标志，true代表有附件，false无附件
			Iterator<Map<String,Object>> iter = listFromMana.iterator();
			while (iter.hasNext()) {
				Map<String,Object> map = iter.next();
				if (map.get("attachmentName") != null) {
					map.put("isHaveAttachment", true);
				} else {
					map.put("isHaveAttachment", false);
				}
				list.add(map);
			}
			iter = listFromTea.iterator();
			while (iter.hasNext()) {
				Map<String,Object> map = iter.next();
				if (map.get("attachmentName") != null) {
					map.put("isHaveAttachment", true);
				} else {
					map.put("isHaveAttachment", false);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new StuException("数据库异常");
		}
		
		return list;
	}
	
	@Override
	public Map<String, Object> readAnnouncementsDetails(int announcementId)
			throws StuException {
		Map<String, Object> map = null;
		try {
		    map = announDao.readAnnouncementsDetails(announcementId);
			if (map == null) {
				throw new StuException("查询异常");
			} else {
				long id = (Long)map.get("publisherId");
				String name = manaDao.findManaNameById(id);
				if (name == null) {
					name = teaDao.findTeaNameById(id);
					if (name == null) {
						throw new StuException("查询异常");
					}
					map.put("name", name);
				} else {
					map.put("name", name);
				}
				if (map.get("attachmentName") != null) {
					map.put("isHaveAttachment", true);
				} else {
					map.put("isHaveAttachment", false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String, Object> attachmentDownload(int announcementId) throws StuException {
		Map<String, Object> attachment = null;
		try {
			attachment = announDao.attachmentDownload(announcementId);
			if (attachment == null) {
				throw new StuException("数据异常");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attachment;
	}
	@Override
	public List<Map<String, Object>> readAllCourses(Student stu) throws StuException {
		List<Map<String, Object>> courses = null;
		try {
			int num = stuDao.autoSetGrade(stu.getId());
			if (num == 0) {
				throw new StuException("自动校正年级失败");
			}
			courses = courDao.readAllCoursesByStu(stu);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
    }

	@Override
	public void selectCourse(Course cour, Student stu) throws StuException {
		try {
			int num = courDao.selectCourse(cour, stu.getId());
			if (num == 0) {
				throw new StuException("failure");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> lookSelectedStu(int courseId)
			throws StuException {
		List<Map<String, Object>> selectedStuTable = null;
		try {
			selectedStuTable = courDao.readSelectedStu(courseId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectedStuTable;
	}

	@Override
	public List<Map<String, Object>> readClassSchedule(Student stu)
			throws StuException {
		List<Map<String, Object>> classSchedule = null;
		try {
			classSchedule = courDao.readClassScheduleByStu(stu.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classSchedule;
	}

	@Override
	public List<Map<String, Object>> lookScoreTable(Student stu)
			throws StuException {
		List<Map<String, Object>> scoreTable = null;
		try {
			scoreTable = scoreDao.lookScoreTableByStu(stu.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookGroupScoreTable(Student stu,
			String studySemester) throws StuException {
		List<Map<String, Object>> scoreTable = null;
		try {
			scoreTable = scoreDao.lookGroupScoreTableByStu(stu.getId(), studySemester);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId)
			throws StuException {
		List<Map<String, Object>> scoreDetail = null;
		try {
			scoreDetail = scoreDao.lookScoreDetail(totalScoreId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreDetail;
	}

	@Override
	public Student readPersonInfo(Student stu) throws StuException {
		Student student = null;
		try {
			student = stuDao.findStuById(stu.getId());
			if (student == null) {//进入系统后，id不应当错误，所以这里不抛业务StuException，而抛SQLException
				throw new SQLException("用户信息错误，页面数据id异常");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return student;
	}

	@Override
	public void modifyPersonInfo(Student stu) throws StuException {
		ManagerThreadLoacl.startTransacation();//开启事务
//		if (stu.getPicURL()) {
			
		try {
				int num = stuDao.modifyPersonInfo(stu);
				num += stuDao.modifyPicURL(stu);
				num += stuDao.autoSetGrade(stu.getId());
				if (num != 3 ) {
					//不是2行，说明未匹配到该id,否则修改成功
					throw new SQLException("用户信息错误，页面数据id异常");
				}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();//回滚
			e.printStackTrace();
		}
//		}
		ManagerThreadLoacl.commit();//提交
		ManagerThreadLoacl.close();//关闭资源
	}
	
	@Override
	public void modifyPassword(Student stu, String newPassword) throws StuException {
		Student student = null;
		try {
			student = stuDao.findStuById(stu.getId());
			if (student == null) {
				//stuent == null说明未匹配该id，前端返回的id错误
				throw new SQLException("用户信息错误，页面数据id异常");
			} else if (!stu.getPassword().equals(student.getPassword())) {
				//原始密码输入错误
				throw new StuException("false");
			} else {
				int num = stuDao.modifyPassword(stu.getId(), newPassword);
				if (num == 0) {
					//0行，说明未匹配到该id,否则修改成功
					throw new SQLException("无此id，页面数据错误");
				} 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
