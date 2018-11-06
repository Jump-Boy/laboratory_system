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

import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.TeaServiceImpl;

import net.sf.json.JSONObject;
/**
 * 该类为随堂打分servlet类，仅能教师随堂打分，通过调用教师业务接口处理。教师点击打分按钮一条一条插入。通过分数进行判断，0为请假，-1为旷课。
 * @author 123
 *
 */
public class MarkEveryServlet extends HttpServlet {

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
		int usualScoreId = Integer.parseInt(jo.getString("usualScoreId"));
		byte usualScore = Byte.parseByte(jo.getString("usualScore"));
		
//		JSONArray ja = JSONArray.fromObject(sb.toString());
		
//		Object[][] params = new Object[ja.size()][];
//		for (int i = 0; i < ja.size(); i++) {
//			JSONObject jo = ja.getJSONObject(i);
//			
//				params[i][0] = jo.getString("vacate");
//				params[i][1] = Byte.parseByte(jo.getString("usualGrade"));
//				params[i][2] = Integer.parseInt(jo.getString("usualGradeId"));
//		}
//		//将请求正文解析后的JSON转成集合
//		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
//		for (int i = 0; i < ja.size(); i++) {
//			JSONObject jo = ja.getJSONObject(i);
//			paramList.get(i).put("vacate", jo.getString("vacate"));
//			paramList.get(i).put("usualGrade", Byte.parseByte(jo.getString("usualGrade")));
//			paramList.get(i).put("usualGradeId", Integer.parseInt(jo.getString("usualGradeId")));
//		}
		
		
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				ts.markEveryClass(usualScore, usualScoreId);
				json.put("markResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("markResult", te.getMessage());
				writer.write(json.toString());
			}
		}
	}

}
