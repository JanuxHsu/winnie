package caesium.utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HtmlHelper {
	public static String listToInnerTable(ArrayList<ArrayList<String>> tableList) {
		String resultString = "";

		int columnCnt = tableList.get(0).size();

		String baseString = "<tr>" + tableList.get(0).stream().map(n -> "<td>%s</td>").collect(Collectors.joining())
				+ "</tr>";

		for (ArrayList<String> rowlist : tableList) {
			ArrayList<String> rowlist_tmp = new ArrayList<>();
			for (int i = 0; i < columnCnt; i++) {
				if (i < rowlist.size()) {
					rowlist_tmp.add(i, rowlist.get(i));
				} else {
					rowlist_tmp.add(i, "-");
				}

				// System.out.println(rowlist_tmp.get(i));

			}
			String rowStr = String.format(baseString, rowlist_tmp.toArray());
			resultString += rowStr;
		}

		return resultString;
	}
}
