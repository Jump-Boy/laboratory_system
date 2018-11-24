package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import team.hmhlyh.domain.Teacher;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为点击"成绩管理"处理的Servlet类，通过调用管理员和教师的业务接口去完成相应的业务逻辑。管理员可以获取所有的课程，而教师只能管理自己的课程。
 * @author 123
 *
 */
public class LoadUseForTotalMarkServlet extends HttpServlet {

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

			ManaService ms = new ManaServiceImpl();
			try {
				List<Map<String, Object>> totalTable = ms.readUseForTotalMark();
				ja = JSONArray.fromObject(totalTable);
				json.put("totalTable", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				writer.write(me.getMessage());
				me.printStackTrace();
			}

		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long) userMap.get("id"));

			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> totalTable = ts
						.readUseForTotalMark(tea);
				ja = JSONArray.fromObject(totalTable);
				json.put("totalTable", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				writer.write(te.getMessage());
				te.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
