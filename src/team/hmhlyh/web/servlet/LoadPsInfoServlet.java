package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
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
 * 该类为读取显示个人信息的Servlet类，通过去调用不同的角色业务接口去完成显示个人信息功能。点击页面个人信息按钮时，前端发送一个请求，
 * 该请求无需带有任何信息，所以为get请求。
 * 服务器通过从session中获取封装有id和userType的map，然后通过userType判断，调用相应Dao层接口，通过id查询个人信息
 * （注意学生需要显示学号id）， 将其以JSON格式写入响应（注意需要在封装JSON的时候，需要过Bean的picURl属性，因为如果JSON中的
 * picURL为SerialBlob类型
 * ，前端页面会无法获取到BASE64解码的字符串。所以需要手动的添加picURL，以String的形式），前端页面进行解析。
 * 
 * @author 123
 * 
 */
public class LoadPsInfoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		// 最终要写入响应的JSON（json）
		JSONObject json = null;
		// 简拼为请求id封装Bean，全拼为数据库查询所得Bean
		Manager mana = new Manager();
		Manager manager = new Manager();
		Teacher tea = new Teacher();
		Teacher teacher = new Teacher();
		Student stu = new Student();
		Student student = new Student();

		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
			mana.setId((Long) userMap.get("id"));
			ManaService ms = new ManaServiceImpl();

			try {
				manager = ms.readPersonInfo(mana);
				String picURL = "";
				Blob blob = manager.getPicURL();
				if (blob != null) {
					try {
						// 将Blob解析成字节数组，再转成字符串
						byte[] data = blob.getBytes(1, (int) blob.length());
						picURL = new String(data);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "id", "password", "institute",
						"picURL" });
				json = JSONObject.fromObject(manager, jc);
				json.put("picURL", picURL);
				writer.write(json.toString());
			} catch (ManaException me) {
				// TODO Auto-generated catch block
				me.printStackTrace();
			}
		} else if (roleFlag.equals("teacher")) {
			tea.setId((Long) userMap.get("id"));
			TeaService ts = new TeaServiceImpl();

			try {
				teacher = ts.readPersonInfo(tea);
				String picURL = "";
				Blob blob = teacher.getPicURL();
				//注意这里需要判断一下blob是否为空（因为可能存在一开始用户比没有上传图片）
				if (blob != null) {
					try {
						// 将Blob解析成字节数组，再转成字符串
						byte[] data = blob.getBytes(1, (int) blob.length());
						picURL = new String(data);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "id", "password", "institute",
						"picURL" });
				json = JSONObject.fromObject(teacher, jc);
				json.put("picURL", picURL);
				writer.write(json.toString());
			} catch (TeaException te) {
				// TODO Auto-generated catch block
				te.printStackTrace();
			}
		} else if (roleFlag.equals("student")) {
			stu.setId((Long) userMap.get("id"));
			StuService ss = new StuServiceImpl();

			try {
				student = ss.readPersonInfo(stu);
				String picURL = "";
				Blob blob = student.getPicURL();
				if (blob != null) {
					try {
						// 将Blob解析成字节数组，再转成字符串
						byte[] data = blob.getBytes(1, (int) blob.length());
						picURL = new String(data);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				JsonConfig jc = new JsonConfig();
				jc.setExcludes(new String[] { "id", "password", "institute",
						"grade", "picURL" });
				json = JSONObject.fromObject(student, jc);
				json.put("studentID", student.getId());
				json.put("picURL", picURL);
				writer.write(json.toString());
			} catch (StuException se) {
				se.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
