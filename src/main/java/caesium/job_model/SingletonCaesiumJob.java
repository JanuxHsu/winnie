package caesium.job_model;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>
 * This type of scheduled Job launch normal executable file in Windows, but will
 * only launch once the previous scheduled job is completed.
 * </p>
 * 
 * @author JanuxHsu
 * 
 */

@DisallowConcurrentExecution
public class SingletonCaesiumJob extends SimpleCaesiumJob {

	static Logger logger = Logger.getLogger(SimpleCaesiumJob.class);

	public SingletonCaesiumJob() {

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		super.execute(context);
	}
}
