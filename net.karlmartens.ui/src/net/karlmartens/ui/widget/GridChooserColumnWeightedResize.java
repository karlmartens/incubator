/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2010,2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.ui.widget;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public final class GridChooserColumnWeightedResize implements PaintListener,
		ControlListener, DisposeListener {

    private final GridChooser _chooser;
    private int[] _weights;
    private int _total;

    public GridChooserColumnWeightedResize(GridChooser chooser) {
        this(chooser, new int[] {});
    }

    public GridChooserColumnWeightedResize(GridChooser chooser, int[] weights) {
        _chooser = chooser;
        _chooser.addPaintListener(this);
        _chooser.addControlListener(this);
        _chooser.addDisposeListener(this);
        setWeights(weights);
    }

    public void setWeights(int[] weights) {
        _weights = weights;
        _total = 0;
        for (int i = 0; i < weights.length; i++) {
            final int w = weights[i];
            if (w < 0)
                throw new IllegalArgumentException();

            _total += weights[i];
        }
        resize();
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
        _chooser.removePaintListener(this);
        _chooser.removeDisposeListener(this);
        _chooser.removeControlListener(this);
    }

    @Override
    public void controlMoved(ControlEvent e) {
        // ignore
    }

    @Override
    public void controlResized(ControlEvent e) {
        resize();
    }

    private void resize() {
        if (_total == 0)
            return;

        if (_chooser == null || _chooser.isDisposed())
            return;

        if (!_chooser.isVisible())
            return;

        resize(_chooser.getAvailableComposite());
        resize(_chooser.getSelectedComposite());
    }

    private void resize(Table table) {
        final int ca = table.getClientArea().width;
        for (int i = 0; i < table.getColumnCount(); i++) {
            final TableColumn column = table.getColumn(i);
            if (i >= _weights.length) {
                column.setWidth(0);
                continue;
            }

            column.setWidth(ca * _weights[i] / _total);
        }
    }

    @Override
    public void paintControl(PaintEvent e) {
        resize();
        _chooser.removePaintListener(this);
    }
}
