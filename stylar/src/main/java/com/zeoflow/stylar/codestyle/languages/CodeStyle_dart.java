package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.compile;

@Extend("clike")
public class CodeStyle_dart
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Grammar dart = GrammarUtils.extend(
      GrammarUtils.require(codeStyle, "clike"),
      "dart",
      token("string",
        pattern(compile("r?(\"\"\"|''')[\\s\\S]*?\\1"), false, true),
        pattern(compile("r?(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
      ),
      token("keyword",
        pattern(compile("\\b(?:async|sync|yield)\\*")),
        pattern(compile("\\b(?:abstract|assert|async|await|break|case|catch|class|const|continue|default|deferred|do|dynamic|else|enum|export|external|extends|factory|final|finally|for|get|if|implements|import|in|library|new|null|operator|part|rethrow|return|set|static|super|switch|this|throw|try|typedef|var|void|while|with|yield)\\b"))
      ),
      token("operator", pattern(compile("\\bis!|\\b(?:as|is)\\b|\\+\\+|--|&&|\\|\\||<<=?|>>=?|~(?:\\/=?)?|[+\\-*\\/%&^|=!<>]=?|\\?")))
    );

    GrammarUtils.insertBeforeToken(dart, "function",
      token("metadata", pattern(compile("@\\w+"), false, false, "symbol"))
    );

    return dart;
  }
}
