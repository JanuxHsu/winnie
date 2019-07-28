package caesium_server.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import caesium.CaesiumMain;

@Path("/scheduler")
public class ScheduleServices {

	Gson gson = new Gson();
	JsonParser jsonParser = new JsonParser();
	Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String doGetSchedulerStatus() {
		JsonObject resJsonObject = new JsonObject();

		Scheduler scheduler = CaesiumMain.getScheduler();

		try {
			if (!scheduler.isInStandbyMode()) {
				resJsonObject.addProperty("scheduluer", "running");

			} else {
				resJsonObject.addProperty("scheduluer", "stopped");

			}

			JsonArray jsonArray = new JsonArray();
			for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())) {

				JsonObject triggerJsonObject = new JsonObject();

				Trigger trigger = scheduler.getTrigger(triggerKey);

				triggerJsonObject.addProperty("trigger_name", trigger.getKey().getName());
				triggerJsonObject.addProperty("trigger_group", trigger.getKey().getGroup());

				if (trigger.getClass().getName().equals("org.quartz.impl.triggers.CronTriggerImpl")) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					triggerJsonObject.addProperty("type", "cronjob");
					triggerJsonObject.addProperty("trigger", cronTrigger.getCronExpression());

				}

				jsonArray.add(triggerJsonObject);
			}

			resJsonObject.add("triggers", jsonArray);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gsonPretty.toJson(resJsonObject);

	}

	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello(@PathParam("param") String action) {
		JsonObject resJsonObject = new JsonObject();

		Scheduler scheduler = CaesiumMain.getScheduler();
		boolean status = false;
		if (action != null && action.equals("toggle")) {
			try {
				if (scheduler.isInStandbyMode()) {
					scheduler.start();
				} else {
					scheduler.standby();
				}

				CaesiumMain.refresh();

				status = !scheduler.isInStandbyMode();
				resJsonObject.addProperty("status", "success");
				resJsonObject.addProperty("Message", "Scheduler running status: " + status);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resJsonObject.addProperty("status", "Error");
				resJsonObject.addProperty("Message", e.getMessage());
			}
		} else {
			resJsonObject.addProperty("status", "error");
			resJsonObject.addProperty("message", "unknown action");

		}

		return gsonPretty.toJson(resJsonObject);

	}

}
