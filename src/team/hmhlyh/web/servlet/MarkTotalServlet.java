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
 * 该类为总成绩打分的servlet类，教师通过调用接口去打分，而若管理员点击打分，则不给予操作（应当老师自己打分），所以直接返回failure
 * @author 123
 *
 */
public class MarkTotalServlet extends HttpServlet {

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
		
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		String roleFlag = (String) userMap.get("userType");
		
		if (roleFlag.equals("manager")) {
			json.put("markResult", "failure");
			writer.write(json.toString());
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			byte totalScore = Byte.parseByte(jo.getString("totalScore"));
			int totalScoreId = Integer.parseInt(jo.getString("totalScoreId"));
			try {
				ts.markTotal(totalScore, totalScoreId);
				json.put("markResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				writer.write(te.getMessage());
				te.printStackTrace();
			}
		}
	}

}
