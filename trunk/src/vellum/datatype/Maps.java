/*
 * 
 */
package vellum.datatype;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author evans
 */
public class Maps {

    public static <K, V extends Comparable> V getMinimumValue(Map<K, V> map) {
        return getMinimumValueEntry(map).getValue();
    }

    public static <K, V extends Comparable> Entry<K, V> getMinimumValueEntry(Map<K, V> map) {
        Entry<K, V> entry = null;
        for (Entry<K, V> element : map.entrySet()) {
            if (entry == null || element.getValue().compareTo(entry.getValue()) < 0) {
                entry = element;
            }
        }
        return entry;
    }

    public static <K, V extends Comparable> V getMaximumValue(Map<K, V> map) {
        return getMaximumValueEntry(map).getValue();
    }

    public static <K, V extends Comparable> Entry<K, V> getMaximumValueEntry(Map<K, V> map) {
        Entry<K, V> entry = null;
        for (Entry<K, V> element : map.entrySet()) {
            if (entry == null || element.getValue().compareTo(entry.getValue()) > 0) {
                entry = element;
            }
        }
        return entry;
    }

    public static <K, V extends Comparable> LinkedList<K> descendingValueKeys(Map<K, V> map) {
        return keyLinkedList(descendingValueEntrySet(map));
    }

    public static <K, V extends Comparable> NavigableSet<Entry<K, V>> ascendingValueEntrySet(Map<K, V> map) {
        TreeSet set = new TreeSet(new Comparator<Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        set.addAll(map.entrySet());
        return set;
    }

    public static <K, V extends Comparable> LinkedList<K> ascendingValueKeys(Map<K, V> map) {
        return keyLinkedList(ascendingValueEntrySet(map));
    }

    public static <K, V extends Comparable> NavigableSet<Entry<K, V>> descendingValueEntrySet(Map<K, V> map) {
        TreeSet set = new TreeSet(new Comparator<Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        set.addAll(map.entrySet());
        return set;
    }

    public static <K, V> LinkedList<K> keyLinkedList(NavigableSet<Entry<K, V>> entrySet) {
        LinkedList<K> keyList = new LinkedList();
        for (Map.Entry<K, V> entry : entrySet) {
            keyList.add(entry.getKey());
        }
        return keyList;
    }
}

class EntryAscendingComparator<K, V extends Comparable> implements Comparator<Entry<K, V>> {

    @Override
    public int compare(Entry o1, Entry o2) {
        return ((Comparable) o1.getValue()).compareTo(o2.getValue());
    }
}

class EntryDescendingComparator<K, V extends Comparable> implements Comparator<Entry<K, V>> {

    @Override
    public int compare(Entry o1, Entry o2) {
        return ((Comparable) o2.getValue()).compareTo(o1.getValue());
    }
}
