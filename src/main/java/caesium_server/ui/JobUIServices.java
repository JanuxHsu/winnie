package caesium_server.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import caesium.CaesiumMain;
import caesium.core.CaesiumStore;
import caesium.model.CaesiumJobStatus;
import caesium.utils.HtmlHelper;

@Path("/jobs")
public class JobUIServices {
	Logger logger = Logger.getLogger(JobUIServices.class);
	Gson gson = new Gson();
	Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String doGetAllClients() {

		String html = "";
		Scheduler scheduler = CaesiumMain.getScheduler();
		html += String.format("<p>%s</p>", CaesiumMain.version);
		try {
			if (scheduler.isInStandbyMode()) {
				html += "<p style=\"width:10%; padding: 15px; color:#FFF; background:red;\">Scheduler: Off</p>";
			} else {
				html += "<p style=\"width:10%; padding: 15px; color:#FFF; background:green;\">Scheduler: On</p>";
			}
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String tableString = "<Table border=\"2\">";
		try {

			ArrayList<ArrayList<String>> tableArrayList = new ArrayList<>();

			String[] headerStrings = { "Job Name", "Job Group", "Prev Schedule Time", "Next Schedule Time",
					"Last update", "Job Processes" };
			tableArrayList.add(new ArrayList<String>(Arrays.asList(headerStrings)));

			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
				ArrayList<String> rowList = new ArrayList<>();
				String triggerTagStr = String.format("<a href=\"..\\api\\jobs\\trigger\\%s\">%s</a>", jobKey.getName(),
						jobKey.getName());
				rowList.add(triggerTagStr);
				rowList.add(jobKey.getGroup());

				if (CaesiumStore.jobStatusMap.containsKey(jobKey.getName())) {
					CaesiumJobStatus caesiumJobStatus = CaesiumStore.jobStatusMap.get(jobKey.getName());

					rowList.add(caesiumJobStatus.getPrevRunTime());
					rowList.add(caesiumJobStatus.getNextRunTime());

					rowList.add(caesiumJobStatus.getUpdateTime());
					ArrayList<ArrayList<String>> processList = new ArrayList<>();

					Map<String, String> proceessMap = caesiumJobStatus.getProcessMap();
					for (String key : proceessMap.keySet()) {
						ArrayList<String> subProcessList = new ArrayList<>();
						subProcessList.add(key);
						subProcessList.add(proceessMap.get(key));
						processList.add(subProcessList);
					}
					String processTable = "<table border=\"1\">" + HtmlHelper.listToInnerTable(processList)
							+ "</table>";

					rowList.add(processTable);
				}

				tableArrayList.add(rowList);

			}
			tableString += HtmlHelper.listToInnerTable(tableArrayList);
			// tableString += "</tbody>";
			tableString += "</table>";
		} catch (Exception e) {
			// TODO Auto-generated catch block

			logger.error(e.getMessage());
			e.printStackTrace();
		}

		html += tableString;

		return html;

	}

	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello(@PathParam("param") String client_id) {

		return "";
	}

}
