package mta.qt;

import java.util.List;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QAbstractListModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt.ItemDataRole;

public class KeyValueModel<K, V> extends QAbstractListModel {

	private final List<K> keys;
	private final List<V> values;
	
	public KeyValueModel(List<K> keys, List<V> values) {
		this.keys = keys;
		this.values = values;
		if (keys.size() != values.size())
			throw new Error("Keys and values must have the same length");
	}
	
	@Override
	@QtBlockedSlot
	public Object data(QModelIndex index, int role) {
		if (role == ItemDataRole.DisplayRole)
			return keys.get(index.row());
		else if (role == ItemDataRole.UserRole)
			return values.get(index.row());
		else
			return null;
	}

	@Override
	@QtBlockedSlot
	public int rowCount(QModelIndex arg0) {
		return keys.size();
	}
}
