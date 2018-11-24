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
import team.hmhlyh.service.impl.ManaServiceImpl;
import team.hmhlyh.service.impl.TeaServiceImpl;

/**
 * 删除公告serlvet
 */
public class DeleteAnnouncementServlet extends HttpServlet {
    public DeleteAnnouncementServlet() {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Writer writer = resp.getWriter();
        JSONObject json = new JSONObject();
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String temp = "";

        while((temp = br.readLine()) != null) {
            sb.append(temp);
        }

        br.close();
        JSONObject jo = JSONObject.fromObject(sb.toString());
        Map<String, Object> userMap = (HashMap)req.getSession().getAttribute("user");
        String roleFlag = (String)userMap.get("userType");
        long id = Long.parseLong(jo.getString("announcementId"));
        if (roleFlag.equals("manager")) {
            ManaServiceImpl ms = new ManaServiceImpl();

            try {
                ms.deleteAnnouncement(id);
                json.put("deleteResult", "success");
                writer.write(json.toString());
            } catch (ManaException var17) {
                json.put("deleteResult", var17.getMessage());
                writer.write(json.toString());
            }
        } else if (roleFlag.equals("teacher")) {
            TeaServiceImpl ts = new TeaServiceImpl();

            try {
                long teaId = (Long)userMap.get("id");
                ts.deleteAnnouncement(id, teaId);
                json.put("deleteResult", "success");
                writer.write(json.toString());
            } catch (TeaException var16) {
                json.put("deleteResult", var16.getMessage());
                writer.write(json.toString());
            }
        }

    }
}