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

import net.sf.json.JSONObject;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.exception.TeaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.TeaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;
/**
 * 该类为添加公告的servlet类
 * @author Administrator
 *
 */
public class AddAnnouncementServlet extends HttpServlet{

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
		
		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		long id = (Long)userMap.get("id");
		//避免因无附件而导致的空指针异常
		JSONObject attachment = jo.getJSONObject("attachment");
		String attachmentName = null;
		String attachmentURL = null;
		System.out.println("attachment-----"+attachment);
//		System.out.println("判断"+attachment != null); true
//		System.out.println(attachment.isNullObject()); true 使用这种
//		System.out.println(attachment.equals(null)); false
//		System.out.println(attachment.getClass().getName());
		//注意不能判断JSONObject == null，而应该使用JSONObject.isnullObject()来判断null
		if (!attachment.isNullObject()) {
			System.out.println("attachment-----"+attachment);
			attachmentName = attachment.getString("attachmentName");
			attachmentURL = attachment.getString("attachmentURL");
		}
		
		if (roleFlag.equals("manager")) {
			ManaService ms = new ManaServiceImpl();
			try {
				ms.addAnnouncement(jo.getString("title"), jo.getString("abstract"), 
						attachmentName,attachmentURL, id);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("addResult", me.getMessage());
				writer.write(json.toString());
			}
		} else if (roleFlag.equals("teacher")) {
			TeaService ts = new TeaServiceImpl();
			try {
				ts.addAnnouncement(jo.getString("title"), jo.getString("abstract"), 
						attachmentName,attachmentURL, id);
				json.put("addResult", "success");
				writer.write(json.toString());
			} catch (TeaException te) {
				json.put("addResult", te.getMessage());
				writer.write(json.toString());
			}
		}
		
	}

}
