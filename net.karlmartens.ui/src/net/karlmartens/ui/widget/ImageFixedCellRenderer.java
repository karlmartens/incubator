/**
 *   Copyright 2012 Karl Martens
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
package net.karlmartens.ui.widget;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTableModel;
import de.kupzog.ktable.renderers.FixedCellRenderer;

public class ImageFixedCellRenderer extends FixedCellRenderer {

  private Image _image;

  public ImageFixedCellRenderer(int style) {
    super(style);
  }

  public void setImage(Image image) {
    _image = image;
  }

  protected void drawCellContent(GC gc, Rectangle rect, int col, int row, Object content, KTableModel model, Color bgColor, Color fgColor) {
    final int originalWidth = rect.width;
    final Point pt = new Point(0, 0);

    Image image = _image;
    if (image != null) {
      final Rectangle imageBounds = image.getBounds();
      pt.x = rect.x + rect.width - 8;
      rect.width -= imageBounds.width + 8;
      pt.y = rect.y + rect.height / 2 - imageBounds.height / 2;

      // do not draw if there is not enough space for the image:
      if (rect.width + 4 < imageBounds.width) {
        rect.width += imageBounds.width + 8;
        image = null;
      }
    }

    drawCellContent(gc, rect, content.toString(), null, fgColor, bgColor);

    if (image != null) {
      gc.fillRectangle(rect.x + rect.width, rect.y, Math.min(image.getBounds().width + 8, originalWidth - rect.width - 1), rect.height);
      gc.drawImage(image, pt.x, pt.y);
    }
  }

}
