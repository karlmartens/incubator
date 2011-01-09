package net.karlmartens.net;

import net.karlmartens.ui.widget.SparklineScrollBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class SparklineScrollBarTest {

	public static void main(String[] args) throws Exception {
		new SparklineScrollBarTest().run();
	}

	private final Display _display;
	private final Shell _shell;
	private final SparklineScrollBar _control;
	
	public SparklineScrollBarTest() {
		_display = Display.getDefault();
		_shell = new Shell(_display);
		_shell.setLayout(new FillLayout());
		
		_control = new SparklineScrollBar(_shell);
		_control.setBackground(_display.getSystemColor(SWT.COLOR_WHITE));
		_control.setForeground(_display.getSystemColor(SWT.COLOR_BLACK));
		_control.setThumbColor(_display.getSystemColor(SWT.COLOR_YELLOW));
		_control.setSparklineColor(_display.getSystemColor(SWT.COLOR_GRAY));
		_control.setLabelColor(_display.getSystemColor(SWT.COLOR_BLACK));
		_control.setLabelFont(new Font(_display, "Arial", 10, SWT.BOLD));
		_control.setMaximum(120);
		_control.setThumb(12);
		_control.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLabel();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Do nothing
			}
		});
		
		final double[] data = new double[_control.getMaximum()+1];
		for (int i=0; i<data.length; i++) {
			data[i] = Math.random();
		}
		_control.setDataPoints(data);		
		_control.setSelection(0);
		_control.setHighlight(8);
		_control.highlight(20, 39);
		_control.highlight(new int[] {100, 101, 103});
		_control.setHighlightColor(_display.getSystemColor(SWT.COLOR_RED));
		updateLabel();
	}
	
	private void run() {
		_shell.open();
		_shell.layout();
		while (!_shell.isDisposed()) {
			if (!_display.readAndDispatch())
				_display.sleep();
		}
	}
	
	private void updateLabel() {
		final int idx = _control.getSelection();
		final String message = new StringBuilder() //
			.append(idx) //
			.append(":") //
			.append(_control.getDataPoints()[idx]) //
			.toString();
		_control.setLabel(message);
	}

}
