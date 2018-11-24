package team.hmhlyh.dao.impl;

import org.apache.commons.dbutils.QueryRunner;
import team.hmhlyh.dao.VisitDao;
import team.hmhlyh.utils.ManagerThreadLoacl;

import java.sql.SQLException;

public class VisitDaoImpl implements VisitDao {


    @Override
    public int addVisitRecord(String ip, long accountId, String accountType, String operate) throws SQLException {
        QueryRunner qr = new QueryRunner();
        int num = qr.update(ManagerThreadLoacl.getConnection(), "INSERT INTO visit_record(visitorIP, accountId, accountType, operate, operateTime) VALUES(?, ?, ?, ?, NOW())", ip, accountId, accountType, operate);
        return num;
    }
}
