package net.karlmartens.ui.widget;

import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public final class SparklineScrollBar extends Composite {
	
	private static final int THUMB_ALPHA_DEFAULT = 60;
	private static final int THUMB_ALPHA_OVER = 80;

	private final Button _left;
	private final FigureCanvas _track;
	private final Button _right;
	private Layer _sparkLineLayer;
	private Layer _thumbLayer;
	private RectangleFigure _thumbFigure;
	
	private int _minimum = 0;
	private int _maximum = 100;
	private int _thumb = 10;
	private int _highlight = -1;
	private int _increment = 1;
	private int _selection = 0;
	private double[] _data = new double[101];
	
	
	public SparklineScrollBar(Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
		final GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		
		final Listener listener = new Listener();
		final Display display = getShell().getDisplay();
		
		_left = new Button(this, SWT.ARROW | SWT.LEFT);
		_left.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		_left.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		_left.addSelectionListener(listener);
		_left.addMouseListener(listener);
		
		_track = new FigureCanvas(this);
		_track.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		_track.setScrollBarVisibility(FigureCanvas.NEVER);
		_track.setContents(createContents(listener));
		_track.getViewport().setContentsTracksHeight(true);
		_track.getViewport().setContentsTracksWidth(true);
		_track.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_track.addMouseListener(listener);
		
		_right = new Button(this, SWT.ARROW | SWT.RIGHT);
		_right.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		_right.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		_right.addSelectionListener(listener);
		_right.addMouseListener(listener);
		
		refresh();
		addControlListener(listener);
	}
	
	private IFigure createContents(Listener listener) {
		final LayeredPane container = new LayeredPane();
		container.setBorder(new LineBorder());
		container.setBackgroundColor(ColorConstants.blue);
		container.setLayoutManager(new StackLayout());
		
		_sparkLineLayer = new Layer();
		_sparkLineLayer.setLayoutManager(new XYLayout());
		container.add(_sparkLineLayer, 0);
		
		_thumbLayer = new Layer();
		_sparkLineLayer.setLayoutManager(new XYLayout());
		container.add(_thumbLayer, 1);
		
		_thumbFigure = new RectangleFigure();
		_thumbFigure.setBackgroundColor(ColorConstants.yellow);
		_thumbFigure.setAlpha(THUMB_ALPHA_DEFAULT);
		_thumbFigure.addMouseListener(listener);
		_thumbFigure.addMouseMotionListener(listener);
		_thumbLayer.add(_thumbFigure);
		
		return container;
	}
	
	private IFigure createDataPointFigure() {
		final RectangleFigure figure = new RectangleFigure();
		figure.setBackgroundColor(ColorConstants.gray);
		figure.setForegroundColor(ColorConstants.gray);
		return figure;
	}

	public void setMinimum(int value) {
		checkWidget();
		if (value < 0 || value >= _maximum)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_minimum = value;
		resizeData();
	}
	
	public int getMinimum() {
		checkWidget();
		return _minimum;
	}
	
	public void setMaximum(int value) {
		checkWidget();
		if (value <= _minimum)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_maximum = value;
		resizeData();
	}
	
	public int getMaximum() {
		checkWidget();
		return _maximum;
	}
	
	public void setThumb(int value) {
		checkWidget();
		if (value <= 0 || value > (_maximum - _minimum + 1))
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_thumb = value;
		if (_highlight >= _thumb)
			_highlight = -1;
		handleResize();
	}
	
	public void setHighlight(int value) {
		checkWidget();
		if (value >= _thumb)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_highlight = value;
		// TODO cause widget to redraw
	}
	
	public int getHighlight() {
		checkWidget();
		return _highlight;
	}
	
	public int getThumb() {
		checkWidget();
		return _thumb;
	}
	
	public void setIncrement(int value) {
		checkWidget();
		if (value < 1)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		_increment = value;
	}
	
	public int getIncrement() {
		checkWidget();
		return _increment;
	}
	
	public void setSelection(int value) {
		checkWidget();
		final int v = Math.min(Math.max(_minimum, value), _maximum - _thumb + 1);
		if (v == _selection)
			return;

		_selection = v;
		handleResize();
		notifyListeners(SWT.Selection, new Event());
	}
	
	public int getSelection() {
		checkWidget();
		return _selection;
	}
	
	public void setData(double[] data) {
		setData(data, _minimum);
	}
	
	public void setData(double data, int index) {
		setData(new double[] { data }, index);
	}
	
	public void setData(double[] data, int index) {
		checkWidget();
		if (index < _minimum || index > _maximum)
			SWT.error(SWT.ERROR_INVALID_RANGE);
		
		final int i = index - _minimum;
		final int l = Math.min(_maximum - _minimum + 1 - i, data.length);
		System.arraycopy(data, 0, _data, i, l);
		handleResize();
	}
	
	public void refresh() {
		final int points = computeDataPoints();
		final List<?> dataPoints = _sparkLineLayer.getChildren();
		for (int i=points; i<dataPoints.size(); i++) {
			_sparkLineLayer.remove((IFigure)dataPoints.get(i));
			_sparkLineLayer.invalidate();
		}

		for (int i=dataPoints.size(); i<points; i++) {
			_sparkLineLayer.add(createDataPointFigure());
			_sparkLineLayer.invalidate();
		}
		
		handleResize();	
	}
	
	private void handleResize() {
		this.layout();
		final Rectangle available = _track.getClientArea();
		
		final double max = computeMaxValue();
		final List<?> c = _sparkLineLayer.getChildren();
		final IFigure[] points = c.toArray(new IFigure[] {});

		final double idealPointWidth = (double)available.width / (double)points.length;
		final int pointWidth = Math.max(1, (int)idealPointWidth);
		final double delta = idealPointWidth - pointWidth;
		
		double error=0.0;
		int x=0;
		for(int i=0; i<points.length; i++) {
			final int width = pointWidth + (int)error;
			error = error + delta - (int)error;
			final int height = (int)((double)available.height * _data[i] / max);
			points[i].setVisible(height >=0 && width >=0);
			
			if (width <= 0)
				continue;
			
			points[i].setSize(new Dimension(width, height));
			points[i].setLocation(new Point(x, available.height - height - 1));
			x+=width;			
		}
		
		_track.getContents().setSize(available.width, available.height);
		_sparkLineLayer.setSize(available.width, available.height);
		_thumbLayer.setSize(available.width, available.height);
		
		_thumbFigure.setSize(Math.max(5, available.width * _thumb / points.length), available.height);
		_thumbFigure.setLocation(new Point(available.width * _selection / points.length, 0));
		_thumbFigure.invalidate();
	}

	private double computeMaxValue() {
		double max = Double.MIN_VALUE;
		for(int i=0; i<computeDataPoints(); i++) {
			final double candidate = _data[i];
			if (candidate > max)
				max = candidate;
		}
		return max;
	}
	
	private int computeDataPoints() {
		return _maximum - _minimum + 1;
	}
	
	private void resizeData() {
		final int points = computeDataPoints();
		if (_data.length == points)
			return;

		if (_data.length > points) {
			Arrays.fill(_data, points, _data.length, 0.0);
			return;
		}

		final double[] newData = new double[points];
		System.arraycopy(_data, 0, newData, 0, _data.length);
		_data = newData;
		refresh();
	}
	
	private void incrementSelection(int direction) {
		setSelection(_selection + (_increment * direction));
	}
	
	private final class Listener implements SelectionListener, MouseListener, org.eclipse.draw2d.MouseListener, MouseMotionListener, ControlListener {

		private Repeater _repeater;
		private Point _startLocation;
		private int _offset = 0;
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (isDisposed())
				return;
			
			final Object source = e.getSource();
			if (source == _left) {
				incrementSelection(-1);
				return;
			}
			
			if (source == _right) {
				incrementSelection(1);
				return;
			}
		}
		
		@Override
		public void mouseDown(MouseEvent e) {
			final Object source = e.getSource();
			if (source == _left) {
				createRepeater(-1);
				return;
			}
			
			if (source == _right) {
				createRepeater(1);
				return;
			}
			
			if (source == _track) {
				final double pct = (double)e.x / (double)_track.getClientArea().width;
				final int points = computeDataPoints(); 
				final int selection =  (int)(points * pct) + _minimum;
				if (selection < _selection || selection >= _selection + _thumb)
					setSelection(selection);
				return;
			}
		}
		
		@Override
		public void mouseUp(MouseEvent e) {
			cancelRepeater();
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// do nothing
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}

		@Override
		public void controlMoved(ControlEvent e) {
			e.getSource();
			// do nothing
		}

		@Override
		public void controlResized(ControlEvent e) {
			if (isDisposed())
				return;
			
			handleResize();
		}

		private void cancelRepeater() {
			if (_repeater == null)
				return;
			
			_repeater._cancelled = true;
			_repeater = null;
		}
		
		private void createRepeater(int direction) {
			cancelRepeater();
			_repeater = new Repeater(direction);
		}

		@Override
		public void mouseDragged(org.eclipse.draw2d.MouseEvent e) {
			if (_startLocation != null && e.getSource() == _thumbFigure) {
				e.consume();
				final int diff = e.getLocation().x - _startLocation.x;
				final double pct = (double)diff / (double)_track.getClientArea().width;

				final int points = computeDataPoints();
				final int correction = (int)(points * pct);
				setSelection(_selection + correction);
				_startLocation.x = _track.getClientArea().width * _selection / points + _offset;
				return;
			}
		}

		@Override
		public void mouseEntered(org.eclipse.draw2d.MouseEvent e) {
			if (e.getSource() == _thumbFigure) {
				e.consume();
				_thumbFigure.setAlpha(THUMB_ALPHA_OVER);
			}
		}

		@Override
		public void mouseExited(org.eclipse.draw2d.MouseEvent e) {
			if (e.getSource() == _thumbFigure) {
				e.consume();
				_thumbFigure.setAlpha(THUMB_ALPHA_DEFAULT);
			}
		}

		@Override
		public void mouseHover(org.eclipse.draw2d.MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseMoved(org.eclipse.draw2d.MouseEvent e) {
			// do nothing
		}

		@Override
		public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent e) {
			// do nothing
		}

		@Override
		public void mousePressed(org.eclipse.draw2d.MouseEvent e) {
			if (e.getSource() == _thumbFigure) {
				e.consume();
				_startLocation = e.getLocation();
				_offset  = _startLocation.x - _thumbFigure.getLocation().x;
			}
		}

		@Override
		public void mouseReleased(org.eclipse.draw2d.MouseEvent e) {
			if (e.getSource() == _thumbFigure) {
				e.consume();
				_startLocation = null;
			}
		}
	}
	
	private class Repeater implements Runnable {
		
		private final int _direction;
		private int _iterations = 0;
		private boolean _cancelled = false;
		
		private Repeater(int direction) {
			_direction = direction;
			schedule();
		}

		@Override
		public void run() {
			if (isDisposed() || _cancelled)
				return;
			
			final int speed = Math.max(Math.min(_iterations++ / 8, 64), 1);
			incrementSelection(_direction*speed);
			schedule();
		}

		private void schedule() {
			getShell().getDisplay().timerExec(20, this);
		}
	}
}
