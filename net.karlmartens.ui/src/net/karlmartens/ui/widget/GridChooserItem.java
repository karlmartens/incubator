package net.karlmartens.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

public final class GridChooserItem extends Item {

	private GridChooser _parent;
	private Image[] _images;
	private Color _background;
	private Color[] _cellBackgrounds;
	private Color _foreground;
	private Color[] _cellForegrounds;
	private Font _font;
	private Font[] _cellFonts;
	private String[] _strings;
	private int _selectionOrder = -1;

	public GridChooserItem(GridChooser parent, int style) {
		this(parent, style, parent.getItemCount());
	}
	
	public GridChooserItem(GridChooser parent, int style,  int rowIndex) {
		super(parent, style);
		_parent = parent;
		parent.createItem(this, rowIndex);
	}
	
	public Rectangle getBounds(int index) {
		checkWidget();
		return _parent.getBounds(this, index);
	}
	
	public Rectangle getBounds() {
		checkWidget();
		return _parent.getBounds(this);
	}
	
	@Override
	public void setImage(Image image) {
		checkWidget();
		setImage(0, image);
	}
	
	public void setImage(Image[] images) {
		checkWidget();
		if (images == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		for (int i=0; i<images.length; i++) {
			final Image image = images[i];
			if (image == null)
				continue;
			setImage(i, image);
		}
	}
	
	public void setImage(int index, Image image) {
		checkWidget();
		if (image == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		if (index == 0) {
			if (image.equals(getImage()))
				return;
			
			super.setImage(image);
		}
		
		final int count = Math.max(1, _parent.getColumnCount());
		if (index < 0 || index >= count)
			return;
		
		if (_images == null && index != 0) {
			_images = new Image[count];
			_images[0] = super.getImage();
		}
		
		if (_images != null) {
			if (image.equals(_images[index]))
				return;
			_images[index] = image;
		}
		
		_parent.redraw();
	}
	
	public void setBackground(Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		if (color == _background)
			return;
		
		if (color != null && color.equals(_background))
			return;
		
		_background = color;
		_parent.redraw();
	}
	
	public void setBackground(int index, Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);

		final int count = Math.max (1, _parent.getColumnCount ());
		if (index < 0 || index >= count) 
			return;
		
		if (_cellBackgrounds == null) {
			_cellBackgrounds = new Color[count];
		}
		
		if (_cellBackgrounds[index] == color)
			return;
		
		if (color != null && color.equals(_cellBackgrounds[index]))
			return;
		
		_cellBackgrounds[index] = color;
		_parent.redraw();
	}
	
	public void setForeground(Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		if (color == _foreground)
			return;
		
		if (color != null && color.equals(_foreground))
			return;
		
		_foreground = color;
		_parent.redraw();
	}
	
	public void setForeground(int index, Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);

		final int count = Math.max (1, _parent.getColumnCount ());
		if (index < 0 || index >= count) 
			return;
		
		if (_cellForegrounds == null) {
			_cellForegrounds = new Color[count];
		}
		
		if (_cellForegrounds[index] == color)
			return;
		
		if (color != null && color.equals(_cellForegrounds[index]))
			return;
		
		_cellForegrounds[index] = color;
		_parent.redraw();
	}

