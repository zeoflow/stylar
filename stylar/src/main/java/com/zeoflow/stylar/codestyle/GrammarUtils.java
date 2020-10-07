package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GrammarUtils
{

    private static final Cloner CLONER = Cloner.create();

    private GrammarUtils()
    {
    }

    /**
     * Helper method to find a token inside grammar. Supports lookup in `inside` grammars. For
     * example given the path: {@code first-token/then-another/and-more } this method will do:
     * <ul>
     * <li>Look for `first-token` at root level of supplied grammar</li>
     * <li>If it\'s found search for first pattern with `inside` grammar</li>
     * <li>If it\'s found search for `then-another` token in this inside grammar</li>
     * <li>etc</li>
     * </ul>
     * Simple path {@code simple-root-level } is also supported
     *
     * @param grammar {@link CodeStyle.Grammar}
     * @param path    argument to find a {@link CodeStyle.Token}
     * @return a found {@link CodeStyle.Token} or null
     */
    @Nullable
    public static CodeStyle.Token findToken(@NotNull CodeStyle.Grammar grammar, @NotNull String path)
    {
        final String[] parts = path.split("/");
        return findToken(grammar, parts, 0);
    }

    @Nullable
    private static CodeStyle.Token findToken(@NotNull CodeStyle.Grammar grammar, @NotNull String[] parts, int index)
    {

        final String part = parts[index];
        final boolean last = index == parts.length - 1;

        for (CodeStyle.Token token : grammar.tokens())
        {
            if (part.equals(token.name()))
            {
                if (last)
                {
                    return token;
                } else
                {
                    final CodeStyle.Grammar inside = findFirstInsideGrammar(token);
                    if (inside != null)
                    {
                        return findToken(inside, parts, index + 1);
                    } else
                    {
                        break;
                    }
                }
            }
        }

        return null;
    }

    // won't work if there are multiple patterns provided for a token (each with inside grammar)
    public static void insertBeforeToken(
        @NotNull CodeStyle.Grammar grammar,
        @NotNull String path,
        CodeStyle.Token... tokens
    )
    {

        if (tokens == null
            || tokens.length == 0)
        {
            return;
        }

        final String[] parts = path.split("/");

        insertBeforeToken(grammar, parts, 0, tokens);
    }

    private static void insertBeforeToken(
        @NotNull CodeStyle.Grammar grammar,
        @NotNull String[] parts,
        int index,
        @NotNull CodeStyle.Token[] tokens)
    {

        final String part = parts[index];
        final boolean last = index == parts.length - 1;

        final List<CodeStyle.Token> grammarTokens = grammar.tokens();

        CodeStyle.Token token;

        for (int i = 0, size = grammarTokens.size(); i < size; i++)
        {

            token = grammarTokens.get(i);

            if (part.equals(token.name()))
            {

                // here we must decide what to do next:
                //  - it can be out found one
                //  - or we need to go deeper (c)
                if (last)
                {
                    // here we go, it's our token
                    insertTokensAt(i, grammarTokens, tokens);
                } else
                {
                    // now we must find a grammar that is inside
                    // token can have multiple patterns
                    // but as they are not identified somehow (no name or anything)
                    // we will try to find first pattern with inside grammar
                    final CodeStyle.Grammar inside = findFirstInsideGrammar(token);
                    if (inside != null)
                    {
                        insertBeforeToken(inside, parts, index + 1, tokens);
                    }
                }

                // break after we have found token with specified name (most likely it won't repeat itself)
                break;
            }
        }
    }

    @Nullable
    public static CodeStyle.Grammar findFirstInsideGrammar(@NotNull CodeStyle.Token token)
    {
        CodeStyle.Grammar grammar = null;
        for (CodeStyle.Pattern pattern : token.patterns())
        {
            if (pattern.inside() != null)
            {
                grammar = pattern.inside();
                break;
            }
        }
        return grammar;
    }

    private static void insertTokensAt(
        int start,
        @NotNull List<CodeStyle.Token> grammarTokens,
        @NotNull CodeStyle.Token[] tokens
    )
    {
        for (int i = 0, length = tokens.length; i < length; i++)
        {
            grammarTokens.add(start + i, tokens[i]);
        }
    }

    @NotNull
    public static CodeStyle.Grammar clone(@NotNull CodeStyle.Grammar grammar)
    {
        return CLONER.clone(grammar);
    }

    @NotNull
    public static CodeStyle.Token clone(@NotNull CodeStyle.Token token)
    {
        return CLONER.clone(token);
    }

    @NotNull
    public static CodeStyle.Pattern clone(@NotNull CodeStyle.Pattern pattern)
    {
        return CLONER.clone(pattern);
    }

    @NotNull
    public static CodeStyle.Grammar extend(
        @NotNull CodeStyle.Grammar grammar,
        @NotNull String name,
        CodeStyle.Token... tokens)
    {

        // we clone the whole grammar, but override top-most tokens that are passed here

        final int size = tokens != null
            ? tokens.length
            : 0;

        if (size == 0)
        {
            return new GrammarImpl(name, clone(grammar).tokens());
        }

        final Map<String, CodeStyle.Token> overrides = new HashMap<>(size);
        for (CodeStyle.Token token : tokens)
        {
            overrides.put(token.name(), token);
        }

        final List<CodeStyle.Token> origins = grammar.tokens();
        final List<CodeStyle.Token> out = new ArrayList<>(origins.size());

        CodeStyle.Token override;

        for (CodeStyle.Token origin : origins)
        {
            override = overrides.get(origin.name());
            if (override != null)
            {
                out.add(override);
            } else
            {
                out.add(clone(origin));
            }
        }

        return new GrammarImpl(name, out);
    }

    @NotNull
    public static CodeStyle.Grammar extend(
        @NotNull CodeStyle.Grammar grammar,
        @NotNull String name,
        @NotNull TokenFilter filter,
        CodeStyle.Token... tokens)
    {

        final int size = tokens != null
            ? tokens.length
            : 0;

        final Map<String, CodeStyle.Token> overrides;
        if (size == 0)
        {
            overrides = Collections.emptyMap();
        } else
        {
            overrides = new HashMap<>(size);
            for (CodeStyle.Token token : tokens)
            {
                overrides.put(token.name(), token);
            }
        }

        final List<CodeStyle.Token> origins = grammar.tokens();
        final List<CodeStyle.Token> out = new ArrayList<>(origins.size());

        CodeStyle.Token override;

        for (CodeStyle.Token origin : origins)
        {

            // filter out undesired tokens
            if (!filter.test(origin))
            {
                continue;
            }

            override = overrides.get(origin.name());
            if (override != null)
            {
                out.add(override);
            } else
            {
                out.add(clone(origin));
            }
        }

        return new GrammarImpl(name, out);
    }

    @NotNull
    public static CodeStyle.Grammar require(@NotNull CodeStyle codeStyle, @NotNull String name)
    {
        final CodeStyle.Grammar grammar = codeStyle.grammar(name);
        if (grammar == null)
        {
            throw new IllegalStateException("Unexpected state, requested language is not found: " + name);
        }
        return grammar;
    }

    /**
     * Used when extending an existing grammar to filter out tokens that should not be cloned.
     *
     * @see #extend(CodeStyle.Grammar, String, TokenFilter, CodeStyle.Token...)
     */
    public interface TokenFilter
    {

        /**
         * @param token {@link CodeStyle.Token} to validate
         * @return a boolean indicating if supplied token should be included (passes the test)
         */
        boolean test(@NotNull CodeStyle.Token token);
    }
}
