package winnie;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import winnie_gui.WinnieSwingGui;
import winnie_gui.WinnieTableModel;

public class WinnieMain {
	public static String version = "Caesium Scheduler v1.2 Beta (By JanuxHsu)";

	static WinnieSwingGui caesiumSwingGui;

	public static void main(String[] args) throws IOException {

		caesiumSwingGui = new WinnieSwingGui();

		caesiumSwingGui.setTilte(version);
		caesiumSwingGui.show();

		caesiumSwingGui.setActionListener("reloadBtn", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

				JsonArray dataList = new JsonArray();
				JsonObject dataObj = new JsonObject();

				dataObj.addProperty("aaaaa", 1121321);
				dataObj.addProperty("bbbbb", 1111111);
				dataObj.addProperty("ccccc", 1111111);
				dataObj.addProperty("ddddd", 1111111);
				dataObj.addProperty("eeeee", 1111111);
				dataObj.addProperty("fffff", 1235333);
				dataObj.addProperty("ggggg", 1111111);

				dataList.add(dataObj);
				dataList.add(dataObj);
				dataList.add(dataObj);
				dataList.add(dataObj);
				dataList.add(dataObj);

				System.out.println(gsonPretty.toJson(dataList));

				List<Map<String, String>> rowList = new ArrayList<>();
				for (JsonElement data : dataList) {

					JsonObject json_data = (JsonObject) data;

					Map<String, String> valuesList = new LinkedHashMap<>();

					for (String key : json_data.keySet()) {
						valuesList.put(key, json_data.get(key).getAsString());
					}

					rowList.add(valuesList);
				}

				caesiumSwingGui.refreshTable(rowList);

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
