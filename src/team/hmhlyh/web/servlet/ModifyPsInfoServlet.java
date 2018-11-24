package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import net.sf.json.JSONObject;
import team.hmhlyh.domain.Manager;
import team.hmhlyh.domain.Student;
import team.hmhlyh.domain.Teacher;
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
 * 该类为修改个人信息的Servlet类，通过去调用不同的角色业务接口去完成修改个人信息功能。前端页面将表单中除过id的信息以JSON通过请求发送到服务器，
 * 这里进行请求正文解析，获取字段信息，从session中获取userType和id，调用Dao层接口，通过id去更新相应字段（注意存储图片采用的方法为：
 * 由前端页面以BASE64解析图片，将解码后的信息通过请求发送，服务器获取，类型为字符串。然后再转成字节数组，将字节数组封装成SerialBlob类型的对象属性，
 * 然后通过JDBC将其更新到表中的LONGBLOB类型的字段中，采用DBUtils很不便）。更新成功，响应返回"success"，否则"failure"。
 * 注意学生的年级在每次修改个人信息的时候做更新，通过班级如：电信14-2，通过字符分割和正则表达式以提出
 * “14”即年份的后两位，当然默认2000-2999年之间，然后获取当前系统的时间做比较，通过定义的逻辑算法去进行年级判断。
 * @author 123
 *
 */
public class ModifyPsInfoServlet extends HttpServlet {

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
		// 表单Bean
		Manager mana = new Manager();
		Teacher tea = new Teacher();
		Student stu = new Student();

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
		//从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>)req.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String)userMap.get("userType");
		if (roleFlag.equals("manager")) {
			mana.setId((Long)userMap.get("id"));
			mana.setName(jo.getString("name"));
			mana.setSex(jo.getString("sex"));
			mana.setProvince(jo.getString("province"));
			mana.setTelephone(jo.getString("tele"));
			byte[] bytes = jo.getString("picURL").getBytes();
			try {
				mana.setPicURL(new SerialBlob(bytes));
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ManaService ms = new ManaServiceImpl();
			try {
				ms.modifyPersonInfo(mana);
				json.put("modifyResult", "success");
				writer.write(json.toString());
			} catch (ManaException me) {
				json.put("modifyResult", "failure");
				writer.write(json.toString());
			}
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long)userMap.get("id"));
			tea.setName(jo.getString("name"));
			tea.setSex(jo.getString("sex"));
			tea.setProvince(jo.getString("province"));
			tea.setTelephone(jo.getString("tele"));
			byte[] bytes = jo.getString("picURL").getBytes();
			try {
				tea.setPicURL(new SerialBlob(bytes));
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			TeaService ts = new TeaServiceImpl();
			try {
				ts.modifyPersonInfo(tea);
				json.put("modifyResult", "success");
				writer.write(json.toString());
			} catch (TeaException me) {
				json.put("modifyResult", "failure");
				writer.write(json.toString());
			}
		} else if (roleFlag.equals("student")) {
			stu.setId((Long)userMap.get("id"));
			stu.setName(jo.getString("name"));
			stu.setSex(jo.getString("sex"));
			stu.setClassName(jo.getString("className"));
			stu.setMajor(jo.getString("major"));
			stu.setEducationLevel(jo.getString("educationLevel"));
			stu.setProvince(jo.getString("province"));
			stu.setTelephone(jo.getString("tele"));
		    
			byte[] bytes = jo.getString("picURL").getBytes();
			try {
				stu.setPicURL(new SerialBlob(bytes));
			} catch (SerialException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			StuService ss = new StuServiceImpl();
			try {
				ss.modifyPersonInfo(stu);
				json.put("modifyResult", "success");
				writer.write(json.toString());
			} catch (StuException e) {
				json.put("modifyResult", "failure");
				writer.write(json.toString());
			}
		}
	}

}
