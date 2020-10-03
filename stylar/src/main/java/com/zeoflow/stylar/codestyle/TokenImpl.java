package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TokenImpl implements CodeStyle.Token
{

    private final String name;
    private final List<CodeStyle.Pattern> patterns;

    public TokenImpl(@NotNull String name, @NotNull List<CodeStyle.Pattern> patterns) {
        this.name = name;
        this.patterns = patterns;
    }

    @NotNull
    @Override
    public String name() {
        return name;
    }

    @NotNull
    @Override
    public List<CodeStyle.Pattern> patterns() {
        return patterns;
    }

    @Override
    public String toString() {
        return ToString.toString(this);
    }
}
