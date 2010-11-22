package net.karlmartens.ui.widget;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;

public final class GridChooserColumn extends Item {

	private TableColumn[] _widgets;

	public GridChooserColumn(GridChooser parent, int style) {
		this(parent, style, parent.getColumnCount());
	}
	
	public GridChooserColumn(GridChooser parent, int style, int index) {
		super(parent, style);
		parent.createItem(this, index);
	}
	
	@Override
	public void setText(String text) {
		for (TableColumn widget : _widgets) {
			widget.setText(text);
		}
	}
	
	@Override
	public void setImage(Image image) {
		for (TableColumn widget : _widgets) {
			widget.setImage(image);
		}
	}
	
	public void setWidth(int width) {
		for (TableColumn widget : _widgets) {
			widget.setWidth(width);
		}
	}
	
	@Override
	public String getText() {
		return _widgets[0].getText();
	}
	
	@Override
	public Image getImage() {
		return _widgets[0].getImage();
	}

	public int getWidth() {
		return _widgets[0].getWidth() ;
	}

	void registerWidgets(TableColumn[] widgets) {
		_widgets = widgets;
	}

	public void release() {
		_widgets = null;
	}
}
