package net.karlmartens.net;

import net.karlmartens.ui.widget.SparklineScrollBar;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class SparklineScrollBarTest {

	public static void main(String[] args) throws Exception {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		
		final SparklineScrollBar control = new SparklineScrollBar(shell);
		control.setSelection(0);
		control.setThumb(50);
		control.setMaximum(1000);
		control.setData(new double[] {1.0,5.0,5.0,6.0,7.0});
		
		shell.open();

		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
