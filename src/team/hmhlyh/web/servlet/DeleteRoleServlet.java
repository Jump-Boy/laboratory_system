package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import team.hmhlyh.domain.Manager;
import team.hmhlyh.domain.Student;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
/**
 * 该类为删除角色的Servlet类，通过去调用ManaService业务接口去完成删除角色功能（删除角色仅为管理员功能）。因为删除功能还需要角色类型和id，所以前端还是
 * 发送post请求，当然该功能所需要完成的是，管理员点击删除功能，删除某个角色的时候，除了要将各自角色表中的信息删掉外，还需要将其他表中涉及到的信息也删掉，
 * 如删除某个学生的信息时，其选课信息以及成绩信息等也同时删掉（功能设计采用触发器，，注意！！触发器语句中，触发事件为delete，执行事件为before）。
 * @author 123
 *
 */
public class DeleteRoleServlet extends HttpServlet {

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
		// 表单Bean
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
				ms.deleteRole(mana);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("deleteResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("教师")) {
			tea.setId(Long.parseLong(jo.getString("id")));
			
			try {
				ms.deleteRole(tea);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("deleteResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("学生")) {
			stu.setId(Long.parseLong(jo.getString("id")));
			
			try {
				ms.deleteRole(stu);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("deleteResult", me.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
