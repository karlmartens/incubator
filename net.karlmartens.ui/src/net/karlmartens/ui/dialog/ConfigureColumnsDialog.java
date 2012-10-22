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
package net.karlmartens.ui.dialog;

import java.util.Arrays;

import net.karlmartens.ui.Activator;
import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;
import net.karlmartens.ui.widget.GridChooserItem;
import net.karlmartens.ui.widget.Table;
import net.karlmartens.ui.widget.TableColumn;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author karl
 * 
 */
public final class ConfigureColumnsDialog extends TitleAreaDialog {

  private static final String DIALOG_SETTINGS_SECTION = "ConfigureColumnsDialogSettings";

  private final Table _table;
  private final String _title;
  private final String _headerTitle;
  private final String _message;

  private GridChooser _columns;
  private int[] _originalIndices;

  public ConfigureColumnsDialog(Shell parentShell, Table table, String title,
      String headerTitle, String message) {
    super(parentShell);
    _table = table;
    _title = title;
    _headerTitle = headerTitle;
    _message = message;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(_title);
  }

  protected Control createDialogArea(Composite parent) {
    final Composite parentComposite = (Composite) super
        .createDialogArea(parent);

    final Composite contents = new Composite(parentComposite, SWT.NONE);
    contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    setTitle(_headerTitle);
    setMessage(_message);

    _columns = new GridChooser(contents);
    _columns.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
        .create());

    final GridChooserColumn column = new GridChooserColumn(_columns, SWT.NONE);
    column.setWidth(100);

    final int[] originalIndices = new int[_table.getColumnCount()];
    int index = 0;
    for (int i = 0; i < originalIndices.length; i++) {
      final TableColumn c = _table.getColumn(i);
      if (!c.isMoveable() || !c.isHideable())
        continue;

      originalIndices[index++] = i;

      final GridChooserItem item = new GridChooserItem(_columns, SWT.NONE);
      item.setText(c.getText());
      item.setSelected(c.isVisible());
      item.setData(c);
    }

    _originalIndices = Arrays.copyOf(originalIndices, index);

    Dialog.applyDialogFont(parentComposite);

    final Point defaultMargins = LayoutConstants.getMargins();

    GridLayoutFactory//
        .fillDefaults()//
        .numColumns(1)//
        .margins(defaultMargins.x, defaultMargins.y)//
        .generateLayout(contents);

    return contents;
  }

  @Override
  protected void okPressed() {
    for (int i = 0; i < _columns.getItemCount(); i++) {
      final GridChooserItem item = _columns.getItem(i);
      final TableColumn column = (TableColumn) item.getData();
      column.setVisible(item.isSelected());

      if (!column.isVisible())
        continue;

      final int fromIndex = _table.indexOf(column);
      final int toIndex = _originalIndices[item.getSelectionOrder()];
      _table.moveColumn(fromIndex, toIndex);
    }

    super.okPressed();
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings() {
    final Activator activator = Activator.getDefault();
    if (activator == null)
      return null;

    final IDialogSettings settings = activator.getDialogSettings();
    IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION);
    if (section == null)
      section = settings.addNewSection(DIALOG_SETTINGS_SECTION);

    return section;
  }

  protected boolean isResizable() {
    return true;
  }

}
