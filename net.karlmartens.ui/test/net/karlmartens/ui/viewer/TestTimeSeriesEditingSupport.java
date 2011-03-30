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
package net.karlmartens.ui.viewer;

import java.text.NumberFormat;

final class TestTimeSeriesEditingSupport implements TimeSeriesEditingSupport {

  private final int _index;
  private final NumberFormat _format;

  public TestTimeSeriesEditingSupport(NumberFormat format, int index) {
    _index = index;
    _format = format;
  }

  @Override
  public NumberFormat getNumberFormat() {
    return _format;
  }

  @Override
  public boolean canEdit(Object element) {
    return true;
  }

  @Override
  public void setValue(Object element, int index, double value) {
    final double[] series = (double[]) ((Object[]) element)[_index];
    series[index] = value;
  }

}
