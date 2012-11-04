/**
 *   Copyright 2011 Karl Martens
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   net.karlmartens.ui, is a library of UI widgets
 */
package net.karlmartens.ui.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.karlmartens.ui.Images;
import net.karlmartens.ui.Messages;
import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableColumn;
import net.karlmartens.ui.widget.TableItem;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.renderers.CheckableCellRenderer;

public class ResizeColumnAction extends Action {

  private static final int IMAGE_SPACER = 6;
  private static final int TEXT_SPACER = 12;

  private final Table _table;
  private int _columnIndex;

  public ResizeColumnAction(Table table, int columnIndex) {
    _table = table;
    setColumnIndex(columnIndex);

    setText(Messages.RESIZE_COLUMN.string());
    setImageDescriptor(Images.RESIZE);
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

    final TableColumn column = _table.getColumn(_columnIndex);
    int width = gc.textExtent(column.getText()).x + TEXT_SPACER;

    final Image columnImage = column.getImage();
    if (columnImage != null) {
      width += columnImage.getBounds().width + TEXT_SPACER;
    }

    if ((column.getStyle() & SWT.CHECK) > 0) {
      width = Math.max(width,
          CheckableCellRenderer.IMAGE_CHECKED.getImageData().width
              + IMAGE_SPACER);
    } else {
      final Map<Font, String> messages = new HashMap<Font, String>();

      for (int i = 0; i < _table.getItemCount(); i++) {
        final TableItem item = _table.getItem(i);
        final Font f = item.getFont(_columnIndex);
        final String m1 = messages.get(f);
        final String m2 = item.getText(_columnIndex);
        if (m1 == null) {
          messages.put(f, m2);
          continue;
        }

        if (m2 == null) {
          messages.put(f, m1);
          continue;
        }

        if (m2.length() > m1.length()) {
          messages.put(f, m2);
        }
      }

      for (Entry<Font, String> entry : messages.entrySet()) {
        width = Math
            .max(width, gc.textExtent(entry.getValue()).x + TEXT_SPACER);
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
        _columnIndex < (_table.getColumnCount()) && //
        _table.getColumn(_columnIndex).isVisible());
  }
}
