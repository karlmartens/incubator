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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTableModel;

class CheckableCellRenderer extends de.kupzog.ktable.renderers.CheckableCellRenderer {
  
  private boolean _active = true;

  /**
   * Creates a cellrenderer that shows boolean values with the given style.<p>
   * @param style 
   * Honored style bits are:<br>
   * - INDICATION_CLICKED<br>
   * - INDICATION_FOCUS<br>
   * - INDICATION_FOCUS_ROW<br>
   * - INDICATION_COMMENT<p>
   * Styles that influence the sign painted when cell value is true:<br>
   * - SIGN_IMAGE<br>
   * - SIGN_X<br>
   * - SIGN_CHECK (default)<br>
   */
  public CheckableCellRenderer(int style) {
      super(style);
  }

  public void setActive(boolean active) {
    _active  = active;
  }
  
  /** 
   * Paint a box with or without a checked symbol.
   * 
   * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int, int, Object, boolean, boolean, boolean, KTableModel)
   */
  public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, 
          boolean focus, boolean fixed, boolean clicked, KTableModel model) {
      
      // draw focus sign:
      if (focus && (m_Style & INDICATION_FOCUS)!=0) {
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
          drawCheckableImage(gc, rect, content, COLOR_BGFOCUS, clicked);
          if (_active)
            gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
          
      } else if (focus && (m_Style & INDICATION_FOCUS_ROW)!=0) {
      
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_BGROWFOCUS, COLOR_BGROWFOCUS);
          drawCheckableImage(gc, rect, content, COLOR_BGROWFOCUS, clicked);
          
      } else {
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
          drawCheckableImage(gc, rect, content, getBackground(), clicked);
      }
      
      if ((m_Style & INDICATION_COMMENT)!=0)
          drawCommentSign(gc, rect);
  }

}
