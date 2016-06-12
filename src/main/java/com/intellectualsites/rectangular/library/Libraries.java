package com.intellectualsites.rectangular.library;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Libraries {

    private static List<Library> enabled = new ArrayList<>();

    public static void setEnabled(Library library, boolean status) {
        if (status && !enabled.contains(library)) {
            enabled.add(library);
        } else if (!status && enabled.contains(library)) {
            enabled.remove(library);
        }
    }

    public static boolean isEnabled(Library library) {
        return enabled.contains(library);
    }

    public static void loadLibrary(Library library) throws IllegalAccessException, InstantiationException {
        library.getClass().newInstance();
    }

    @RequiredArgsConstructor
    public enum Library {
        ;

        @Getter
        private final Class<?> clazz;
    }

}
