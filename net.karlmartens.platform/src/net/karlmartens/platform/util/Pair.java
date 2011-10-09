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
 *   net.karlmartens.platform, is a library of shared basic utility classes
 */

package net.karlmartens.platform.util;

public final class Pair<A, B> {

  private final A _a;
  private final B _b;

  public Pair(A a, B b) {
    _a = a;
    _b = b;
  }

  public A a() {
    return _a;
  }

  public B b() {
    return _b;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_a == null) ? 0 : _a.hashCode());
    result = prime * result + ((_b == null) ? 0 : _b.hashCode());
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
    final Pair<?, ?> other = (Pair<?, ?>) obj;
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
