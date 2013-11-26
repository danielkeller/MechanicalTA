package mta.qt;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QAbstractListModel;
import com.trolltech.qt.core.QModelIndex;

public class NullModel extends QAbstractListModel {
	public static NullModel model = new NullModel();
	@Override
	@QtBlockedSlot
	public Object data(QModelIndex arg0, int arg1) {
		return null;
	}
	@Override
	@QtBlockedSlot
	public int rowCount(QModelIndex arg0) {
		return 0;
	}
}
