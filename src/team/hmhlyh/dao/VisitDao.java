package team.hmhlyh.dao;

import java.sql.SQLException;

public interface VisitDao {

    /**
     * 添加访问记录
     * @param ip
     * @param accountId
     * @param accountType
     * @param opreate
     * @return
     */
    public int addVisitRecord(String ip, long accountId, String accountType, String opreate) throws SQLException;

}
