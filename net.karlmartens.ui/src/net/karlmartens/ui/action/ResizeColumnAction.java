/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.ui.action;

import java.util.ResourceBundle;

import net.karlmartens.ui.widget.TimeSeriesTable;
import net.karlmartens.ui.widget.TimeSeriesTableColumn;
import net.karlmartens.ui.widget.TimeSeriesTableItem;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.renderers.CheckableCellRenderer;

public class ResizeColumnAction extends Action {

	private static final int IMAGE_SPACER = 6;
	private static final int TEXT_SPACER = 12;

	private final TimeSeriesTable _table;
	private int _columnIndex;

	public ResizeColumnAction(TimeSeriesTable table, int columnIndex) {
		_table = table;
		setColumnIndex(columnIndex);

		final ResourceBundle bundle = ResourceBundle
				.getBundle("net.karlmartens.ui.locale.messages");
		setText(bundle.getString("ResizeColumnAction.TEXT"));
	}

	public void setColumnIndex(int index) {
		_columnIndex = index;
		updateEnablement();
	}

	@Override
	public void run() {
		if (!isEnabled())
			return;

		final GC gc = new GC(_table);
		final Font font = createColumnHeaderFont();
		gc.setFont(font);

		final TimeSeriesTableColumn column = _table.getColumn(_columnIndex);
		int width = gc.textExtent(column.getText()).x + TEXT_SPACER;

		if ((column.getStyle() & SWT.CHECK) > 0) {
			width = Math.max(width,
					CheckableCellRenderer.IMAGE_CHECKED.getImageData().width
							+ IMAGE_SPACER);
		} else {
			for (int i = 0; i < _table.getItemCount(); i++) {
				final TimeSeriesTableItem item = _table.getItem(i);
				gc.setFont(item.getFont(i));
				width = Math.max(width,
						gc.textExtent(item.getText(_columnIndex)).x
								+ TEXT_SPACER);
			}
		}
		gc.dispose();
		font.dispose();

		if (width <= 0)
			return;

		column.setWidth(width);
	}

	private Font createColumnHeaderFont() {
		final Display display = _table.getDisplay();
		final FontData[] fontData = _table.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		return new Font(display, fontData);
	}

	private void updateEnablement() {
		setEnabled(_columnIndex >= 0 && //
				_columnIndex < (_table.getColumnCount() + _table
						.getPeriodCount()) && //
				_table.getColumn(_columnIndex).isVisible());
	}
}
