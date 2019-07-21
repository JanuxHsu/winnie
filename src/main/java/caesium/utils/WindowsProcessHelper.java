package caesium.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class WindowsProcessHelper {

	public static long getProcessID(Process p) {
		long result = -1;
		try {
			// for windows
			if (p.getClass().getName().equals("java.lang.Win32Process")
					|| p.getClass().getName().equals("java.lang.ProcessImpl")) {
				Field f = p.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				long handl = f.getLong(p);
				Kernel32 kernel = Kernel32.INSTANCE;
				WinNT.HANDLE hand = new WinNT.HANDLE();
				hand.setPointer(Pointer.createConstant(handl));
				result = kernel.GetProcessId(hand);
				f.setAccessible(false);
			}
			// for Unix based operating systems
			else if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				result = f.getLong(p);
				f.setAccessible(false);
			}
		} catch (Exception ex) {
			result = -1;
		}
		return result;
	}

	public static String[] getNewConsoleCmdArray(String jobName, String workingDir, String exec_path) {

		List<String> cmds = new ArrayList<>();
		if (!exec_path.isEmpty()) {

			cmds.add("cmd");
			cmds.add("/c");

			if (!workingDir.isEmpty()) {
				cmds.add("cd");
				cmds.add(workingDir);
				cmds.add("&");
			}

			cmds.add("start");
			cmds.add("\"" + jobName + "\"");
			cmds.add("cmd");
			cmds.add("/c");
			cmds.add("\"" + exec_path + "\"");

		}
		return cmds.toArray(new String[0]);
	}

	public static String getCmdOutput(String[] cmd) {
		String outpString = "";
		try {
			Process p = new ProcessBuilder(cmd).start();
			String stderr = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());
			String stdout = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
			outpString = stdout + stderr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outpString;
	}

	public static Map<String, String> getProcessIdsFromTitle(String titleName) {
		String[] findTitleProcessId_cmds = { "cmd", "/C", "tasklist", "/FI", "\"WINDOWTITLE eq " + titleName + "*\"",
				"/fo", "csv", "/nh" };

		String windowPID = WindowsProcessHelper.getCmdOutput(findTitleProcessId_cmds).split(",")[1].trim().replace("\"",
				"");

		Map<String, String> childPidsMap = WindowsProcessHelper.getChildPidsFromParentPid(windowPID);

		return childPidsMap;

	}

	private static Map<String, String> getChildPidsFromParentPid(String pid) {

		Map<String, String> childProcessMap = new HashMap<>();

		String[] findAllChildProcesses_cmds = { "cmd", "/c",
				"wmic process where (ParentProcessId=" + pid + ") get Caption, ProcessId /format:rawxml" };

		String rawXML = WindowsProcessHelper.getCmdOutput(findAllChildProcesses_cmds);
		// System.out.println(rawXML);
		try {
			Document document = DocumentHelper.parseText(rawXML);

			@SuppressWarnings("unchecked")
			List<Node> resultNode = document.selectNodes("/COMMAND/RESULTS/CIM/INSTANCE[@CLASSNAME='Win32_Process']");

			for (Node instance_node : resultNode) {

				Document instanceDocument = DocumentHelper.parseText(instance_node.asXML());

				Node nameNode = instanceDocument.selectSingleNode("//INSTANCE//PROPERTY[@NAME='Caption']/VALUE");
				Node pidNode = instanceDocument.selectSingleNode("//INSTANCE//PROPERTY[@NAME='ProcessId']/VALUE");

				String processName = nameNode.getText();
				String pidString = pidNode.getText();
				childProcessMap.put(processName, pidString);
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return childProcessMap;
	}
}
