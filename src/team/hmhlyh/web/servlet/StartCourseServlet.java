package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import team.hmhlyh.domain.Student;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.StuException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.StuService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.StuServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;

/**
 * 该类为点击开始选课时进行的可选课程的显示功能的Servlet类，通过去调用学生业务接口去完成可选课程显示功能。系统需要完成自动的判断学生情况，
 * 以筛选出适合学生 选择的课程。通过学生的专业和年级以筛选出适合学生的课程。（注意学生的年级在每次修改个人信息的时候做更新。）
 * 
 * @author 123
 * 
 */
public class StartCourseServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		JSONArray ja = null;

		Teacher tea = new Teacher();
		Student stu = new Student();
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
			ManaService ms = new ManaServiceImpl();
			try {
				List<Map<String, Object>> courses = ms.readAllCourses();
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "teaId", "id" });
				ja = JSONArray.fromObject(courses, jc);
				json.put("courses", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long) userMap.get("id"));
			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> courses = ts.readAllCourses(tea);
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "teaId", "id" });
				ja = JSONArray.fromObject(courses, jc);
				json.put("courses", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				te.printStackTrace();
			}
		} else if (roleFlag.equals("student")) {
			stu.setId((Long) userMap.get("id"));

			StuService ss = new StuServiceImpl();
			try {
				stu = ss.readPersonInfo(stu);
				List<Map<String, Object>> courses = ss.readAllCourses(stu);
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "teaId", "id" });
				ja = JSONArray.fromObject(courses, jc);
				json.put("courses", courses);
				writer.write(json.toString());
			} catch (StuException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
