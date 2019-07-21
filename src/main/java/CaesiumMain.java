
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
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

	static Logger logger = Logger.getRootLogger();
	static StdScheduler scheduler;
	static FileSystemXmlApplicationContext context;
	static CaesiumSwingGui caesiumSwingGui;

	public static void main(String[] args) throws IOException, SchedulerException {
		caesiumSwingGui = new CaesiumSwingGui();

		loadApplicationContext();
		// context = new FileSystemXmlApplicationContext("Caesium-beans-config.xml");

		caesiumSwingGui.setTilte("Caesium Scheduler v1.1 Beta (By JanuxHsu)");
		caesiumSwingGui.show();

		CaesiumLogAppender caesiumLogAppender = new CaesiumLogAppender();
		logger.addAppender(caesiumLogAppender);

		scheduler = (StdScheduler) context.getBean("schedulerFactoryBean");

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

					scheduler.triggerJob(jobKey);
				} catch (SchedulerException e1) {
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
}
