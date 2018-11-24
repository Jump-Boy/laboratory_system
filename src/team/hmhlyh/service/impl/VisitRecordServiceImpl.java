package team.hmhlyh.service.impl;

import team.hmhlyh.dao.VisitDao;
import team.hmhlyh.dao.impl.VisitDaoImpl;
import team.hmhlyh.service.VisitRecordService;

import java.sql.SQLException;

public class VisitRecordServiceImpl implements VisitRecordService {

    private VisitDao visitDao = new VisitDaoImpl();

    @Override
    public void addVisitRecord(String ip, long accountId, String accountType, String opreate) {
        try {
            visitDao.addVisitRecord(ip, accountId, accountType, opreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
