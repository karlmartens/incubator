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
 *   net.karlmartens.platform, is a library of shared basic utility classes
 */

package net.karlmartens.platform;

/**
 * @author karl
 * 
 */
public final class TestSummarizer {

  public static String lines(String... lines) {
    final StringBuilder builder = new StringBuilder();
    for (String line : lines) {
      if (builder.length() > 0)
        builder.append("\n");

      builder.append(line);
    }

    return builder.toString();
  }

}