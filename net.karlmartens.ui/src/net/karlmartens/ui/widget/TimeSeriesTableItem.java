package net.karlmartens.ui.widget;

import net.karlmartens.ui.widget.TimeSeriesTableModel.Row;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

public class TimeSeriesTableItem extends Item {

	private TimeSeriesTable _parent;
	private Row _row;
	private Color _background;
	private Color[] _cellBackgrounds;
	private Color _foreground;
	private Color[] _cellForegrounds;
	private Font _font;
	private Font[] _cellFonts;

	public TimeSeriesTableItem(TimeSeriesTable parent, int style) {
		this(parent, style, parent.getItemCount());
	}
	
	public TimeSeriesTableItem(TimeSeriesTable parent, int style,  int rowIndex) {
		super(parent, style);
		_parent = parent;
		parent.createItem(this, rowIndex);
	}

	public TimeSeriesTable getParent() {
		checkWidget();
		return _parent;
	}
	
	Row getRow() {
		checkWidget();
		return _row;
	}

	public Image getImage(int columnIndex) {
		return null;
	}

	public void setImage(int columnIndex, Image image) {
	}
	
	@Override
	public String getText() {
		checkWidget();
		return getText(0);
	}

	public String getText(int index) {
		checkWidget();
		return _row.getValue(index);
	}
	
	@Override
	public void setText(String text) {
		checkWidget();
		setText(0, text);
	}

	public void setText(int index, String text) {
		checkWidget();
		if (text == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		_row.setValue(text, index);
	}
	
	public Color getBackground() {
		checkWidget();
		if (_background == null) {
            return _parent.getBackground();
		}
		
		return _background;
	}

	public Color getBackground(int index) {
		checkWidget();

		if (_cellBackgrounds == null || index < 0 || index >= _cellBackgrounds.length 
				|| _cellBackgrounds[index] == null) 
			return getBackground();
		
		return _cellBackgrounds[index];
	}

	public void setBackground(final int index, final Color color) {
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
		_row.setBackground(index, color);
	}
	
	public void setBackground(final Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		if (color == _background)
			return;
		
		if (color != null && color.equals(_background))
			return;
		
		_background = color;
		_row.setBackground(color);
	}

	
	public Color getForeground() {
		checkWidget();
		if (_foreground == null) {
            return _parent.getForeground();
		}
		
		return _foreground;
	}

	public Color getForeground(int index) {
		checkWidget();

		if (_cellForegrounds == null || index < 0 || index >= _cellForegrounds.length 
				|| _cellForegrounds[index] == null) 
			return getForeground();
		
		return _cellForegrounds[index];
	}

	public void setForeground(final int index, final Color color) {
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
		_row.setForeground(index, color);
	}
	
	public void setForeground(final Color color) {
		checkWidget();
		if (color != null && color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		
		if (color == _background)
			return;
		
		if (color != null && color.equals(_background))
			return;
		
		_background = color;
		_row.setForeground(color);
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
		_row.setFont(index, font);
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
		_row.setFont(font);
	}
	
	public Font getFont(int columnIndex) {
		checkWidget();
		if (_font == null)
			return _parent.getFont();
		
		return _font;
	}

	public Rectangle getBounds(int columnIndex) {
		checkWidget();
		return _parent.getBounds(this, columnIndex);
	}

	public Rectangle getBounds() {
		checkWidget();
		return _parent.getBounds(this);
	}

	void register(Row row) {
		checkWidget();
		_row = row;
	}

	void clear() {
		checkWidget();
		_background = null;
		_cellBackgrounds = null;
		_foreground = null;
		_cellForegrounds = null;
		_font = null;
		_cellFonts = null;
		
		_row.setBackground(null);
		_row.setForeground(null);
		_row.setFont(null);
		for (int i=0; i<_parent.getColumnCount(); i++) {
			_row.setValue("", i);
			_row.setBackground(i, null);
			_row.setForeground(i, null);
			_row.setFont(i, null);
		}
	}

	void release() {
		checkWidget();
		clear();
		_parent = null;
		_row = null;
	}
}
