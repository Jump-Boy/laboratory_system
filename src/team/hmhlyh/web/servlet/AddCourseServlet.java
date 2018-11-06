package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import team.hmhlyh.domain.Course;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为添加课程的Servlet类，通过去调用不同的角色业务接口去完成添加课程功能。添加课程时，需要将课程分两步添加，先添加到course表中，
 * 在将具体时间信息添加到courseTime表中，注意前端所返回的时间和周期都为字符串，这里需要进行字符串的分割以获取到各部分的值，以分别存储
 * 开始周期，结束周期，开始时间和结束时间等。
 * @author 123
 *
 */
public class AddCourseServlet extends HttpServlet{

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
		Teacher tea = new Teacher();

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
		long id = (Long)userMap.get("id");
		tea.setId(id);
		//courseName, attribute, majorScope, credit, studySemester, teaId, limitNum, startWeek, "
		//+ "endWeek, location, intro
		cour.setCourseName(jo.getString("courseName"));
		cour.setAttribute(jo.getString("attribute"));
		cour.setMajorScope(jo.getString("majorScope"));
		cour.setCredit(new Byte(jo.getString("credit")));
		cour.setStudySemester(jo.getString("studySemester"));
		cour.setTeaId(id);
		cour.setLimitNum(new Byte(jo.getString("limitNum")));
		String[] courseWeeks = jo.getString("classWeeks").split("~");
		cour.setStartWeeks(Byte.parseByte((courseWeeks[0])));
		cour.setEndWeeks(Byte.parseByte((courseWeeks[1])));
		cour.setLocation(new Integer(jo.getString("location")));
//		cour.setIntro(jo.getString("intro"));
		String[] courseTime = jo.getString("classTime").split(" ");
		cour.setWeek(courseTime[0]);
		String[] strs = courseTime[1].split("~");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			cour.setStartTime(new Time(sdf.parse(strs[0]).getTime()));
			cour.setEndTime(new Time(sdf.parse(strs[1]).getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (roleFlag.equals("manager")) {
			
//				ms.addCourse(cour, id);
				json.put("addResult", "failure");
				writer.write(json.toString());
			
			
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				ts.addCourse(cour, tea);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("addResult", te.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
