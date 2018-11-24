package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * 该类为点击年级按年级查询后的成绩管理Servlet类，通过调用管理员和教师相应的接口去处理业务逻辑。
 * @author 123
 *
 */
public class LoadGroupUseForTotalServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		//json数组
		JSONArray ja = null;

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
		
		Teacher tea = new Teacher();
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
			
			ManaService ms = new ManaServiceImpl();
			try {
				List<Map<String, Object>> totalTable =ms.readGroupUseForTotalMark(jo.getString("groupGrade"));
				ja = JSONArray.fromObject(totalTable);
				json.put("totalTable", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				writer.write(me.getMessage());
				me.printStackTrace();
			}
			
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long)userMap.get("id"));
			
			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> totalTable =ts.readGroupUseForTotalMark(tea,jo.getString("groupGrade"));
				ja = JSONArray.fromObject(totalTable);
				json.put("totalTable", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				writer.write(te.getMessage());
				te.printStackTrace();
			}
		}
	}

}
