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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTableModel;

/**
 * @author karl
 * 
 */
public class FixedCellRenderer extends de.kupzog.ktable.renderers.FixedCellRenderer {

  public static Color COLOR_INACTIVE_BGROWFOCUS = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
  public static Color COLOR_INACTIVE_FGROWFOCUS = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
  
  private boolean _active = true;

  /**
   * A constructor that lets the caller specify the style.
   * 
   * @param style
   *          The style that should be used to paint.
   *          <p>
   *          - Use SWT.FLAT for a flat look.<br>
   *          - Use SWT.PUSH for a button-like look. (default)
   *          <p>
   *          The following additional indications can be activated: <br>
   *          - INDICATION_FOCUS changes the background color if the fixed cell
   *          has focus.<br>
   *          - INDICATION_FOCUS_ROW changes the background color so that it
   *          machtes with normal cells in rowselection mode.<br>
   *          - INDICATION_SORT shows the sort direction when using a
   *          KTableSortedModel.<br>
   *          - INDICATION_CLICKED shows a click feedback, if STYLE_PUSH is
   *          specified. For text styles, the styles SWT.BOLD and SWT.ITALIC can
   *          be given.
   */
  public FixedCellRenderer(int style) {
    super(style);
  }
  
  public void setActive(boolean active) {
    _active  = active;
  }

  public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, boolean focus, boolean fixed, boolean clicked, KTableModel model) {
    applyFont(gc);
    
    // set up the colors:
    Color bgColor = getBackground();
    Color bottomBorderColor = COLOR_LINE_DARKGRAY;
    Color rightBorderColor = COLOR_LINE_DARKGRAY;
    Color fgColor = getForeground();
    if (focus && (m_Style & INDICATION_FOCUS) != 0) {
      bgColor = COLOR_FIXEDHIGHLIGHT;
      bottomBorderColor = COLOR_TEXT;
      rightBorderColor = COLOR_TEXT;
    }
    if (focus && (m_Style & INDICATION_FOCUS_ROW) != 0) {
      if (_active) {
        bgColor = COLOR_BGROWFOCUS;
        bottomBorderColor = COLOR_BGROWFOCUS;
        rightBorderColor = COLOR_BGROWFOCUS;
        fgColor = COLOR_FGROWFOCUS;
      } else {
        bgColor = COLOR_INACTIVE_BGROWFOCUS;
        bottomBorderColor = COLOR_INACTIVE_BGROWFOCUS;
        rightBorderColor = COLOR_INACTIVE_BGROWFOCUS;
        fgColor = COLOR_INACTIVE_FGROWFOCUS;        
      }
    }

    // STYLE_FLAT:
    if ((m_Style & STYLE_FLAT) != 0) {
      rect = drawDefaultSolidCellLine(gc, rect, bottomBorderColor, rightBorderColor);

      // draw content:
      drawCellContent(gc, rect, col, row, content, model, bgColor, fgColor);

    } else { // STYLE_PUSH
      drawCellButton(gc, rect, "", clicked && (m_Style & INDICATION_CLICKED) != 0);

      // push style border is drawn, exclude:
      rect.x += 2;
      rect.y += 2;
      rect.width -= 5;
      rect.height -= 5;

      // draw content:
      drawCellContent(gc, rect, col, row, content, model, bgColor, fgColor);
    }
    resetFont(gc);
  }

}