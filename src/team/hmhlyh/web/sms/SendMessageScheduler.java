package team.hmhlyh.web.sms;

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

public class SendMessageScheduler {

	public static void main(String[] args) {
		// 创建JobDetail实例，绑定一个Job实现类
		JobDetail jobDetail = JobBuilder.newJob(SendMessageJob.class)
				.withIdentity("myJob", "group1").build();
		// 创建Trigger，每天的06：00 ~ 19：00每小时执行一次
		Trigger trigger = (CronTrigger) TriggerBuilder
				.newTrigger()
				.withIdentity("myTrigger", "group1")
				.withSchedule(
						CronScheduleBuilder.cronSchedule("* 0/10 6-19 * * ?"))
				.build();
		// 创建Scheduler实例
		SchedulerFactory sfact = new StdSchedulerFactory();
		Scheduler scheduler = null;
		try {
			scheduler = sfact.getScheduler();
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
