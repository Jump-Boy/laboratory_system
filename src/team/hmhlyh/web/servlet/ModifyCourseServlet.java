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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import team.hmhlyh.domain.Course;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为修改课程的Servlet类，通过去调用不同的角色业务接口去完成修改课程功能。修改课程即数据库中课程信息的更新，需要分别更新course和courseTime。
 * 通过课程id去进行修改courseTime，通过courseNo修改course表。
 * @author 123
 *
 */
public class ModifyCourseServlet extends HttpServlet {

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
		//courseName, attribute, majorScope, credit, studySemester, teaId, limitNum, startWeek, "
		//+ "endWeek, location, intro
		cour.setId(Integer.parseInt(jo.getString("courseId")));
		cour.setCourseName(jo.getString("courseName"));
		cour.setAttribute(jo.getString("attribute"));
		cour.setMajorScope(jo.getString("majorScope"));
		cour.setCredit(new Byte(jo.getString("credit")));
		cour.setStudySemester(jo.getString("studySemester"));
		cour.setTeaId(id);
		cour.setLimitNum(new Byte(jo.getString("limitNum")));
		String[] courseWeeks = jo.getString("classWeeks").split("~");
		cour.setStartWeeks(new Byte(courseWeeks[0]));
		//从“12周”中取出数字
		String regex = "[^0-9]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(courseWeeks[1]);
		cour.setEndWeeks(new Byte(m.replaceAll("").trim()));
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
			
			ManaService ms = new ManaServiceImpl();
//			try {
//				ms.modifyCourse(cour);
				json.put("modifyResult", "failure");
				writer.write(json.toString());
//			} catch (ManaException me) {
//				json.put("modifyResult", me.getMessage());
//				writer.write(json.toString());
//			}
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				ts.modifyCourse(cour);
				json.put("modifyResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("modifyResult", te.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
