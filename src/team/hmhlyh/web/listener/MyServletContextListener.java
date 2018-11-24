package team.hmhlyh.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import team.hmhlyh.web.sms.SendMessageJob;
/**
 * 监听器（负责监听服务器启动，当应用启动时即Context创建时，就启动定时任务）
 */
public class MyServletContextListener implements
		ServletContextListener {

	private static Scheduler scheduler;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("定时任务结束，应用服务结束！");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("定时任务开启，应用服务开启！");
		// 创建JobDetail实例，绑定一个Job实现类
		JobDetail jobDetail = JobBuilder.newJob(SendMessageJob.class)
				.withIdentity("myJob", "group1").build();
		//测试
		System.out.println("jobDetail绑定");
		
		// 创建Trigger，每天的06：00 ~ 19：00每10分钟执行一次
		Trigger trigger = (CronTrigger) TriggerBuilder
				.newTrigger()
				.withIdentity("myTrigger", "group1")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("0 0/10 6-19 * * ?"))
				.build();
		//测试
		System.out.println("Trigger绑定");
		
		// 创建Scheduler实例
		SchedulerFactory sfact = new StdSchedulerFactory();
//		Scheduler scheduler = null;
		try {
			scheduler = sfact.getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("scheduler开始");
	}

}
