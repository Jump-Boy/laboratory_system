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
 * 该类为添加角色的Servlet类，通过去调用ManaService业务接口去完成添加角色功能（添加角色仅为管理员功能）。前端页面将表单信息通过请求正文以JSON格式发送服务器，这里获取请求正文
 * 中的数据（注意表单中角色类型为汉字），添加封装后传入相应接口的方法中，如果学号（教职工号）查询到数据库中已存在，则添加失败，否则添加到数据库中相应角色的表，
 * 注意Dao层中添加方法需要添加默认密码，默认密码和id一致，并且以MD5加密存储。添加成功。
 * @author 123
 *
 */
public class AddRoleServlet extends HttpServlet {

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
		/* 注意userType和"管理员","教师","学生"去比较，而不是manager，teacher，student
		 * （分三种情况是因为需要setId，Object不能setId，并且需要根据角色类型插入到不同的表中）
		 */
		if (userType.equals("管理员")) {
			mana.setId(Long.parseLong(jo.getString("id")));
			mana.setName(jo.getString("name"));
			
			try {
				ms.addRole(mana);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("addResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("教师")) {
			tea.setId(Long.parseLong(jo.getString("id")));
			tea.setName(jo.getString("name"));
			
			try {
				ms.addRole(tea);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("addResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (userType.equals("学生")) {
			stu.setId(Long.parseLong(jo.getString("id")));
			stu.setName(jo.getString("name"));
			
			try {
				ms.addRole(stu);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("addResult", me.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
