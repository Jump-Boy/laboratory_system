package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;

public class DeleteSelectedStuServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		// 最终要写入响应的JSON（json）
		JSONObject json = new JSONObject();

		// 创建IO流读取请求正文
		BufferedReader br = new BufferedReader(new InputStreamReader(
				req.getInputStream(), "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String temp = "";
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();
		// 将读取到请求正文解析成JSON（jo）
		JSONObject jo = JSONObject.fromObject(sb.toString());
		
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		
		int courseId = Integer.parseInt(jo.getString("courseId"));
		long stuId = Long.parseLong(jo.getString("stuId"));
		if (roleFlag.equals("manager")) {
			ManaService ms = new ManaServiceImpl();
			try {
				ms.deleteFromSelectedStu(courseId, stuId);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("deleteResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				ts.deleteFromSelectedStu(courseId, stuId);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("deleteResult", te.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
