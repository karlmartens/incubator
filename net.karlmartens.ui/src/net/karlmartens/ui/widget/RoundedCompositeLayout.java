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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * @author karl
 * 
 */
final class RoundedCompositeLayout extends Layout {

  static final int RADIUS = 8;
  static final int DIAMETER = RADIUS + RADIUS;

  private Point _cache;

  @Override
  protected Point computeSize(Composite composite, int wHint, int hHint,
      boolean flushCache) {
    final Control client = client(composite);
    cachePreferedSizes(client, flushCache);
    return new Point(_cache.x + DIAMETER + 2, _cache.y + 2);
  }

  @Override
  protected void layout(Composite composite, boolean flushCache) {
    final Control client = client(composite);
    cachePreferedSizes(client, flushCache);

    final Rectangle clientArea = composite.getClientArea();
    int width = _cache.x;
    int height = _cache.y;

    final int style = composite.getStyle();

    int x = 0;
    if ((style & SWT.LEFT) != 0) {
      // nothing to do
    } else if ((style & SWT.RIGHT) != 0) {
      x = clientArea.width - RADIUS - 1 - width;
    } else if ((style & SWT.CENTER) != 0) {
      x = (clientArea.width - width) / 2;
    } else {
      width = clientArea.width - DIAMETER - 2;
    }
    x = Math.max(x, RADIUS + 1);
    width = Math.max(0, width);

    int y = 0;
    if ((style & SWT.TOP) != 0) {
      // nothing to do
    } else if ((style & SWT.BOTTOM) != 0) {
      y = clientArea.height - 1 - height;
    } else if ((style & SWT.CENTER) != 0) {
      y = (clientArea.height - height) / 2;
    } else {
      height = clientArea.height - 2;
    }
    y = Math.max(y, 1);
    height = Math.max(0, height);

    client.setBounds(x, y, width, height);
  }

  private void cachePreferedSizes(Control client, boolean flushCache) {
    if (flushCache || _cache == null) {
      _cache = client.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
    }
  }

  private Control client(Composite composite) {
    final RoundedComposite rcomposite = (RoundedComposite) composite;
    return rcomposite.getClient();
  }

}
