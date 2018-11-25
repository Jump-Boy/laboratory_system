package team.hmhlyh.service.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import team.hmhlyh.dao.AnnounDao;
import team.hmhlyh.dao.CourDao;
import team.hmhlyh.dao.ManaDao;
import team.hmhlyh.dao.ScoreDao;
import team.hmhlyh.dao.TeaDao;
import team.hmhlyh.dao.impl.AnnounDaoImpl;
import team.hmhlyh.dao.impl.CourDaoImpl;
import team.hmhlyh.dao.impl.ManaDaoImpl;
import team.hmhlyh.dao.impl.ScoreDaoImpl;
import team.hmhlyh.dao.impl.TeaDaoImpl;
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.utils.ManagerThreadLoacl;

/**
 * TeaServicesImpl实现了TeaServices接口中的方法，实现后的方法中使用try，catch块进行日志记录，所以不将异常向上抛出
 * 
 * @author 123
 * 
 */
public class TeaServiceImpl implements TeaService {

	TeaDao teaDao = new TeaDaoImpl();
	ManaDao manaDao = new ManaDaoImpl();
	CourDao courDao = new CourDaoImpl();
	ScoreDao scoreDao = new ScoreDaoImpl();
	AnnounDao announDao = new AnnounDaoImpl();
	
	@Override
	public Teacher login(Teacher tea) throws TeaException {
		Teacher teacher = null;
		try {
			teacher = teaDao.findTeaById(tea.getId());
			if (teacher == null) {
				throw new TeaException("notFound");// 如果未查到，则抛出TeaException异常，错误信息为notFound
			} else if (!tea.getPassword().equals(teacher.getPassword())) {
				throw new TeaException("error");// 如果密码不正确，则抛出TeaException异常，错误信息为error
			}
		} catch (SQLException e) {// 注意这里只catch SQLException
			e.printStackTrace();
		}
		return teacher;
	}

