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

import net.sf.json.JSON;
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

public class LoadScoreTableServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		//json数组
		JSON ja = null;

		Teacher tea = new Teacher();
		Student stu = new Student();
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
//			ManaService ms = new ManaServiceImpl();
//			try {
//				List<Map<String, Object>> scoreTable = ms.lookScoreTable();
//				ja = JSONArray.fromObject(scoreTable);
//				json.put("scoreTable", ja);
//				writer.write(json.toString());
//			} catch (ManaException me) {
//				writer.write(me.getMessage());
//				me.printStackTrace();
//		}
				Object[] obj = {};
			    json.put("scoreTable", obj);
			    writer.write(json.toString());
		} else if (roleFlag.equals("teacher")) {
//			
//			TeaService ts = new TeaServiceImpl();
//			try {
//				List<Map<String, Object>> scoreTable = ts.lookScoreTable();
//				ja = JSONArray.fromObject(scoreTable);
//				json.put("scoreTable", ja);
//				writer.write(json.toString());
//			} catch (TeaException te) {
//				writer.write(te.getMessage());
//				te.printStackTrace();
//			}
			Object[] obj = {};
		    json.put("scoreTable", obj);
		    writer.write(json.toString());
		} else if (roleFlag.equals("student")) {
			stu.setId((Long) userMap.get("id"));
			
			StuService ss = new StuServiceImpl();
			try {
				List<Map<String, Object>> scoreTable = ss.lookScoreTable(stu);
				ja = JSONArray.fromObject(scoreTable);
				json.put("scoreTable", ja);
				writer.write(json.toString());
			} catch (StuException se) {
				writer.write(se.getMessage());
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