	public void setFont(Font font) {
		checkWidget();
		if (font != null && font.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		if (_font == font)
			return;
			
		if (font != null && font.equals(_font))
			return;
		
		_font = font;
		_parent.redraw();
	}
	
	public void setFont(int index, Font font) {
		checkWidget();
		if (font != null && font.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);

		final int count = Math.max (1, _parent.getColumnCount ());
		if (index < 0 || index >= count) 
			return;

		if (_cellFonts == null)
			_cellFonts = new Font[count];
		
		if (_cellFonts[index] == font)
			return;
			
		if (font != null && font.equals(_cellFonts[index]))
			return;
		
		_cellFonts[index] = font;
		_parent.redraw();
	}
	
	@Override
	public void setText(String text) {
		checkWidget();
		setText(0, text);
	}
	
	public void setText(String[] strings) {
		checkWidget();
		if (strings == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		for (int i=0; i<strings.length; i++) {
			final String text = strings[i];
			if (text == null)
				continue;
			setText(i, text);
		}
		_parent.redraw();
	}
	
	public void setText(int index, String text) {
		checkWidget();
		if (text == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		if (index == 0) {
			if (text.equals(getText()))
				return;
			
			super.setText(text);
		}
		
		final int count = Math.max(1, _parent.getColumnCount());
		if (index < 0 || index >= count)
			return;
		
		if (_strings == null && index != 0) {
			_strings = new String[count];
			_strings[0] = super.getText();
		}
		
		if (_strings != null) {
			if (text.equals(_strings[index]))
				return;
			_strings[index] = text;
		}
		
		_parent.redraw();
	}

	public void setSelected(boolean selected) {
		checkWidget();
		if (selected && _selectionOrder >= 0) 
			return;
		
		if (!selected && _selectionOrder < 0) 
			return;
		
		setSelectionOrder(selected ? _parent.getSelectionCount() : -1, true);
		_parent.redraw();
	}
	
	void setSelectionOrder(int order, boolean allowDeselect) {
		final int minIndex = allowDeselect ? -1 : 0;
		final int maxIndex = _parent.getSelectionCount() - (_selectionOrder < 0 ? 0 : 1);
		final int targetOrder = Math.max(minIndex, Math.min(maxIndex, order)); 
		if (targetOrder == _selectionOrder)
			return;
		
		final GridChooserItem[] items = _parent.getSelection();
		final int lower; 
		final int upper; 
		final int correction;
		if (targetOrder < 0) {
			lower = _selectionOrder + 1;
			upper = items.length - 1;
			correction = -1;
		} else if (_selectionOrder < 0) {
			lower = targetOrder;
			upper = items.length - 1;
			correction = 1;
		} else if (_selectionOrder < targetOrder){
			lower = _selectionOrder + 1;
			upper = Math.min(targetOrder, items.length - 1);
			correction = -1;
		} else {
			lower = targetOrder;
			upper = Math.max(0, _selectionOrder - 1);
			correction = 1;
		}
		for(int i=lower; i<=upper; i++) {
			items[i]._selectionOrder += correction;
		}

		_selectionOrder = targetOrder;
		_parent.redraw();
	}
	
	public void setSelectionOrder(int order) {
		setSelectionOrder(order, true);
	}

	public GridChooser getParent() {
		return _parent;
	}
	
	public Color getBackground() {
		checkWidget();
		if (_background == null)
			return _parent.getBackground();
		
		return _background;
	}

	public Color getBackground(int index) {
		checkWidget();

		if (_cellBackgrounds == null || index < 0 || index >= _cellBackgrounds.length 
				|| _cellBackgrounds[index] == null) 
			return getBackground();
		
		return _cellBackgrounds[index];
	}
	
	public Color getForeground() {
		checkWidget();
		if (_foreground == null)
			return _parent.getForeground();
		
		return _foreground;
	}

	public Color getForeground(int index) {
		checkWidget();

		if (_cellForegrounds == null || index < 0 || index >= _cellForegrounds.length 
				|| _cellForegrounds[index] == null) 
			return getForeground();
		
		return _cellForegrounds[index];
	}

	public Font getFont() {
		checkWidget();
		if (_font == null)
			return _parent.getFont();
		
		return _font;
	}
	
	public Font getFont(int index) {
		checkWidget();

		if (_cellFonts == null || index < 0 || index >= _cellFonts.length 
				|| _cellFonts[index] == null) 
			return getFont();
		
		return _cellFonts[index];
	}

	public Image getImage(int index) {
		checkWidget();
		if (index == 0) return getImage();
		if (_images == null || index < 0 || index >= _images.length)
			return null;

		return _images[index];
	}

	public String getText(int index) {
		checkWidget();
		if (index == 0) return getText();
		if (_strings == null || index < 0 || index >= _strings.length)
			return null;

		return _strings[index];
	}

	public boolean isSelected() {
		return _selectionOrder >= 0;
	}

	public int getSelectionOrder() {
		return _selectionOrder;
	}
	
	void clear() {
		super.setText("");
		super.setImage(null);
		_strings = null;
		_images = null;
		_font = null;
		_cellFonts = null;
		_background = null;
		_cellBackgrounds = null;
		_foreground = null;
		_cellForegrounds = null;
	}
	
	void release() {
		_parent = null;
		clear();
	}
}
