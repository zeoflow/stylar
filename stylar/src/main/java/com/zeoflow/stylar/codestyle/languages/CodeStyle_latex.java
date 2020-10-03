package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class CodeStyle_latex
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final Pattern funcPattern = compile("\\\\(?:[^a-z()\\[\\]]|[a-z*]+)", CASE_INSENSITIVE);

    final CodeStyle.Grammar insideEqu = grammar("inside",
      token("equation-command", pattern(funcPattern, false, false, "regex"))
    );

    return grammar("latex",
      token("comment", pattern(compile("%.*", MULTILINE))),
      token("cdata", pattern(
        compile("(\\\\begin\\{((?:verbatim|lstlisting)\\*?)\\})[\\s\\S]*?(?=\\\\end\\{\\2\\})"),
        true
        )
      ),
      token("equation",
        pattern(
          compile("\\$(?:\\\\[\\s\\S]|[^\\\\$])*\\$|\\\\\\([\\s\\S]*?\\\\\\)|\\\\\\[[\\s\\S]*?\\\\\\]"),
          false,
          false,
          "string",
          insideEqu
        ),
        pattern(
          compile("(\\\\begin\\{((?:equation|math|eqnarray|align|multline|gather)\\*?)\\})[\\s\\S]*?(?=\\\\end\\{\\2\\})"),
          true,
          false,
          "string",
          insideEqu
        )
      ),
      token("keyword", pattern(
        compile("(\\\\(?:begin|end|ref|cite|label|usepackage|documentclass)(?:\\[[^\\]]+\\])?\\{)[^}]+(?=\\})"),
        true
      )),
      token("url", pattern(
        compile("(\\\\url\\{)[^}]+(?=\\})"),
        true
      )),
      token("headline", pattern(
        compile("(\\\\(?:part|chapter|section|subsection|frametitle|subsubsection|paragraph|subparagraph|subsubparagraph|subsubsubparagraph)\\*?(?:\\[[^\\]]+\\])?\\{)[^}]+(?=\\}(?:\\[[^\\]]+\\])?)"),
        true,
        false,
        "class-name"
      )),
      token("function", pattern(
        funcPattern,
        false,
        false,
        "selector"
      )),
      token("punctuation", pattern(compile("[\\[\\]{}&]")))
    );
  }
}
