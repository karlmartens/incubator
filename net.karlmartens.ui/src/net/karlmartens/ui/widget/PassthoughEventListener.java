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
package net.karlmartens.ui.widget;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

final class PassthoughEventListener {

  private final Control _target;
  private final Collection<Control> _sources = new ArrayList<Control>();
  private final ListenerImpl _listener = new ListenerImpl();

  PassthoughEventListener(Control target) {
    _target = target;
    _target.addDisposeListener(_listener);
  }

  void addSource(Control source) {
    _sources.add(source);
    hookControl(source);
  }

  void removeSource(Control source) {
    _sources.remove(source);
    releaseControl(source);
  }

  private void hookControl(Control control) {
    control.addMouseListener(_listener);
    control.addMouseMoveListener(_listener);
    control.addKeyListener(_listener);
    control.addDisposeListener(_listener);
  }

  private void releaseControl(Control control) {
    control.removeMouseListener(_listener);
    control.removeMouseMoveListener(_listener);
    control.removeKeyListener(_listener);
    control.removeDisposeListener(_listener);
  }

  private class ListenerImpl implements MouseListener, MouseMoveListener, KeyListener, DisposeListener {

    @Override
    public void widgetDisposed(DisposeEvent e) {
      if (e.getSource() == _target) {
        for (Control source : _sources) {
          releaseControl(source);
        }
        _sources.clear();
      } else {
        removeSource((Control) e.getSource());
      }
    }

    @Override
    public void keyPressed(KeyEvent e) {
      _target.notifyListeners(SWT.KeyDown, convertEvent(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
      _target.notifyListeners(SWT.KeyUp, convertEvent(e));
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      _target.notifyListeners(SWT.MouseDoubleClick, convertEvent(e));
    }

    @Override
    public void mouseDown(MouseEvent e) {
      _target.notifyListeners(SWT.MouseDown, convertEvent(e));
    }

    @Override
    public void mouseUp(MouseEvent e) {
      _target.notifyListeners(SWT.MouseUp, convertEvent(e));
    }

    @Override
    public void mouseMove(MouseEvent e) {
      _target.notifyListeners(SWT.MouseMove, convertEvent(e));
    }

    private Event convertEvent(MouseEvent e) {
      final Event event = new Event();
      event.button = e.button;
      event.count = e.count;
      event.data = e.data;
      event.stateMask = e.stateMask;
      event.time = e.time;
      final Rectangle r = ((Control) e.getSource()).getBounds();
      event.x = e.x + r.x;
      event.y = e.y + r.y;
      return event;
    }

    private Event convertEvent(KeyEvent e) {
      final Event event = new Event();
      event.character = e.character;
      event.data = e.data;
      event.keyCode = e.keyCode;
      event.stateMask = e.stateMask;
      event.time = e.time;
      return event;
    }
  }
}
