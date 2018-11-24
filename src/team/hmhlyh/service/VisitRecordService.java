package team.hmhlyh.service;

public interface VisitRecordService {

    /**
     * 记录访问者信息
     * @param ip
     * @param accountId
     * @param accountType
     * @param opreate
     */
    public void addVisitRecord(String ip, long accountId, String accountType, String opreate);

}
