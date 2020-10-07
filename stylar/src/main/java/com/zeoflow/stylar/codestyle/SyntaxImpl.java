package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SyntaxImpl implements CodeStyle.Syntax
{

    private final String type;
    private final List<? extends CodeStyle.Node> children;
    private final String alias;
    private final String matchedString;
    private final boolean greedy;
    private final boolean tokenized;

    public SyntaxImpl(
        @NotNull String type,
        @NotNull List<? extends CodeStyle.Node> children,
        @Nullable String alias,
        @NotNull String matchedString,
        boolean greedy,
        boolean tokenized)
    {
        this.type = type;
        this.children = children;
        this.alias = alias;
        this.matchedString = matchedString;
        this.greedy = greedy;
        this.tokenized = tokenized;
    }

    @Override
    public int textLength()
    {
        return matchedString.length();
    }

    @Override
    public final boolean isSyntax()
    {
        return true;
    }

    @NotNull
    @Override
    public String type()
    {
        return type;
    }

    @NotNull
    @Override
    public List<? extends CodeStyle.Node> children()
    {
        return children;
    }

    @Nullable
    @Override
    public String alias()
    {
        return alias;
    }

    @NotNull
    @Override
    public String matchedString()
    {
        return matchedString;
    }

    @Override
    public boolean greedy()
    {
        return greedy;
    }

    @Override
    public boolean tokenized()
    {
        return tokenized;
    }

    @Override
    public String toString()
    {
        return "SyntaxImpl{" +
            "type='" + type + '\'' +
            ", children=" + children +
            ", alias='" + alias + '\'' +
            ", matchedString='" + matchedString + '\'' +
            ", greedy=" + greedy +
            ", tokenized=" + tokenized +
            '}';
    }
}
