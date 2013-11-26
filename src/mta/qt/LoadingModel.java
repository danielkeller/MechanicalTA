package mta.qt;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QAbstractListModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt.ItemDataRole;

public class LoadingModel extends QAbstractListModel {
	public static LoadingModel model = new LoadingModel(); 
	
	@Override
	@QtBlockedSlot
	public Object data(QModelIndex idx, int role) {
		if (role == ItemDataRole.DisplayRole)
			return "Loading...";
		return null;
	}

	@Override
	@QtBlockedSlot
	public int rowCount(QModelIndex idx) {
		return 1;
	}

}
