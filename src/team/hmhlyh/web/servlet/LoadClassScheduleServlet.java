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
 * 该类为查看课表的Servlet类，通过去调用不同的角色业务接口去完成查看课表的功能
 * @author 123
 *
 */
public class LoadClassScheduleServlet extends HttpServlet {

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
				List<Map<String, Object>> classSchedule = ms.readAllCourses();
				ja = JSONArray.fromObject(classSchedule);
				json.put("classSchedule", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long)userMap.get("id"));
			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> classSchedule = ts.readClassSchedule(tea);
				ja = JSONArray.fromObject(classSchedule);
				json.put("classSchedule", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				te.printStackTrace();
			} 
		}else if (roleFlag.equals("student")) {
			stu.setId((Long)userMap.get("id"));
			
			StuService ss = new StuServiceImpl();
			try {
				List<Map<String, Object>> classSchedule = ss.readClassSchedule(stu);
				ja = JSONArray.fromObject(classSchedule);
				json.put("classSchedule", ja);
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
