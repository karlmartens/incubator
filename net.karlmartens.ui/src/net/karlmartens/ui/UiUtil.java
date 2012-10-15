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
package net.karlmartens.ui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public final class UiUtil {

  /**
   * Determines if a control has focus or not. When the all flag is true will
   * check for a child component having focus.
   * 
   * @param control
   * @param all
   * @return
   */
  public static boolean hasFocus(Control control, boolean all) {
    if (control == null || control.isDisposed())
      return false;

    final Queue<Control> controls = new LinkedList<Control>();
    controls.add(control);
    
    final Control focusContol = control.getDisplay().getFocusControl();
    
    while (!controls.isEmpty()) {
      final Control candidate = controls.poll();
      if (candidate == focusContol)
        return true;

      if (all && (candidate instanceof Composite))
        controls.addAll(Arrays.asList(((Composite)candidate).getChildren()));
    }

    return false;
  }

  /**
   * Prevents the event from being responded to by other event listeners.
   * 
   * The listener should have been add to a {@link Display} using
   * {@link Display#addFilter(int, org.eclipse.swt.widgets.Listener)}; for this
   * to work as intended.
   * 
   * @param event
   */
  public static void consume(Event event) {
    event.type = SWT.None;
    event.doit = false;
  }

}
