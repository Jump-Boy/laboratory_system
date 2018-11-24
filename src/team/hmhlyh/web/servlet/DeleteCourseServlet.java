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
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为删除课程的Servlet类，通过去调用不同的角色业务接口去完成删除课程功能。通过课程id从courseTime中删除课程，
 * 注意在从courseTime删除之前需要先将学生选课表中的相关信息也删除，采用触发器完成该功能。
 * @author 123
 *
 */
public class DeleteCourseServlet extends HttpServlet {

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
		Course cour = new Course();

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
		if (roleFlag.equals("manager")) {
//			ManaService ms = new ManaServiceImpl();
//			try {
//				ms.deleteCourse(cour);
//				json.put("deleteResult", "success");
//				writer.write(json.toString());
//			} catch (ManaException me) {
				json.put("deleteResult", "failure");
				writer.write(json.toString());
//			}
		} else if (roleFlag.equals("teacher")) {
			cour.setId(Integer.parseInt(jo.getString("courseId")));
			TeaService ts = new TeaServiceImpl();
			try {
				ts.deleteCourse(cour);
				json.put("deleteResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("deleteResult", te.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
