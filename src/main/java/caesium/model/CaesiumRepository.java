package caesium.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import caesium.job_model.SingletonCaesiumJob;

public class CaesiumRepository {

	private boolean doScheduling = false;

	// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
	// maximumPoolSize, keepAliveTime, unit, workQueue)

	static ConcurrentLinkedQueue<SingletonCaesiumJob> waitingJobList = new ConcurrentLinkedQueue<>();
	static ConcurrentHashMap<Long, SingletonCaesiumJob> runningJobList = new ConcurrentHashMap<>();
	static HashMap<String, SingletonCaesiumJob> jobListMap = new HashMap<>();

	public void setRunScheduledJobs(boolean flag) {
		this.doScheduling = flag;
	};

	public boolean isRunScheduling() {
		return doScheduling;
	}

}
