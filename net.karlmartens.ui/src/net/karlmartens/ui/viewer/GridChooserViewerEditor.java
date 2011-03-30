/**
 *   Copyright 2010,2011 Karl Martens
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

import net.karlmartens.ui.widget.GridChooserEditor;
import net.karlmartens.ui.widget.GridChooserItem;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public final class GridChooserViewerEditor extends ColumnViewerEditor {

  private final GridChooserViewer _viewer;
  private final GridChooserEditor _editor;

  public GridChooserViewerEditor(GridChooserViewer viewer, ColumnViewerEditorActivationStrategy activationStrategy, int feature) {
    super(viewer, activationStrategy, feature);
    _viewer = viewer;
    _editor = new GridChooserEditor(viewer.getControl());
    addEditorActivationListener(_listener);
  }

  @Override
  protected void setEditor(Control w, Item item, int columnIndex) {
    final GridChooserItem chooserItem = (GridChooserItem) item;
    _editor.setEditor(w, chooserItem, columnIndex);
  }

  @Override
  protected void setLayoutData(LayoutData layoutData) {
    _editor.grabHorizontal = layoutData.grabHorizontal;
    _editor.horizontalAlignment = layoutData.horizontalAlignment;
    _editor.minimumWidth = layoutData.minimumWidth;
    _editor.verticalAlignment = layoutData.verticalAlignment;

    if (layoutData.minimumHeight != SWT.DEFAULT) {
      _editor.minimumHeight = layoutData.minimumHeight;
    }
  }

  @Override
  protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {

    // Nothing to do
  }

  public static void create(GridChooserViewer viewer, ColumnViewerEditorActivationStrategy activationStrategy, int feature) {
    final GridChooserViewerEditor editor = new GridChooserViewerEditor(viewer, activationStrategy, feature);
    viewer.setColumnViewerEditor(editor);
  }

  private final ColumnViewerEditorActivationListener _listener = new ColumnViewerEditorActivationListener() {
    @Override
    public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
      // nothing to do
    }

    @Override
    public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
      // nothing to do
    }

    @Override
    public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
      // Nothing to do
    }

    @Override
    public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
      _viewer.refresh();
    }
  };
}
