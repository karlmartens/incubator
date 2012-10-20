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
package net.karlmartens.ui.util;

import org.eclipse.swt.graphics.Color;

/**
 * @author kmartens
 *
 */
public final class Colors {
  
  private Colors() {
    // Nothing to do
  }

  public static Color blend(Color dst, Color src, double srcAlpha) {
    final int red = blend(dst.getRed(), src.getRed(), srcAlpha);
    final int green = blend(dst.getGreen(), src.getGreen(), srcAlpha);
    final int blue = blend(dst.getBlue(), src.getBlue(), srcAlpha);
    return new Color(null, red, green, blue);
  }
  
  public static int blend(int dst, int src, double srcAlpha) {
    final int out = (int)((double)src * srcAlpha + (double)dst * (1.0 - srcAlpha));
    return out;
  }

}
