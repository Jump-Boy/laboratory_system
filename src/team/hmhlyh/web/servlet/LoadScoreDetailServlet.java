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

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.StuException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.StuService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.StuServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为查看成绩明细的Servlet类，通过去调用不同的角色业务接口去完成查看成绩明显的功能。
 * @author 123
 *
 */
public class LoadScoreDetailServlet extends HttpServlet {

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
		JSON ja = null;

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
			int totalScoreId = Integer.parseInt(jo.getString("totalScoreId"));
			ManaService ms = new ManaServiceImpl();
			try {
				List<Map<String, Object>> scoreDetail = ms.lookScoreDetail(totalScoreId);
				ja = JSONArray.fromObject(scoreDetail);
				json.put("scoreDetail", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				writer.write(me.getMessage());
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			int totalScoreId = Integer.parseInt(jo.getString("totalScoreId"));
			TeaService ts = new TeaServiceImpl();
			try {
				List<Map<String, Object>> scoreDetail = ts.lookScoreDetail(totalScoreId);
				ja = JSONArray.fromObject(scoreDetail);
				json.put("scoreDetail", ja);
				writer.write(json.toString());
			} catch (TeaException te) {
				writer.write(te.getMessage());
				te.printStackTrace();
			}
		} else if (roleFlag.equals("student")) {
			int totalScoreId = Integer.parseInt(jo.getString("totalScoreId"));
			StuService ss = new StuServiceImpl();
			try {
				List<Map<String, Object>> scoreDetail = ss.lookScoreDetail(totalScoreId);
				ja = JSONArray.fromObject(scoreDetail);
				json.put("scoreDetail", ja);
				writer.write(json.toString());
			} catch (StuException se) {
				writer.write(se.getMessage());
				se.printStackTrace();
			}
		}
	}

}
