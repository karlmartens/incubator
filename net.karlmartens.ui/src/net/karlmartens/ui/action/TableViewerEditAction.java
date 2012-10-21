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
package net.karlmartens.ui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.karlmartens.ui.viewer.TableViewerClipboardManager;

import org.eclipse.jface.action.Action;

/**
 * @author karl
 * 
 */
abstract class TableViewerEditAction extends Action {

  private final PropertyChangeListener _listener;
  private TableViewerClipboardManager _manager;

  protected TableViewerEditAction() {
    _listener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        updateEnablement();
      }
    };

    updateEnablement();
  }

  public final void register(TableViewerClipboardManager manager) {
    release();
    _manager = manager;
    hook();
    updateEnablement();
  }

  protected final TableViewerClipboardManager manager() {
    return _manager;
  }

  private void hook() {
    if (_manager == null)
      return;

    _manager.addPropertyChangeListener(_listener);
  }

  private void release() {
    if (_manager == null)
      return;

    _manager.removePropertyChangeListener(_listener);

    _manager = null;
  }

  private void updateEnablement() {
    if (_manager == null) {
      setEnabled(false);
      return;
    }

    setEnabled(doIsEnabled());
  }

  protected abstract boolean doIsEnabled();
}
