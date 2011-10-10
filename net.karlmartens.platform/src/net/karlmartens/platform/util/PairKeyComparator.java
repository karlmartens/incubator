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

import java.util.Comparator;

public class PairKeyComparator<K, V> implements Comparator<Pair<K, V>> {
  
  private final Comparator<K> _comparator;
  
  public PairKeyComparator(Comparator<K> comparator) {
    _comparator = comparator;
  }

  @Override
  public int compare(Pair<K, V> o1, Pair<K, V> o2) {
    if (o1 == o2)
      return 0;
    
    if (o1 == null)
      return -1;
    
    if (o2 == null)
      return 1;
    
    final K k1 = o1.a();
    final K k2 = o2.a();
    return _comparator.compare(k1, k2);
  }

}
