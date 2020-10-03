package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.compile;

public abstract class CodeStyle_clike
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle) {
    return grammar(
      "clike",
      token(
        "comment",
        pattern(compile("(^|[^\\\\])\\/\\*[\\s\\S]*?(?:\\*\\/|$)"), true),
        pattern(compile("(^|[^\\\\:])\\/\\/.*"), true, true)
      ),
      token(
        "string",
        pattern(compile("([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
      ),
      token(
        "class-name",
        pattern(
          compile("((?:\\b(?:class|interface|extends|implements|trait|instanceof|new)\\s+)|(?:catch\\s+\\())[\\w.\\\\]+"),
          true,
          false,
          null,
          grammar("inside", token("punctuation", pattern(compile("[.\\\\]"))))
        )
      ),
      token(
        "keyword",
        pattern(compile("\\b(?:if|else|while|do|for|return|in|instanceof|function|new|try|throw|catch|finally|null|break|continue)\\b"))
      ),
      token("boolean", pattern(compile("\\b(?:true|false)\\b"))),
      token("function", pattern(compile("[a-z0-9_]+(?=\\()", Pattern.CASE_INSENSITIVE))),
      token(
        "number",
        pattern(compile("\\b0x[\\da-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?", Pattern.CASE_INSENSITIVE))
      ),
      token("operator", pattern(compile("--?|\\+\\+?|!=?=?|<=?|>=?|==?=?|&&?|\\|\\|?|\\?|\\*|\\/|~|\\^|%"))),
      token("punctuation", pattern(compile("[{}\\[\\];(),.:]")))
    );
  }

  private CodeStyle_clike() {
  }
}
