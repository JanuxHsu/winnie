package caesium_gui;

import javax.swing.table.DefaultTableModel;

public class CaesiumTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -6191854715406702714L;

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
