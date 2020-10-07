package com.zeoflow.stylar.codestyle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class Cloner
{

    @NotNull
    static Cloner create()
    {
        return new Impl();
    }

    @NotNull
    abstract CodeStyle.Grammar clone(@NotNull CodeStyle.Grammar grammar);

    @NotNull
    abstract CodeStyle.Token clone(@NotNull CodeStyle.Token token);

    @NotNull
    abstract CodeStyle.Pattern clone(@NotNull CodeStyle.Pattern pattern);

    static class Impl extends Cloner
    {

        @NotNull
        @Override
        CodeStyle.Grammar clone(@NotNull CodeStyle.Grammar grammar)
        {
            return clone(new ContextImpl(), grammar);
        }

        @NotNull
        @Override
        CodeStyle.Token clone(@NotNull CodeStyle.Token token)
        {
            return clone(new ContextImpl(), token);
        }

        @NotNull
        @Override
        CodeStyle.Pattern clone(@NotNull CodeStyle.Pattern pattern)
        {
            return clone(new ContextImpl(), pattern);
        }

        @NotNull
        private CodeStyle.Grammar clone(@NotNull Context context, @NotNull CodeStyle.Grammar grammar)
        {

            CodeStyle.Grammar clone = context.grammar(grammar);
            if (clone != null)
            {
                return clone;
            }

            final List<CodeStyle.Token> tokens = grammar.tokens();
            final List<CodeStyle.Token> out = new ArrayList<>(tokens.size());

            clone = new GrammarImpl(grammar.name(), out);
            context.save(grammar, clone);

            for (CodeStyle.Token token : tokens)
            {
                out.add(clone(context, token));
            }

            return clone;
        }

        @NotNull
        private CodeStyle.Token clone(@NotNull Context context, @NotNull CodeStyle.Token token)
        {

            CodeStyle.Token clone = context.token(token);
            if (clone != null)
            {
                return clone;
            }

            final List<CodeStyle.Pattern> patterns = token.patterns();
            final List<CodeStyle.Pattern> out = new ArrayList<>(patterns.size());

            clone = new TokenImpl(token.name(), out);
            context.save(token, clone);

            for (CodeStyle.Pattern pattern : patterns)
            {
                out.add(clone(context, pattern));
            }

            return clone;
        }

        @NotNull
        private CodeStyle.Pattern clone(@NotNull Context context, @NotNull CodeStyle.Pattern pattern)
        {

            CodeStyle.Pattern clone = context.pattern(pattern);
            if (clone != null)
            {
                return clone;
            }

            final CodeStyle.Grammar inside = pattern.inside();

            clone = new PatternImpl(
                pattern.regex(),
                pattern.lookbehind(),
                pattern.greedy(),
                pattern.alias(),
                inside != null ? clone(context, inside) : null
            );

            context.save(pattern, clone);

            return clone;
        }

        interface Context
        {

            @Nullable
            CodeStyle.Grammar grammar(@NotNull CodeStyle.Grammar origin);

            @Nullable
            CodeStyle.Token token(@NotNull CodeStyle.Token origin);

            @Nullable
            CodeStyle.Pattern pattern(@NotNull CodeStyle.Pattern origin);


            void save(@NotNull CodeStyle.Grammar origin, @NotNull CodeStyle.Grammar clone);

            void save(@NotNull CodeStyle.Token origin, @NotNull CodeStyle.Token clone);

            void save(@NotNull CodeStyle.Pattern origin, @NotNull CodeStyle.Pattern clone);
        }

        private static class ContextImpl implements Context
        {

            private final Map<Integer, Object> cache = new HashMap<>(3);

            private static int key(@NotNull Object o)
            {
                return System.identityHashCode(o);
            }

            @Nullable
            @Override
            public CodeStyle.Grammar grammar(@NotNull CodeStyle.Grammar origin)
            {
                return (CodeStyle.Grammar) cache.get(key(origin));
            }

            @Nullable
            @Override
            public CodeStyle.Token token(@NotNull CodeStyle.Token origin)
            {
                return (CodeStyle.Token) cache.get(key(origin));
            }

            @Nullable
            @Override
            public CodeStyle.Pattern pattern(@NotNull CodeStyle.Pattern origin)
            {
                return (CodeStyle.Pattern) cache.get(key(origin));
            }

            @Override
            public void save(@NotNull CodeStyle.Grammar origin, @NotNull CodeStyle.Grammar clone)
            {
                cache.put(key(origin), clone);
            }

            @Override
            public void save(@NotNull CodeStyle.Token origin, @NotNull CodeStyle.Token clone)
            {
                cache.put(key(origin), clone);
            }

            @Override
            public void save(@NotNull CodeStyle.Pattern origin, @NotNull CodeStyle.Pattern clone)
            {
                cache.put(key(origin), clone);
            }
        }
    }
}
