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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * @author karl
 * 
 */
final class RoundedBorder extends SchemeBorder {

  private final Dimension _corner;

  public RoundedBorder(Scheme scheme, Dimension cornerDimension) {
    super(scheme);
    _corner = cornerDimension;
  }

  @Override
  protected void paint(Graphics graphics, IFigure fig, Insets insets,
      Color[] tl, Color[] br) {
    graphics.setLineWidth(1);
    graphics.setLineStyle(Graphics.LINE_SOLID);
    graphics.setXORMode(false);

    final Rectangle rect = getPaintRectangle(fig, insets);

    final int top = rect.y;
    final int left = rect.x;
    int bottom = rect.bottom() - 1;
    int right = rect.right() - 1;

    final int radx = _corner.width / 2;
    final int rady = _corner.height / 2;

    final Point topLeft = new Point(left, top);
    final Point topRight = new Point(right - _corner.width, top);
    final Point bottomLeft = new Point(left, bottom - _corner.height);
    final Point bottomRight = new Point(right - _corner.width, bottom
        - _corner.height);

    Color color;
    for (int i = 0; i < br.length; i++) {
      color = br[i];
      graphics.setForegroundColor(color);

      graphics.drawLine(left + radx, bottom - i, right - radx, bottom - i);
      graphics.drawLine(right - i, top + rady, right - i, bottom - rady);

      graphics.drawArc(topRight.x, topRight.y, _corner.width - i,
          _corner.height - i, 0, 45);
      graphics.drawArc(bottomRight.x, bottomRight.y, _corner.width - i,
          _corner.height, 270, 90);
      graphics.drawArc(bottomLeft.x, bottomLeft.y, _corner.width - i,
          _corner.height - i, 225, 45);
    }

    for (int i = 0; i < tl.length; i++) {
      color = tl[i];
      graphics.setForegroundColor(color);

      graphics.drawLine(left + radx, top + i, right - radx, top + i);
      graphics.drawLine(left + i, top + rady, left + i, bottom - rady);

      graphics.drawArc(topRight.x, topRight.y, _corner.width - i,
          _corner.height - i, 45, 45);
      graphics.drawArc(topLeft.x, topLeft.y, _corner.width - i, _corner.height
          - i, 90, 90);
      graphics.drawArc(bottomLeft.x, bottomLeft.y, _corner.width - i,
          _corner.height - i, 180, 45);
    }
  }
}
