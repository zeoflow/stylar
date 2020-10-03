package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;

import org.jetbrains.annotations.NotNull;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class CodeStyle_git
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {
    return grammar("git",
      token("comment", pattern(compile("^#.*", MULTILINE))),
      token("deleted", pattern(compile("^[-–].*", MULTILINE))),
      token("inserted", pattern(compile("^\\+.*", MULTILINE))),
      token("string", pattern(compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1", MULTILINE))),
      token("command", pattern(
        compile("^.*\\$ git .*$", MULTILINE),
        false,
        false,
        null,
        grammar("inside",
          token("parameter", pattern(compile("\\s--?\\w+", MULTILINE)))
        )
      )),
      token("coord", pattern(compile("^@@.*@@$", MULTILINE))),
      token("commit_sha1", pattern(compile("^commit \\w{40}$", MULTILINE)))
    );
  }
}
