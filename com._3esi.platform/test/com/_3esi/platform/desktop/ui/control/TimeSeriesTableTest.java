package com._3esi.platform.desktop.ui.control;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jidesoft.utils.Lm;

public final class TimeSeriesTableTest {

	public static void main(String[] args) {
		Lm.verifyLicense("3esi", "esi.manage", //$NON-NLS-1$//$NON-NLS-2$
	            ".kk4DMgzlNhCN9eJ.H.:oZQYOhx0At23");
		
		final Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		
		final TimeSeriesTable table = new TimeSeriesTable(shell);
		//table.setHeaderVisible(true);
		
		final TimeSeriesColumn[] columns = new TimeSeriesColumn[] { //
				new TimeSeriesColumn(table), //
				new TimeSeriesColumn(table) //
		};
		
		columns[0].setText("Column 1");
		//columns[0].setWidth(100);
		
		columns[1].setText("Column 2");
		//columns[1].setWidth(100);
		
		//final TimeSeriesItem[] items = new TimesSeriesItem[] { //
		//	new TimeSeriesItem("a", "b"), //
		//	new TimeSeriesItem("b", "c") //
		//};

		shell.open();
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
