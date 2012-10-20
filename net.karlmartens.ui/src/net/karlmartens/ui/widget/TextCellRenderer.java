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

import net.karlmartens.ui.util.Colors;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.kupzog.ktable.KTableModel;

class TextCellRenderer extends de.kupzog.ktable.renderers.TextCellRenderer {
  
  private boolean _active;

  /**
   * Creates a cellrenderer that prints text in the cell.<p>
   * 
   * <p>
   * @param style 
   * Honored style bits are:<br>
   * - INDICATION_FOCUS makes the cell that has the focus have a different
   *   background color and a selection border.<br>
   * - INDICATION_FOCUS_ROW makes the cell show a selection indicator as it
   *   is often seen in row selection mode. A deep blue background and white content.<br>
   * - INDICATION_COMMENT lets the renderer paint a small triangle to the
   *   right top corner of the cell.<br>
   * - SWT.BOLD Makes the renderer draw bold text.<br>
   * - SWT.ITALIC Makes the renderer draw italic text<br>
   */
  public TextCellRenderer(int style) {
      super(style);        
  }
  
  public void setActive(boolean active) {
    _active = active;
  }
  /** 
   * A default implementation that paints cells in a way that is more or less
   * Excel-like. Only the cell with focus looks very different.
   * @see de.kupzog.ktable.KTableCellRenderer#drawCell(GC, Rectangle, int, int, Object, boolean, boolean, boolean, KTableModel)
   */
  public void drawCell(GC gc, Rectangle rect, int col, int row, Object content, 
          boolean focus, boolean fixed, boolean clicked, KTableModel model) {
      applyFont(gc);
      
      /*int topWidth = 1; int bottomWidth=1; int leftWidth=1; int rightWidth=1; 
       rect = drawSolidCellLines(gc, rect, vBorderColor, hBorderColor, 
       topWidth, bottomWidth, leftWidth, rightWidth);
       */
      
      // draw focus sign:
      if (focus && (m_Style & INDICATION_FOCUS)!=0) {
          // draw content:
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
          drawCellContent(gc, rect, content.toString(), null, getForeground(), Colors.blend(getBackground(), COLOR_BGFOCUS, 0.75));
          if (_active) 
            gc.drawFocus(rect.x, rect.y, rect.width, rect.height);
          
      } else if (focus && (m_Style & INDICATION_FOCUS_ROW)!=0) {
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_BGROWFOCUS, COLOR_BGROWFOCUS);
          // draw content:
          drawCellContent(gc, rect, content.toString(), null, COLOR_FGROWFOCUS, COLOR_BGROWFOCUS);
          
      } else {
          rect = drawDefaultSolidCellLine(gc, rect, COLOR_LINE_LIGHTGRAY, COLOR_LINE_LIGHTGRAY);
          // draw content:
          drawCellContent(gc, rect, content.toString(), null, getForeground(), getBackground());
      }
      
      if ((m_Style & INDICATION_COMMENT)!=0)
          drawCommentSign(gc, rect);
      
      resetFont(gc);
  }  

}
