package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

@Extend("clike")
public class CodeStyle_c
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Grammar c = GrammarUtils.extend(
      GrammarUtils.require(codeStyle, "clike"),
      "c",
      token ->
      {
        final String name = token.name();
        return !"class-name".equals(name) && !"boolean".equals(name);
      },
      token("keyword", pattern(compile("\\b(?:_Alignas|_Alignof|_Atomic|_Bool|_Complex|_Generic|_Imaginary|_Noreturn|_Static_assert|_Thread_local|asm|typeof|inline|auto|break|case|char|const|continue|default|do|double|else|enum|extern|float|for|goto|if|int|long|register|return|short|signed|sizeof|static|struct|switch|typedef|union|unsigned|void|volatile|while)\\b"))),
      token("operator", pattern(compile("-[>-]?|\\+\\+?|!=?|<<?=?|>>?=?|==?|&&?|\\|\\|?|[~^%?*\\/]"))),
      token("number", pattern(compile("(?:\\b0x[\\da-f]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?)[ful]*", CASE_INSENSITIVE)))
    );

    GrammarUtils.insertBeforeToken(c, "string",
      token("macro", pattern(
        compile("(^\\s*)#\\s*[a-z]+(?:[^\\r\\n\\\\]|\\\\(?:\\r\\n|[\\s\\S]))*", CASE_INSENSITIVE | MULTILINE),
        true,
        false,
        "property",
        grammar("inside",
          token("string", pattern(compile("(#\\s*include\\s*)(?:<.+?>|(\"|')(?:\\\\?.)+?\\2)"), true)),
          token("directive", pattern(
            compile("(#\\s*)\\b(?:define|defined|elif|else|endif|error|ifdef|ifndef|if|import|include|line|pragma|undef|using)\\b"),
            true,
            false,
            "keyword"
          ))
        )
      )),
      token("constant", pattern(compile("\\b(?:__FILE__|__LINE__|__DATE__|__TIME__|__TIMESTAMP__|__func__|EOF|NULL|SEEK_CUR|SEEK_END|SEEK_SET|stdin|stdout|stderr)\\b")))
    );

    return c;
  }
}
