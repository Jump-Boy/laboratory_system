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
import net.sf.json.JsonConfig;

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
 * 该类为登录的Servlet类，通过去调用不同的角色业务接口去完成登录功能.异常代表登录失败，否则登录成功。
 * 登录请求方式为post，表单数据是以JSON的格式，通过请求正文发送过来的，所以这里通过IO流将正文中的JSON读取并解析，
 * 然后将解析所得到的登录表单数据封装成一个实体Bean（其中密码MD5加密），根据角色判断的结果，将作为参数传递到相应角色的login接口中，
 * 在根据login所返回的信息做判断，如果捕捉到异常，则将异常信息写入响应中，如果未捕捉到异常，则将返回的由查询结果所封装的Bean，
 * 过滤掉一部分信息，留下id和name，并连同userType作为personInfo的value和state一起以Json格式写入响应中，传给前端页面。
 * 注意还需要将id和userType写入session中，这样在登录系统成功后，就无需前端每次请求在带上id和userType。
 * （为了避免浏览器的JSESSIONID的cookie恶意修改，在web.xml中配置cookie的http-only）
 * 
 * @author 123
 * 
 */
public class LoginServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
		// //简拼为表单Bean，全拼为数据库查询所得Bean
		// Student stu = new Student();
		// Student student = null;
		//
		// Manager mana = new Manager();
		// Manager manager = null;
		//
		// Teacher tea = new Teacher();
		// Teacher teacher = null;
		//
		// // ObjectOutputStream output = new
		// // ObjectOutputStream(resp.getOutputStream());
		// Writer writer = resp.getWriter();
		// // 登录的角色判断标志
		// String roleFlag = req.getParameter("userType");
		// // manager代表管理员 ， teacher代表教师 ， student代表学生
		// if (roleFlag.equals("manager")) {
		// mana.setId(Long.parseLong(req.getParameter("username")));
		// mana.setPassword(MD5Util.md5(req.getParameter("password")));
		// ManaService ms = new ManaServiceImpl();
		// try {
		// manager = ms.login(mana);
		// // 设置JSON过滤字段
		// JsonConfig jc = new JsonConfig();
		// jc.setExcludes(new String[] { "password" });
		// JSONObject jo = JSONObject.fromObject(manager, jc);
		// // 向JSON中添加state和userType两对key-value
		// jo.put("state", "success");
		// jo.put("userType", "manager");
		// // output.writeObject(jo);
		// writer.write(jo.toString());
		// } catch (ManaException me) {
		// // output.writeObject(me.getMessage());
		// writer.write(me.getMessage());
		// }
		// } else if (roleFlag.equals("teacher")) {
		// tea.setId(Long.parseLong(req.getParameter("username")));
		// tea.setPassword(MD5Util.md5(req.getParameter("password")));
		// TeaService ts = new TeaServiceImpl();
		// try {
		// teacher = ts.login(tea);
		// JsonConfig jc = new JsonConfig();
		// jc.setExcludes(new String[] { "password", "age", "institute",
		// "qq", "sex", "telphone", "weChat" });
		// JSONObject jo = JSONObject.fromObject(teacher, jc);
		// jo.put("state", "success");
		// jo.put("userType", "teacher");
		// // output.writeObject(jo);
		// writer.write(jo.toString());
		// } catch (TeaException te) {
		// // output.writeObject(te.getMessage());
		// writer.write(te.getMessage());
		// }
		// } else if (roleFlag.equals("student")) {
		// // BeanUtils.populate(stu, req.getParameterMap());
		// stu.setId(Long.parseLong(req.getParameter("username")));
		// stu.setPassword(MD5Util.md5(req.getParameter("password")));
		// StuService ss = new StuServiceImpl();
		// try {
		// student = ss.login(stu);
		// JsonConfig jc = new JsonConfig();
		// jc.setExcludes(new String[] { "age", "grade", "institute",
		// "major", "password", "qq", "sex", "telphone", "weChat" });
		// JSONObject jo = JSONObject.fromObject(student, jc);
		// jo.put("state", "success");
		// jo.put("userType", "student");
		// // output.writeObject(jo);
		// writer.write(jo.toString());
		// } catch (StuException se) {
		// // output.writeObject(se.getMessage());
		// writer.write(se.getMessage());
		// }
		// }

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// 简拼为表单Bean，全拼为数据库查询所得Bean
		Student stu = new Student();
		Student student = null;

		Manager mana = new Manager();
		Manager manager = null;

		Teacher tea = new Teacher();
		Teacher teacher = null;

		Writer writer = resp.getWriter();
		// 创建IO流读取请求正文
		BufferedReader br = new BufferedReader(new InputStreamReader(
				req.getInputStream(), "UTF-8"));
		StringBuffer sb = new StringBuffer("");
		String temp = "";
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();
		String params = sb.toString();
		// 将读取到请求正文解析成JSON（jo）
		JSONObject jo = JSONObject.fromObject(params);
		// 最终要写入响应的JSON（json）
		JSONObject json = new JSONObject();

