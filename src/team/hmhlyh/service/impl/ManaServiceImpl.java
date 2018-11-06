package team.hmhlyh.service.impl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import team.hmhlyh.dao.AnnounDao;
import team.hmhlyh.dao.CourDao;
import team.hmhlyh.dao.ManaDao;
import team.hmhlyh.dao.OpenDateDao;
import team.hmhlyh.dao.ScoreDao;
import team.hmhlyh.dao.StuDao;
import team.hmhlyh.dao.TeaDao;
import team.hmhlyh.dao.impl.AnnounDaoImpl;
import team.hmhlyh.dao.impl.CourDaoImpl;
import team.hmhlyh.dao.impl.ManaDaoImpl;
import team.hmhlyh.dao.impl.OpenDateDaoImpl;
import team.hmhlyh.dao.impl.ScoreDaoImpl;
import team.hmhlyh.dao.impl.StuDaoImpl;
import team.hmhlyh.dao.impl.TeaDaoImpl;
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Manager;
import team.hmhlyh.domain.Student;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.utils.ManagerThreadLoacl;

/**
 * ManaServicesImpl实现了ManaServices接口中的方法，实现后的方法中使用try，catch块进行日志记录，所以不将异常向上抛出
 * 
 * @author 123
 * 
 */
public class ManaServiceImpl implements ManaService {

	ManaDao manaDao = new ManaDaoImpl();
	TeaDao teaDao = new TeaDaoImpl();
	StuDao stuDao = new StuDaoImpl();
	CourDao courDao = new CourDaoImpl();
	ScoreDao scoreDao = new ScoreDaoImpl();
	OpenDateDao openDateDao = new OpenDateDaoImpl();
	AnnounDao announDao = new AnnounDaoImpl();

	@Override
	public Manager login(Manager mana) throws ManaException {
		Manager manager = null;
		try {
			manager = manaDao.findManaById(mana.getId());
			if (manager == null) {// 如果未查到，则抛出ManaException异常，错误信息为notFound
				throw new ManaException("notFound");
			} else if (!mana.getPassword().equals(manager.getPassword())) {// 如果密码不正确，则抛出ManaException异常，错误信息为error
				throw new ManaException("error");
			}
		} catch (SQLException e) {// 注意这里只catch SQLException
			e.printStackTrace();
		}
		return manager;
	}

