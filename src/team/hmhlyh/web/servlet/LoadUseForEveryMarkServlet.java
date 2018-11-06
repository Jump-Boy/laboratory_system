package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 该类为实现教师点击随堂打分按钮所显示的结果的servlet类，通过调用教师接口实现相应的逻辑。通过获取系统管理员输入的开学日期，来推断出如当前周次。
 * 再通过当前周次 和当前系统时间来筛选出教师当前正在上的课程。
 * 
 * @author 123
 * 
 */
public class LoadUseForEveryMarkServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		JSONArray ja = null;

		Teacher tea = new Teacher();
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
			writer.write("failure");
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long) userMap.get("id"));

			ManaService ms = new ManaServiceImpl();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date openingDay = null;

			try {
				openingDay = sdf.parse((String) ms.readOpenDate().get(
						"termStartDate"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Date currentDate = new Date();// 系统当前日期
			// 开学日与当前系统日期相隔天数
			int differDays = (int) ((currentDate.getTime() - openingDay.getTime()) / (24 * 60 * 60 * 1000));
			// 获取开学日和当前日期的对应星期
			String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
					"星期六" };
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			// SUNDAY为1，MONDAY为2
			int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
			// if (w < 0) {
			// w = 0;
			// }
			String currentWeek = weekDays[w];// 当前星期几
			cal.setTime(openingDay);
			/*
			 * w = cal.get(Calendar.DAY_OF_WEEK) - 1; if (w < 0) { w = 0; }
			 * String openingWeek = weekDays[w];//开学日为星期几
			 */
			w = cal.get(Calendar.DAY_OF_WEEK);
			if (w == 1) {
				w = 7;
			} else {
				w -= 1;
			}
			byte currentWeeks = 0;// 当前第几周
			if ((differDays % 7 + w) <= 7) {
				currentWeeks = (byte) (differDays / 7 + 1);
			} else {
				currentWeeks = (byte) (differDays / 7 + 2);
			}

			TeaService ts = new TeaServiceImpl();
			try {

				// System.out.println("currentweek:"+currentWeek+","+"currentweeks:"+currentWeeks);

				List<Map<String, Object>> list = ts.readUseForEveryMark(tea,
						currentWeek, currentWeeks);
				ja = JSONArray.fromObject(list);
				json.put("everyMarkTable", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				writer.write(te.getMessage());// 如果没有查到，则返回failure
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
