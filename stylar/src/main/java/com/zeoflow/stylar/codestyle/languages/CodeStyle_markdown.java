package com.zeoflow.stylar.codestyle.languages;

import androidx.annotation.Nullable;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

@Extend("markup")
public class CodeStyle_markdown
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Grammar markdown = GrammarUtils.extend(
      GrammarUtils.require(codeStyle, "markup"),
      "markdown"
    );

    final CodeStyle.Token bold = token("bold", pattern(
      compile("(^|[^\\\\])(\\*\\*|__)(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
      true,
      false,
      null,
      grammar("inside", token("punctuation", pattern(compile("^\\*\\*|^__|\\*\\*$|__$"))))
    ));

    final CodeStyle.Token italic = token("italic", pattern(
      compile("(^|[^\\\\])([*_])(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
      true,
      false,
      null,
      grammar("inside", token("punctuation", pattern(compile("^[*_]|[*_]$"))))
    ));

    final CodeStyle.Token url = token("url", pattern(
      compile("!?\\[[^\\]]+\\](?:\\([^\\s)]+(?:[\\t ]+\"(?:\\\\.|[^\"\\\\])*\")?\\)| ?\\[[^\\]\\n]*\\])"),
      false,
      false,
      null,
      grammar("inside",
        token("variable", pattern(compile("(!?\\[)[^\\]]+(?=\\]$)"), true)),
        token("string", pattern(compile("\"(?:\\\\.|[^\"\\\\])*\"(?=\\)$)")))
      )
    ));

    GrammarUtils.insertBeforeToken(markdown, "prolog",
      token("blockquote", pattern(compile("^>(?:[\\t ]*>)*", MULTILINE))),
      token("code",
        pattern(compile("^(?: {4}|\\t).+", MULTILINE), false, false, "keyword"),
        pattern(compile("``.+?``|`[^`\\n]+`"), false, false, "keyword")
      ),
      token(
        "title",
        pattern(
          compile("\\w+.*(?:\\r?\\n|\\r)(?:==+|--+)"),
          false,
          false,
          "important",
          grammar("inside", token("punctuation", pattern(compile("==+$|--+$"))))
        ),
        pattern(
          compile("(^\\s*)#+.+", MULTILINE),
          true,
          false,
          "important",
          grammar("inside", token("punctuation", pattern(compile("^#+|#+$"))))
        )
      ),
      token("hr", pattern(
        compile("(^\\s*)([*-])(?:[\\t ]*\\2){2,}(?=\\s*$)", MULTILINE),
        true,
        false,
        "punctuation"
      )),
      token("list", pattern(
        compile("(^\\s*)(?:[*+-]|\\d+\\.)(?=[\\t ].)", MULTILINE),
        true,
        false,
        "punctuation"
      )),
      token("url-reference", pattern(
        compile("!?\\[[^\\]]+\\]:[\\t ]+(?:\\S+|<(?:\\\\.|[^>\\\\])+>)(?:[\\t ]+(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\)))?"),
        false,
        false,
        "url",
        grammar("inside",
          token("variable", pattern(compile("^(!?\\[)[^\\]]+"), true)),
          token("string", pattern(compile("(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\))$"))),
          token("punctuation", pattern(compile("^[\\[\\]!:]|[<>]")))
        )
      )),
      bold,
      italic,
      url
    );

    add(GrammarUtils.findFirstInsideGrammar(bold), url, italic);
    add(GrammarUtils.findFirstInsideGrammar(italic), url, bold);

    return markdown;
  }

  private static void add(@Nullable CodeStyle.Grammar grammar, @NotNull CodeStyle.Token first, @NotNull CodeStyle.Token second)
  {
    if (grammar != null)
    {
      grammar.tokens().add(first);
      grammar.tokens().add(second);
    }
  }
}
