package caesium.model;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;

import caesium.model.CaesiumJob;

public class CaesiumRepository {

	private boolean doScheduling = false;

	// ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
	// maximumPoolSize, keepAliveTime, unit, workQueue)

	static ConcurrentLinkedQueue<CaesiumJob> waitingJobList = new ConcurrentLinkedQueue<>();
	static ConcurrentHashMap<Long, CaesiumJob> runningJobList = new ConcurrentHashMap<>();

	static HashMap<String, CaesiumJob> jobListMap = new HashMap<>();

	public void setRunScheduledJobs(boolean flag) {
		this.doScheduling = flag;
	};

	public boolean isRunScheduling() {
		return doScheduling;
	}

}
