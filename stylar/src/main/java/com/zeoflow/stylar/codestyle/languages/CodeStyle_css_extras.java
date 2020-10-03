package com.zeoflow.stylar.codestyle.languages;

import androidx.annotation.Nullable;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Modify;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Modify("css")
public class CodeStyle_css_extras
{

  @Nullable
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle) {

    final CodeStyle.Grammar css = codeStyle.grammar("css");

    if (css != null) {

      final CodeStyle.Token selector = GrammarUtils.findToken(css, "selector");
      if (selector != null) {
        final CodeStyle.Pattern pattern = pattern(
          compile("[^{}\\s][^{}]*(?=\\s*\\{)"),
          false,
          false,
          null,
          grammar("inside",
            token("pseudo-element", pattern(compile(":(?:after|before|first-letter|first-line|selection)|::[-\\w]+"))),
            token("pseudo-class", pattern(compile(":[-\\w]+(?:\\(.*\\))?"))),
            token("class", pattern(compile("\\.[-:.\\w]+"))),
            token("id", pattern(compile("#[-:.\\w]+"))),
            token("attribute", pattern(compile("\\[[^\\]]+\\]")))
          )
        );
        selector.patterns().clear();
        selector.patterns().add(pattern);
      }

      GrammarUtils.insertBeforeToken(css, "function",
        token("hexcode", pattern(compile("#[\\da-f]{3,8}", CASE_INSENSITIVE))),
        token("entity", pattern(compile("\\\\[\\da-f]{1,8}", CASE_INSENSITIVE))),
        token("number", pattern(compile("[\\d%.]+")))
      );
    }
    return null;
  }
}
