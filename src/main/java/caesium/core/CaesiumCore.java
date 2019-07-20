package caesium.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import caesium.model.CaesiumJob;
import caesium.model.CaesiumRepository;

public class CaesiumCore {
	static Logger logger = Logger.getLogger(CaesiumCore.class.getName());
	private static CaesiumCore instance = null;
	private volatile CaesiumRepository caesiumStatus;

	private SchedulerFactory schedulerFactory;

	private CaesiumCore() {

		this.schedulerFactory = new StdSchedulerFactory();
		try {
			this.schedulerFactory.getScheduler().getListenerManager().addJobListener(new CaesiumJobListener("main"));
		} catch (SchedulerException e) {

			e.printStackTrace();
			logger.error("Add Listener Fail.", e);
		}

	}

	public void setLogger(Logger logger) {
		CaesiumCore.logger = logger;
	}

	public static CaesiumCore getInstance() {
		if (instance == null) {
			synchronized (CaesiumCore.class) {
				if (instance == null) {
					instance = new CaesiumCore();
				}
			}
		}
		return instance;
	}

	public CaesiumRepository getSCaesiumCoreStatus() {
		return this.caesiumStatus;
	}

	public Scheduler getScheduler() throws SchedulerException {
		return this.schedulerFactory.getScheduler();
	}

	public void runScheduler() throws SchedulerException {
		this.schedulerFactory.getScheduler().start();
	}

	public Map<String, CaesiumJob> listAllJobs() throws SchedulerException {
		Map<String, CaesiumJob> jobList = new HashMap<>();
		Scheduler scheduler = this.getScheduler();

		for (String groupName : scheduler.getJobGroupNames()) {

			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();

				scheduler.getTriggersOfJob(jobKey);
				// get job's trigger
				List<?> triggers = scheduler.getTriggersOfJob(jobKey);
				Date nextFireTime = ((Trigger) triggers.get(0)).getNextFireTime();

				System.out.println("[jobName] : " + jobName + " [groupName] : " + jobGroup + " - " + nextFireTime);

			}

		}
		return jobList;
	}

}
