package caesium.model;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CaesiumJob implements Job {

	public CaesiumJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		String action = context.getJobDetail().getJobDataMap().get("action").toString();
		System.out.println(action + "1!!!!!");

		for (int i = 0; i <= Integer.parseInt(action); i++) {
			System.out.println(Thread.currentThread().getName() + " " + i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
