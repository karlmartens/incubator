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
		control.setMaximum(18262);
		control.setThumb(365);
		
		final double[] data = new double[18263];
		for (int i=0; i<data.length; i++) {
			data[i] = Math.random();
		}
		control.setData(data);
		
		shell.open();

		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
