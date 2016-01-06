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

    protected static String toIdentifier(@NotNull String name) {
        String id = name.replaceAll(" ", "_").replaceAll("'", "")
                .replaceAll("\"", "");
    
        if (RESERVED_WORDS.indexOf(id) != -1) {
            id = "_" + id;
        }
    
        return id;
    }

}
