package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Extend("java")
public class CodeStyle_scala
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {
    final CodeStyle.Grammar scala = GrammarUtils.extend(
      GrammarUtils.require(codeStyle, "java"),
      "scala",
      new GrammarUtils.TokenFilter()
      {
        @Override
        public boolean test(@NotNull CodeStyle.Token token)
        {
          final String name = token.name();
          return !"class-name".equals(name) && !"function".equals(name);
        }
      },
      token("keyword", pattern(
        compile("<-|=>|\\b(?:abstract|case|catch|class|def|do|else|extends|final|finally|for|forSome|if|implicit|import|lazy|match|new|null|object|override|package|private|protected|return|sealed|self|super|this|throw|trait|try|type|val|var|while|with|yield)\\b")
      )),
      token("string",
        pattern(compile("\"\"\"[\\s\\S]*?\"\"\""), false, true),
        pattern(compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
      ),
      token("number", pattern(
        compile("\\b0x[\\da-f]*\\.?[\\da-f]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e\\d+)?[dfl]?", CASE_INSENSITIVE)
      ))
    );

    scala.tokens().add(
      token("symbol", pattern(compile("'[^\\d\\s\\\\]\\w*")))
    );

    GrammarUtils.insertBeforeToken(scala, "number",
      token("builtin", pattern(compile("\\b(?:String|Int|Long|Short|Byte|Boolean|Double|Float|Char|Any|AnyRef|AnyVal|Unit|Nothing)\\b")))
    );

    return scala;
  }
}
