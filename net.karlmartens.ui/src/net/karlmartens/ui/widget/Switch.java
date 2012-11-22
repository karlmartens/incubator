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

import static net.karlmartens.platform.util.ArraySupport.max;
import static net.karlmartens.ui.widget.SchemeConstants.FLAT;
import static net.karlmartens.ui.widget.SchemeConstants.FLAT_INACTIVE;
import static org.eclipse.draw2d.PositionConstants.EAST;
import static org.eclipse.draw2d.PositionConstants.LEFT;
import static org.eclipse.draw2d.PositionConstants.RIGHT;
import static org.eclipse.draw2d.PositionConstants.WEST;
import static org.eclipse.draw2d.SchemeBorder.SCHEMES.LOWERED;
import static org.eclipse.swt.SWT.COLOR_LIST_SELECTION;
import static org.eclipse.swt.SWT.COLOR_TITLE_INACTIVE_BACKGROUND;
import static org.eclipse.swt.SWT.COLOR_TITLE_INACTIVE_FOREGROUND;
import static org.eclipse.swt.SWT.COLOR_WHITE;
import static org.eclipse.swt.SWT.COLOR_WIDGET_BACKGROUND;
import net.karlmartens.ui.util.Colors;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.SchemeBorder.Scheme;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

/**
 * @author karl
 * 
 */
public class Switch extends Composite {

  private static final int MARGIN = 2;
  private static final int BORDER = 2;

  private LayeredPane _container;
  private Panel _track;
  private Label _leftLabel;
  private RoundedRectangle _left;
  private RectangleFigure _center;
  private Label _rightLabel;
  private RoundedRectangle _right;
  private RoundedRectangle _button;

  private Color _activeBgColor;
  private Color _activeAltBgColor;
  private Color _inactiveBgColor;
  private Color _inactiveAltBgColor;
  private Color _activeFgColor;
  private Color _activeAltFgColor;
  private Color _inactiveFgColor;
  private Color _inactiveAltFgColor;
  private Color _buttonBgColor;
  private Color _buttonAltBgColor;

  private boolean _selected = true;

  public Switch(Composite parent) {
    super(parent, SWT.NONE);
    setBackground(parent.getBackground());
    setLayout(new FillLayout());

    final FigureCanvas canvas = new FigureCanvas(this);
    canvas.setBackground(parent.getBackground());
    canvas.setScrollBarVisibility(FigureCanvas.NEVER);
    canvas.setContents(createContents());
    canvas.getViewport().setContentsTracksHeight(true);
    canvas.getViewport().setContentsTracksWidth(true);

    final Display display = getDisplay();
    _activeBgColor = display.getSystemColor(COLOR_LIST_SELECTION);
    _activeFgColor = display.getSystemColor(COLOR_WHITE);
    _inactiveBgColor = display.getSystemColor(COLOR_TITLE_INACTIVE_BACKGROUND);
    _inactiveFgColor = display.getSystemColor(COLOR_TITLE_INACTIVE_FOREGROUND);
    _buttonBgColor = display.getSystemColor(COLOR_WIDGET_BACKGROUND);

    _activeAltBgColor = gray(_activeBgColor);
    _activeAltFgColor = gray(_activeFgColor);
    _inactiveAltBgColor = gray(_inactiveBgColor);
    _inactiveAltFgColor = gray(_inactiveFgColor);
    _buttonAltBgColor = gray(_buttonBgColor);

    doUpdate();

    hook();
  }

  @Override
  public org.eclipse.swt.graphics.Point computeSize(int wHint, int hHint,
      boolean changed) {

    final GC gc = new GC(this);
    gc.setFont(getFont());

    final Dimension min = new Dimension(_right.getCornerDimensions());
    final Dimension activeText = new Dimension(gc.stringExtent(_leftLabel
        .getText()));
    final Dimension inactiveText = new Dimension(gc.stringExtent(_rightLabel
        .getText()));

    gc.dispose();

    int width = 2 * (max(activeText.width, inactiveText.width, min.height) + 2
        * MARGIN + BORDER);
    if (wHint != SWT.DEFAULT)
      width = wHint;

    int height = max(activeText.height, inactiveText.height, min.height) + 2
        * (MARGIN + BORDER);
    if (hHint != SWT.DEFAULT)
      height = hHint;

    return new org.eclipse.swt.graphics.Point(width, height);
  }

