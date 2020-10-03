package com.zeoflow.stylar.codestyle.languages;

import android.util.Log;

import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

@Extend("clike")
public class CodeStyle_java
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Token keyword = token("keyword", pattern(compile("\\b(?:abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b")));

    final CodeStyle.Grammar java = GrammarUtils.extend(GrammarUtils.require(codeStyle, "clike"), "java",
      keyword,
      token("number", pattern(compile("\\b0b[01]+\\b|\\b0x[\\da-f]*\\.?[\\da-fp-]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?[df]?", CASE_INSENSITIVE))),
      token("operator", pattern(
        compile("(^|[^.])(?:\\+[+=]?|-[-=]?|!=?|<<?=?|>>?>?=?|==?|&[&=]?|\\|[|=]?|\\*=?|\\/=?|%=?|\\^=?|[?:~])", MULTILINE),
        true
      ))
    );

    GrammarUtils.insertBeforeToken(java, "function",
      token("annotation", pattern(
        compile("(^|[^.])@\\w+"),
        true,
        false,
        "punctuation"
      ))
    );

    GrammarUtils.insertBeforeToken(java, "class-name",
      token("generics", pattern(
        compile("<\\s*\\w+(?:\\.\\w+)?(?:\\s*,\\s*\\w+(?:\\.\\w+)?)*>", CASE_INSENSITIVE),
        false,
        false,
        "function",
        grammar(
          "inside",
          keyword,
          token("punctuation", pattern(compile("[<>(),.:]")))
        )
      ))
    );

    return java;
  }
}
