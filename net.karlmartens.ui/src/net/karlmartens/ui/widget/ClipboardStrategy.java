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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public class ClipboardStrategy {

  public static final int OPERATION_NONE = 0;
  public static final int OPERATION_COPY = 1;
  public static final int OPERATION_CUT = 2;
  public static final int OPERATION_PASTE = 4;

  public boolean isClipboardEvent(Event e) {
    if ((e.stateMask & (SWT.MODIFIER_MASK ^ SWT.MOD1)) != 0 || ((e.stateMask & SWT.MOD1) == 0))
      return false;

    return 'c' == e.keyCode //
        || 'v' == e.keyCode //
        || 'x' == e.keyCode;
  }

  public int getOperation(Event e) {
    if (!isClipboardEvent(e))
      return OPERATION_NONE;

    switch (e.keyCode) {
      case 'c':
        return OPERATION_COPY;

      case 'v':
        return OPERATION_PASTE;

      case 'x':
        return OPERATION_CUT;
    }

    return OPERATION_NONE;
  }

}
