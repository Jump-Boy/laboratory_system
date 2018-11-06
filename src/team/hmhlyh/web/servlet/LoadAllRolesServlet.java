package team.hmhlyh.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import team.hmhlyh.exception.ManaException;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.utils.DateJsonValueProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
/**
 * 该类为查看全部角色的Servlet类，通过去调用ManaService业务接口去完成查看所有角色功能（查看角色仅为管理员功能）。因为该功能为管理员独有的功能，且无需其他数据，
 * 所以前端采用get请求。需注意为了避免需要显示创建时间和最后一次修改时间而去增设三个模型层各自的属性所带来的不便，所以Dao层中DBUtil采用MapListHandler。然后
 * 遍历集合的同时，每次添加createdAt和updatedAt字段和字段值。注意封装JSON时需要过滤其余字段，注意过滤字段必须保证和数据库字段大小写一样，否则过滤失败。
 * （因为这里采用的是MapListHandler）需要特别注意，这里json-lib会把时间字段转换成不是我们想要的形式，所以需要这里进行格式化，通过JSONConfig注册时间转化器，
 * 注意数据库中的DATETIME对应Java中的Timestamp。
 * @author 123
 *
 */
public class LoadAllRolesServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Writer writer = resp.getWriter();
		JSONObject json = new JSONObject();
		JSONArray ja = null;

		// 从session中取出封装有id和userType的map
		Map<String, Object> userMap = (HashMap<String, Object>) req
				.getSession().getAttribute("user");
		// 当前系统角色的判断标志
		String roleFlag = (String) userMap.get("userType");
		if (roleFlag.equals("manager")) {
			ManaService ms = new ManaServiceImpl();
			try {
				List<Map<String, Object>> roles = ms.readAllRoles();
				JsonConfig jc = new JsonConfig();
				jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss")); 
				jc.setExcludes(new String[] { "PASSWORD", "sex", "province",
						"telephone", "grade", "major", "className",
						"educationLevel", "picURL" });
				ja = JSONArray.fromObject(roles, jc);
				json.put("roles", ja);
				writer.write(json.toString());
			} catch (ManaException me) {
				me.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

}
