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

  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  public int hashCode() {
    return super.hashCode();
  }

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

  public Filter<T> and(Filter<T> other) {
    return new AndFilter<T>(this, other);
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_accepts == null) ? 0 : _accepts.hashCode());
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
      EnumeratedFilter<?> other = (EnumeratedFilter<?>) obj;
      if (_accepts == null) {
        if (other._accepts != null)
          return false;
      } else if (!_accepts.equals(other._accepts))
        return false;
      return true;
    }
  }

  private static class AndFilter<T> extends Filter<T> {

    private final Filter<T> _a;
    private final Filter<T> _b;

    public AndFilter(Filter<T> a, Filter<T> b) {
      _a = a;
      _b = b;
    }

    @Override
    public boolean accepts(T candidate) {
      return _a.accepts(candidate) && _b.accepts(candidate);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_a == null) ? 0 : _a.hashCode());
      result = prime * result + ((_b == null) ? 0 : _b.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (getClass() != obj.getClass())
        return false;
      AndFilter<?> other = (AndFilter<?>) obj;
      if (_a == null) {
        if (other._a != null)
          return false;
      } else if (!_a.equals(other._a))
        return false;
      if (_b == null) {
        if (other._b != null)
          return false;
      } else if (!_b.equals(other._b))
        return false;
      return true;
    }
  }
}
