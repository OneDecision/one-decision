package io.onedecision.engine.decisions.impl;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

public class IdHelper {

    // Currently the actual (not future) reserved words of EcmaScript
    private static final List<String> RESERVED_WORDS = Arrays
            .asList(new String[] {
                    "break", "case", "catch", "continue",
                    "debugger", "default", "delete", "do", "else", "finally",
                    "for", "function", "if", "in", "instanceof", "new",
                    "return", "switch", "this", "throw", "try", "typeof",
                    "var", "void", "while", "with" });

    public static String toIdentifier(@NotNull String name) {
        String id = name.replaceAll(" ", "_").replaceAll("'", "")
                .replaceAll("\"", "");
    
        if (RESERVED_WORDS.indexOf(id) != -1) {
            id = "_" + id;
        }
    
        return id;
    }

    public static String toName(@NotNull String id) {
        String name = removeFileExtension(id).replaceAll("_", " ").replaceAll(
                "([a-z])([A-Z])", "$1 $2");
        return name;
    }

    private static String removeFileExtension(String id) {
        if (id.toLowerCase().endsWith(".dmn")) {
            return id.substring(0, id.length() - 4);
        }
        return id;
    }
}
