package winnie_gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import winnie.WinnieMain;

public class WinnieSwingGui {

	public enum IconMessageMode {
		ALWAYS, PERIODIC
	}

	protected JFrame mainWindow;
	TrayIcon trayIcon;
	Font monoFont = new Font(Font.MONOSPACED, Font.BOLD, 10);
	Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	Font loggerFont = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
	JLabel statusLabel;

	JLabel contextTimeLabel;
	JLabel selectedJobLabel;

	JLabel workerIndicator;
	JTable systemInfoTable;
	JTextArea logArea;

	JButton toggleSchedulerButton;

	Map<String, ActionListener> actionListenerMap = new HashMap<>();
	Map<String, MouseListener> mouseListenerMap = new HashMap<>();

	private Long iconShowMessageTimestamp = Calendar.getInstance().getTimeInMillis();
	JLabel schedulingLabel;

	public WinnieSwingGui() {

		JFrame window = new JFrame();

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}

		window.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getClassLoader().getResource("resources/clock64.png")));

		// window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(300, 400));
		window.setMinimumSize(new Dimension(300, 400));

		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(setupTopPanel(), BorderLayout.NORTH);
		mainPanel.add(setupCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(setupBottomPanel(), BorderLayout.SOUTH);

		window.add(mainPanel);

		window.pack();
		window.setLocationRelativeTo(null);
		this.mainWindow = window;

		this.trayIcon = this.addSystemTray();

		TrayIcon icon = this.trayIcon;

		this.mainWindow.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				icon.displayMessage("Caesium Scheduler", "Caesium is still running!", MessageType.INFO);
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	private TrayIcon addSystemTray() {

		JFrame main = this.mainWindow;

		// checking for support
		if (!SystemTray.isSupported()) {
			System.out.println("System tray is not supported !!! ");
			return null;
		}
		// get the systemTray of the systems
		SystemTray systemTray = SystemTray.getSystemTray();

		Image image = Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getClassLoader().getResource("resources/clock64.png"));

		// Pop-up menu
		PopupMenu trayPopupMenu = new PopupMenu();

		// add menu item for Pop-up menu
		MenuItem version = new MenuItem("Version : " + WinnieMain.version);
		version.setEnabled(false);
		trayPopupMenu.add(version);
		trayPopupMenu.addSeparator();

		MenuItem status = new MenuItem("Status: Unknown");
		status.setEnabled(false);
		trayPopupMenu.add(status);
		trayPopupMenu.addSeparator();

		MenuItem action = new MenuItem("Show");

		action.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.setVisible(true);
			}
		});
		trayPopupMenu.add(action);

		// 2nd menu item of pop-up menu
		MenuItem close = new MenuItem("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		trayPopupMenu.add(close);

		// setting tray icon
		TrayIcon trayIcon = new TrayIcon(image, this.mainWindow.getTitle(), trayPopupMenu);
		// adjust to default size as per system recommendation
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
		} catch (AWTException awtException) {
			awtException.printStackTrace();
		}
		// trayIcon.displayMessage("Hello, World", "notification demo",
		// MessageType.NONE);
		return trayIcon;
	}

	private JPanel setupTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());

		JPanel topBar = new JPanel(new BorderLayout());
		JLabel statusLabel = new JLabel("Connecting...");
		this.statusLabel = statusLabel;
		statusLabel.setOpaque(true);
		statusLabel.setBackground(Color.RED);
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setFont(labelFont);

		statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		JLabel hydraIcon = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getClassLoader().getResource("resources/clock24.png"))));
		hydraIcon.setHorizontalAlignment(JLabel.CENTER);
		topBar.add(hydraIcon, BorderLayout.WEST);
		topBar.add(statusLabel, BorderLayout.CENTER);

		JPanel dashboardPanel = new JPanel(new GridLayout(1, 3));

		dashboardPanel.setOpaque(true);
		dashboardPanel.setBackground(new Color(44, 62, 80));

		JPanel leftDashPanel = new JPanel(new GridLayout(4, 1, 2, 2));

		leftDashPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JLabel connectionIndicator = new JLabel("Initializing...");
		connectionIndicator.setOpaque(true);
		connectionIndicator.setHorizontalAlignment(JLabel.CENTER);
		connectionIndicator.setBackground(Color.GRAY);
		connectionIndicator.setForeground(Color.white);
		// connectionIndicator.setFont(defaultFont);
		connectionIndicator.setHorizontalAlignment(JLabel.LEFT);
		connectionIndicator.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		connectionIndicator.setFont(defaultFont);

		this.contextTimeLabel = connectionIndicator;
		leftDashPanel.add(connectionIndicator);

		JLabel schedulingLabel = new JLabel("Scheduler Off");
		schedulingLabel.setOpaque(true);
		schedulingLabel.setHorizontalAlignment(JLabel.CENTER);
		schedulingLabel.setBackground(Color.red);
		schedulingLabel.setForeground(Color.white);
		schedulingLabel.setHorizontalAlignment(JLabel.LEFT);
		schedulingLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		schedulingLabel.setFont(defaultFont);

		this.schedulingLabel = schedulingLabel;

		JLabel emptyLabe2 = new JLabel("");
		emptyLabe2.setOpaque(true);
		emptyLabe2.setHorizontalAlignment(JLabel.CENTER);
		emptyLabe2.setBackground(Color.white);
		emptyLabe2.setForeground(Color.white);
		emptyLabe2.setHorizontalAlignment(JLabel.LEFT);
		emptyLabe2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		emptyLabe2.setFont(defaultFont);

		leftDashPanel.add(schedulingLabel);
		leftDashPanel.add(emptyLabe2);

		JLabel selectedJobLabel = new JLabel(" - - - ");
		selectedJobLabel.setOpaque(true);
		selectedJobLabel.setHorizontalAlignment(JLabel.CENTER);
		selectedJobLabel.setBackground(Color.GRAY);
		selectedJobLabel.setForeground(Color.white);
		// workerIndicator.setFont(defaultFont);
		selectedJobLabel.setHorizontalAlignment(JLabel.LEFT);
		selectedJobLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		selectedJobLabel.setFont(defaultFont);

		this.selectedJobLabel = selectedJobLabel;
		leftDashPanel.add(selectedJobLabel);

		dashboardPanel.add(leftDashPanel);

		JPanel rightDashPanel = new JPanel(new BorderLayout());

		JButton reloadConfigBtn = new JButton("Reload");
		// reloadConfigBtn.setContentAreaFilled(false);
		reloadConfigBtn.setFocusable(false);
		reloadConfigBtn.setFont(defaultFont);
		reloadConfigBtn.setBorder(BorderFactory.createLineBorder(Color.black));
		reloadConfigBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (actionListenerMap.get("reloadBtn") != null) {
					actionListenerMap.get("reloadBtn").actionPerformed(e);
				}

			}
		});

		JButton triggerJobButton = new JButton("Trigger");
		triggerJobButton.setFocusable(false);
		triggerJobButton.setFont(defaultFont);
		triggerJobButton.setBorder(BorderFactory.createLineBorder(Color.black));
		triggerJobButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (actionListenerMap.get("triggerJobButton") != null) {
					actionListenerMap.get("triggerJobButton").actionPerformed(e);
				}

			}
		});

		JPanel barContainer = new JPanel(new GridLayout(4, 1, 2, 2));
		barContainer.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JButton toggleSchedulingButton = new JButton("Disable");
		toggleSchedulingButton.setFocusable(false);
		toggleSchedulingButton.setFont(defaultFont);
		toggleSchedulingButton.setBorder(BorderFactory.createLineBorder(Color.black));
		toggleSchedulingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (actionListenerMap.get("toggleSchedule") != null) {
					actionListenerMap.get("toggleSchedule").actionPerformed(e);
				}

			}
		});

		this.toggleSchedulerButton = toggleSchedulingButton;

		JButton openWebUIButton = new JButton("Open Web UI");
		openWebUIButton.setFocusable(false);
		openWebUIButton.setFont(defaultFont);
		openWebUIButton.setBorder(BorderFactory.createLineBorder(Color.black));
		openWebUIButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (actionListenerMap.get("openWebUI") != null) {
					actionListenerMap.get("openWebUI").actionPerformed(e);
				}

			}
		});

		barContainer.add(reloadConfigBtn);
		barContainer.add(toggleSchedulingButton);
		barContainer.add(openWebUIButton);
		barContainer.add(triggerJobButton);

		rightDashPanel.add(barContainer, BorderLayout.CENTER);
		dashboardPanel.add(rightDashPanel);

		// dashboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		dashboardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// dashboardPanel.setPreferredSize(new Dimension(1000, 70));
		topPanel.add(topBar, BorderLayout.NORTH);
		topPanel.add(dashboardPanel, BorderLayout.SOUTH);

		// topPanel.setPreferredSize(new Dimension(800, 80));

		return topPanel;
	}

	private JPanel setupCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout());

		centerPanel.setOpaque(true);
		centerPanel.setBackground(new Color(44, 62, 80));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		WinnieTableModel tableModel = new WinnieTableModel();
		tableModel.addColumn("Item");
		tableModel.addColumn("Value");

		JTable resultTable = new JTable(tableModel);

		resultTable.setFont(defaultFont);
		resultTable.getTableHeader().setFont(defaultFont);

		resultTable.setAutoCreateRowSorter(true);

		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		resultTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				if (mouseListenerMap.get("jobTable") != null) {

					mouseListenerMap.get("jobTable").mouseClicked(e);
				}

			}
		});

		this.systemInfoTable = resultTable;
		centerPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

		return centerPanel;
	}

	private JPanel setupBottomPanel() {
		JPanel logPanel = new JPanel(new GridLayout(1, 1));

		logPanel.setOpaque(true);
		logPanel.setBackground(new Color(44, 62, 80));
		logPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		logPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		JTextArea logArea = new JTextArea();

		logArea.setLineWrap(true);
		logArea.setEditable(false);
		logArea.setFont(loggerFont);
		this.logArea = logArea;

		JScrollPane scrollPane = new JScrollPane(logArea);

		scrollPane.setMaximumSize(new Dimension(2000, 50));
		scrollPane.setPreferredSize(new Dimension(0, 50));
		logPanel.add(scrollPane);

		return logPanel;

	}

	public void setContextTimeText(long startupDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		SwingUtilities.invokeLater(() -> {
			this.contextTimeLabel.setText("Last Load: " + sdf.format(new Date(startupDate)));
			this.contextTimeLabel.setBackground(new Color(39, 174, 96));
			this.contextTimeLabel.setForeground(Color.white);
		});

	}

	public void displayIconMessage(String caption, String message, MessageType messageType, IconMessageMode mode) {
		// TODO Auto-generated method stub

		if (mode.equals(IconMessageMode.ALWAYS)) {
			this.trayIcon.displayMessage(caption, message, messageType);
		} else {
			Long lapsed = Calendar.getInstance().getTimeInMillis() - this.iconShowMessageTimestamp;

			if (lapsed > 20000 && !this.mainWindow.isVisible()) {
				this.trayIcon.displayMessage(caption, message, messageType);
				this.iconShowMessageTimestamp = Calendar.getInstance().getTimeInMillis();
			}

		}

	}

	public void displaySystemLog(String line) {
		String lineText = String.format("%s%n", line);

		SwingUtilities.invokeLater(() -> {
			if (this.logArea.getLineCount() > 10) {
				logArea.setText("");
			}

			this.logArea.append(lineText);
		});

	}

	public void setTilte(String title) {
		SwingUtilities.invokeLater(() -> {
			mainWindow.setTitle(title);
		});
	}

	public void show() {
		this.mainWindow.setVisible(true);
	}

	public void setActionListener(String componentId, ActionListener listener) {
		this.actionListenerMap.put(componentId, listener);
	}

	public void setMouseListener(String componentId, MouseListener listener) {
		this.mouseListenerMap.put(componentId, listener);
	}

	public void refreshTable(List<List<String>> rowList) {

		try {

			DefaultTableModel model = (DefaultTableModel) this.systemInfoTable.getModel();

			SwingUtilities.invokeLater(() -> {
				@SuppressWarnings("unchecked")
				Vector<Vector<String>> dataVector = model.getDataVector();
				dataVector.clear();

				rowList.stream().forEach(row -> {
					Vector<String> vect = row.stream().map(columnVal -> {

						return columnVal.trim();
					}).collect(Collectors.toCollection(Vector::new));
					dataVector.add(vect);

				});

				model.fireTableDataChanged();

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setSelectedJob(String jobName) {
		SwingUtilities.invokeLater(() -> {
			selectedJobLabel.setText(jobName);
		});

	}

	public String getSeletedJobName() {

		return this.selectedJobLabel.getText().trim();
	}

	public void setStatusText(String statusTextString) {
		SwingUtilities.invokeLater(() -> {
			this.statusLabel.setText(statusTextString);
			this.statusLabel.setBackground(new Color(39, 174, 96));
			this.statusLabel.setForeground(Color.white);
		});

	}

	public void setSchedulerLightStatus(boolean inStandbyMode) {
		SwingUtilities.invokeLater(() -> {
			if (inStandbyMode) {
				this.schedulingLabel.setText("Scheduler Off");
				this.toggleSchedulerButton.setText("Enable");
				this.schedulingLabel.setBackground(Color.red);
				this.schedulingLabel.setForeground(Color.white);
			} else {
				this.schedulingLabel.setText("Scheduler On");
				this.toggleSchedulerButton.setText("Disable");
				this.schedulingLabel.setBackground(new Color(39, 174, 96));
				this.schedulingLabel.setForeground(Color.white);
			}

		});

	}

}
