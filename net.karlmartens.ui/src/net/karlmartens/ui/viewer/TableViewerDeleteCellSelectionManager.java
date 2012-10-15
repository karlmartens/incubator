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
package net.karlmartens.ui.viewer;

import java.util.Arrays;

import net.karlmartens.ui.UiUtil;
import net.karlmartens.ui.widget.Table;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;

public final class TableViewerDeleteCellSelectionManager extends
    CellSelectionModifier {

  private final TableViewer _viewer;

  public TableViewerDeleteCellSelectionManager(IWorkbenchPartSite site,
      TableViewer viewer) {
    super(viewer);
    _viewer = viewer;

    final IHandlerService service = (IHandlerService) site
        .getService(IHandlerService.class);
    activateHandler(service);
  }

  public TableViewerDeleteCellSelectionManager(TableViewer viewer) {
    super(viewer);
    _viewer = viewer;
    hookControl();
  }

  private boolean delete() {
    final Point[] selection = _viewer.doGetCellSelections();
    if (selection == null || selection.length == 0)
      return false;

    if (!isEditable(selection))
      return false;

    final String[] values = new String[selection.length];
    Arrays.fill(values, "");
    setValues(selection, values);
    _viewer.refresh(true);
    return true;
  }

  private void activateHandler(final IHandlerService service) {

    final IHandlerActivation activation = service.activateHandler(
        IWorkbenchCommandConstants.EDIT_DELETE, new AbstractHandler() {
          @Override
          public Object execute(ExecutionEvent event) throws ExecutionException {
            delete();
            return null;
          }

          @Override
          public void setEnabled(Object evaluationContext) {
            setBaseEnabled(UiUtil.hasFocus(_viewer.getControl(), true));
          }
        });

    final Table table = _viewer.getControl();
    new Listener() {
      {
        table.addListener(SWT.Dispose, this);
        table.addListener(SWT.FocusIn, this);
        table.addListener(SWT.FocusOut, this);
      }

      @Override
      public void handleEvent(Event event) {
        if (event.type == SWT.Dispose) {
          handleDispose(event);
        } else if (event.type == SWT.FocusIn || event.type == SWT.FocusOut) {
          ((IHandler2) activation.getHandler()).setEnabled(null);
        }
      }

      private void handleDispose(Event event) {
        table.removeListener(SWT.Dispose, this);
        table.removeListener(SWT.FocusIn, this);
        table.removeListener(SWT.FocusOut, this);
        service.deactivateHandler(activation);
      }
    };
  }

  private void hookControl() {
    final Table table = _viewer.getControl();
    final Display display = table.getDisplay();

    new Listener() {
      {
        display.addFilter(SWT.KeyDown, this);
        table.addListener(SWT.Dispose, this);
      }

      @Override
      public void handleEvent(Event event) {
        if (event.type == SWT.KeyDown)
          handleKeyPressed(event);
        else if (event.type == SWT.Dispose)
          handleDispose(event);
      }

      private void handleDispose(Event event) {
        display.removeFilter(SWT.KeyDown, this);
        table.removeListener(SWT.Dispose, this);
      }

      private void handleKeyPressed(Event event) {
        if ((event.stateMask & SWT.MODIFIER_MASK) != 0)
          return;
        
        if (!hasFocus())
          return;

        switch (event.keyCode) {
          case SWT.DEL:
          case SWT.BS:
            if (delete())
              UiUtil.consume(event);
        }
      }

      private boolean hasFocus() {
        Control focus = display.getFocusControl();
        while (focus != null) {
          if (focus == table)
            return true;
          
          focus = focus.getParent();
        }

        return false;
      }
    };
  }
}
