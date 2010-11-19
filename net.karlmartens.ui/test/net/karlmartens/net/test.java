package net.karlmartens.net;

import net.karlmartens.ui.widget.GridChooser;
import net.karlmartens.ui.widget.GridChooserColumn;
import net.karlmartens.ui.widget.GridChooserItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public final class test {

	public static void main(String[] args) throws Exception {
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		
		final GridChooser chooser = new GridChooser(shell);
		chooser.setHeaderVisible(true);
		
		GridChooserColumn gc1 = new GridChooserColumn(chooser, SWT.NONE);
		gc1.setText("Test");
		gc1.setWidth(100);
		
		GridChooserColumn gc2 = new GridChooserColumn(chooser, SWT.NONE);
		gc2.setText("Test2");
		gc2.setWidth(100);
		
		GridChooserItem gcItem1 = new GridChooserItem(chooser, SWT.NONE);
		gcItem1.setText(new String[] {"1", "2", "3"});
		
		
		Table t = new Table(shell, SWT.BORDER);
		t.setHeaderVisible(true);
		
		TableColumn c = new TableColumn(t, SWT.NONE);
		c.setText("Test");
		c.setWidth(100);

		TableColumn c2 = new TableColumn(t, SWT.NONE);
		c2.setText("Test2");
		c2.setWidth(100);
		
		TableItem item = new TableItem(t, SWT.NONE);
		item.setText(new String[] {"1", "2", "3"});
		
		shell.open();
		
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
