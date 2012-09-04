/**
 *   Copyright 2012 Karl Martens
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
package net.karlmartens.ui.widget;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static net.karlmartens.platform.util.DateSupport.toLocalDate;
import static org.eclipse.swt.SWT.ERROR_INVALID_ARGUMENT;
import static org.eclipse.swt.SWT.ERROR_INVALID_RANGE;
import static org.eclipse.swt.SWT.error;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * @author karl
 * 
 */
public final class Calendar extends Composite {

  private static final int ROW_COUNT = 7;
  private static final YearMonthDay _NONE = new YearMonthDay(-1, -1, -1);

  public static final int[] NO_SELECTION = _NONE.toArray();

  private final DateTimeFormatter _titlePrinter;
  private final java.util.Calendar _cal;
  private final LocalResourceManager _resourceManger;
  private final int _weekdayCount;

  private YearMonth _minimum;
  private YearMonth _maximum;
  private YearMonth _month;
  private YearMonthDay _date;

  private Color _alternateBackgroundColor;
  private Color _selectionColor;

  private Block _title;
  private Block[] _blocks;

  private boolean _requiresRefresh;

  public Calendar(Composite parent, int style) {
    this(parent, style, Locale.getDefault());
  }

  public Calendar(Composite parent, int style, Locale locale) {
    super(parent, style);
    final DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
    _cal = java.util.Calendar.getInstance(locale);
    _titlePrinter = new DateTimeFormatterBuilder()//
        .appendMonthOfYearText()//
        .appendLiteral(' ')//
        .appendYear(4, 4)//
        .toFormatter();

    _resourceManger = new LocalResourceManager(
        JFaceResources.getResources(getDisplay()));

    _date = new YearMonthDay(_cal);
    _month = _date.toYearMonth();
    _minimum = _month.addYears(-100);
    _maximum = _month.addYears(100);

    final String[] weekdays = createCalendarWeekdays(_cal, symbols);
    _weekdayCount = weekdays.length;

    final Display display = getDisplay();
    _alternateBackgroundColor = display
        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    _selectionColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION);

    final GridLayout layout = new GridLayout(_weekdayCount, false);
    layout.horizontalSpacing = 0;
    layout.marginHeight = 1;
    layout.marginWidth = 1;
    layout.verticalSpacing = 0;

    super.setLayout(layout);
    super.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

    final Font titleFont = _resourceManger.createFont(FontDescriptor
        .createFrom("Arial", 12, SWT.BOLD));

    _title = createBlock();
    _title.setLayoutData(createLayoutData().span(_weekdayCount, 1).create());
    _title.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
    _title.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
    _title.setFont(titleFont);

    final Font calendarFont = _resourceManger.createFont(FontDescriptor
        .createFrom("Arial", 10, SWT.NONE));
    _blocks = new Block[_weekdayCount * ROW_COUNT];
    for (int i = 0; i < _blocks.length; i++) {
      if (i == _weekdayCount)
        createSeparator();

      final Block block = _blocks[i] = createBlock();
      block.setLayoutData(createLayoutData().create());
      block.setFont(calendarFont);

      if (i < _weekdayCount) {
        block.setText(weekdays[i]);
        continue;
      }

    }

    _requiresRefresh = true;

    new ListenerImpl();

