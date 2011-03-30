/**
 *   Copyright 2011 Karl Martens
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *       
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   net.karlmartens.ui, is a library of UI widgets
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
