package winnie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JTable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import winnie_gui.WinnieSwingGui;
import winnie_gui.WinnieTableModel;

public class WinnieMain {
	public static String version = "Caesium Scheduler v1.2 Beta (By JanuxHsu)";

	static WinnieSwingGui caesiumSwingGui;

	public static void main(String[] args) throws IOException {

		JsonObject dataObj = new JsonObject();

		caesiumSwingGui = new WinnieSwingGui();

		caesiumSwingGui.setTilte(version);
		caesiumSwingGui.show();

		caesiumSwingGui.setActionListener("reloadBtn", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		caesiumSwingGui.setActionListener("triggerJobButton", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		caesiumSwingGui.setActionListener("toggleSchedule", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		caesiumSwingGui.setActionListener("openWebUI", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		caesiumSwingGui.setMouseListener("jobTable", new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int rowIdx = target.getSelectedRow();
				WinnieTableModel model = (WinnieTableModel) target.getModel();
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

		String statusTextString = String.format("[Local Mode]");

		caesiumSwingGui.setStatusText(statusTextString);

	}

}
