/**
 *  net.karlmartens.ui, is a library of UI widgets
 *  Copyright (C) 2011
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
package net.karlmartens.ui;

import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtTester<T> {

  private final Initializer<T> _initializer;
  private final Queue<Task<T>> _queue = new LinkedList<Task<T>>();

  private SwtTester(Initializer<T> initializer) {
    _initializer = initializer;
  }

  public final SwtTester<T> add(Task<T> task) {
    _queue.add(task);
    return this;
  }

  public final void run() {
    final Shell shell = new Shell();
    shell.setLayout(new FillLayout());

    final T context = _initializer.run(shell);

    final Display display = shell.getDisplay();
    shell.open();
    shell.setVisible(false);
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        final Task<T> task = _queue.poll();
        if (task != null) {
          task.run(context);
        } else {
          shell.close();
        }
      }
    }
  }

  public static <T> SwtTester<T> test(Initializer<T> initializer) {
    return new SwtTester<T>(initializer);
  }

  public interface Initializer<T> {

    T run(Shell shell);

  }

  public interface Task<T> {

    void run(T context);

  }
}