  @Override
  public void setFont(Font font) {
    checkWidget();
    super.setFont(font);
    _leftLabel.setFont(font);
    _rightLabel.setFont(font);
  }

  public void setActiveText(String text) {
    checkWidget();
    if (text == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _leftLabel.setText(text);
  }

  public void setActiveBackground(Color color) {
    checkWidget();
    if (color == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _activeBgColor = color;
    _activeAltBgColor.dispose();
    _activeAltBgColor = gray(color);
    doUpdate();
  }

  public void setActiveForeground(Color color) {
    checkWidget();
    if (color == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _activeFgColor = color;
    _activeAltFgColor.dispose();
    _activeAltFgColor = gray(color);
    doUpdate();
  }

  public void setInactiveText(String text) {
    checkWidget();
    if (text == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _rightLabel.setText(text);
  }

  public void setInactiveBackground(Color color) {
    checkWidget();
    if (color == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _inactiveBgColor = color;
    _inactiveAltBgColor.dispose();
    _inactiveAltBgColor = gray(color);
    doUpdate();
  }

  public void setInactiveForeground(Color color) {
    checkWidget();
    if (color == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _inactiveFgColor = color;
    _inactiveAltFgColor.dispose();
    _inactiveAltFgColor = gray(color);
    doUpdate();
  }

  public void setButtonColor(Color color) {
    checkWidget();
    if (color == null)
      SWT.error(SWT.ERROR_NULL_ARGUMENT);

    _buttonBgColor = color;
    _buttonAltBgColor.dispose();
    _buttonAltBgColor = gray(color);
    doUpdate();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    doUpdate();
  }

  public boolean getSelection() {
    checkWidget();
    return _selected;
  }

  public void setSelection(boolean selection) {
    checkWidget();
    if (_selected == selection)
      return;

    _selected = selection;
    doUpdate();
    notifyListeners(SWT.Selection, new Event());
  }

  public void addSelectionListener(SelectionListener listener) {
    final TypedListener typedListener = new TypedListener(listener);
    addListener(SWT.Selection, typedListener);
    addListener(SWT.DefaultSelection, typedListener);
  }

  public void removeSelectionListener(SelectionListener listener) {
    final TypedListener typedListener = new TypedListener(listener);
    removeListener(SWT.Selection, typedListener);
    removeListener(SWT.DefaultSelection, typedListener);
  }

  private IFigure createContents() {
    final Dimension corner = new Dimension(16, 16);
    final Font font = getFont();

    _leftLabel = new Label();
    _leftLabel.setLabelAlignment(LEFT);
    _leftLabel.setTextPlacement(WEST);
    _leftLabel.setFont(font);

    _rightLabel = new Label();
    _rightLabel.setLabelAlignment(RIGHT);
    _rightLabel.setTextPlacement(EAST);
    _rightLabel.setFont(font);

    _left = new RoundedRectangle();
    _left.setCornerDimensions(corner);
    _left.setFill(true);

    _center = new RectangleFigure();
    _center.setFill(true);

    _right = new RoundedRectangle();
    _right.setCornerDimensions(corner);
    _right.setFill(true);

    _button = new RoundedRectangle();
    _button.setCornerDimensions(corner);
    _button.setFill(true);

    _container = new LayeredPane();
    _container.setLayoutManager(new XYLayout());

    _track = new Panel();
    _track.setLayoutManager(new XYLayout());
    _track.setBorder(new RoundedBorder(LOWERED, corner));

    _track.add(_center);
    _track.add(_left);
    _track.add(_leftLabel);
    _track.add(_right);
    _track.add(_rightLabel);

    _container.add(_track);
    _container.add(_button);

    return _container;
  }

  private void hook() {
    addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        resize();
      }
    });

    addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        _activeAltBgColor.dispose();
        _activeAltFgColor.dispose();
        _inactiveAltBgColor.dispose();
        _inactiveAltFgColor.dispose();
        _buttonAltBgColor.dispose();
      }
    });

    final MouseListener listener = new MouseListener() {
      @Override
      public void mousePressed(MouseEvent me) {
        // nothing to do
      }

      @Override
      public void mouseReleased(MouseEvent me) {
        setSelection(!getSelection());
      }

      @Override
      public void mouseDoubleClicked(MouseEvent me) {
        // nothing to do
      }
    };

    _leftLabel.addMouseListener(listener);
    _rightLabel.addMouseListener(listener);
    _left.addMouseListener(listener);
    _center.addMouseListener(listener);
    _right.addMouseListener(listener);
    _button.addMouseListener(listener);
  }

