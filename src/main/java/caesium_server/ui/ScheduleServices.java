package caesium_server.ui;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import caesium.CaesiumMain;

@Path("/scheduler")
public class ScheduleServices {

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
				JsonElement jobJsonElement = gson.toJsonTree(jobKey, JobKey.class);
				jsonArray.add(jobJsonElement);
			}
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gsonPretty.toJson(jsonArray);

	}

	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello(@PathParam("param") String action) {
		Scheduler scheduler = CaesiumMain.getScheduler();
		if (action != null && action.equals("toggle")) {
			try {
				if (scheduler.isInStandbyMode()) {
					scheduler.start();
				} else {
					scheduler.standby();
				}
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		boolean status = false;
		try {
			status = !scheduler.isInStandbyMode();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CaesiumMain.refresh();

		return "Scheduling status : " + status;

	}

}
