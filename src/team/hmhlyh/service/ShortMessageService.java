package team.hmhlyh.service;

import java.util.List;
import java.util.Map;

/**
 * 为发送短信提供服务
 * @author 123
 *
 */
public interface ShortMessageService {
	//为发送短信服务（查找课程下的学生信息）
	public List<Map<String, Object>> forSendSM();
	
}