	@Override
	public void addAnnouncement(String title, String abstractContent,
			String attachmentName,String attachmentURL, long publisherId) throws TeaException {
		try {
			int num = announDao.addAnnouncement(title, abstractContent, attachmentName,attachmentURL, publisherId);
			if (num == 0) {
				throw new TeaException("failure");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TeaException("failure");
		}
	}

	public void deleteAnnouncement(long id, long teaId) throws TeaException {
		try {
			int num = this.announDao.deleteAnnouncementByTea(id, teaId);
			if (num == 0) {
				throw new TeaException("failure");
			}
		} catch (SQLException var6) {
			var6.printStackTrace();
		}

	}

	@Override
	public List<Map<String, Object>> readAnnouncements(long id) throws TeaException {
		//最终list
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		try {
			List<Map<String, Object>> listFromMana = announDao.readAnnouncementsFromMana();
			List<Map<String, Object>> listFromTea = announDao.readAnnouncementsFromTeaByTea(id);
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
			throw new TeaException("数据库异常");
		}
		
		return list;
	}
	
	@Override
	public Map<String, Object> readAnnouncementsDetails(int announcementId)
			throws TeaException {
		Map<String, Object> map = null;
		try {
		    map = announDao.readAnnouncementsDetails(announcementId);
			if (map == null) {
				throw new TeaException("查询异常");
			} else {
				long id = (Long)map.get("publisherId");
				String name = manaDao.findManaNameById(id);
				if (name == null) {
					name = teaDao.findTeaNameById(id);
					if (name == null) {
						throw new TeaException("查询异常");
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
	public Map<String, Object> attachmentDownload(int announcementId) throws TeaException {
		Map<String, Object> attachment = null;
		try {
			attachment = announDao.attachmentDownload(announcementId);
			if (attachment == null) {
				throw new TeaException("数据异常");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attachment;
	}
	
	@Override
	public List<Map<String, Object>> readAllCourses(Teacher tea)
			throws TeaException {
		List<Map<String, Object>> courses = null;
		try {
			courses = courDao.readAllCoursesByTea(tea.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return courses;
	}

	@Override
	public void addCourse(Course cour, Teacher tea) throws TeaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			List<Map<String, Object>> list = courDao.findCourse(cour);
			// 注意查询结果得到的集合不会为null，只会为[]，也就是说集合大小为0，但不为null
			if (list.size() == 0) {
				list = courDao.courseIsHaven(cour, tea.getId());
				// 除了保证上课时间等信息不能重复外，还要分情况，如果总课程，即courseNo这学期以存在则只需insert
				// courseTime，若不存在，则先insert course
				if (list.size() == 0) {
					int num = courDao.addToCourse(cour);
					num += courDao.addToCourseTime(cour, tea.getId());
					if (num != 2) {
						// 不等于2行，说明未匹配到该id,否则修改成功
						throw new SQLException("用户信息错误，页面数据id异常");
					}
				} else {
					int num = courDao.addToCourseTime(cour, tea.getId());
					if (num != 1) {
						// 不等于1行，说明未匹配到该id,否则修改成功
						throw new SQLException("用户信息错误，页面数据id异常");

					}
				}
			} else {
				throw new TeaException("failure");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			e.printStackTrace();
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public void modifyCourse(Course cour) throws TeaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			int num = 0;
			List<Map<String, Object>> list = courDao.findCourse(cour);
			if (list.size() == 0) {

				num = courDao.modifyCourse(cour);
				num += courDao.modifyCourseTime(cour);
			} else {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					// 将课程id转成字符串后比较（若时间冲突，只需要比较id，若课程id一样，则只需要更新原课程的内容，因为时间冲突，说明除过时间其他内容改变了）
					String mapStr = "" + map.get("id");
					String courStr = "" + cour.getId();
					if (mapStr.equals(courStr)) {
						num = courDao.modifyCourse(cour);
						num += courDao.modifyCourseTime(cour);
						break;
					}
				}
			}
			if (num != 2) {
				// 不等于2行，说明未匹配到该id,否则修改成功
				throw new SQLException("用户信息错误，页面数据id异常");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			throw new TeaException("failure");
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public void deleteCourse(Course cour) throws TeaException {
		try {
			int num = courDao.deleteCourse(cour.getId());
			if (num == 0) {
				throw new TeaException("failure");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> lookSelectedStu(int courseId)
			throws TeaException {
		List<Map<String, Object>> selectedStuTable = null;
		try {
			selectedStuTable = courDao.readSelectedStu(courseId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return selectedStuTable;
	}

	@Override
	public List<Map<String, Object>> readClassSchedule(Teacher tea)
			throws TeaException {
		List<Map<String, Object>> classSchedule = null;
		try {
			classSchedule = courDao.readClassScheduleByTea(tea.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classSchedule;
	}

	@Override
	public void deleteFromSelectedStu(int courseId, long stuId)
			throws TeaException {
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
	public List<Map<String, Object>> readUseForEveryMark(Teacher tea,
			String week, byte currentWeeks) throws TeaException {
		String weeks = "第" + currentWeeks + "周";
		String teaName = "";
		List<Map<String, Object>> list = null;
		try {
			// 先判断是否有课程，有则先初始化stuusualscore
			list = scoreDao.findHavenCourse(tea.getId(), week, currentWeeks);
			if (list.size() == 0) {
				throw new TeaException("failure");
			} else {
				System.out.println("删除前list-------");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					System.out.println(map.get("stuId") + "" + map.get("courseId"));
				}

				// 先验证平时成绩表中是否有该记录，有则从list中移除（在参数化的时候，则无需加进去），即无需再初始化该条记录，否则无，则初始化。
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					Map<String, Object> usualMap = scoreDao
							.findHavenUsualScore((Long) map.get("stuId"),
									(Integer) map.get("courseId"),weeks);
					if (usualMap != null)
						list.remove(i--);//remove后原本第二个元素变成了第一个，但下一次循环却从第二个开始，所以要i--
				}
				System.out.println("删除后list-------");
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					System.out.println(map.get("stuId") + "" + map.get("courseId"));
				}

				Object[][] params = new Object[list.size()][];
				// for (int i = 0; i < params.length; i++) {
				// params[i][0] = new Object();
				// params[i][1] = new Object();
				// params[i][2] = new Object();
				// }
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					params[i] = new Object[] { map.get("stuId"),
							map.get("courseId"), "第" + currentWeeks + "周" };
					// params[0][0] = map.get("stuId");
					// params[i][1] = map.get("courseId");
					// params[i][2] = currentWeeks;
				}
				int[] num = scoreDao.iniUsualScore(params);
				if (num.length != params.length) {
					throw new SQLException("数据库异常，初始化随堂成绩异常");
				} else {
					for (int i = 0; i < num.length; i++) {
						if (num[i] != 1) {
							throw new SQLException("数据库异常，初始化随堂成绩异常");
						}
					}
				}
			}
			list = scoreDao.readForMarkEvery(tea.getId(), week, currentWeeks);
			teaName += teaDao.findTeaNameById(tea.getId());
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				// map.put("currentWeeks", currentWeeks);
				map.put("teaName", teaName);
				map.put("markSign", false);//打分按钮的判断标志
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// @Override
	// public void markEveryClass(List<Map<String, Object>> paramList)
	// throws TeaException {
	// ManagerThreadLoacl.startTransacation();// 开启事务
	// int[] usualGradeIds = new int[paramList.size()];
	// for (int i = 0; i < paramList.size(); i++) {
	// usualGradeIds[i] = (Integer) paramList.get(i).get("usualGradeId");
	// }
	// String[] signUps = null;
	// try {
	// //获取注册状态，因为通过id数组按顺序查看，所以请假和旷课也都一一对应
	// signUps = scoreDao.readSignUpState(usualGradeIds);
	// //请假
	// String[] truancys = new String[signUps.length];
	// //旷课
	// String[] vacates = new String[signUps.length];
	// byte[] usualGrades = new byte[signUps.length];
	// for (int i = 0; i < signUps.length; i++) {
	// vacates[i] = (String) paramList.get(i).get("vacate");
	// usualGrades[i] = (Byte) paramList.get(i).get("usualGrade");
	// // 通过判断是否请假和签到,来确定是否旷课
	// if (signUps[i].equals("true")) {
	// truancys[i] = "false";
	// } else if (signUps[i].equals("false")
	// && vacates[i].equals("true")) {
	// truancys[i] = "false";
	// } else if (signUps[i].equals("false")
	// && vacates[i].equals("false")) {
	// truancys[i] = "true";
	// }
	// }
	// int[] num = scoreDao.markEvery(vacates, truancys, usualGrades,
	// usualGradeIds);
	// for (int i : num) {
	// if (i != 1) {
	// throw new TeaException("failure");
	// }
	// }
	// } catch (SQLException e) {
	// ManagerThreadLoacl.rollback();// 回滚
	// e.printStackTrace();
	// }
	// ManagerThreadLoacl.commit();// 提交
	// ManagerThreadLoacl.close();// 关闭资源
	// }

	@Override
	public void markEveryClass(byte usualScore, int usualScoreId)
			throws TeaException {
		String vacate = "";
		String truancy = "";
		if (usualScore == 0) {
			vacate += "true";
			truancy += "false";
		} else if (usualScore == -1) {
			vacate += "false";
			truancy += "true";
		} else {
			vacate += "false";
			truancy += "false";
		}
		try {
			int num = scoreDao.markEvery(vacate, truancy, usualScore,
					usualScoreId);
			if (num == 0) {
				throw new SQLException("随堂打分数据插入异常");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Map<String, Object>> readUseForTotalMark(Teacher tea)
			throws TeaException {
		List<Map<String, Object>> totalList = null;
		try {
			//先通过教师id列出该教师课程所有的学生，若无内容则说明无学生，直接返回failure。否则跟成绩表中的比较，若内容不一致，在初始化插入。
			totalList = scoreDao.readForMarkTotalByTea(tea.getId());
			List<Map<String, Object>> courStus = scoreDao
					.readAllCourStuByTea(tea.getId());
			if (courStus.size() == 0) {
				throw new TeaException("failure");
			} else if (totalList.size() != courStus.size()) {
				for (Map<String, Object> map : courStus) {
					int courseNo = (Integer) map.get("courseNo");
					long stuId = (Long) map.get("stuId");
					// 当点击成绩管理后，确保总成绩表中有有记录，若无，则先插入（会判断表中是否已经有）
					scoreDao.iniTotalScore(courseNo, stuId);
					totalList = scoreDao.readForMarkTotalByTea(tea.getId());
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
				map.put("teaName", teaDao.findTeaNameById(tea.getId()));
				map.put("markSign", false);//“打分”按钮显示方式的判断标志

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalList;
	}

	@Override
	public List<Map<String, Object>> readGroupUseForTotalMark(Teacher tea,
			String groupGrade) throws TeaException {
		List<Map<String, Object>> totalList = null;
		try {
			//如果分组查询点的是全部，则直接调用“成绩管理”查询的接口，否则将想要分组查询的年级传入参数进行查询
			if (groupGrade.equals("全部")) {
				totalList = scoreDao.readForMarkTotalByTea(tea.getId());
			} else {
				totalList = scoreDao.readGroupForMarkTotalByTea(tea.getId(),
						groupGrade);
				if (totalList.size() == 0) {
					throw new TeaException("failure");
				} else {
					for (Map<String, Object> map : totalList) {
						int courseNo = (Integer) map.get("courseNo");
						long stuId = (Long) map.get("stuId");
						int vacateNum = scoreDao.getVacateNum(courseNo, stuId);
						int truancyNum = scoreDao.getTruancy(courseNo, stuId);
						int referScore = scoreDao
								.getReferScore(courseNo, stuId);
						map.put("vacateNum", vacateNum);
						map.put("truancyNum", truancyNum);
						map.put("referScore", referScore);
						map.put("teaName", teaDao.findTeaNameById(tea.getId()));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalList;
	}

	@Override
	public void markTotal(byte totalScore, int totalScoreId)
			throws TeaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			//注意数据库中boolean会自动转成tinyint(0,1)存储
			int num = scoreDao.markTotal(totalScore, totalScoreId);
			boolean pass = false;
			float gpa = 0;
			byte getCredit = 0;
			boolean isRebuild = false;
			// 如果总成绩大于60，则及格且获得课程相应学分，否则不及格，且获得学分为0
			if (totalScore >= 60) {
				pass = true;
				getCredit = courDao.getCredit(scoreDao
						.getCourseNo(totalScoreId));
			}
			//通过该课程的名字去查找，看以前该学生选过的课程中是否有过同样的课程名字
			Map<String, Object> map = scoreDao.readCourseByTotalId(totalScoreId);
			int courseNo = (Integer)map.get("courseNo");
			String courseName = (String)map.get("courseName");
			Timestamp createdAt = (Timestamp)map.get("createdAt");
			long stuId = scoreDao.readStuIdByTotalId(totalScoreId);
			String[] courseNames = scoreDao.readCourseNameByTotal(courseNo, stuId, createdAt);
			for (int i = 0; i < courseNames.length; i++) {
				if (courseNames[i].equals(courseName)) {
					isRebuild = true;
					break;
				} 
			}
			
//			if (scoreDao.isRebuild(totalScoreId) == 0) {
//				isRebuild += "false";
//			} else {
//				isRebuild += "true";
//			}
			if (totalScore > 90) {
				gpa = 4.0f;
			} else if (totalScore > 85) {
				gpa = 3.7f;
			} else if (totalScore > 82) {
				gpa = 3.3f;
			} else if (totalScore > 78) {
				gpa = 3.0f;
			} else if (totalScore > 75) {
				gpa = 2.7f;
			} else if (totalScore > 72) {
				gpa = 2.3f;
			} else if (totalScore > 68) {
				gpa = 2.0f;
			} else if (totalScore > 64) {
				gpa = 1.5f;
			} else if (totalScore > 60) {
				gpa = 1.3f;
			} else if (totalScore == 60 && isRebuild == true) {
				gpa = 1.0f;
			} else {
				gpa = 0;
			}
			num += scoreDao.setOtherUseTotal(pass, gpa, getCredit, isRebuild,
					totalScoreId);
			if (num != 2) {
				throw new TeaException("failure");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			e.printStackTrace();
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public Teacher readPersonInfo(Teacher tea) throws TeaException {
		Teacher teacher = null;
		try {
			teacher = teaDao.findTeaById(tea.getId());
			if (teacher == null) {// 进入系统后，id不应当错误，所以这里不抛业务TeaException，而抛SQLException
				throw new SQLException("用户信息错误，页面数据id异常，读取个人信息异常");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teacher;
	}

	@Override
	public void modifyPersonInfo(Teacher tea) throws TeaException {
		ManagerThreadLoacl.startTransacation();// 开启事务
		try {
			int num = teaDao.modifyPersonInfo(tea);
			num += teaDao.modifyPicURL(tea);
			if (num != 2) {
				// 不等于2行，说明未匹配到该id,否则修改成功
				throw new SQLException("用户信息错误，页面数据id异常，修改个人信息异常");
			}
		} catch (SQLException e) {
			ManagerThreadLoacl.rollback();// 回滚
			e.printStackTrace();
		}
		ManagerThreadLoacl.commit();// 提交
		ManagerThreadLoacl.close();// 关闭资源
	}

	@Override
	public void modifyPassword(Teacher tea, String newPassword)
			throws TeaException {
		Teacher teacher = null;
		try {
			teacher = teaDao.findTeaById(tea.getId());
			if (teacher == null) {
				// teacher == null说明未匹配该id，前端返回的id错误
				throw new SQLException("用户信息错误，页面数据id异常，修改密码异常");
			} else if (!tea.getPassword().equals(teacher.getPassword())) {
				// 原始密码输入错误
				throw new TeaException("false");
			} else {
				int num = teaDao.modifyPassword(tea.getId(), newPassword);
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
	public List<Map<String, Object>> lookScoreTable() throws TeaException {
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
			throws TeaException {
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
			throws TeaException {
		List<Map<String, Object>> scoreDetail = null;
		try {
			scoreDetail = scoreDao.lookScoreDetail(totalScoreId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scoreDetail;
	}

}
