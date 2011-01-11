package net.karlmartens.ui.widget;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JTable;

import net.karlmartens.platform.util.UiThreadUtil;
import net.karlmartens.platform.util.UiThreadUtil.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;

import com.jidesoft.grid.TableSelectionEvent;
import com.jidesoft.grid.TableSelectionListener;

class TimeSeriesTableEventAdapter implements TableSelectionListener, KeyListener, MouseListener, DisposeListener {
	private static final long serialVersionUID = 1L;

	private static final int[][] MASK_MAP = {
		{KeyEvent.ALT_DOWN_MASK, SWT.ALT}, //
		{KeyEvent.CTRL_DOWN_MASK, SWT.CTRL}, //
		{KeyEvent.META_DOWN_MASK, SWT.COMMAND}, //
		{KeyEvent.SHIFT_DOWN_MASK, SWT.SHIFT}, //
		{KeyEvent.BUTTON1_DOWN_MASK, SWT.BUTTON1}, //
		{KeyEvent.BUTTON2_DOWN_MASK, SWT.BUTTON2}, //
		{KeyEvent.BUTTON3_DOWN_MASK, SWT.BUTTON3}, //
	};
	
	
	private final TimeSeriesTable _widget;
	private final TableScrollPane _control;
	private int[] _selection = new int[]{};

	
	TimeSeriesTableEventAdapter(TimeSeriesTable widget, TableScrollPane control) {
		_widget = widget;
		_control = control;
		_control.addTableSelectionListener(this);
		_control.addKeyListener(this);
		_control.addMouseListener(this);
		_widget.addDisposeListener(this);
	}
	
	@Override
	public void widgetDisposed(DisposeEvent e) {
		_widget.removeDisposeListener(this);
		_control.removeTableSelectionListener(this);
		_control.removeKeyListener(this);
		_control.removeMouseListener(this);
	}
	
	public int[] getSelectionIndices() {
		final int[] selection = new int[_selection.length];
		System.arraycopy(_selection, 0, selection, 0, selection.length);
		return selection;
	}
	
	@Override
	public void valueChanged(TableSelectionEvent event) {
		final int[] candidate =  _control.getSelectionIndices();
		if (!Arrays.equals(_selection, candidate) && !event.getValueIsAdjusting()) {
			_selection = candidate;
			fire(SWT.Selection, new Event());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		final Event event = convert(e);			
		fire(SWT.KeyDown, event);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		final Event event = convert(e);			
		fire(SWT.KeyUp, event);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// ignored
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (2 == e.getClickCount()) {
			final Event event = convert(e);
			fire(SWT.MouseDoubleClick, event);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// ignored
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// ignored
	}

	@Override
	public void mousePressed(MouseEvent e) {
		final Event event = convert(e);
		fire(SWT.MouseDown, event);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		final Event event = convert(e);
		fire(SWT.MouseUp, event);
	}
	
	private Event convert(MouseEvent e) {
		final Event event = new Event();
		switch(e.getButton()) {
			case MouseEvent.BUTTON1:
				event.button = 1;
				break;
				
			case MouseEvent.BUTTON2:
				event.button = 2;
				break;
				
			case MouseEvent.BUTTON3:
				event.button = 3;
				break;
		}
		
		event.x = ((JTable)e.getSource()).getLocation().x + e.getX();
		event.y = ((JTable)e.getSource()).getLocation().y + e.getY();
		
		convert((InputEvent)e, event);
		return event;
	}
	
	
	private Event convert(KeyEvent e) {
		final Event event = new Event();
		event.keyCode = e.getKeyCode();
		event.character = e.getKeyChar();
		
		switch(e.getKeyLocation()) {
			case KeyEvent.KEY_LOCATION_LEFT:
				event.keyLocation = SWT.LEFT;
				break;
			case KeyEvent.KEY_LOCATION_RIGHT:
				event.keyLocation = SWT.RIGHT;
				break;
		
			case KeyEvent.KEY_LOCATION_NUMPAD:
				event.keyLocation = SWT.KEYPAD;
				break;
			
			case KeyEvent.KEY_LOCATION_UNKNOWN:
			case KeyEvent.KEY_LOCATION_STANDARD:
			default:
				event.keyLocation = SWT.NONE;
		}

		convert((InputEvent)e, event);
		return event;
	}
	
	private void convert(InputEvent e, Event appendTo) {
		appendTo.stateMask = 0;
		for (int i=0; i<MASK_MAP.length; i++) {
			if ((e.getModifiersEx() & MASK_MAP[i][0]) != 0);
			appendTo.stateMask |= MASK_MAP[i][1];
		}
		
	}
	
	private void fire(final int type, final Event event) {
		UiThreadUtil.performAsyncOnSwt(new Task() {
			@Override
			public void run() {
				// TODO update navigation bar
				_widget.notifyListeners(type, event);
			}
		});
	}
}