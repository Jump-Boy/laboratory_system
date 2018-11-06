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
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为登录的课程管理中的显示所有课程类，通过去调用不同的角色业务接口去完成显示所有课程的功能。管理员点击课程管理，可以显示所有的课程，而老师点击课程管理
 * ，只显示该教师自己的课程。每一行为一条课程记录，封装为Map，然后所有记录封装为List。（注意需要在数据库查询的时候做字段拼接，将开始周期和结束周期合到一块显示，
 * 将开始时间和结束时间合到一起显示）
 * @author 123
 *
 */
public class LoadAllCoursesServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		JSONArray ja = null;

		Teacher tea = new Teacher();
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
				jc.setExcludes(new String[] {"teaId","id"});
				ja = JSONArray.fromObject(courses, jc);
				json.put("courses", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long)userMap.get("id"));
			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> courses = ts.readAllCourses(tea);
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] {"teaId", "id"});
				ja = JSONArray.fromObject(courses, jc);
				json.put("courses", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				te.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
