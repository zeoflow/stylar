package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.compile;

public class CodeStyle_brainfuck
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {
    return grammar("brainfuck",
      token("pointer", pattern(compile("<|>"), false, false, "keyword")),
      token("increment", pattern(compile("\\+"), false, false, "inserted")),
      token("decrement", pattern(compile("-"), false, false, "deleted")),
      token("branching", pattern(compile("\\[|\\]"), false, false, "important")),
      token("operator", pattern(compile("[.,]"))),
      token("comment", pattern(compile("\\S+")))
    );
  }
}
