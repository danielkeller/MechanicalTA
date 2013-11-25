package mta.qt;

import java.util.List;

import org.junit.runner.RunWith;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QAbstractListModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt.ItemDataRole;
import com.trolltech.qt.gui.QIcon;

public class ClassListModel extends QAbstractListModel {

	private final List<Class<?>> classes;
	
	public ClassListModel(List<Class<?>> clss) {
		classes = clss;
	}
	
	@Override
	@QtBlockedSlot
	public Object data(QModelIndex index, int role) {
		if (role == ItemDataRole.DisplayRole)
			return classes.get(index.row()).getName();
		else if (role == ItemDataRole.DecorationRole) {
			if (classes.get(index.row()).isAnnotationPresent(RunWith.class))
				return QIcon.fromTheme("emblem-system");
			else
				return QIcon.fromTheme("application-x-executable");
		}
		else
			return null;
	}

	@Override
	@QtBlockedSlot
	public int rowCount(QModelIndex arg0) {
		return classes.size();
	}

}
