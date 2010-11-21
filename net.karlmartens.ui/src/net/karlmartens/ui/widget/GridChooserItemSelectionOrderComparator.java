package net.karlmartens.ui.widget;

import java.util.Comparator;

final class GridChooserItemSelectionOrderComparator implements
		Comparator<GridChooserItem> {
	@Override
	public int compare(GridChooserItem o1, GridChooserItem o2) {
		if (o1 == o2)
			return 0;
		
		if (o1 == null)
			return -1;
		
		if (o2 == null)
			return 1;
		
		return o1.getSelectionOrder() - o2.getSelectionOrder();
	}
}
