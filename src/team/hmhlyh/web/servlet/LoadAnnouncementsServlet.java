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
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.StuException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.StuService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.StuServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
import team.hmhlyh.utils.DateJsonValueProcessor;

public class LoadAnnouncementsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		JSONArray ja = null;

		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");

		JsonConfig jc = new JsonConfig();
		// 注册时间格式处理器
		jc.registerJsonValueProcessor(java.sql.Date.class,
				new DateJsonValueProcessor("yyyy-MM-dd"));
		jc.setExcludes(new String[]{"attachment"});

		List<Map<String, Object>> announcements = null;
		if (roleFlag.equals("manager")) {
			ManaService ms = new ManaServiceImpl();
			try {
				announcements = ms.readAnnouncements();
				ja = JSONArray.fromObject(announcements, jc);
				json.put("announcements", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				announcements = ts.readAnnouncements((Long) userMap.get("id"));
				ja = JSONArray.fromObject(announcements, jc);
				json.put("announcements", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				te.printStackTrace();
			}
		} else if (roleFlag.equals("student")) {
			StuService ss = new StuServiceImpl();
			try {
				announcements = ss.readAnnouncements((Long) userMap.get("id"));
				ja = JSONArray.fromObject(announcements, jc);
				json.put("announcements", ja);
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
