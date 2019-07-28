package caesium;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import caesium.core.CaesiumJobListener;
import caesium.log.CaesiumLogAppender;
import caesium.log.LogListener;
import caesium.utils.CaesiumJobHelper;
import caesium.utils.CaesiumTriggerHelper;
import caesium_gui.CaesiumSwingGui;
import caesium_gui.CaesiumTableModel;

public class CaesiumMain {
	public static String version = "Caesium Scheduler v1.2 Beta (By JanuxHsu)";
	static Logger logger = Logger.getRootLogger();
	static StdScheduler scheduler;
	static FileSystemXmlApplicationContext context;
	static CaesiumSwingGui caesiumSwingGui;

	public static void main(String[] args) throws IOException, SchedulerException {

		Options options = new Options();
		Option serverParam = new Option("m", "mode", true, "Caesium Sceduler Mode (local/web)");
		serverParam.setRequired(true);
		options.addOption(serverParam);

		Option portParam = new Option("p", "port", true, "web Server Port");
		options.addOption(portParam);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("--", options);

			System.exit(1);
		}

		String mode = cmd.getOptionValue("m");
		String port = cmd.getOptionValue("p");

		caesiumSwingGui = new CaesiumSwingGui();

		loadApplicationContext();
		// context = new FileSystemXmlApplicationContext("Caesium-beans-config.xml");

		caesiumSwingGui.setTilte(version);
		caesiumSwingGui.show();

		CaesiumLogAppender caesiumLogAppender = new CaesiumLogAppender();
		logger.addAppender(caesiumLogAppender);

		scheduler = (StdScheduler) context.getBean("schedulerFactoryBean");

		caesiumSwingGui.setSchedulerLightStatus(scheduler.isInStandbyMode());

		scheduler.getListenerManager().addJobListener(new CaesiumJobListener("main"));

		loadJobsToGui();

		caesiumSwingGui.setActionListener("reloadBtn", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reloading context...");
				try {
					scheduler.clear();
					scheduler.shutdown();
					loadApplicationContext();
					scheduler = (StdScheduler) context.getBean("schedulerFactoryBean");
					loadJobsToGui();
					caesiumSwingGui.setSchedulerLightStatus(scheduler.isInStandbyMode());

				} catch (SchedulerException e1) {
					// TODO Auto-generated catch block
					context.close();
					e1.printStackTrace();
				}
				System.out.println("Reload Completed.");
			}
		});

		caesiumSwingGui.setActionListener("triggerJobButton", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String jobNameString = caesiumSwingGui.getSeletedJobName();
				try {
					JobKey jobKey = CaesiumJobHelper.findJobKey(jobNameString, scheduler);
					if (jobKey != null) {
						scheduler.triggerJob(jobKey);
					} else {
						logger.warn(jobNameString + " not found!");
					}

				} catch (SchedulerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		caesiumSwingGui.setActionListener("toggleSchedule", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (scheduler.isInStandbyMode()) {
					try {
						scheduler.start();
					} catch (SchedulerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					scheduler.standby();
				}

				caesiumSwingGui.setSchedulerLightStatus(scheduler.isInStandbyMode());

			}
		});

		caesiumSwingGui.setActionListener("openWebUI", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI("http://localhost:" + port + "/ui/jobs"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		caesiumSwingGui.setMouseListener("jobTable", new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int rowIdx = target.getSelectedRow();
				CaesiumTableModel model = (CaesiumTableModel) target.getModel();
				@SuppressWarnings("unchecked")
				Vector<String> rowdata = (Vector<String>) model.getDataVector().get(rowIdx);

				System.out.println(rowdata.get(0));
				caesiumSwingGui.setSelectedJob(rowdata.get(0));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});

		caesiumLogAppender.addLogSubscriber(new LogListener() {

			@Override
			public void onLog(String logText) {
				caesiumSwingGui.displaySystemLog(logText);

			}
		});

		if (mode.trim().equalsIgnoreCase("web")) {

			int serverPort = port == null || port.equals("") ? 8090 : Integer.parseInt(port);
			logger.info("Server Opened at port: " + serverPort);
			Server server = new Server(serverPort);

			ServletContextHandler servletContextHandler = new ServletContextHandler();

			servletContextHandler.setContextPath("/");
			server.setHandler(servletContextHandler);

			ServletHolder api_servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
			api_servletHolder.setInitOrder(0);
			api_servletHolder.setInitParameter("jersey.config.server.provider.packages", "caesium_server.api");

			ServletHolder ui_servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/ui/*");
			ui_servletHolder.setInitOrder(1);
			ui_servletHolder.setInitParameter("jersey.config.server.provider.packages", "caesium_server.ui");

			try {
				String statusTextString = String.format("[WebServer Mode]: Port: " + serverPort);
				caesiumSwingGui.setStatusText(statusTextString);

				server.start();
				server.join();

				System.out.println("----------------------------");

			} catch (Exception e) {

				e.printStackTrace();
			}

		} else {

			String statusTextString = String.format("[Local Mode]");

			caesiumSwingGui.setStatusText(statusTextString);
		}

	}

	public static void loadApplicationContext() {
		if (context == null) {
			context = new FileSystemXmlApplicationContext("Caesium-beans-config.xml");
		} else {
			context.refresh();
		}

		if (caesiumSwingGui != null) {
			caesiumSwingGui.setContextTimeText(context.getStartupDate());

		}

	}

	public static void loadJobsToGui() throws SchedulerException {
		Set<JobKey> jobs = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
		List<List<String>> rowList = new ArrayList<>();
		for (JobKey jobKey : jobs) {
			List<String> valuesList = new ArrayList<>();
			List<? extends Trigger> jobTriggers = scheduler.getTriggersOfJob(jobKey);
			valuesList.add(jobKey.getName());

			for (Trigger trigger : jobTriggers) {

				valuesList.add(CaesiumTriggerHelper.getTriggerInfo(trigger));

			}

			rowList.add(valuesList);
		}
		caesiumSwingGui.refreshTable(rowList);

	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static void refresh() {
		caesiumSwingGui.setSchedulerLightStatus(scheduler.isInStandbyMode());
	}
}
