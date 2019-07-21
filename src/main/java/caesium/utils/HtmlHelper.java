package caesium.utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HtmlHelper {
	public static String listToInnerTable(ArrayList<ArrayList<String>> tableList) {
		String resultString = "";

		String baseString = "<tr>" + tableList.get(0).stream().map(n -> "<td>%s</td>").collect(Collectors.joining())
				+ "</tr>";

		for (ArrayList<String> rowlist : tableList) {
			String rowStr = String.format(baseString, rowlist.toArray());

			resultString += rowStr;
		}

		return resultString;
	}
}
