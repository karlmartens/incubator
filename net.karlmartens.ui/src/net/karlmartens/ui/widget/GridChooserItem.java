package net.karlmartens.ui.widget;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;

public final class GridChooserItem extends Item {

	private TableItem _deselectedWidget;
	private TableItem _selectedWidget;
	private boolean selected = false;

	public GridChooserItem(GridChooser parent, int style) {
		super(parent, style);
		parent.createItem(this, parent.getItemCount());
	}

	void registerSelectedWidget(TableItem widget) {
		_selectedWidget = widget;
	}
	
	void registerUnselectedWidget(TableItem widget) {
		_deselectedWidget = widget;
	}
	
	@Override
	public void setImage(Image image) {
		_selectedWidget.setImage(image);
		_deselectedWidget.setImage(image);
	}
	
	@Override
	public void setText(String string) {
		_selectedWidget.setText(string);
		_deselectedWidget.setText(string);
	}
	
	public void setText(String[] strings) {
		_selectedWidget.setText(strings);
		_deselectedWidget.setText(strings);
	}
	
	@Override
	public Display getDisplay() {
		return _deselectedWidget.getDisplay();
	}
	
	@Override
	public Image getImage() {
		return _deselectedWidget.getImage();
	}
	
	@Override
	public String getText() {
		return _deselectedWidget.getText();
	}
}
