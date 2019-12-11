package org.ikasan.vaadin.visjs.network.util;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Maps steam of objects to pairs.
 *
 * <pre>
 Usage e.g. Stream.of("0", "1", "2", "3", "4","5")
   .sequential().flatMap(new {@literal PairCollater<>()})
 * </pre>
 *
 * @param <T>
 */
public class PairCollater<T> implements Function<T, Stream<Pair<T, T>>> {
  T prev;

  @Override
  public Stream<Pair<T, T>> apply(T curr) {
    if (prev == null) {
      prev = curr;
      return Stream.empty();
    }
    try {
      return Stream.of(Pair.of(prev, curr));
    } finally {
      prev = null;
    }
  }
}
