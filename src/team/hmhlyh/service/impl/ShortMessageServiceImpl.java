package team.hmhlyh.service.impl;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import team.hmhlyh.dao.CourDao;
import team.hmhlyh.dao.impl.CourDaoImpl;
import team.hmhlyh.service.ManaService;
import team.hmhlyh.service.ShortMessageService;

public class ShortMessageServiceImpl implements ShortMessageService {
	CourDao courDao = new CourDaoImpl();
	@Override
	public List<Map<String, Object>> forSendSM() {
		ManaService ms = new ManaServiceImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date openingDay = null;
		try {
			
//				openingDay = sdf.parse((sdf.format(ms.readOpenDate())));
				openingDay = sdf.parse((String)ms.readOpenDate().get("termStartDate"));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Date currentDate = new Date();//系统当前日期
		//开学日与当前系统日期相隔天数
		int differDays = (int)((currentDate.getTime() - openingDay.getTime()) / (24 * 60 * 60 * 1000));
		//获取开学日和当前日期的对应星期
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		
		/*
		 * 该块负责初始化当前时间Time（hour需要+1，因为提前1小时查）
		 */
		int hour = cal.get(Calendar.HOUR_OF_DAY) + 1;
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		Time currentTime = new Time(hour, minute, second);
		//SUNDAY为1，MONDAY为2
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
//		if (w < 0) {
//			w = 0;
//		}
		String currentWeek = weekDays[w];//当前星期几
		cal.setTime(openingDay);
		w = cal.get(Calendar.DAY_OF_WEEK);
		if (w == 1) {
			w = 7;
		} else {
			w -= 1;
		}
		byte currentWeeks = 0;//当前第几周
		if ((differDays % 7 + w) <= 7) {
			currentWeeks = (byte) (differDays / 7 + 1);
		} else {
			currentWeeks = (byte) (differDays /7 + 2);
		}
		
		List<Map<String, Object>> list = null;
		try {
			list = courDao.getCourAndStu(currentWeeks, currentWeek, currentTime);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}
