package org.ikasan.dashboard.security;

import com.vaadin.flow.component.UI;

import java.util.HashMap;

public class ContextCache {
    private static HashMap<String, String> CONTEXT_MAP = new HashMap<>();

    public static void addContext(String id, String context) {
        CONTEXT_MAP.put(id, context);
    }

    public static String getContext(String id) {
        String context =  CONTEXT_MAP.get(id);
        CONTEXT_MAP.remove(id);

        return context;
    }
}