//		// 禁止同个会话下，多点登录
//		Cookie[] cookies = req.getCookies();
//		for (int i = 0; cookies != null && i < cookies.length; i++) {
//			if (cookies[i].getName().equals("JSESSIONID")) {
//				json.put("state", "notFound");
//				writer.write(json.toString());
//				break;
//			}
//
//		}
//		if (json.get("state") == null) {

			// 登录的角色判断标志
			String roleFlag = jo.getString("userType");
			// manager代表管理员 ， teacher代表教师 ， student代表学生
			if (roleFlag.equals("manager")) {
				mana.setId(Long.parseLong(jo.getString(("username"))));
				mana.setPassword(MD5Util.md5(jo.getString(("password"))));
				ManaService ms = new ManaServiceImpl();
				try {
					manager = ms.login(mana);
					// 设置JSON过滤字段
					JsonConfig jc = new JsonConfig();
					jc.setExcludes(new String[] { "password", "province",
							"sex", "telephone", "picURL" });
					jo = JSONObject.fromObject(manager, jc);
					// 向JSON中添加state和userType两对key-value
					jo.put("userType", "manager");
					json.put("state", "success");
					json.put("personInfo", jo);
					// output.writeObject(jo);
					writer.write(json.toString());
					// 将存有id和userType的map写入session
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("id", manager.getId());
					userMap.put("userType", "manager");
					req.getSession().setAttribute("user", userMap);
				} catch (ManaException me) {
					json.put("state", me.getMessage());
					writer.write(json.toString());
				}
			} else if (roleFlag.equals("teacher")) {
				tea.setId(Long.parseLong(jo.getString(("username"))));
				tea.setPassword(MD5Util.md5(jo.getString(("password"))));
				TeaService ts = new TeaServiceImpl();
				try {
					teacher = ts.login(tea);
					JsonConfig jc = new JsonConfig();
					jc.setExcludes(new String[] { "password", "institute",
							"province", "sex", "telephone", "picURL" });
					jo = JSONObject.fromObject(teacher, jc);
					jo.put("userType", "teacher");
					json.put("state", "success");
					json.put("personInfo", jo);
					writer.write(json.toString());
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("id", teacher.getId());
					userMap.put("userType", "teacher");
					req.getSession().setAttribute("user", userMap);
				} catch (TeaException te) {
					json.put("state", te.getMessage());
					writer.write(json.toString());
				}
			} else if (roleFlag.equals("student")) {
				// BeanUtils.populate(stu, req.getParameterMap());
				stu.setId(Long.parseLong(jo.getString(("username"))));
				stu.setPassword(MD5Util.md5(jo.getString(("password"))));
				StuService ss = new StuServiceImpl();
				try {
					student = ss.login(stu);
					JsonConfig jc = new JsonConfig();
					jc.setExcludes(new String[] { "grade", "institute",
							"password", "className", "sex", "telephone",
							"province", "educationLevel", "picURL" });
					jo = JSONObject.fromObject(student, jc);
					jo.put("userType", "student");
					json.put("state", "success");
					json.put("personInfo", jo);
					writer.write(json.toString());
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("id", student.getId());
					userMap.put("userType", "student");
					req.getSession().setAttribute("user", userMap);
				} catch (StuException se) {
					json.put("state", se.getMessage());
					writer.write(json.toString());
//				}
			}
		}
	}

}