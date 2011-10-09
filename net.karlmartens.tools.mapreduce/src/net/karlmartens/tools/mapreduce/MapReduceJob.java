/**
 *  
 *  Copyright (C) 2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.tools.mapreduce;

import java.util.Comparator;
import java.util.Iterator;

import net.karlmartens.platform.util.Pair;
import net.karlmartens.platform.util.ReflectSupport;

public final class MapReduceJob<K, V, K1, V1, V2> {

  private final Iterable<Pair<K, V>> _input;
  private final Class<? extends Mapper<K, V, K1, V1>> _mapper;
  private final Comparator<K1> _comparator;
  private final Class<? extends Reducer<K1, V1, V2>> _reducer;

  public MapReduceJob(Class<? extends Mapper<K, V, K1, V1>> mapper, Class<? extends Reducer<K1, V1, V2>> reducer, Comparator<K1> comparator,
      Iterable<Pair<K, V>> input) {
    _input = input;
    _mapper = mapper;
    _comparator = comparator;
    _reducer = reducer;
  }

  public Iterator<Pair<K, V>> getInput() {
    return _input.iterator();
  }

  public Mapper<K, V, K1, V1> newMappper() {
    return ReflectSupport.newInstance(_mapper);
  }

  public Comparator<K1> getComparator() {
    return _comparator;
  }

  public Reducer<K1, V1, V2> newReducer() {
    return ReflectSupport.newInstance(_reducer);
  }
}
