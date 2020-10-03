package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Basic class to locate grammars
 *
 * @see CodeStyle#CodeStyle(GrammarLocator)
 */
public interface GrammarLocator {

    @Nullable
    CodeStyle.Grammar grammar(@NotNull CodeStyle codeStyle, @NotNull String language);

    Set<String> languages();
}
