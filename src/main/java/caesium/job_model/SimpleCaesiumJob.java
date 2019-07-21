package caesium.job_model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import caesium.utils.CaesiumTriggerHelper;
import caesium.utils.WindowsProcessHelper;

/**
 * <p>
 * This type of scheduled Job launch normal executable file in Windows
 * </p>
 * 
 * @author JanuxHsu
 * 
 */
public class SimpleCaesiumJob implements Job {

	static Logger logger = Logger.getLogger(SimpleCaesiumJob.class);

	public SimpleCaesiumJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		JobDetail jobDetail = context.getJobDetail();

		String jobName = jobDetail.getKey().getName() + "-" + sdf.format(Calendar.getInstance().getTime());
		String doGetPidStr = (String) Optional.ofNullable(jobDataMap.get("doGetPid")).orElse("false");
		boolean doGetPid = Boolean.parseBoolean(doGetPidStr);

		String workingDir = (String) Optional.ofNullable(jobDataMap.get("workingDir")).orElse("");
		String exec_path = (String) Optional.ofNullable(jobDataMap.get("exec_path")).orElse("");

		System.out.println("Next:" + context.getNextFireTime() + "["
				+ CaesiumTriggerHelper.getTriggerInfo(context.getTrigger()) + "]");

		try {

			if (!exec_path.isEmpty()) {

				String[] cmds = WindowsProcessHelper.getNewConsoleCmdArray(jobName, workingDir, exec_path);
				Process process = Runtime.getRuntime().exec(cmds);
				process.waitFor(20, TimeUnit.SECONDS);

				logger.info(jobName + " Triggered!");

				if (doGetPid) {
					Map<String, String> outString = WindowsProcessHelper.getProcessIdsFromTitle(jobName);

					for (String keString : outString.keySet()) {
						System.out.println(keString + "|" + outString.get(keString));
					}
				}

			}

			// process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
