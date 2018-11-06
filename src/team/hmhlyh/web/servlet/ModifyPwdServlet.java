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

import team.hmhlyh.domain.Manager;
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
import team.hmhlyh.utils.MD5Util;
/**
 * 该类为修改密码的Servlet类，通过去调用不同的角色业务接口去完成修改密码功能.异常代表修改失败（原密码输入错误），否则修改密码成功。
 * 前端页面将输入的原密码，要修改后的密码以JSON通过请求访问服务器，这里通过解析请求正文中的JSON，获取相应字段的数据。从session中获取id和userType，
 * 将id和原密码封装到实体Bean中，作为参数通过调用相应接口，先判断id是否存在，在判断原密码是否输入正确，若正确将新密码更新到数据库中（注意更新为MD5加密格式），
 * 并将{“modifyResult”：true}写入响应，否则原密码错误，将{“modifyResult”：false}写入响应，传回前端页面。
 * @author 123
 *
 */
public class ModifyPwdServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		//最终要写入响应的JSON（json）
		JSONObject json = new JSONObject();
		//表单Bean
		Manager mana = new Manager();
		Teacher tea = new Teacher();
		Student stu = new Student();
		
		//创建IO流读取请求正文
		BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String temp = "";
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();
		//将读取到请求正文解析成JSON（jo）
		JSONObject jo = JSONObject.fromObject(sb.toString());
		//从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>)req.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String)userMap.get("userType");
		if (roleFlag.equals("manager")) {
			mana.setId((Long)userMap.get("id"));
			mana.setPassword(MD5Util.md5(jo.getString("password")));
			
			ManaService ms = new ManaServiceImpl();
			try {
				ms.modifyPassword(mana, jo.getString("newPassword"));
				json.put("modifyResult", true);
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("modifyResult", false);
				writer.write(json.toString());
			}
		}else if (roleFlag.equals("teacher")) {
				tea.setId((Long)userMap.get("id"));
				tea.setPassword(MD5Util.md5(jo.getString("password")));
				
				TeaService ts = new TeaServiceImpl();
				try {
					ts.modifyPassword(tea, jo.getString("newPassword"));
					json.put("modifyResult", true);
					writer.write(json.toString());
				} catch (TeaException me) {
					json.put("modifyResult", false);
					writer.write(json.toString());
				}
			} else if (roleFlag.equals("student")) {
			stu.setId((Long)userMap.get("id"));
			stu.setPassword(MD5Util.md5(jo.getString("password")));
			
			StuService ss = new StuServiceImpl();
			try {
				ss.modifyPassword(stu, jo.getString("newPassword"));
				json.put("modifyResult", true);
				writer.write(json.toString());
			} catch (StuException e) {
				json.put("modifyResult", false);
				writer.write(json.toString());
			}
		}
	}

	
	
}
