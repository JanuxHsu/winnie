package caesium.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.quartz.JobKey;

public class CaesiumJobStatus {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
	final Map<String, String> processMap;

	final JobKey jobKey;

	final Date updateTime;

	Date nextExecDate;
	Date prevExeDate;

	public CaesiumJobStatus(JobKey jobKey, Map<String, String> processMap) {
		this.jobKey = jobKey;
		this.processMap = processMap;
		this.updateTime = Calendar.getInstance().getTime();
	}

	public void setNextExec(Date next_run_date) {
		this.nextExecDate = next_run_date;
	}

	public void setPrevExec(Date prev_run_date) {
		this.prevExeDate = prev_run_date;
	}

	public JobKey getJobKey() {
		return this.jobKey;
	}

	public Map<String, String> getProcessMap() {
		return this.processMap;
	}

	public String getUpdateTime() {
		return this.updateTime == null ? "---" : sdf.format(this.updateTime);
	}

	public String getNextRunTime() {

		return this.nextExecDate == null ? "---" : sdf.format(this.nextExecDate);
	}

	public String getPrevRunTime() {

		return this.prevExeDate == null ? "---" : sdf.format(this.prevExeDate);
	}

}
