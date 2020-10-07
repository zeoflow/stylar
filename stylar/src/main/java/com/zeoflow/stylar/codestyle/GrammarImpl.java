package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GrammarImpl implements CodeStyle.Grammar
{

    private final String name;
    private final List<CodeStyle.Token> tokens;

    public GrammarImpl(@NotNull String name, @NotNull List<CodeStyle.Token> tokens)
    {
        this.name = name;
        this.tokens = tokens;
    }

    @NotNull
    @Override
    public String name()
    {
        return name;
    }

    @NotNull
    @Override
    public List<CodeStyle.Token> tokens()
    {
        return tokens;
    }

    @Override
    public String toString()
    {
        return ToString.toString(this);
    }
}