  private void resize() {
    final org.eclipse.swt.graphics.Rectangle rect = getClientArea();

    final int leftWidth = rect.width / 2;
    final int rightWidth = rect.width - leftWidth;
    _left.setSize(leftWidth, rect.height);
    _right.setSize(rightWidth, rect.height);
    _right.setLocation(new Point(leftWidth, 0));

    final int centerWidth = _left.getCornerDimensions().width;
    final int centerOffset = centerWidth / 2;
    _center.setSize(centerWidth, rect.height);
    _center.setLocation(new Point(leftWidth - centerOffset, 0));

    final int buttonWidth = max(leftWidth, _right.getSize().width);
    _button.setSize(buttonWidth, rect.height);

    _container.setSize(rect.width, rect.height);
    _track.setSize(rect.width, rect.height);

    final GC gc = new GC(Switch.this);
    gc.setFont(getFont());
    final Dimension activeText = new Dimension(gc.stringExtent(_leftLabel
        .getText()));
    final Dimension inactiveText = new Dimension(gc.stringExtent(_rightLabel
        .getText()));
    gc.dispose();

    final int activeX = BORDER + (leftWidth - activeText.width) / 2;
    final int activeY = (rect.height - activeText.height) / 2;
    _leftLabel.setSize(activeText);
    _leftLabel.setLocation(new Point(activeX, activeY));

    final int inactiveX = leftWidth - BORDER
        + (rightWidth - inactiveText.width) / 2;
    final int inactiveY = (rect.height - inactiveText.height) / 2;
    _rightLabel.setSize(inactiveText);
    _rightLabel.setLocation(new Point(inactiveX, inactiveY));

    doUpdate();
  }

  private Color gray(Color color) {
    final RGB rgb = Colors.blend(color.getRGB(), new RGB(240, 240, 240), 0.80);
    return new Color(getDisplay(), rgb);
  }

  private void doUpdate() {
    final Color activeBgColor;
    final Color activeFgColor;
    final Color inactiveBgColor;
    final Color inactiveFgColor;
    final Color buttonBgColor;
    final Scheme buttonBorder;
    if (isEnabled()) {
      activeBgColor = _activeBgColor;
      activeFgColor = _activeFgColor;
      inactiveBgColor = _inactiveBgColor;
      inactiveFgColor = _inactiveFgColor;
      buttonBgColor = _buttonBgColor;
      buttonBorder = FLAT;
    } else {
      activeBgColor = _activeAltBgColor;
      activeFgColor = _activeAltFgColor;
      inactiveBgColor = _inactiveAltBgColor;
      inactiveFgColor = _inactiveAltFgColor;
      buttonBgColor = _buttonAltBgColor;
      buttonBorder = FLAT_INACTIVE;
    }

    _left.setBackgroundColor(activeBgColor);
    _left.setForegroundColor(activeBgColor);
    _leftLabel.setForegroundColor(activeFgColor);
    _right.setBackgroundColor(inactiveBgColor);
    _right.setForegroundColor(inactiveBgColor);
    _rightLabel.setForegroundColor(inactiveFgColor);
    _button.setBackgroundColor(buttonBgColor);
    _button.setBorder(new RoundedBorder(buttonBorder, _right
        .getCornerDimensions()));

    final Point pt;
    final Color color;
    if (_selected) {
      pt = new Point(_right.getLocation());
      color = activeBgColor;
    } else {
      pt = new Point(_left.getLocation());
      color = inactiveBgColor;
    }

    _button.setLocation(pt);
    _center.setBackgroundColor(color);
    _center.setForegroundColor(color);
  }
}
