package com.ikasan.sample.spring.boot.builderpattern;

import java.util.ArrayList;
import java.util.List;

public class FakeDataProvider {
    private static List<String> DATA_PROVIDER = new ArrayList<>();

    public static void add(String data) {
        DATA_PROVIDER.add(data);
    }

    public static String get() {
        return DATA_PROVIDER.get(0);
    }

    public static String get(int i) {
        return DATA_PROVIDER.get(i);
    }

    public static void remove(String data) {
        DATA_PROVIDER.remove(data);
    }

    public static int size() {
        return DATA_PROVIDER.size();
    }

    public static void reset() {
        DATA_PROVIDER = new ArrayList<>();
    }
}
