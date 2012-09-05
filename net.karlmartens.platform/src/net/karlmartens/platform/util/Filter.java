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

package net.karlmartens.platform.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author karl
 * 
 */
public abstract class Filter<T> {

  public abstract boolean accepts(T candidate);

  public static final Filter<Object> ALL = new Filter<Object>() {
    @Override
    public boolean accepts(Object candidate) {
      return true;
    }
  };

  public static final Filter<Object> NONE = new Filter<Object>() {
    @Override
    public boolean accepts(Object candidate) {
      return false;
    }
  };

  @SuppressWarnings("unchecked")
  public static <T> Filter<T> all() {
    return (Filter<T>) ALL;
  }

  @SuppressWarnings("unchecked")
  public static <T> Filter<T> none() {
    return (Filter<T>) NONE;
  }

  public static <T> Filter<T> accepting(T... items) {
    return new EnumeratedFilter<T>(items);
  }

  private static class EnumeratedFilter<T> extends Filter<T> {

    private final Set<T> _accepts;

    public EnumeratedFilter(T... items) {
      _accepts = new HashSet<T>(Arrays.asList(items));
    }

    @Override
    public boolean accepts(T candidate) {
      return _accepts.contains(candidate);
    }

  }
}
