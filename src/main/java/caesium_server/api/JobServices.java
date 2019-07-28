package caesium_server.api;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import caesium.CaesiumMain;
import caesium.core.CaesiumStore;
import caesium.model.CaesiumJobStatus;

@Path("/jobs")
public class JobServices {

	Gson gson = new Gson();
	JsonParser jsonParser = new JsonParser();
	Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String doGetAllClients() {
		JsonArray jsonArray = new JsonArray();
		Scheduler scheduler = CaesiumMain.getScheduler();

		try {

			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
				JsonObject jobJsonElement = (JsonObject) gson.toJsonTree(jobKey, JobKey.class);

				CaesiumJobStatus caesiumJobStatus = CaesiumStore.jobStatusMap.get(jobKey.getName());
				if (caesiumJobStatus != null) {
					jobJsonElement.addProperty("pre-startTime", caesiumJobStatus.getPrevRunTime());
					jobJsonElement.addProperty("update_time", caesiumJobStatus.getUpdateTime());
					jobJsonElement.addProperty("next-startTime", caesiumJobStatus.getNextRunTime());

					Trigger trigger = scheduler.getTriggersOfJob(jobKey).get(0);

					if (trigger.getClass().getName().equals("org.quartz.impl.triggers.CronTriggerImpl")) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						jobJsonElement.addProperty("job_schedule", cronTrigger.getCronExpression());
					}

					jobJsonElement.add("processes", gson.toJsonTree(caesiumJobStatus.getProcessMap(), Map.class));
				}

				jsonArray.add(jobJsonElement);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gsonPretty.toJson(jsonArray);

	}

	@GET
	@Path("trigger/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello(@PathParam("param") String job_name) {
		JsonObject resObject = new JsonObject();
		Scheduler scheduler = CaesiumMain.getScheduler();
		try {
			scheduler.getJobKeys(GroupMatcher.anyJobGroup());
			JobKey tgt_jobKey = JobKey.jobKey(job_name);
			Set<JobKey> jobs = scheduler.getJobKeys(GroupMatcher.anyJobGroup());

			if (jobs.contains(tgt_jobKey)) {
				scheduler.triggerJob(tgt_jobKey);
				resObject.addProperty("status", "success");
				resObject.addProperty("message", "Job: " + job_name + " is triggered.");
			} else {
				resObject.addProperty("status", "Error");
				resObject.addProperty("message", "Job: " + job_name + " is not found.");
			}

		} catch (SchedulerException e) {

			e.printStackTrace();
			resObject.addProperty("status", "Error");
			resObject.addProperty("message", e.getMessage());
		}

		return gsonPretty.toJson(resObject);
	}

}
