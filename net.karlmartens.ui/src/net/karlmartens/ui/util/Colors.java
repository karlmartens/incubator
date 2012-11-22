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

import org.eclipse.swt.graphics.RGB;

/**
 * @author kmartens
 * 
 */
public final class Colors {

  private Colors() {
    // Nothing to do
  }

  public static RGB blend(RGB dst, RGB src, double srcAlpha) {
    final int red = blend(dst.red, src.red, srcAlpha);
    final int green = blend(dst.green, src.green, srcAlpha);
    final int blue = blend(dst.blue, src.blue, srcAlpha);
    return new RGB(red, green, blue);
  }

  public static RGB gray(RGB dst) {
    final int red = dst.red & 0xFF;
    final int green = dst.green & 0xFF;
    final int blue = dst.blue & 0xFF;
    final int intensity = ((red + red + green + green + green + green + green + blue) >> 3) & 0xFF;
    return new RGB(intensity, intensity, intensity);
  }

  private static int blend(int dst, int src, double srcAlpha) {
    final int out = (int) ((double) src * srcAlpha + (double) dst
        * (1.0 - srcAlpha));
    return out;
  }

}
