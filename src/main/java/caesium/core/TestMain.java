package caesium.core;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import caesium.log.CaesiumLogAppender;
import caesium.model.CaesiumJob;

public class TestMain {

	static Logger logger = Logger.getRootLogger();

	public static void main(String[] args) throws SchedulerException {
		//Logger.getRootLogger().setLevel(Level.OFF);
		CaesiumLogAppender caesiumLogAppender = new CaesiumLogAppender();
		logger.addAppender(caesiumLogAppender);

		CaesiumCore caesiumCore = CaesiumCore.getInstance();
		
		

		caesiumCore.runScheduler();

		System.out.println("------");

		Scheduler scheduler = caesiumCore.getScheduler();

		JobKey jobKey = new JobKey("job1");

		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("action", 10);
		jobDetail = JobBuilder.newJob(CaesiumJob.class).withIdentity(jobKey).setJobData(jobDataMap).build();

		Trigger trigger = TriggerBuilder.newTrigger()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(100))
				.build();

		if (jobDetail == null) {

			scheduler.scheduleJob(jobDetail, trigger);

		} else {
			scheduler.deleteJob(jobKey);
			scheduler.scheduleJob(jobDetail, trigger);
		}

		Map<String, CaesiumJob> jobs = caesiumCore.listAllJobs();

	}
}
