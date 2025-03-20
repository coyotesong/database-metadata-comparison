package com.coyotesong.database.formatters;

import java.util.function.Function;

/**
 * Enumeration of per-format 'character escape' functions.
 * <p>
 * Note: this looks like a wierd choice but it significantly simplifies
 * the code elsewhere. We could also implement BaseStream to eliminate
 * the need to call 'map()'
 * </p>
 */
public enum CharacterEscape implements Function<String, String> {
    UNCHANGED(s -> s),
    MARKDOWN(s -> (s == null) ? null : s.replace("\\", "\\\\")
            .replace("_", "\\_")
            .replace("*", "\\*")
            .replace("~", "\\~")),
    HTML(s -> (s == null) ? null : s.replace("<", "&lt;")
            .replace(">", "&gt;"));

    private final Function<String, String> function;

    CharacterEscape(Function<String, String> function) {
        this.function = function;
    }

    public String apply(String s) {
        return function.apply(s);
    }
}
