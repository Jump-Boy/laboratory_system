package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;

import net.sf.json.JSONObject;

public class SetTermStartDateServlet extends HttpServlet {

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
		
		String openDate = jo.getString("termStartDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//java.sql.Date（为了方便插入数据库中，与数据库类型相匹配）
		Date date = null;
//		Map<String, Object> map = null;
		ManaService ms = new ManaServiceImpl();
		try {
			
			date = new Date(sdf.parse(openDate).getTime());
			ms.setOpenDate(date);
//			map = ms.readOpenDate();//如果设置时间失败，抛异常，则不会执行到这行，map将不会初始化，所以需要在catch中也要初始化map
//			map.put("setResult", "success");
			json.put("setResult", "success");
			writer.write(json.toString());
		} catch (ParseException e) {
			e.printStackTrace();
//			map = ms.readOpenDate();
//			map.put("setResult", "failure");
			json.put("setResult", "failure");
			writer.write(json.toString());
		} catch (ManaException me) {
//			map = ms.readOpenDate();
//			map.put("setResult", me.getMessage());
			json.put("setResult", me.getMessage());
			writer.write(json.toString());
		}
	}

}