	@Override
	public void setOpenDate(Date date) throws ManaException {
		try {
			if (openDateDao.readOpenDate() == null) {
				int num = openDateDao.setOpenDate(date);
				if (num == 0) {
					throw new ManaException("failure");
				}
			} else {
				int num = openDateDao.updateOpenDate(date);
				if (num == 0) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Map<String, Object> readOpenDate() {
		Map<String, Object> map = new HashMap<String, Object>();
		Date date = null;
		try {
			date = openDateDao.readOpenDate();
			if (date == null) {
				map.put("noStartTime", true);
				map.put("termStartDate", null);
				map.put("settedStartTime", true);
			} else {// 有时间的情况下，判断出是否可修改
				map.put("noStartTime", false);
				map.put("termStartDate", date.toString());
				if (openDateDao.getResidueTime() < 24) {
					map.put("settedStartTime", false);
				} else {
					map.put("settedStartTime", true);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public void addAnnouncement(String title, String abstractContent,
			String attachmentName, String attachmentURL, long publisherId)
			throws ManaException {
		try {
			int num = announDao.addAnnouncement(title, abstractContent,
					attachmentName, attachmentURL, publisherId);
			if (num == 0) {
				throw new ManaException("failure");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ManaException("failure");
		}

	}

	@Override
	public List<Map<String, Object>> readAnnouncements() throws ManaException {
		// 最终list
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		try {
			List<Map<String, Object>> listFromMana = announDao
					.readAnnouncementsFromMana();
			List<Map<String, Object>> listFromTea = announDao
					.readAnnouncementsFromTeaByMana();
			// 通过迭代器遍历集合将每一条记录放入总list中，遍历过程中，判断附件，初始化map的isHaveAttachment判断标志，true代表有附件，false无附件
			Iterator<Map<String, Object>> iter = listFromMana.iterator();
			while (iter.hasNext()) {
				Map<String, Object> map = iter.next();
				if (map.get("attachmentName") != null) {
					map.put("isHaveAttachment", true);
				} else {
					map.put("isHaveAttachment", false);
				}
				list.add(map);
			}
			iter = listFromTea.iterator();
			while (iter.hasNext()) {
				Map<String, Object> map = iter.next();
				if (map.get("attachmentName") != null) {
					map.put("isHaveAttachment", true);
				} else {
					map.put("isHaveAttachment", false);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ManaException("数据库异常");
		}

		return list;
	}

	@Override
	public Map<String, Object> readAnnouncementsDetails(int announcementId)
			throws ManaException {
		Map<String, Object> map = null;
		try {
			map = announDao.readAnnouncementsDetails(announcementId);
			if (map == null) {
				throw new ManaException("查询异常");
			} else {
				long id = (Long) map.get("publisherId");
				System.out.println(id);
				String name = manaDao.findManaNameById(id);
				if (name == null) {
					name = teaDao.findTeaNameById(id);
					if (name == null) {
						throw new ManaException("查询异常");
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
	public Map<String, Object> attachmentDownload(int announcementId)
			throws ManaException {
		Map<String, Object> attachment = null;
		try {
			attachment = announDao.attachmentDownload(announcementId);
			if (attachment == null) {
				throw new ManaException("数据异常");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attachment;
	}

	@Override
	public List<Map<String, Object>> readAllRoles() throws ManaException {
		// 最终返回的是包含所有管理员记录的list，所有教师记录的list和所有学生记录的list的list集合，需要手动的将3个list放到一个大list中去
		List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> managers = manaDao.readAllManagers();
			for (int i = 0; i < managers.size(); i++) {
				roles.add(managers.get(i));
			}
			List<Map<String, Object>> teachers = teaDao.readAllTeachers();
			for (int j = 0; j < teachers.size(); j++) {
				roles.add(teachers.get(j));
			}
			List<Map<String, Object>> students = stuDao.readAllStudents();
			for (int k = 0; k < students.size(); k++) {
				roles.add(students.get(k));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roles;
	}

	@Override
	public void addRole(Object obj) throws ManaException {
		try {
			if (obj instanceof Manager) {
				Manager mana = (Manager) obj;
				Manager manager = null;
				manager = manaDao.findManaById(mana.getId());
				if (manager == null) {
					int num = manaDao.addManager(mana);
					if (num == 0) {
						throw new SQLException("数据库异常导致插入信息失败");
					}
				} else {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Teacher) {
				Teacher tea = (Teacher) obj;
				Teacher teacher = null;
				teacher = teaDao.findTeaById(tea.getId());
				if (teacher == null) {
					int num = teaDao.addTeacher(tea);
					if (num == 0) {
						throw new SQLException("数据库异常导致插入信息失败");
					}
				} else {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Student) {
				Student stu = (Student) obj;
				Student student = null;
				student = stuDao.findStuById(stu.getId());
				if (student == null) {
					int num = stuDao.addStudent(stu);
					if (num == 0) {
						throw new SQLException("数据库异常导致插入信息失败");
					}
				} else {
					throw new ManaException("failure");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void resetPassword(Object obj) throws ManaException {
		try {
			if (obj instanceof Manager) {
				Manager mana = (Manager) obj;
				int num = manaDao.resetPassword(mana.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Teacher) {
				Teacher tea = (Teacher) obj;
				int num = teaDao.resetPassword(tea.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Student) {
				Student stu = (Student) obj;
				int num = stuDao.resetPassword(stu.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deleteRole(Object obj) throws ManaException {
		try {
			if (obj instanceof Manager) {
				Manager mana = (Manager) obj;
				int num = manaDao.deleteManager(mana.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Teacher) {
				Teacher tea = (Teacher) obj;
				int num = teaDao.deleteTeacher(tea.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			} else if (obj instanceof Student) {
				Student stu = (Student) obj;
				int num = stuDao.deleteStudent(stu.getId());
				if (num == 0) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> readAllCourses() throws ManaException {
		List<Map<String, Object>> courses = null;
		try {
			courses = courDao.readAllCoursesByMana();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	// @Override
	// public void addCourse(Course cour, long id) throws ManaException {
	// ManagerThreadLoacl.startTransacation();//开启事务
	// try {
	// Map<String, Object> map = courDao.findCourse(cour);
	// if (map == null) {
	// int num = courDao.addToCourse(cour);
	// num += courDao.addToCourseTime(cour, id);
	// if (num != 2) {
	// // 不等于2行，说明未匹配到该id,否则修改成功
	// throw new SQLException("用户信息错误，页面数据id异常");
	// }
	// } else {
	// throw new ManaException("failure");
	// }
	// } catch (SQLException e) {
	// ManagerThreadLoacl.rollback();//回滚
	// e.printStackTrace();
	// }
	// ManagerThreadLoacl.commit();//提交
	// ManagerThreadLoacl.close();//关闭资源
	// }

	@Override
	public void modifyCourse(Course cour) throws ManaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			int num = courDao.modifyCourse(cour);
			num += courDao.modifyCourseTime(cour);
			if (num != 2) {
				// 不等于2行，说明未匹配到该id,否则修改成功
				throw new SQLException("用户信息错误，页面数据id异常");
			} else {
				throw new ManaException("failure");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			e.printStackTrace();
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public void deleteCourse(Course cour) throws ManaException {
		try {
			int num = courDao.deleteCourse(cour.getId());
			if (num == 0) {
				throw new ManaException("failure");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> lookSelectedStu(int courseId)
			throws ManaException {
		List<Map<String, Object>> selectedStuTable = null;
		try {
			selectedStuTable = courDao.readSelectedStu(courseId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectedStuTable;
	}

	@Override
	public void deleteFromSelectedStu(int courseId, long stuId)
			throws ManaException {
		try {
			int num = courDao.deleteFromSelectedStu(courseId, stuId);
			if (num == 0) {
				throw new SQLException("数据库删除异常");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> readClassSchedule() throws ManaException {
		List<Map<String, Object>> classSchedule = null;
		try {
			classSchedule = courDao.readClassScheduleByMana();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classSchedule;
	}

	@Override
	public Manager readPersonInfo(Manager mana) throws ManaException {
		Manager manager = null;
		try {
			manager = manaDao.findManaById(mana.getId());
			if (manager == null) {// 进入系统后，id不应当错误，所以这里不抛业务ManaException，而抛SQLException
				throw new SQLException("用户信息错误，页面数据id异常");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return manager;
	}

	@Override
	public void modifyPersonInfo(Manager mana) throws ManaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			int num = manaDao.modifyPersonInfo(mana);
			num += manaDao.modifyPicURL(mana);
			if (num != 2) {
				// 不等于2行，说明未匹配到该id,否则修改成功
				throw new SQLException("用户信息错误，页面数据id异常");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			e.printStackTrace();
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public void modifyPassword(Manager mana, String newPassword)
			throws ManaException {
		Manager manager = null;
		try {
			manager = manaDao.findManaById(mana.getId());
			if (manager == null) {
				// manager == null说明未匹配该id，前端返回的id错误（必须判断，否则判断原密码时会抛空异常）
				throw new SQLException("用户信息错误，页面数据id异常");
			} else if (!mana.getPassword().equals(manager.getPassword())) {
				// 原始密码输入错误
				throw new ManaException("false");
			} else {
				int num = manaDao.modifyPassword(mana.getId(), newPassword);
				if (num == 0) {
					// 0行，说明未匹配到该id,否则修改成功
					throw new SQLException("无此id，页面数据错误");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> readUseForTotalMark() throws ManaException {
		List<Map<String, Object>> totalList = null;
		try {
			totalList = scoreDao.readForMarkTotalByMana();
			List<Map<String, Object>> courStus = scoreDao
					.readAllCourStuByMana();
			if (courStus.size() == 0) {
				throw new ManaException("failure");
			} else if (totalList.size() != courStus.size()) {

				for (Map<String, Object> map : courStus) {
					int courseNo = (Integer) map.get("courseNo");
					long stuId = (Long) map.get("stuId");
					// 当点击成绩管理后，确保总成绩表中有有记录，若无，则先插入
					scoreDao.iniTotalScore(courseNo, stuId);
					totalList = scoreDao.readForMarkTotalByMana();
				}
			}
			for (Map<String, Object> map : totalList) {
				int courseNo = (Integer) map.get("courseNo");
				long stuId = (Long) map.get("stuId");
				int truancyNum = scoreDao.getTruancy(courseNo, stuId);
				int vacateNum = scoreDao.getVacateNum(courseNo, stuId);
				int referScore = scoreDao.getReferScore(courseNo, stuId);
				map.put("vacateNum", vacateNum);
				map.put("truancyNum", truancyNum);
				map.put("referScore", referScore);
				map.put("teaName",
						teaDao.findTeaNameById((Long) map.get("teaId")));
				map.put("markSign", false);// “打分”按钮显示方式的判断标志

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalList;
	}

	@Override
	public List<Map<String, Object>> readGroupUseForTotalMark(String groupGrade)
			throws ManaException {
		List<Map<String, Object>> totalList = null;
		try {
			if (groupGrade.equals("全部")) {
				totalList = scoreDao.readForMarkTotalByMana();
			} else {
				totalList = scoreDao.readGroupForMarkTotalByMana(groupGrade);
				if (totalList.size() == 0) {
					throw new ManaException("failure");
				} else {
					for (Map<String, Object> map : totalList) {
						int courseNo = (Integer) map.get("courseNo");
						long stuId = (Long) map.get("stuId");
						// 当点击成绩管理后，确保总成绩表中有有记录，若无，则先插入
						scoreDao.iniTotalScore(courseNo, stuId);
						int vacateNum = scoreDao.getVacateNum(courseNo, stuId);
						int truancyNum = scoreDao.getTruancy(courseNo, stuId);
						int referScore = scoreDao
								.getReferScore(courseNo, stuId);
						map.put("vacateNum", vacateNum);
						map.put("truancyNum", truancyNum);
						map.put("referScore", referScore);
						map.put("teaName",
								teaDao.findTeaNameById((Long) map.get("teaId")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalList;
	}

	@Override
	public List<Map<String, Object>> lookScoreTable() throws ManaException {
		List<Map<String, Object>> scoreTable = null;
		try {
			scoreTable = scoreDao.lookScoreTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookGroupScoreTable(String studySemester)
			throws ManaException {
		List<Map<String, Object>> scoreTable = null;
		try {
			scoreTable = scoreDao.lookGroupScoreTable(studySemester);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreTable;
	}

	@Override
	public List<Map<String, Object>> lookScoreDetail(int totalScoreId)
			throws ManaException {
		List<Map<String, Object>> scoreDetail = null;
		try {
			scoreDetail = scoreDao.lookScoreDetail(totalScoreId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreDetail;
	}

	@Override
	public void addManagers(Object[][] params) throws ManaException {
		int[] num = null;
		try {
			num = manaDao.addManagers(params);
			if (num.length != params.length) {
				throw new ManaException("failure");
			}
			for (int i = 0; i < num.length; i++) {
				if (num[i] != 1) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ManaException("failure");// 比如已经存在该角色（id重复），因为有主键约束，所以会捕捉到sqlexception，但应该也是添加失败，所以这里抛出ManaException
		}
	}

	@Override
	public void addTeachers(Object[][] params) throws ManaException {
		int[] num = null;
		try {
			num = teaDao.addTeachers(params);
			if (num.length != params.length) {
				throw new ManaException("failure");
			}
			for (int i = 0; i < num.length; i++) {
				if (num[i] != 1) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ManaException("failure");
		}
	}

	@Override
	public void addStudents(Object[][] params) throws ManaException {
		int[] num = null;
		try {
			num = stuDao.addStudents(params);
			if (num.length != params.length) {
				throw new ManaException("failure");
			}
			for (int i = 0; i < num.length; i++) {
				if (num[i] != 1) {
					throw new ManaException("failure");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ManaException("failure");
		}
	}

}
