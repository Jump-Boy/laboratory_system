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

import team.hmhlyh.domain.Manager;
import team.hmhlyh.domain.Student;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;

import net.sf.json.JSONObject;

public class ResetPasswordServlet extends HttpServlet {

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

		Manager mana = new Manager();
		Teacher tea = new Teacher();
		Student stu = new Student();

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

		String userType = jo.getString("userType");

		ManaService ms = new ManaServiceImpl();
		if (userType.equals("管理员")) {
			mana.setId(Long.parseLong(jo.getString("id")));
			try {
				ms.resetPassword(mana);
				json.put("resetResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("resetResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("教师")) {
			tea.setId(Long.parseLong(jo.getString("id")));
			try {
				ms.resetPassword(tea);
				json.put("resetResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("resetResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("学生")) {
			stu.setId(Long.parseLong(jo.getString("id")));
			try {
				ms.resetPassword(stu);
				json.put("resetResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("resetResult", me.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
