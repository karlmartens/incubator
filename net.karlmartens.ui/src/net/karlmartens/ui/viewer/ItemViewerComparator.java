package net.karlmartens.ui.viewer;

import java.util.Comparator;

import net.karlmartens.platform.util.ComparableComparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Item;

public class ItemViewerComparator extends ViewerComparator {
	
	private final Comparator<String> _comparator;

	public ItemViewerComparator() {
		this(new ComparableComparator<String>());
	}
	
	public ItemViewerComparator(Comparator<String> comparator) {
		if (comparator == null)
			throw new NullPointerException();
		_comparator = comparator;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof Item && e2 instanceof Item) {
			final String s1 = ((Item)e1).getText();
			final String s2 = ((Item)e2).getText();
			return _comparator.compare(s1, s2);
		}
		return super.compare(viewer, e1, e2);
	}
}