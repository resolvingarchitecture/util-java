package ra.util;

import java.io.Serializable;
import java.util.*;

/**
 * Properties map that has its keySet ordered consistently (via the key's lexicographical ordering).
 * This is useful in environments where maps must stay the same order (e.g. for signature verification)
 * This does NOT support remove against the iterators / etc.
 *
 * Now unsorted until the keyset or entryset is requested.
 * The class is unsynchronized.
 * The keySet() and entrySet() methods return ordered sets.
 * Others - such as the enumerations values(), keys(), propertyNames() - do not.
 */
public class OrderedProperties extends Properties {

    public OrderedProperties() {
        super();
    }

    @Override
    public Set<Object> keySet() {
        if (size() <= 1)
            return super.keySet();
        return Collections.unmodifiableSortedSet(new TreeSet<Object>(super.keySet()));
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        if (size() <= 1)
            return super.entrySet();
        TreeSet<Map.Entry<Object, Object>> rv = new TreeSet<Map.Entry<Object, Object>>(new EntryComparator());
        rv.addAll(super.entrySet());
        return Collections.unmodifiableSortedSet(rv);
    }

    private static class EntryComparator implements Comparator<Map.Entry<Object, Object>>, Serializable {
        public int compare(Map.Entry<Object, Object> l, Map.Entry<Object, Object> r) {
            return ((String)l.getKey()).compareTo(((String)r.getKey()));
        }
    }
}
