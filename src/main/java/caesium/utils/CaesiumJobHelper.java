package caesium.utils;

import java.util.Objects;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

public class CaesiumJobHelper {
	public static JobKey findJobKey(String jobName, Scheduler scheduler) throws SchedulerException {
		// Check running jobs first
		for (JobExecutionContext runningJob : scheduler.getCurrentlyExecutingJobs()) {
			if (Objects.equals(jobName, runningJob.getJobDetail().getKey().getName())) {
				return runningJob.getJobDetail().getKey();
			}
		}
		// Check all jobs if not found
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				if (Objects.equals(jobName, jobKey.getName())) {
					return jobKey;
				}
			}
		}
		return null;
	}
}
