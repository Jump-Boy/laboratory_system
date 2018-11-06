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
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Student;
import team.hmhlyh.exception.StuException;
import team.hmhlyh.service.StuService;
import team.hmhlyh.service.impl.StuServiceImpl;
/**
 * 该类为选修课程的Servlet类，通过去调用学生业务接口去完成学生选修课程功能。选修课程即向学生选修表中插入一条记录。
 * @author 123
 *
 */
public class SelectCourseServlet extends HttpServlet {

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
		//添加的课程实体Bean
		Course cour = new Course();
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
		
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		
		if (roleFlag.equals("student")) {
			stu.setId((Long)userMap.get("id"));
			cour.setId(Integer.parseInt(jo.getString("courseId")));
			
			StuService ss = new StuServiceImpl();
			try {
				ss.selectCourse(cour, stu);
				json.put("selectResult", "success");
				writer.write(json.toString());
			} catch (StuException se) {
				json.put("selectResult", se.getMessage());
				writer.write(json.toString());
			}
		}
		
	}

}
