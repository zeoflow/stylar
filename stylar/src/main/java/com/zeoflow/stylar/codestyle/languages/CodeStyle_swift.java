package com.zeoflow.stylar.codestyle.languages;

import com.zeoflow.stylar.codestyle.CodeStyle;
import com.zeoflow.stylar.codestyle.GrammarUtils;
import com.zeoflow.stylar.codestyle.annotations.Extend;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zeoflow.stylar.codestyle.CodeStyle.grammar;
import static com.zeoflow.stylar.codestyle.CodeStyle.pattern;
import static com.zeoflow.stylar.codestyle.CodeStyle.token;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@Extend("clike")
public class CodeStyle_swift
{

  @NotNull
  public static CodeStyle.Grammar create(@NotNull CodeStyle codeStyle)
  {

    final CodeStyle.Grammar swift = GrammarUtils.extend(
      GrammarUtils.require(codeStyle, "clike"),
      "swift",
      token("string", pattern(
        compile("(\"|')(\\\\(?:\\((?:[^()]|\\([^)]+\\))+\\)|\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"),
        false,
        true,
        null,
        grammar("inside", token("interpolation", pattern(
          compile("\\\\\\((?:[^()]|\\([^)]+\\))+\\)"),
          false,
          false,
          null,
          grammar("inside", token("delimiter", pattern(
            compile("^\\\\\\(|\\)$"),
            false,
            false,
            "variable"
          )))
        )))
      )),
      token("keyword", pattern(
        compile("\\b(?:as|associativity|break|case|catch|class|continue|convenience|default|defer|deinit|didSet|do|dynamic(?:Type)?|else|enum|extension|fallthrough|final|for|func|get|guard|if|import|in|infix|init|inout|internal|is|lazy|left|let|mutating|new|none|nonmutating|operator|optional|override|postfix|precedence|prefix|private|protocol|public|repeat|required|rethrows|return|right|safe|self|Self|set|static|struct|subscript|super|switch|throws?|try|Type|typealias|unowned|unsafe|var|weak|where|while|willSet|__(?:COLUMN__|FILE__|FUNCTION__|LINE__))\\b")
      )),
      token("number", pattern(
        compile("\\b(?:[\\d_]+(?:\\.[\\de_]+)?|0x[a-f0-9_]+(?:\\.[a-f0-9p_]+)?|0b[01_]+|0o[0-7_]+)\\b", CASE_INSENSITIVE)
      ))
    );

    final List<CodeStyle.Token> tokens = swift.tokens();

    tokens.add(token("constant", pattern(compile("\\b(?:nil|[A-Z_]{2,}|k[A-Z][A-Za-z_]+)\\b"))));
    tokens.add(token("atrule", pattern(compile("@\\b(?:IB(?:Outlet|Designable|Action|Inspectable)|class_protocol|exported|noreturn|NS(?:Copying|Managed)|objc|UIApplicationMain|auto_closure)\\b"))));
    tokens.add(token("builtin", pattern(compile("\\b(?:[A-Z]\\S+|abs|advance|alignof(?:Value)?|assert|contains|count(?:Elements)?|debugPrint(?:ln)?|distance|drop(?:First|Last)|dump|enumerate|equal|filter|find|first|getVaList|indices|isEmpty|join|last|lexicographicalCompare|map|max(?:Element)?|min(?:Element)?|numericCast|overlaps|partition|print(?:ln)?|reduce|reflect|reverse|sizeof(?:Value)?|sort(?:ed)?|split|startsWith|stride(?:of(?:Value)?)?|suffix|swap|toDebugString|toString|transcode|underestimateCount|unsafeBitCast|with(?:ExtendedLifetime|Unsafe(?:MutablePointers?|Pointers?)|VaList))\\b"))));

    final CodeStyle.Token interpolationToken = GrammarUtils.findToken(swift, "string/interpolation");
    final CodeStyle.Grammar interpolationGrammar = interpolationToken != null
      ? GrammarUtils.findFirstInsideGrammar(interpolationToken)
      : null;
    if (interpolationGrammar != null)
    {
      interpolationGrammar.tokens().addAll(swift.tokens());
    }

    return swift;
  }
}
