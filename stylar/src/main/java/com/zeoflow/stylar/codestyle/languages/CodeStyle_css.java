package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Modify;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Modify("markup")
public abstract class CodeStyle_css
{

  // todo: really important one..
  // before a language is requested (fro example css)
  // it won't be initialized (so we won't modify markup to highlight css) before it was requested...

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Grammar grammar = grammar(
      "css",
      token("comment", pattern(compile("\\/\\*[\\s\\S]*?\\*\\/"))),
      token(
        "atrule",
        pattern(
          compile("@[\\w-]+?.*?(?:;|(?=\\s*\\{))", CASE_INSENSITIVE),
          false,
          false,
          null,
          grammar(
            "inside",
            token("rule", pattern(compile("@[\\w-]+")))
          )
        )
      ),
      token(
        "url",
        pattern(compile("url\\((?:([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1|.*?)\\)", CASE_INSENSITIVE))
      ),
      token("selector", pattern(compile("[^{}\\s][^{};]*?(?=\\s*\\{)"))),
      token(
        "string",
        pattern(compile("(\"|')(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
      ),
      token(
        "property",
        pattern(compile("[-_a-z\\xA0-\\uFFFF][-\\w\\xA0-\\uFFFF]*(?=\\s*:)", CASE_INSENSITIVE))
      ),
      token("important", pattern(compile("\\B!important\\b", CASE_INSENSITIVE))),
      token("function", pattern(compile("[-a-z0-9]+(?=\\()", CASE_INSENSITIVE))),
      token("punctuation", pattern(compile("[(){};:]")))
    );

    // can we maybe add some helper to specify simplified location?

    // now we need to put the all tokens from grammar inside `atrule` (except the `atrule` of cause)
    final CodeStyle.Token atrule = grammar.tokens().get(1);
    final CodeStyle.Grammar inside = GrammarUtils.findFirstInsideGrammar(atrule);
    if (inside != null)
    {
      for (CodeStyle.Token token : grammar.tokens())
      {
        if (!"atrule".equals(token.name()))
        {
          inside.tokens().add(token);
        }
      }
    }

    final CodeStyle.Grammar markup = codeStyle.grammar("markup");
    if (markup != null)
    {
      GrammarUtils.insertBeforeToken(markup, "tag",
        token(
          "style",
          pattern(
            compile("(<style[\\s\\S]*?>)[\\s\\S]*?(?=<\\/style>)", CASE_INSENSITIVE),
            true,
            true,
            "language-css",
            grammar
          )
        )
      );

      // important thing here is to clone found grammar
      // otherwise we will have stackoverflow (inside tag references style-attr, which
      // references inside tag, etc)
      final CodeStyle.Grammar markupTagInside;
      {
        CodeStyle.Grammar _temp = null;
        final CodeStyle.Token token = GrammarUtils.findToken(markup, "tag");
        if (token != null)
        {
          _temp = GrammarUtils.findFirstInsideGrammar(token);
          if (_temp != null)
          {
            _temp = GrammarUtils.clone(_temp);
          }
        }
        markupTagInside = _temp;
      }

      GrammarUtils.insertBeforeToken(markup, "tag/attr-value",
        token(
          "style-attr",
          pattern(
            compile("\\s*style=(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1", CASE_INSENSITIVE),
            false,
            false,
            "language-css",
            grammar(
              "inside",
              token(
                "attr-name",
                pattern(
                  compile("^\\s*style", CASE_INSENSITIVE),
                  false,
                  false,
                  null,
                  markupTagInside
                )
              ),
              token("punctuation", pattern(compile("^\\s*=\\s*['\"]|['\"]\\s*$"))),
              token(
                "attr-value",
                pattern(
                  compile(".+", CASE_INSENSITIVE),
                  false,
                  false,
                  null,
                  grammar
                )
              )

            )
          )
        )
      );
    }

    return grammar;
  }

  private CodeStyle_css()
  {
  }
}
