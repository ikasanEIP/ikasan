package org.ikasan.module.migration.util;

import java.util.*;

public class OrderedProperties extends Properties {
    private final LinkedHashSet<Object> keyOrder = new LinkedHashSet<>();

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(keyOrder);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        // Add key to the ordered set
        keyOrder.add(key);
        // Call the parent Hashtable put method
        return super.put(key, value);
    }

    @Override
    public Set<Object> keySet() {
        return keyOrder;
    }

    @Override
    public Enumeration<?> propertyNames() {
        return Collections.enumeration(keyOrder);
    }
}
