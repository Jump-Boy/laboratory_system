package team.hmhlyh.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
/**
 * 该类为批量添加角色的servlet类，通过将需要添加的内容信息取出并赋值到对象数组中去，以方便传参给DBUtils，然后调用分别调用三个接口的QueryRunner的batch完成批量操作
 * @author 123
 *
 */
public class AddRolesServlet extends HttpServlet {

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
		//将请求传过来的数据分别装入三个集合中
		List<Object[]> manaList = new ArrayList<Object[]>();
		List<Object[]> teaList = new ArrayList<Object[]>();
		List<Object[]> stuList = new ArrayList<Object[]>();
		
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
		JSONArray ja = JSONArray.fromObject(jo.getString("data"));
		Iterator<Object> iter = ja.iterator();
		while (iter.hasNext()) {
			jo = JSONObject.fromObject(iter.next());
			if (jo.getString("角色类型").equals("管理员")) {
				manaList.add(new Object[]{Long.parseLong(jo.getString("账号")),jo.getString("姓名"),Long.parseLong(jo.getString("账号"))});
			} else if (jo.getString("角色类型").equals("教师")) {
				teaList.add(new Object[]{Long.parseLong(jo.getString("账号")),jo.getString("姓名"),Long.parseLong(jo.getString("账号"))});
			} else if (jo.getString("角色类型").equals("学生")) {
				stuList.add(new Object[]{Long.parseLong(jo.getString("账号")),jo.getString("姓名"),Long.parseLong(jo.getString("账号"))});
			}  
		}
		ManaService ms = new ManaServiceImpl();
		//将三个集合内容分别赋值给是三个对象参数，再依次传入方法中，用DBUtils的batch（批量操作）
		Object[][] manaParams = new Object[manaList.size()][];
		Object[][] teaParams = new Object[teaList.size()][];
		Object[][] stuParams = new Object[stuList.size()][];
		for (int i = 0; i < manaParams.length; i++) {
			manaParams[i] = manaList.get(i);
		}
		for (int j = 0; j < teaParams.length; j++) {
			teaParams[j] = teaList.get(j);
		}
		for (int k = 0; k < stuParams.length; k++) {
			stuParams[k] = stuList.get(k);
		}
		try {
			ms.addManagers(manaParams);
			ms.addTeachers(teaParams);
			ms.addStudents(stuParams);
			json.put("addResult", "success");
			writer.write(json.toString());
		} catch (ManaException me) {
			json.put("addResult", me.getMessage());
			writer.write(json.toString());
		}
		
	}

}
