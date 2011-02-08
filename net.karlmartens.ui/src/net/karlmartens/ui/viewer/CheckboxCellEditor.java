package net.karlmartens.ui.viewer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public final class CheckboxCellEditor extends CellEditor {

	private Button _button;

	public CheckboxCellEditor(Composite parent) {
		super(parent);
	}
	
	@Override
	public void activate() {
		_button.setSelection(!_button.getSelection());
	}
	
    public void activate(ColumnViewerEditorActivationEvent activationEvent) {
    	if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
    		super.activate(activationEvent);
    	}
    }
	
	@Override
	public LayoutData getLayoutData() {
		final LayoutData data = super.getLayoutData();
		data.horizontalAlignment = SWT.CENTER;
		data.grabHorizontal = true;
		data.verticalAlignment = SWT.CENTER;
		return data;
	}
	
    @Override
	protected Control createControl(Composite parent) {
    	final Composite c = new Composite(parent, SWT.NONE);
    	c.setLayout(new FormLayout());
		c.setFont(parent.getFont());
		c.setBackground(parent.getBackground());
    	
		_button = new Button(c, SWT.CHECK);
		_button.setFont(parent.getFont());
		_button.setBackground(parent.getBackground());

		final Point size = _button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		final FormData data = new FormData();
		data.left = new FormAttachment(1, 2, size.x / -2 + 1);
		data.top = new FormAttachment(1, 2, size.y / -2 - 1);

		_button.setLayoutData(data);
		
		return c;
	}

	@Override
	protected Object doGetValue() {
		return _button.getSelection();
	}

	@Override
	protected void doSetFocus() {
		if (_button != null) {
			_button.setFocus();
		}
	}

	@Override
	protected void doSetValue(Object value) {
		Assert.isTrue(_button != null && (value instanceof Boolean));
		_button.setSelection(Boolean.TRUE.equals(value));
	}
}
