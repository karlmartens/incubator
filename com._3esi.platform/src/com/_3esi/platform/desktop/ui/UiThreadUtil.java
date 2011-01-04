// (c) Copyright 3ES Innovation Inc. 2010.  All rights reserved.

package com._3esi.platform.desktop.ui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.eclipse.swt.widgets.Display;

public final class UiThreadUtil {

    private UiThreadUtil() {
        // nothing to do
    }

    public static void assertSwingThread() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Current Thread is not the Swing Thread"); //$NON-NLS-1$
        }
    }

    public static void assertSwtThread() {
        if (!isDisplayThread()) {
            throw new RuntimeException("Current Thread is not the SWT Thread"); //$NON-NLS-1$
        }
    }

    public static final <T> T performSyncOnSwt(final TaskWithResult<T> task) {
        T result = null;
        if (null != task) {
            if (isDisplayThread())
                result = task.run();
            else {
                Display display = Display.getDefault();
                ResultRunner<T> runner = new ResultRunner<T>(task);
                display.syncExec(runner);
                result = runner.getResult();
            }
        }
        return result;
    }

    public static final void performSyncOnSwt(final Task task) {
        if (null != task) {
            if (isDisplayThread())
                task.run();
            else {
                Display display = Display.getDefault();
                Runner runner = new Runner(task);
                display.syncExec(runner);
            }
        }
    }

    public static final void performAsyncOnSwt(final Task task) {
        if (null != task) {
            Display display = Display.getDefault();
            Runner runner = new Runner(task);
            display.asyncExec(runner);
        }
    }

    public static final <T> T performSyncOnSwing(final TaskWithResult<T> task) {
        T result = null;
        if (null != task) {
            if (SwingUtilities.isEventDispatchThread())
                result = task.run();
            else {
                try {
                    ResultRunner<T> runner = new ResultRunner<T>(task);
                    SwingUtilities.invokeAndWait(runner);
                    result = runner.getResult();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public static final void performSyncOnSwing(final Task task) {
        if (null != task) {
            if (SwingUtilities.isEventDispatchThread())
                task.run();
            else {
                try {
                    Runner runner = new Runner(task);
                    SwingUtilities.invokeAndWait(runner);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static final void performAsyncOnSwing(final Task task) {
        if (null != task) {
            final Runner runner = new Runner(task);
            SwingUtilities.invokeLater(runner);
        }
    }

    private static boolean isDisplayThread() {
        return Display.getDefault() == Display.findDisplay(Thread.currentThread());
    }

    private static class ResultRunner<T> implements Runnable {

        private final TaskWithResult<T> _taskWithResult;
        private T _result;

        public ResultRunner(final TaskWithResult<T> task) {
            _taskWithResult = task;
        }

        @Override
        public void run() {
            _result = _taskWithResult.run();
        }

        T getResult() {
            return _result;
        }

    }

    private static class Runner implements Runnable {

        private final Task _task;

        public Runner(final Task task) {
            _task = task;
        }

        @Override
        public void run() {
            _task.run();
        }

    }

}
