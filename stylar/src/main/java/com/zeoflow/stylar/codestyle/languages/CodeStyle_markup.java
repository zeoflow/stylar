package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.annotations.Aliases;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.compile;

@Aliases({"xml", "html", "mathml", "svg"})
public abstract class CodeStyle_markup
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle) {
    final CodeStyle.Token entity = token("entity", pattern(compile("&#?[\\da-z]{1,8};", Pattern.CASE_INSENSITIVE)));
    return grammar(
      "markup",
      token("comment", pattern(compile("<!--[\\s\\S]*?-->"))),
      token("prolog", pattern(compile("<\\?[\\s\\S]+?\\?>"))),
      token("doctype", pattern(compile("<!DOCTYPE[\\s\\S]+?>", Pattern.CASE_INSENSITIVE))),
      token("cdata", pattern(compile("<!\\[CDATA\\[[\\s\\S]*?]]>", Pattern.CASE_INSENSITIVE))),
      token(
        "tag",
        pattern(
          compile("<\\/?(?!\\d)[^\\s>\\/=$<%]+(?:\\s+[^\\s>\\/=]+(?:=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+))?)*\\s*\\/?>", Pattern.CASE_INSENSITIVE),
          false,
          true,
          null,
          grammar(
            "inside",
            token(
              "tag",
              pattern(
                compile("^<\\/?[^\\s>\\/]+", Pattern.CASE_INSENSITIVE),
                false,
                false,
                null,
                grammar(
                  "inside",
                  token("punctuation", pattern(compile("^<\\/?"))),
                  token("namespace", pattern(compile("^[^\\s>\\/:]+:")))
                )
              )
            ),
            token(
              "attr-value",
              pattern(
                compile("=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+)", Pattern.CASE_INSENSITIVE),
                false,
                false,
                null,
                grammar(
                  "inside",
                  token(
                    "punctuation",
                    pattern(compile("^=")),
                    pattern(compile("(^|[^\\\\])[\"']"), true)
                  ),
                  entity
                )
              )
            ),
            token("punctuation", pattern(compile("\\/?>"))),
            token(
              "attr-name",
              pattern(
                compile("[^\\s>\\/]+"),
                false,
                false,
                null,
                grammar(
                  "inside",
                  token("namespace", pattern(compile("^[^\\s>\\/:]+:")))
                )
              )
            )
          )
        )
      ),
      entity
    );
  }

  private CodeStyle_markup() {
  }
}
