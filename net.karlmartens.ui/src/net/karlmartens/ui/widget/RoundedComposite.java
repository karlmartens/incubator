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

import static net.karlmartens.ui.widget.RoundedCompositeLayout.DIAMETER;
import static net.karlmartens.ui.widget.RoundedCompositeLayout.RADIUS;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author karl
 * 
 */
public final class RoundedComposite extends Composite {

  private Control _client;
  private final int _style;

  public RoundedComposite(Composite parent, int style) {
    super(parent, SWT.TRANSPARENT | SWT.NO_BACKGROUND);
    _style = style;
    setLayout(new RoundedCompositeLayout());

    addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        RoundedComposite.this.paintControl(e);
      }
    });
  }

  @Override
  public Control[] getChildren() {
    return new Control[0];
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.widgets.Widget#getStyle()
   */
  @Override
  public int getStyle() {
    return _style;
  }

  public void setClient(Control client) {
    checkWidget();
    if (client == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    if (client.getParent() != this)
      SWT.error(SWT.ERROR_INVALID_ARGUMENT);

    _client = client;
    layout(true);
  }

  @Override
  public boolean setFocus() {
    return _client.setFocus();
  }

  private void paintControl(PaintEvent e) {
    final GC gc = e.gc;
    gc.fillArc(0, 0, DIAMETER, e.height, 90, 180);
    gc.fillRectangle(RADIUS, 0, e.width - DIAMETER, e.height);
    gc.fillArc(e.width - DIAMETER, 0, DIAMETER, e.height, 270, 180);

    final int style = _style;
    if ((style & SWT.BORDER) != 0) {
      int tl = SWT.COLOR_WIDGET_BORDER;
      int br = SWT.COLOR_WIDGET_BORDER;

      if ((style & SWT.SHADOW_OUT) != 0) {
        tl = SWT.COLOR_WIDGET_LIGHT_SHADOW;
        br = SWT.COLOR_WIDGET_NORMAL_SHADOW;
      } else if ((style & SWT.SHADOW_IN) != 0) {
        tl = SWT.COLOR_WIDGET_NORMAL_SHADOW;
        br = SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW;
      }

      final Color fg = gc.getForeground();

      gc.setForeground(gc.getDevice().getSystemColor(tl));
      gc.drawArc(0, 0, DIAMETER, e.height, 90, 135);
      gc.drawLine(RADIUS, 0, e.width - RADIUS, 0);
      gc.drawArc(e.width - DIAMETER, 0, DIAMETER, e.height, 45, 45);

      gc.setForeground(gc.getDevice().getSystemColor(br));
      gc.drawArc(0, 0, DIAMETER, e.height, 225, 45);
      gc.drawLine(RADIUS, e.height - 1, e.width - RADIUS, e.height - 1);
      gc.drawArc(e.width - DIAMETER - 1, 0, DIAMETER, e.height, 270, 135);

      gc.setForeground(fg);
    }
  }

  public Control getClient() {
    return _client;
  }

}