    final PassthoughEventListener pListener = new PassthoughEventListener(this);
    pListener.addSource(_title);
    for (int i = 0; i < _blocks.length; i++)
      pListener.addSource(_blocks[i]);
  }

  @Override
  public void setBackground(Color color) {
    super.setBackground(color);
    for (int i = 0; i < _weekdayCount; i++) {
      _blocks[i].setBackground(color);
    }

    _requiresRefresh = true;
    redraw();
  }

  public void setAlternateBackground(Color color) {
    checkWidget();
    _alternateBackgroundColor = color;

    _requiresRefresh = true;
    redraw();
  }

  public void setSelectionColor(Color color) {
    checkWidget();
    _selectionColor = color;

    _requiresRefresh = true;
    redraw();
  }

  @Override
  public void setForeground(Color color) {
    super.setForeground(color);
    for (int i = 0; i < _blocks.length; i++) {
      _blocks[i].setForeground(color);
    }
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    for (int i = 0; i < _blocks.length; i++) {
      _blocks[i].setFont(font);
    }
    layout(true, true);
  }

  public void setTitleBackground(Color color) {
    checkWidget();
    _title.setBackground(color);
  }

  public void setTitleForeground(Color color) {
    checkWidget();
    _title.setForeground(color);
  }

  public void setTitleFont(Font font) {
    checkWidget();
    _title.setFont(font);
    layout(true, true);
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    _title.setEnabled(enabled);
    for (int i = 0; i < _blocks.length; i++) {
      _blocks[i].setEnabled(enabled);
    }
  }

  public int[] getMinimum() {
    checkWidget();
    return _minimum.toArray();
  }

  public void setMinimum(int year, int month) {
    checkWidget();
    final YearMonth candidate = new YearMonth(year, month);
    checkYearMonth(candidate);
    checkRange(_cal, candidate, _maximum);
    checkRange(_cal, candidate, _month);
    checkRange(_cal, candidate, _date.toYearMonth());
    _minimum = candidate;
  }

  public int[] getMaximum() {
    checkWidget();
    return _maximum.toArray();
  }

  public void setMaximum(int year, int month) {
    checkWidget();
    final YearMonth candidate = new YearMonth(year, month);
    checkYearMonth(candidate);
    checkRange(_cal, _minimum, candidate);
    checkRange(_cal, _month, candidate);
    checkRange(_cal, _date.toYearMonth(), candidate);
    _maximum = candidate;
  }

  public int[] getSelection() {
    checkWidget();
    return _date.toArray();
  }

  public void setSelection(int year, int month, int day) {
    checkWidget();
    final YearMonthDay candidate = new YearMonthDay(year, month, day);
    if (candidate.equals(_date))
      return;

    checkYearMonthDay(candidate);
    checkRange(_cal, _minimum, candidate.toYearMonth());
    checkRange(_cal, candidate.toYearMonth(), _maximum);

    internalSetSelection(candidate);
  }

  public void addDays(int days) {
    checkWidget();
    if (_NONE.equals(_date))
      return;

    _date.apply(_cal);
    _cal.add(DAY_OF_MONTH, days);
    final YearMonthDay candidate = new YearMonthDay(_cal);
    checkYearMonthDay(candidate);
    checkRange(_cal, _minimum, candidate.toYearMonth());
    checkRange(_cal, candidate.toYearMonth(), _maximum);

    internalSetSelection(candidate);
  }

  public void scrollTo(int year, int month) {
    checkWidget();

    final YearMonth candidate = new YearMonth(year, month);
    if (_month.equals(candidate))
      return;

    checkYearMonth(candidate);
    checkRange(_cal, _minimum, candidate);
    checkRange(_cal, candidate, _maximum);

    internalScrollTo(candidate);
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed) {
    final GC gc = new GC(this);
    gc.setFont(getFont());
    int width = (gc.stringExtent("33").x + 4) * _weekdayCount + 2;
    int height = (gc.getFontMetrics().getHeight() + 4) * ROW_COUNT + 2;

    gc.setFont(_title.getFont());
    height += gc.getFontMetrics().getHeight();
    width = Math
        .max(gc.getFontMetrics().getAverageCharWidth() * 14 + 10, width);

    gc.dispose();

    if (wHint != SWT.DEFAULT)
      width = wHint;

    if (hHint != SWT.DEFAULT)
      height = hHint;

    return new Point(width, height);
  }

  private void internalSetSelection(YearMonthDay selection) {
    if (selection.equals(_date))
      return;

    _date = selection;
    notifyListeners(SWT.Selection, new Event());

    _requiresRefresh = true;
    redraw();
  }

  private void internalScrollTo(YearMonth value) {
    if (_month.equals(value) || _NONE.toYearMonth().equals(value))
      return;

    _month = value;

    _requiresRefresh = true;
    redraw();
  }

  private void computeCalendarStart() {
    _month.apply(_cal);
    _cal.add(DAY_OF_MONTH, _cal.getFirstDayOfWeek() - _cal.get(DAY_OF_WEEK));
  }

  private void internalRefresh() {
    _month.apply(_cal);
    _title.setText(_titlePrinter.print(toLocalDate(_cal)));

    computeCalendarStart();

    for (int i = _weekdayCount; i < _blocks.length; i++) {
      final YearMonthDay ymd = new YearMonthDay(_cal);

      final Block block = _blocks[i];
      block.setText(Integer.toString(_cal.get(DAY_OF_MONTH)));
      // block.setEnabled(_month.equals(ymd.toYearMonth()));
      block.setBackground(computeBackground(ymd));

      _cal.add(DAY_OF_MONTH, 1);
    }

    layout(true, true);
  }

  private Color computeBackground(YearMonthDay ymd) {
    if (ymd.equals(_date))
      return _selectionColor;

    if (!ymd.toYearMonth().equals(_month))
      return _alternateBackgroundColor;

    return getBackground();
  }

  private YearMonthDay computeNeighbor(int x, int y) {
    if (_NONE.equals(_date))
      return _NONE;

    _date.apply(_cal);
    _cal.add(DAY_OF_MONTH, x);
    _cal.add(DAY_OF_MONTH, y * _weekdayCount);
    return new YearMonthDay(_cal);
  }

  private YearMonthDay computePage(int x, int y) {
    if (_NONE.equals(_date))
      return _NONE;

    _date.apply(_cal);
    _cal.add(MONTH, y);
    _cal.add(YEAR, x);
    return new YearMonthDay(_cal);
  }

  private YearMonthDay computeHome() {
    if (_NONE.equals(_date))
      return _NONE;

    _date.apply(_cal);
    final int sow = _cal.getFirstDayOfWeek();
    final int dow = _cal.get(DAY_OF_WEEK);
    _cal.add(DAY_OF_MONTH, sow - dow);
    return new YearMonthDay(_cal);
  }

  private YearMonthDay computeEnd() {
    if (_NONE.equals(_date))
      return _NONE;

    _date.apply(_cal);
    final int sow = _cal.getFirstDayOfWeek();
    final int dow = _cal.get(DAY_OF_WEEK);
    _cal.add(DAY_OF_MONTH, sow - dow + _weekdayCount - 1);
    return new YearMonthDay(_cal);
  }

  private void navigateTo(YearMonthDay candidate) {
    if (candidate == null)
      return;

    if (!_NONE.equals(candidate)) {
      if (candidate.compareTo(_minimum) < 0) {
        _minimum.apply(_cal);
        _cal.set(DAY_OF_MONTH, _cal.getActualMinimum(DAY_OF_MONTH));
        candidate = new YearMonthDay(_cal);
      } else if (candidate.compareTo(_maximum) > 0) {
        _maximum.apply(_cal);
        _cal.set(DAY_OF_MONTH, _cal.getActualMaximum(DAY_OF_MONTH));
        candidate = new YearMonthDay(_cal);
      }
    }

    internalSetSelection(candidate);
    internalScrollTo(candidate.toYearMonth());
  }

  private void keyPressed(Event event) {
    YearMonthDay candidate = null;
    switch (event.keyCode) {
      case SWT.ARROW_DOWN:
        if (event.stateMask == SWT.MOD1) {
          candidate = computePage(0, 1);
        } else if (event.stateMask == SWT.NONE) {
          candidate = computeNeighbor(0, 1);
        }
        break;

      case SWT.ARROW_UP:
        if (event.stateMask == SWT.MOD1) {
          candidate = computePage(0, -1);
        } else if (event.stateMask == SWT.NONE) {
          candidate = computeNeighbor(0, -1);
        }
        break;

      case SWT.ARROW_RIGHT:
        if (event.stateMask == SWT.CTRL) {
          candidate = computePage(1, 0);
        } else if (event.stateMask == SWT.MOD1) {
          candidate = computeEnd();
        } else if (event.stateMask == SWT.NONE) {
          candidate = computeNeighbor(1, 0);
        }
        break;

      case SWT.ARROW_LEFT:
        if (event.stateMask == SWT.CTRL) {
          candidate = computePage(-1, 0);
        } else if (event.stateMask == SWT.MOD1) {
          candidate = computeHome();
        } else if (event.stateMask == SWT.NONE) {
          candidate = computeNeighbor(-1, 0);
        }
        break;

      case SWT.HOME:
        candidate = computeHome();
        break;

      case SWT.END:
        candidate = computeEnd();
        break;

      case SWT.PAGE_UP:
        candidate = computePage(0, -1);
        break;

      case SWT.PAGE_DOWN:
        candidate = computePage(0, 1);
        break;
    }

    navigateTo(candidate);
  }

  private void mouseDown(Event event) {
    for (int i = _weekdayCount; i < _blocks.length; i++) {
      if (event.widget == _blocks[i]) {
        computeCalendarStart();
        _cal.add(DAY_OF_MONTH, i - _weekdayCount);
        final YearMonthDay candidate = new YearMonthDay(_cal);
        navigateTo(candidate);
      }
    }
  }

  private static String[] createCalendarWeekdays(java.util.Calendar calendar,
      DateFormatSymbols symbols) {
    final String[] weekdays = symbols.getShortWeekdays();
    final int min = calendar.getMinimum(DAY_OF_WEEK);
    final int days = calendar.getMaximum(DAY_OF_WEEK) - min + 1;
    final int firstWeekday = calendar.getFirstDayOfWeek();
    final String[] result = new String[days];
    for (int i = 0; i < result.length; i++) {
      final String weekday = weekdays[(firstWeekday + i - min) % days + min]
          .substring(0, 1);
      result[i] = weekday;
    }

    return result;
  }

  private Block createBlock() {
    final Block block = new Block(this);
    block.setForeground(getForeground());
    block.setBackground(getBackground());
    block.setFont(getFont());
    block.setAlignment(SWT.CENTER);
    return block;
  }

  private void createSeparator() {
    final Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL
        | SWT.CENTER);
    separator.setLayoutData(createLayoutData()//
        .span(_weekdayCount, 1)//
        .align(SWT.FILL, SWT.CENTER)//
        .grab(true, false)//
        .create());
  }

  private GridDataFactory createLayoutData() {
    return GridDataFactory//
        .fillDefaults()//
        .grab(true, true);
  }

  private void checkYearMonth(YearMonth candidate) {
    if (_NONE.toYearMonth().equals(candidate))
      return;

    _cal.clear();
    final int year = candidate.year();
    if (_cal.getActualMinimum(YEAR) > year
        || _cal.getActualMaximum(YEAR) < year)
      error(ERROR_INVALID_ARGUMENT);

    _cal.set(YEAR, year);

    final int zeroBasedMonth = candidate.month() - 1;
    if (_cal.getActualMinimum(MONTH) > zeroBasedMonth
        || _cal.getActualMaximum(MONTH) < zeroBasedMonth)
      error(ERROR_INVALID_ARGUMENT);
  }

  private void checkYearMonthDay(YearMonthDay value) {
    if (_NONE.equals(value))
      return;

    checkYearMonth(value.toYearMonth());

    _cal.set(MONTH, value.month() - 1);

    final int day = value.day();
    if (_cal.getActualMinimum(DAY_OF_MONTH) > day
        || _cal.getActualMaximum(DAY_OF_MONTH) < day)
      error(ERROR_INVALID_ARGUMENT);
  }

  private static void checkRange(java.util.Calendar calendar,
      YearMonth minimum, YearMonth maximum) {
    if (_NONE.toYearMonth().equals(minimum)
        || _NONE.toYearMonth().equals(maximum))
      return;

    final java.util.Calendar min = (java.util.Calendar) calendar.clone();
    minimum.apply(min);

    final java.util.Calendar max = (java.util.Calendar) calendar.clone();
    maximum.apply(max);

    if (min.after(max))
      error(ERROR_INVALID_RANGE);
  }

  private class ListenerImpl implements Listener {

    ListenerImpl() {
      addListener(SWT.KeyDown, this);
      addListener(SWT.Dispose, this);
      addListener(SWT.Paint, this);

      for (int i = 0; i < _blocks.length; i++) {
        if (i >= _weekdayCount)
          _blocks[i].addListener(SWT.MouseDown, this);

        _blocks[i].addListener(SWT.MouseVerticalWheel, this);
        _blocks[i].addListener(SWT.MouseHorizontalWheel, this);
      }
    }

    private void dispose() {
      removeListener(SWT.Dispose, this);
      removeListener(SWT.MouseDown, this);
      removeListener(SWT.Paint, this);

      for (int i = 0; i < _blocks.length; i++) {
        if (i >= _weekdayCount)
          _blocks[i].removeListener(SWT.MouseDown, this);

        _blocks[i].removeListener(SWT.MouseVerticalWheel, this);
        _blocks[i].removeListener(SWT.MouseHorizontalWheel, this);
      }

      _resourceManger.dispose();
    }

    @Override
    public void handleEvent(Event event) {
      if (event.type == SWT.Paint) {
        if (_requiresRefresh) {
          internalRefresh();
          _requiresRefresh = false;
        }
        return;
      }
      if (event.type == SWT.KeyDown) {
        keyPressed(event);
        return;
      }

      if (event.type == SWT.MouseDown) {
        mouseDown(event);
        return;
      }

      if (event.type == SWT.MouseHorizontalWheel) {
        navigateTo(computePage(event.count * -1, 0));
        return;
      }

      if (event.type == SWT.MouseVerticalWheel) {
        navigateTo(computePage(0, event.count * -1));
        return;
      }

      if (event.type == SWT.Dispose) {
        dispose();
        return;
      }

    }

  }

  private static class YearMonth implements Comparable<YearMonth> {
    private final int _year;
    private final int _month;

    public YearMonth(int year, int month) {
      _year = year;
      _month = month;
    }

    public YearMonth(java.util.Calendar calendar) {
      _year = calendar.get(YEAR);
      _month = calendar.get(MONTH) + 1;
    }

    public void apply(java.util.Calendar calendar) {
      calendar.clear();
      calendar.set(YEAR, _year);
      calendar.set(MONTH, _month - 1);
      calendar.set(DAY_OF_MONTH, 1);
    }

    public int year() {
      return _year;
    }

    public int month() {
      return _month;
    }

    public YearMonth addYears(int years) {
      return new YearMonth(_year + years, _month);
    }

    @Override
    public int compareTo(YearMonth o) {
      final int i = _year - o._year;
      if (i != 0)
        return i;

      return _month - o._month;
    }

    public int[] toArray() {
      return new int[] { _year, _month };
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _month;
      result = prime * result + _year;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      YearMonth other = (YearMonth) obj;
      if (_month != other._month)
        return false;
      if (_year != other._year)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return String.format("year: %1$4d month: %2$2d", _year, _month);
    }
  }

  private static final class YearMonthDay implements Comparable<YearMonth> {

    private final YearMonth _yearMonth;
    private final int _day;

    public YearMonthDay(int year, int month, int day) {
      _yearMonth = new YearMonth(year, month);
      _day = day;
    }

    public YearMonthDay(java.util.Calendar calendar) {
      _yearMonth = new YearMonth(calendar);
      _day = calendar.get(DAY_OF_MONTH);
    }

    public void apply(java.util.Calendar calendar) {
      _yearMonth.apply(calendar);
      calendar.set(DAY_OF_MONTH, _day);
    }

    public int month() {
      return _yearMonth.month();
    }

    public int day() {
      return _day;
    }

    @Override
    public int compareTo(YearMonth o) {
      return _yearMonth.compareTo(o);
    }

    public YearMonth toYearMonth() {
      return _yearMonth;
    }

    public int[] toArray() {
      final int[] base = _yearMonth.toArray();
      final int[] arr = Arrays.copyOf(base, base.length + 1);
      arr[base.length] = _day;
      return arr;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _day;
      result = prime * result
          + ((_yearMonth == null) ? 0 : _yearMonth.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      YearMonthDay other = (YearMonthDay) obj;
      if (_day != other._day)
        return false;
      if (_yearMonth == null) {
        if (other._yearMonth != null)
          return false;
      } else if (!_yearMonth.equals(other._yearMonth))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return String.format("%1$s day: %2$2d", super.toString(), _day);
    }
  }
}
