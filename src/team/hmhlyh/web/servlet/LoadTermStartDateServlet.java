package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;

import net.sf.json.JSONObject;
/**
 * 该类为获取开学时间的servlet类
 * @author 123
 *
 */
public class LoadTermStartDateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = null;
		
		ManaService ms = new ManaServiceImpl();
		
			//将时间转成字符串类型写入JSON-value中
//			Date date = ms.readOpenDate();
//			if (date != null) {
//				
//			}h
			//直接将map转成JSON
			json = JSONObject.fromObject(ms.readOpenDate());
			writer.write(json.toString());
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
