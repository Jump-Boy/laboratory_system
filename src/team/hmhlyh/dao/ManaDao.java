package team.hmhlyh.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import team.hmhlyh.domain.Manager;
/**
 * 该接口为数据库Dao层中的管理员类交互接口
 * @author 123
 *
 */
public interface ManaDao {

	//读取所有管理员
	public List<Map<String, Object>> readAllManagers() throws SQLException;
	
	//重置密码
	public int resetPassword(long id) throws SQLException;
	
	//向数据库中添加管理员
	public int addManager(Manager mana) throws SQLException;
	
	//向数据库中添加多个管理员
	public int[] addManagers(Object[][] params) throws SQLException;
	
//	//修改角色
//	public int modifyRoleInfo(Manager mana) throws SQLException;
	
	//通过id删除管理员
	public int deleteManager(long id) throws SQLException;
	
    //通过id从数据库中查询管理员
	public Manager findManaById(long id) throws SQLException;
	
	//通过id从数据库中查询管理员名字
	public String findManaNameById(long id) throws SQLException;
	
	//通过id修改个人信息
	public int modifyPersonInfo(Manager mana) throws SQLException;
	
	//修改图片表信息
	public int modifyPicURL(Manager mana) throws SQLException;
	
	//通过id修改密码
	public int modifyPassword(long id, String newPassword) throws SQLException;
	
}
