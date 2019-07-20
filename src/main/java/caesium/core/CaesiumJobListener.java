package caesium.core;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class CaesiumJobListener implements JobListener {

	String listenerName = null;

	public CaesiumJobListener(String listenerName) {
		this.listenerName = listenerName;
	}

	@Override
	public String getName() {

		return this.listenerName;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {

	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		System.out.println(context.getJobDetail().getKey().getName() + " done");
		
		if (jobException != null) {
			jobException.printStackTrace();
		}

	}

}
